/*
// $Id: //open/mondrian/src/main/mondrian/rolap/RolapResult.java#55 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// (C) Copyright 2001-2005 Kana Software, Inc. and others.
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
//
// jhyde, 10 August, 2001
*/

package mondrian.rolap;
import mondrian.olap.*;
import mondrian.olap.fun.MondrianEvaluationException;
import mondrian.rolap.agg.AggregationManager;
import mondrian.resource.MondrianResource;

import org.apache.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

/**
 * A <code>RolapResult</code> is the result of running a query.
 *
 * @author jhyde
 * @since 10 August, 2001
 * @version $Id: //open/mondrian/src/main/mondrian/rolap/RolapResult.java#55 $
 */
class RolapResult extends ResultBase {

    private static final Logger LOGGER = Logger.getLogger(ResultBase.class);

    private static final int MAX_AGGREGATION_PASS_COUNT = 5;

    private final RolapEvaluator evaluator;
    /**
     * Evaluator containing context resulting from evaluating the slicer.
     */
    private RolapEvaluator slicerEvaluator;
    private final CellKey point;
    private final Map cellValues;
    private final FastBatchingCellReader batchingReader;
    AggregatingCellReader aggregatingReader = new AggregatingCellReader();
    private final int[] modulos;
    private final Random random =
            Util.createRandom(MondrianProperties.instance().TestSeed.get());

    /**
     * Maps expressions to the dimensions which they are independent of.
     */
    private final Map expIndDims = new HashMap();


    /**
     * Maps the names of sets to their values. Populated on demand.
     */
    private final Map namedSetValues = new HashMap();

    RolapResult(Query query) {
        super(query, new RolapAxis[query.axes.length]);


        this.point = new CellKey(new int[query.axes.length]);
        final int expDeps = MondrianProperties.instance().TestExpDependencies.get();
        if (expDeps > 0) {
            this.evaluator = new DependencyTestingEvaluator(this, expDeps);
        } else {
            final RolapEvaluator.RolapEvaluatorRoot root =
                    new RolapEvaluator.RolapEvaluatorRoot(this);
            this.evaluator = new RolapEvaluator(root);
        }
        RolapCube rcube = (RolapCube) query.getCube();
        this.batchingReader = new FastBatchingCellReader(rcube);
        this.cellValues = new HashMap();

        try {
            for (int i = -1; i < axes.length; i++) {
                QueryAxis axis;
                if (i == -1) {
                    if (query.slicer != null) {
                        FunCall call = new FunCall(
                                "{}",
                                Syntax.Braces,
                                new Exp[] {
                                    new FunCall(
                                            "()",
                                            Syntax.Parentheses,
                                            new Exp[] {
                                                query.slicer}
                                    )});
                        Exp call2 = call.accept(query.createValidator());
                        axis = new QueryAxis(
                                false,
                                call2,
                                AxisOrdinal.Slicer,
                                QueryAxis.SubtotalVisibility.Undefined);
                    } else {
                        axis = null;
                    }
                } else {
                    axis = query.axes[i];
                }

                int attempt = 0;
                while (true) {
                    evaluator.setCellReader(batchingReader);
                    RolapAxis axisResult = executeAxis(evaluator.push(), axis);
                    Util.discard(axisResult);
                    evaluator.clearExpResultCache();
                    if (!batchingReader.loadAggregations()) {
                        break;
                    }
                    if (attempt++ > MAX_AGGREGATION_PASS_COUNT) {
                        throw Util.newInternal("Failed to load all aggregations after " +
                                MAX_AGGREGATION_PASS_COUNT +
                                "passes; there's probably a cycle");
                    }
                }

                evaluator.setCellReader(aggregatingReader);
                RolapAxis axisResult = executeAxis(evaluator.push(), axis);
                evaluator.clearExpResultCache();

                if (i == -1) {
                    this.slicerAxis = axisResult;
                    // Use the context created by the slicer for the other
                    // axes.  For example, "select filter([Customers], [Store
                    // Sales] > 100) on columns from Sales where
                    // ([Time].[1998])" should show customers whose 1998 (not
                    // total) purchases exceeded 100.
                    switch (this.slicerAxis.positions.length) {
                    case 0:
                        throw MondrianResource.instance().EmptySlicer.ex();
                    case 1:
                        break;
                    default:
                        throw MondrianResource.instance().CompoundSlicer.ex();
                    }
                    Position position = this.slicerAxis.positions[0];
                    for (int j = 0; j < position.members.length; j++) {
                        Member member = position.members[j];
                        if (member == null) {
                            throw MondrianResource.instance().EmptySlicer.ex();
                        }
                        evaluator.setContext(member);
                    }
                    slicerEvaluator = (RolapEvaluator) evaluator.push();
                } else {
                    this.axes[i] = axisResult;
                }
            }
            // Now that the axes are evaluated, make sure that the number of
            // cells does not exceed the result limit.
            int limit = MondrianProperties.instance().ResultLimit.get();
            if (limit > 0) {
                // result limit exceeded, throw an exception
                long n = 1;
                for (int i = 0; i < axes.length; i++) {
                    n = n * axes[i].positions.length;
                }
                if (n > limit) {
                    throw MondrianResource.instance().
                        LimitExceededDuringCrossjoin.ex(
                                new Long(n), new Long(limit));
                }
            }

            // Suppose the result is 4 x 3 x 2, then modulo = {1, 4, 12, 24}.
            //
            // Then the ordinal of cell (3, 2, 1)
            //  = (modulo[0] * 3) + (modulo[1] * 2) + (modulo[2] * 1)
            //  = (1 * 3) + (4 * 2) + (12 * 1)
            //  = 23
            //
            // Reverse calculation:
            // p[0] = (23 % modulo[1]) / modulo[0] = (23 % 4) / 1 = 3
            // p[1] = (23 % modulo[2]) / modulo[1] = (23 % 12) / 4 = 2
            // p[2] = (23 % modulo[3]) / modulo[2] = (23 % 24) / 12 = 1
            this.modulos = new int[axes.length + 1];
            int modulo = modulos[0] = 1;
            for (int i = 0; i < axes.length; i++) {
                modulo *= axes[i].positions.length;
                modulos[i + 1] = modulo;
            }
            executeBody(query);
        } finally {
            evaluator.clearExpResultCache();
        }
        // RME : what is this doing???
        query.getCube().getDimensions();
    }

