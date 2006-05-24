package unikn.dbis.univis.sql;

import unikn.dbis.univis.meta.VDimension;
import unikn.dbis.univis.meta.Filterable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Stack;

/**
 * TODO: document me!!!
 * <p/>
 * VQuery.
 * <p/>
 * User: raedler, weiler
 * Date: 10.05.2006
 * Time: 10:05:27
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public class VQuery {

    // The logger to log info, error and other occuring messages
    // or exceptions.
    public static final transient Log LOG = LogFactory.getLog(VQuery.class);

    // The history of query statements.
    private Stack<VQueryStatement> history;

    private String cubeName = "sos_cube";
    private String cubeAttribute = "SUM(koepfe)";
    private String testSql;

    /**
     *
     */
    public VQuery() {
        history = new Stack<VQueryStatement>();
    }

    /**
     * @param dimension
     * @param blueprint
     * @return
     */
    public String createChartQuery(VDimension dimension, VDimension blueprint) {

        // The current query statement that will be used to create
        // cube browsing.
        VQueryStatement statement;

        if (history.isEmpty()) {
            // Create initial query statement if history is empty.
            statement = new VQueryStatement();
        }
        else {
            // Create a new statement for history and querying.
            statement = new VQueryStatement(history.peek());
        }

        // Push the new statement onto the stack.
        history.push(statement);

        // Return the chart query.
        return statement.createChartQuery(dimension, blueprint);
    }

    public void setCubeAttribute(String cubeAttribute) {
        this.cubeAttribute = cubeAttribute;
    }

    public void setCubeName(String cubeName) {
        this.cubeName = cubeName;
    }

    /**
     * Resets the query to initial state.
     */
    public void reset() {
        // Clears the query history.
        history.clear();
    }

    /**
     * Removes the last query statement from the
     * history.
     */
    public void historyBack() {
        // Remove the top query element of the stack.
        history.pop();
    }

    /**
     * Returns whether the query history is empty
     * or not.
     *
     * @return Whether the query history is empty
     *         or not.
     */
    public boolean isEmpty() {
        return history.isEmpty();
    }

    private final class VQueryStatement {

        private VQueryStatement parent;

        StringBuilder sql = new StringBuilder();

        private StringBuilder select;
        private StringBuilder from;
        private StringBuilder where;
        private StringBuilder group;
        private StringBuilder order;
        private StringBuilder concatenation;
        private String lastTableName;

        private VQueryStatement() {
            this.from = new StringBuilder(" FROM ");
            this.where = new StringBuilder(" WHERE ");
            this.group = new StringBuilder(" GROUP BY ");
            this.order = new StringBuilder(" ORDER BY ");
            this.select = new StringBuilder("SELECT " + cubeAttribute);
            this.concatenation = new StringBuilder();
        }

        private VQueryStatement(VQueryStatement parent) {
            this.parent = parent;

            this.select = new StringBuilder(parent.select);
            this.from = new StringBuilder(parent.from);
            this.where = new StringBuilder(parent.where);
            this.group = new StringBuilder(parent.group);
            this.order = new StringBuilder(parent.order);
            this.concatenation = new StringBuilder(parent.concatenation);
            this.lastTableName = parent.lastTableName;
        }

        /**
         * @param dimension
         * @param blueprint
         * @return
         */
        public String createChartQuery(VDimension dimension, VDimension blueprint) {

            String bluepName = blueprint.getTableName();
            String tableName = dimension.getTableName();

            if (history.size() <= 1) {

                if (!dimension.equals(blueprint)) {
                    from.append(cubeName).append(", ").append(tableName).append(", ").append(bluepName);
                    where.append(tableName).append(".id = ").append(bluepName).append(".").append(dimension.getJoinable()).append(" AND ").append(bluepName).append(".id = ").append(cubeName).append(".").append(blueprint.getJoinable());

                    String filter = getFilterWhereClause(blueprint);
                    if (filter.length() > 0) {
                        where.append(" ").append(filter);
                    }
                }
                else {
                    from.append(cubeName).append(", ").append(tableName);
                    where.append(tableName).append(".id = ").append(cubeName).append(".").append(dimension.getJoinable());

                    if (blueprint.getSelections() != null && blueprint.getSelections().size() > 0) {
                        where.append(" AND (");

                        int size = blueprint.getSelections().size();
                        for (Filterable filter : blueprint.getSelections()) {
                            where.append(cubeName).append(".").append(blueprint.getJoinable()).append(" = ").append(filter.getId());

                            if (size > 1) {
                                where.append(" OR ");
                                size--;
                            }
                        }
                        where.append(")");
                    }
                }
            }
            else {
                if (!dimension.equals(blueprint)) {
                    from.append(", ").append(bluepName).append(", ").append(tableName);
                    where.append(" AND ").append(tableName).append(".id = ").append(bluepName).append(".").append(dimension.getJoinable()).append(" AND ").append(bluepName).append(".id = ").append(cubeName).append(".").append(blueprint.getJoinable());

                    String filter = getFilterWhereClause(blueprint);
                    if (filter.length() > 0) {
                        where.append(" ").append(filter);
                    }
                }
                else {
                    from.append(", ").append(tableName);
                    where.append(" AND ").append(tableName).append(".id = ").append(cubeName).append(".").append(dimension.getJoinable());

                    if (blueprint.getSelections() != null && blueprint.getSelections().size() > 0) {
                        where.append(" AND (");

                        int size = blueprint.getSelections().size();
                        for (Filterable filter : blueprint.getSelections()) {
                            where.append(cubeName).append(".").append(blueprint.getJoinable()).append(" = ").append(filter.getId());

                            if (size > 1) {
                                where.append(" OR ");
                                size--;
                            }
                        }
                        where.append(")");
                    }
                }
                group.append(", ");
                order.append(", ");
            }

            group.append(tableName).append(".name");
            order.append(tableName).append(".name");

            if (concatenation.length() > 0) {
                concatenation.append(" || '_' || ");
            }

            if (lastTableName != null && !"".equals(lastTableName)) {
                concatenation.append(lastTableName).append(".name");
            }

            lastTableName = tableName;

            select.append(", ").append(tableName).append(".name");

            sql.append(select);

            if (concatenation.length() > 0) {
                sql.append(", ").append(concatenation);
            }

            sql.append(from).append(where).append(group).append(order);

            if (LOG.isDebugEnabled()) {
                LOG.debug(sql.toString());
            }
            testSql = createTestSql(dimension);
            return sql.toString();

        }

        public String toString() {
            return sql.toString();
        }

        public String createTestSql(VDimension dimension) {
            String sql = "SELECT DISTINCT " + lastTableName + ".name FROM " + lastTableName;

            if (dimension.getSelections() != null && dimension.getSelections().size() > 0) {
                sql += " WHERE (";

                int size = dimension.getSelections().size();
                for (Filterable filter : dimension.getSelections()) {
                    sql += lastTableName + ".id = " + filter.getId();

                    if (size > 1) {
                        sql += " OR ";
                        size--;
                    }
                }
                sql += ")";
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug(sql);
            }

            return sql;
        }
    }

    public String getTestSql() {
        return testSql;
    }

    public String getFilterWhereClause(VDimension dimension) {

        String tableName = dimension.getTableName();

        //StringBuffer from = new StringBuffer("FROM " + tableName + " AS " + tableName);
        StringBuffer where = new StringBuffer();

        VDimension oldDimension = dimension;

        boolean filtered = true;
        while (dimension.isParentable() && filtered) {

            dimension = (VDimension) dimension.getParent();

            boolean appended = false;

            for (Filterable filter : dimension.getSelections()) {

                Long selection = (Long) filter.getId();

                appended = true;
                filtered = false;

                if (where.length() > 0) {
                    where.append(" OR ");
                }
                else {
                    where.append("(");
                }

                if (oldDimension.isVisible()) {
                    where.append(tableName).append(".parent = ").append(selection);
                }
                else {
                    where.append(tableName).append(".").append(dimension.getJoinable()).append(" = ").append(selection);
                }
            }

            if (appended) {
                where.append(")");
            }
        }

        if (where.length() > 0) {
            where.insert(0, " AND ");
        }

        //return from.toString() + where.toString();
        return where.toString();
    }
}