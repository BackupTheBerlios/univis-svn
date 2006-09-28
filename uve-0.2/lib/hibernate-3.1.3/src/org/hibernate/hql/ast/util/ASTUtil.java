// $Id: ASTUtil.java 9528 2006-03-01 04:24:08Z steve.ebersole@jboss.com $
package org.hibernate.hql.ast.util;

import java.util.ArrayList;
import java.util.List;

import antlr.ASTFactory;
import antlr.collections.AST;
import antlr.collections.impl.ASTArray;

/**
 * Provides utility methods for AST traversal and manipulation.
 *
 * @author Joshua Davis (pgmjsd@sourceforge.net)
 */
public final class ASTUtil {
	/**
	 * Private empty constructor.
	 * (or else checkstyle says: 'warning: Utility classes should not have a public or default constructor.')
	 *
	 * @deprecated (tell clover to ignore this)
	 */
	private ASTUtil() {
	}

	/**
	 * Creates a single node AST.
	 *
	 * @param astFactory The factory.
	 * @param type       The node type.
	 * @param text       The node text.
	 * @return AST - A single node tree.
	 */
	public static AST create(ASTFactory astFactory, int type, String text) {
		AST node = astFactory.create( type, text );
		return node;
	}

	/**
	 * Creates a single node AST as a sibling.
	 *
	 * @param astFactory  The factory.
	 * @param type        The node type.
	 * @param text        The node text.
	 * @param prevSibling The previous sibling.
	 * @return AST - A single node tree.
	 */
	public static AST createSibling(ASTFactory astFactory, int type, String text, AST prevSibling) {
		AST node = astFactory.create( type, text );
		node.setNextSibling( prevSibling.getNextSibling() );
		prevSibling.setNextSibling( node );
		return node;
	}

	public static AST insertSibling(AST node, AST prevSibling) {
		node.setNextSibling( prevSibling.getNextSibling() );
		prevSibling.setNextSibling( node );
		return node;
	}

	/**
	 * Creates a 'binary operator' subtree, given the information about the
	 * parent and the two child nodex.
	 *
	 * @param factory    The AST factory.
	 * @param parentType The type of the parent node.
	 * @param parentText The text of the parent node.
	 * @param child1     The first child.
	 * @param child2     The second child.
	 * @return AST - A new sub-tree of the form "(parent child1 child2)"
	 */
	public static AST createBinarySubtree(ASTFactory factory, int parentType, String parentText, AST child1, AST child2) {
		ASTArray array = createAstArray( factory, 3, parentType, parentText, child1 );
		array.add( child2 );
		return factory.make( array );
	}

	/**
	 * Creates a single parent of the specified child (i.e. a 'unary operator'
	 * subtree).
	 *
	 * @param factory    The AST factory.
	 * @param parentType The type of the parent node.
	 * @param parentText The text of the parent node.
	 * @param child      The child.
	 * @return AST - A new sub-tree of the form "(parent child)"
	 */
	public static AST createParent(ASTFactory factory, int parentType, String parentText, AST child) {
		ASTArray array = createAstArray( factory, 2, parentType, parentText, child );
		return factory.make( array );
	}

	public static AST createTree(ASTFactory factory, AST[] nestedChildren) {
		AST[] array = new AST[2];
		int limit = nestedChildren.length - 1;
		for ( int i = limit; i >= 0; i-- ) {
			if ( i != limit ) {
				array[1] = nestedChildren[i + 1];
				array[0] = nestedChildren[i];
				factory.make( array );
			}
		}
		return array[0];
	}

	/**
	 * Finds the first node of the specified type in the chain of children.
	 *
	 * @param parent The parent
	 * @param type   The type to find.
	 * @return The first node of the specified type, or null if not found.
	 */
	public static AST findTypeInChildren(AST parent, int type) {
		AST n = parent.getFirstChild();
		while ( n != null && n.getType() != type )
			n = n.getNextSibling();
		return n;
	}

	/**
	 * Returns the last direct child of 'n'.
	 *
	 * @param n The parent
	 * @return The last direct child of 'n'.
	 */
	public static AST getLastChild(AST n) {
		return getLastSibling( n.getFirstChild() );
	}

	/**
	 * Returns the last sibling of 'a'.
	 *
	 * @param a The sibling.
	 * @return The last sibling of 'a'.
	 */
	private static AST getLastSibling(AST a) {
		AST last = null;
		while ( a != null ) {
			last = a;
			a = a.getNextSibling();
		}
		return last;
	}