    protected Logger getLogger() {
        return LOGGER;
    }

    // implement Result
    public Axis[] getAxes() {
        return axes;
    }
    public Cell getCell(int[] pos) {
        if (pos.length != point.ordinals.length) {
            throw Util.newError(
                    "coordinates should have dimension " + point.ordinals.length);
        }
        Object value = cellValues.get(new CellKey(pos));
        if (value == null) {
            value = Util.nullValue;
        }
        return new RolapCell(this, getCellOrdinal(pos), value);
    }

    private RolapAxis executeAxis(Evaluator evaluator, QueryAxis axis) {
        Position[] positions;
        if (axis == null) {
            // Create an axis containing one position with no members (not
            // the same as an empty axis).
            Member[] members = new Member[0];
            RolapPosition position = new RolapPosition(members);
            positions = new Position[] {position};

        } else {
            Exp exp = axis.set;
            evaluator.setNonEmpty(axis.nonEmpty);
            Object value = exp.evaluate(evaluator);
            evaluator.setNonEmpty(false);
            if (value == null) {
                value = Collections.EMPTY_LIST;
            }
            Util.assertTrue(value instanceof List);
            List list = (List) value;
            positions = new Position[list.size()];
            for (int i = 0; i < list.size(); i++) {
                Member[] members = null;
                Object o = list.get(i);
                if (o instanceof Object[]) {
                    Object[] a = (Object[]) o;
                    members = new Member[a.length];
                    for (int j = 0; j < a.length; j++) {
                        members[j] = (Member) a[j];
                    }
                } else {
                    members = new Member[] {(Member) o};
                }
                RolapPosition position = new RolapPosition(members);
                positions[i] = position;
            }
        }
        return new RolapAxis(positions);
    }

    private void executeBody(Query query) {
        try {
            // Compute the cells several times. The first time, use a dummy
            // evaluator which collects requests.
            int count = 0;
            while (true) {
                cellValues.clear();

                evaluator.setCellReader(this.batchingReader);
                executeStripe(query.axes.length - 1,
                    (RolapEvaluator) evaluator.push());
                evaluator.clearExpResultCache();

                // Retrieve the aggregations collected.
                //
                //
                if (!batchingReader.loadAggregations()) {
                    // We got all of the cells we needed, so the result must be
                    // correct.
                    return;
                }
                if (count++ > MAX_AGGREGATION_PASS_COUNT) {
                    if (evaluator instanceof DependencyTestingEvaluator) {
                        // The dependency testing evaluator can trigger new
                        // requests every cycle. So let is run as normal for
                        // the first N times, then run it disabled.
                        ((DependencyTestingEvalutorRoot) evaluator.root).disabled = true;
                        if (count > MAX_AGGREGATION_PASS_COUNT * 2) {
                            throw Util.newInternal("Query required more than "
                                + count + " iterations");
                        }
                    } else {
                        throw Util.newInternal("Query required more than "
                            + count + " iterations");
                    }
                }
            }
        } finally {
            RolapCube cube = (RolapCube) query.getCube();
            cube.clearCache();
        }
    }

