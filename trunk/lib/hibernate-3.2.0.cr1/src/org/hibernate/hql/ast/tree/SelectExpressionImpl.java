// $Id: SelectExpressionImpl.java 7460 2005-07-12 20:27:29Z steveebersole $
package org.hibernate.hql.ast.tree;

import antlr.SemanticException;
import antlr.collections.AST;

/**
 * A select expression that was generated by a FROM element.
 *
 * @author josh Nov 6, 2004 8:27:38 AM
 */
public class SelectExpressionImpl extends FromReferenceNode implements SelectExpression {

	public void resolveIndex(AST parent) throws SemanticException {
		throw new UnsupportedOperationException();
	}

	public void setScalarColumnText(int i) throws SemanticException {
		String text = getFromElement().renderScalarIdentifierSelect( i );
		setText( text );
	}

	public void resolve(boolean generateJoin, boolean implicitJoin, String classAlias, AST parent) throws SemanticException {
		// Generated select expressions are already resolved, nothing to do.
		return;
	}
}