	/**
	 * Returns the 'list' representation with some brackets around it for debugging.
	 *
	 * @param n The tree.
	 * @return The list representation of the tree.
	 */
	public static String getDebugString(AST n) {
		StringBuffer buf = new StringBuffer();
		buf.append( "[ " );
		buf.append( ( n == null ) ? "{null}" : n.toStringTree() );
		buf.append( " ]" );
		return buf.toString();
	}

	/**
	 * Find the previous sibling in the parent for the given child.
	 *
	 * @param parent the parent node
	 * @param child  the child to find the previous sibling of
	 * @return the previous sibling of the child
	 */
	public static AST findPreviousSibling(AST parent, AST child) {
		AST prev = null;
		AST n = parent.getFirstChild();
		while ( n != null ) {
			if ( n == child ) {
				return prev;
			}
			prev = n;
			n = n.getNextSibling();
		}
		throw new IllegalArgumentException( "Child not found in parent!" );
	}

	/**
	 * Makes the child node a sibling of the parent, reconnecting all siblings.
	 *
	 * @param parent the parent
	 * @param child  the child
	 */
	public static void makeSiblingOfParent(AST parent, AST child) {
		AST prev = findPreviousSibling( parent, child );
		if ( prev != null ) {
			prev.setNextSibling( child.getNextSibling() );
		}
		else { // child == parent.getFirstChild()
			parent.setFirstChild( child.getNextSibling() );
		}
		child.setNextSibling( parent.getNextSibling() );
		parent.setNextSibling( child );
	}

	public static String getPathText(AST n) {
		StringBuffer buf = new StringBuffer();
		getPathText( buf, n );
		return buf.toString();
	}

	private static void getPathText(StringBuffer buf, AST n) {
		AST firstChild = n.getFirstChild();
		// If the node has a first child, recurse into the first child.
		if ( firstChild != null ) {
			getPathText( buf, firstChild );
		}
		// Append the text of the current node.
		buf.append( n.getText() );
		// If there is a second child (RHS), recurse into that child.
		if ( firstChild != null && firstChild.getNextSibling() != null ) {
			getPathText( buf, firstChild.getNextSibling() );
		}
	}

	public static boolean hasExactlyOneChild(AST n) {
		return n != null && n.getFirstChild() != null && n.getFirstChild().getNextSibling() == null;
	}

	public static void appendSibling(AST n, AST s) {
		while ( n.getNextSibling() != null )
			n = n.getNextSibling();
		n.setNextSibling( s );
	}

	/**
	 * Inserts the child as the first child of the parent, all other children are shifted over to the 'right'.
	 *
	 * @param parent the parent
	 * @param child  the new first child
	 */
	public static void insertChild(AST parent, AST child) {
		if ( parent.getFirstChild() == null ) {
			parent.setFirstChild( child );
		}
		else {
			AST n = parent.getFirstChild();
			parent.setFirstChild( child );
			child.setNextSibling( n );
		}
	}

	/**
	 * Filters nodes out of a tree.
	 */
	public static interface FilterPredicate {
		/**
		 * Returns true if the node should be filtered out.
		 *
		 * @param n The node.
		 * @return true if the node should be filtered out, false to keep the node.
		 */
		boolean exclude(AST n);
	}

	/**
	 * A predicate that uses inclusion, rather than exclusion semantics.
	 */
	public abstract static class IncludePredicate implements FilterPredicate {
		public final boolean exclude(AST node) {
			return !include( node );
		}

		public abstract boolean include(AST node);
	}

	private static ASTArray createAstArray(ASTFactory factory, int size, int parentType, String parentText, AST child1) {
		ASTArray array = new ASTArray( size );
		array.add( factory.create( parentType, parentText ) );
		array.add( child1 );
		return array;
	}

	public static List collectChildren(AST root, FilterPredicate predicate) {
		return new CollectingNodeVisitor( predicate ).collect( root );
	}

	private static class CollectingNodeVisitor implements NodeTraverser.VisitationStrategy {
		private final FilterPredicate predicate;
		private final List collectedNodes = new ArrayList();

		public CollectingNodeVisitor(FilterPredicate predicate) {
			this.predicate = predicate;
		}

		public void visit(AST node) {
			if ( predicate == null || !predicate.exclude( node ) ) {
				collectedNodes.add( node );
			}
		}

		public List getCollectedNodes() {
			return collectedNodes;
		}

		public List collect(AST root) {
			NodeTraverser traverser = new NodeTraverser( this );
			traverser.traverseDepthFirst( root );
			return collectedNodes;
		}
	}
}