    /**
     * Evaluates a named set.
     *
     * <p>A given set is only evaluated once each time a query is executed; the
     * result is added to the {@link #namedSetValues} cache on first execution
     * and re-used.
     *
     * <p>Named sets are always evaluated in the context of the slicer.
     */
    Object evaluateNamedSet(String name, Exp exp) {
        Object value = namedSetValues.get(name);
        if (value == null) {
            value = evaluateExp(exp, (RolapEvaluator) slicerEvaluator.push());

            namedSetValues.put(name, value);
        }
        return value;
    }

    private Object evaluateExp(Exp exp, RolapEvaluator evaluator) {
        int attempt = 0;
        boolean dirty = batchingReader.isDirty();
        while (true) {
            RolapEvaluator ev = (RolapEvaluator) evaluator.push();

            ev.setCellReader(batchingReader);
            Object preliminaryValue = exp.evaluate(ev);
            Util.discard(preliminaryValue);
            if (!batchingReader.loadAggregations()) {
                break;
            }
            if (attempt++ > MAX_AGGREGATION_PASS_COUNT) {
                throw Util.newInternal("Failed to load all aggregations after " +
                        MAX_AGGREGATION_PASS_COUNT +
                        "passes; there's probably a cycle");
            }
        }

        // If there were pending reads when we entered, some of the other
        // expressions may have been evaluated incorrectly. Set the reaader's
        // 'dirty' flag so that the caller knows that it must re-evaluate them.
        if (dirty) {
            batchingReader.setDirty(true);
        }
        RolapEvaluator ev = (RolapEvaluator) evaluator.push();
        ev.setCellReader(aggregatingReader);
        Object value = exp.evaluate(ev);
        return value;
    }

    /**
     * An <code>AggregatingCellReader</code> reads cell values from the
     * {@link RolapAggregationManager}.
     **/
    private static class AggregatingCellReader implements CellReader {
        private final RolapAggregationManager aggMan =
            AggregationManager.instance();
        /**
         * Overrides {@link CellReader#get}. Returns <code>null</code> if no
         * aggregation contains the required cell.
         **/
        // implement CellReader
        public Object get(Evaluator evaluator) {
            final RolapEvaluator rolapEvaluator = (RolapEvaluator) evaluator;
            return aggMan.getCellFromCache(rolapEvaluator.getCurrentMembers());
        }
    }

    private void executeStripe(int axisOrdinal, RolapEvaluator evaluator) {
        if (axisOrdinal < 0) {
            RolapAxis axis = (RolapAxis) slicerAxis;
            int count = axis.positions.length;
            for (int i = 0; i < count; i++) {
                RolapPosition position = (RolapPosition) axis.positions[i];
                for (int j = 0; j < position.members.length; j++) {
                    evaluator.setContext(position.members[j]);
                }
                Object o;
                try {
                    o = evaluator.evaluateCurrent();
                } catch (MondrianEvaluationException e) {
                    o = e;
                }
                if (o != null && o != RolapUtil.valueNotReadyException) {
                    CellKey key = point.copy();
                    cellValues.put(key, o);
                    // Compute the formatted value, to ensure that any needed
                    // values are in the cache.
                    try {
                        Cell cell = getCell(point.ordinals);
                        Util.discard(cell.getFormattedValue());
                    } catch (MondrianEvaluationException e) {
                        // ignore
                    } catch (Throwable e) {
                        Util.discard(e);
                    }
                }
            }
        } else {
            RolapAxis axis = (RolapAxis) axes[axisOrdinal];
            int count = axis.positions.length;
            for (int i = 0; i < count; i++) {
                point.ordinals[axisOrdinal] = i;
                RolapPosition position = (RolapPosition) axis.positions[i];
                for (int j = 0; j < position.members.length; j++) {
                    evaluator.setContext(position.members[j]);
                }
                executeStripe(axisOrdinal - 1, evaluator);
            }
        }
    }

