/*
// $Id: //open/mondrian/src/main/mondrian/olap/Evaluator.java#14 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// (C) Copyright 2001-2005 Kana Software, Inc. and others.
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
//
// jhyde, 27 July, 2001
*/

package mondrian.olap;

/**
 * An <code>Evaluator</code> holds the context necessary to evaluate an
 * expression.
 *
 * @author jhyde
 * @since 27 July, 2001
 * @version $Id: //open/mondrian/src/main/mondrian/olap/Evaluator.java#14 $
 **/
public interface Evaluator {

    /**
     * Returns the current cube.
     */
    Cube getCube();

    /**
     * Returns the current query.
     */
    Query getQuery();

    /**
     * Creates a new evaluator with each given member overriding the state of
     * the current validator for its dimension. Other dimensions retain the
     * same state as this validator.
     */
    Evaluator push(Member[] members);

    /**
     * Creates a new evaluator with the same state.
     * Equivalent to {@link #push(Member[]) push(new Member[0])}.
     */
    Evaluator push();

    /**
     * Creates a new evaluator with the same state except for one member.
     * Equivalent to
     * {@link #push(Member[]) push(new Member[] &#124;member&#125;)}.
     */
    Evaluator push(Member member);

    /**
     * Restores previous evaluator.
     */
    Evaluator pop();

    /**
     * Makes <code>member</code> the current member of its dimension. Returns
     * the previous context.
     *
     * @pre member != null
     * @post return != null
     */
    Member setContext(Member member);

    void setContext(Member[] members);

    Member getContext(Dimension dimension);

    Object evaluateCurrent();

    /**
     * Visits a literal and returns its value.
     */
    Object visit(Literal literal);

    /**
     * Visits a function call and returns its value.
     */
    Object visit(FunCall funCall);

    /**
     * Visits an identifier and returns its value.
     */
    Object visit(Id id);

    /**
     * Visits a catalog element, such as a dimension or member, and returns its
     * value.
     */
    Object visit(OlapElement mdxElement);

    /**
     * Visits a parameter and returns its value.
     */
    Object visit(Parameter parameter);

    /**
     * Formats a value as a string according to the current context's
     * format.
     */
    String format(Object o);

    /**
     * Returns number of ancestor evaluators. Used to check for infinite
     * loops.
     *
     * @post return getParent() == null ? 0 : getParent().getDepth() + 1
     */
    int getDepth();

    /**
     * Returns parent evaluator.
     */
    Evaluator getParent();

    /**
     * Retrieves the value of property <code>name</code>. If more than one
     * member in the current context defines that property, the one with the
     * highest solve order has precedence.
     *
     * <p>If the property is not defined, default value is returned.
     */
    Object getProperty(String name, Object defaultValue);

    /**
     * Returns a {@link SchemaReader} appropriate for the current
     * access-control context.
     */
    SchemaReader getSchemaReader();

    /**
     * Simple caching of the result of an <code>Exp</code>. The
     * key for the cache consists of all members of the current
     * context that <code>exp</code> depends on. Members of
     * independent dimensions are not part of the key.
     *
     * @see Exp#dependsOn
     */
    Object getCachedResult(ExpCacheDescriptor key);

    /**
     * Returns true for an axis that is NON EMPTY.
     *
     * <p>May be used by expression
     * evaluators to optimize their result. For example, a top-level crossjoin
     * may be optimized by removing all non-empty set elements before
     * performing the crossjoin. This is possible because of the identity
     *
     * <blockquote><code>nonempty(crossjoin(a, b)) ==
     * nonempty(crossjoin(nonempty(a), nonempty(b));</code></blockquote>
     */
    boolean isNonEmpty();

    /**
     * Sets whether an expression evaluation should filter out empty cells.
     * Allows expressions to modify non empty flag to evaluate their children.
     */
    void setNonEmpty(boolean nonEmpty);

    /**
     * Creates an exception which indicates that an error has occurred during
     * the runtime evaluation of a function. The caller should then throw that
     * exception.
     */
    RuntimeException newEvalException(Object context, String s);

    /**
     * Evaluates a named set.
     */
    Object evaluateNamedSet(String name, Exp exp);

    /**
     * Returns an array of the members which make up the current context.
     */
    Member[] getMembers();
}

// End Evaluator.java