    /**
     * Converts a cell ordinal to a set of cell coordinates. Converse of
     * {@link #getCellOrdinal}. For example, if this result is 10 x 10 x 10,
     * then cell ordinal 537 has coordinates (5, 3, 7).
     */
    public int[] getCellPos(int cellOrdinal) {
        int[] pos = new int[axes.length];
        for (int j = 0; j < axes.length; j++) {
            pos[j] = (cellOrdinal % modulos[j + 1]) / modulos[j];
        }
        return pos;
    }

    /**
     * Converts a set of cell coordinates to a cell ordinal. Converse of
     * {@link #getCellPos}.
     */
    int getCellOrdinal(int[] pos) {
        int ordinal = 0;
        for (int j = 0; j < axes.length; j++) {
            ordinal += pos[j] * modulos[j];
        }
        return ordinal;
    }

    RolapEvaluator getCellEvaluator(int[] pos) {
        final RolapEvaluator cellEvaluator = (RolapEvaluator) evaluator.push();
        for (int i = 0; i < pos.length; i++) {
            Position position = axes[i].positions[pos[i]];
            for (int j = 0; j < position.members.length; j++) {
                cellEvaluator.setContext(position.members[j]);
            }
        }
        return cellEvaluator;
    }

    Evaluator getEvaluator(int[] pos) {
        // Set up evaluator's context, so that context-dependent format
        // strings work properly.
        Evaluator cellEvaluator = evaluator.push();
        for (int i = -1; i < axes.length; i++) {
            Axis axis;
            int index;
            if (i < 0) {
                axis = slicerAxis;
                index = 0;
            } else {
                axis = axes[i];
                index = pos[i];
            }
            Position position = axis.positions[index];
            for (int j = 0; j < position.members.length; j++) {
                Member member = position.members[j];
                cellEvaluator.setContext(member);
            }
        }
        return cellEvaluator;
    }

    /**
     * Evaluator which checks dependencies of expressions.
     *
     * <p>For each expression evaluation, this valuator evaluates each
     * expression more times, and makes sure that the results of the expression
     * are independent of dimensions which the expression claims to be
     * independent of.
     *
     * <p>Since it evaluates each expression twice, it also exposes function
     * implementations which change the context of the evaluator.
     */
    private class DependencyTestingEvaluator extends RolapEvaluator {

        /**
         * Creates an evaluator.
         */
        DependencyTestingEvaluator(RolapResult result, int expDeps) {
            super(new DependencyTestingEvalutorRoot(result, expDeps));
        }

        /**
         * Creates a child evaluator.
         */
        private DependencyTestingEvaluator(
                RolapEvaluatorRoot root,
                DependencyTestingEvaluator evaluator,
                CellReader cellReader,
                Member[] cloneCurrentMembers) {
            super(root, evaluator, cellReader, cloneCurrentMembers);
        }

        /**
         * Returns the dimensions an expression depends on, caching the result.
         */
        private Dimension[] getIndependentDimensions(Exp exp) {
            Dimension[] indDims = (Dimension[]) expIndDims.get(exp);
            if (indDims == null) {
                List indDimList = new ArrayList();
                final Dimension[] dims = root.cube.getDimensions();
                for (int i = 0; i < dims.length; i++) {
                    Dimension dim = dims[i];
                    if (!exp.dependsOn(dim)) {
                        indDimList.add(dim);
                    }
                }
                indDims = (Dimension[])
                        indDimList.toArray(new Dimension[indDimList.size()]);
                expIndDims.put(exp, indDims);
            }
            return indDims;
        }

        public RolapEvaluator _push() {
            Member[] cloneCurrentMembers =
                    (Member[]) this.getCurrentMembers().clone();
            return new DependencyTestingEvaluator(
                    root,
                    this,
                    cellReader,
                    cloneCurrentMembers);
        }

        public Object visit(FunCall funCall) {
            final DependencyTestingEvalutorRoot dteRoot =
                    (DependencyTestingEvalutorRoot) root;
            if (dteRoot.faking) {
                ++dteRoot.fakeCallCount;
            } else {
                ++dteRoot.callCount;
            }
            // Evaluate the call for real.
            final Object result = super.visit(funCall);
            if (batchingReader.isDirty()) {
                return result;
            }

            // Change one of the allegedly independent dimensions and evaluate
            // again.
            //
            // Don't do it if the faking is disabled,
            // or if we're already faking another dimension,
            // or if we're filtering out nonempty cells (which makes us
            // dependent on everything),
            // or if the ratio of fake evals to real evals is too high (which
            // would make us too slow).
            if (dteRoot.disabled ||
                    dteRoot.faking ||
                    isNonEmpty() ||
                    (double) dteRoot.fakeCallCount >
                    (double) dteRoot.callCount * random.nextDouble() * 2 *
                    dteRoot.expDeps) {
                return result;
            }
            Dimension[] independentDimensions =
                    getIndependentDimensions(funCall);
            if (independentDimensions.length == 0) {
                return result;
            }
            dteRoot.faking = true;
            ++dteRoot.fakeCount;
            ++dteRoot.fakeCallCount;
            final int i = random.nextInt(independentDimensions.length);
            final Member saveMember = getContext(independentDimensions[i]);
            final Member otherMember =
                    chooseOtherMember(saveMember, query.getSchemaReader(false));
            setContext(otherMember);
            final Object otherResult = super.visit(funCall);
            if (!equals(otherResult, result)) {
                final Member[] members = getCurrentMembers();
                final StringBuffer buf = new StringBuffer();
                for (int j = 0; j < members.length; j++) {
                    if (j > 0) {
                        buf.append(", ");
                    }
                    buf.append(members[j].getUniqueName());
                }
                throw Util.newInternal(
                        "Expression '" + funCall.toMdx() +
                        "' claims to be independent of dimension " +
                        saveMember.getDimension() + " but is not; context is {" +
                        buf.toString() + "}; First result: " +
                        toString(result) + ", Second result: " +
                        toString(otherResult));
            }
            // Restore context.
            setContext(saveMember);
            dteRoot.faking = false;
            return result;
        }

        /**
         * Chooses another member of the same hierarchy.
         * The member will come from all levels with the same probability.
         * For example, given [Gender].[M], the result has a
         * @param save
         * @param schemaReader
         * @return
         */
        private Member chooseOtherMember(
                final Member save, SchemaReader schemaReader) {
            final Hierarchy hierarchy = save.getHierarchy();
            while (true) {
                // Choose a random level.
                final Level[] levels = hierarchy.getLevels();
                final int levelDepth = random.nextInt(levels.length) + 1;
                Member member = null;
                for (int i = 0; i < levelDepth; i++) {
                    Member[] members;
                    if (i == 0) {
                        members = schemaReader.getLevelMembers(levels[i]);
                    } else {
                        members = schemaReader.getMemberChildren(member);
                    }
                    if (members.length == 0) {
                        break;
                    }
                    member = members[random.nextInt(members.length)];
                }
                // If the member chosen happens to be the same as the original
                // member, try again.
                if (member != save) {
                    return member;
                }
            }
        }

        private boolean equals(Object o1, Object o2) {
            if (o1 == null) {
                return o2 == null;
            }
            if (o2 == null) {
                return false;
            }
            if (o1 instanceof Object[]) {
                if (o2 instanceof Object[]) {
                    Object[] a1 = (Object[]) o1;
                    Object[] a2 = (Object[]) o2;
                    if (a1.length == a2.length) {
                        for (int i = 0; i < a1.length; i++) {
                            if (!equals(a1[i], a2[i])) {
                                return false;
                            }
                        }
                        return true;
                    }
                }
                return false;
            }
            if (o1 instanceof List) {
                if (o2 instanceof List) {
                    return equals(
                            ((List) o1).toArray(),
                            ((List) o2).toArray());
                }
                return false;
            }
            return o1.equals(o2);
        }

        private String toString(Object o) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            toString(o, pw);
            return sw.toString();
        }

        private void toString(Object o, PrintWriter pw) {
            if (o instanceof Object[]) {
                Object[] a = (Object[]) o;
                pw.print("{");
                for (int i = 0; i < a.length; i++) {
                    Object o1 = a[i];
                    if (i > 0) {
                        pw.print(", ");
                    }
                    toString(o1, pw);
                }
                pw.print("}");
            } else if (o instanceof List) {
                List list = (List) o;
                toString(list.toArray(), pw);
            } else if (o instanceof Member) {
                Member member = (Member) o;
                pw.print(member.getUniqueName());
            } else {
                pw.print(o);
            }
        }
    }

    private static class DependencyTestingEvalutorRoot
            extends RolapEvaluator.RolapEvaluatorRoot {
        final int expDeps;
        int callCount;
        int fakeCallCount;
        int fakeCount;
        boolean faking;
        boolean disabled;

        DependencyTestingEvalutorRoot(RolapResult result, int expDeps) {
            super(result);
            this.expDeps = expDeps;
        }

    }
}

// End RolapResult.java
