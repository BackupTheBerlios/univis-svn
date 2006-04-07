/*
// $Id: //open/mondrian/src/main/mondrian/olap/fun/ParenthesesFunDef.java#6 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// (C) Copyright 2002-2005 Kana Software, Inc. and others.
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
//
// jhyde, 3 March, 2002
*/
package mondrian.olap.fun;
import mondrian.olap.*;
import mondrian.olap.type.Type;

import java.io.PrintWriter;

/**
 * <code>ParenthesesFunDef</code> implements the parentheses operator as if it
 * were a function.
 *
 * @author jhyde
 * @since 3 March, 2002
 * @version $Id: //open/mondrian/src/main/mondrian/olap/fun/ParenthesesFunDef.java#6 $
 **/
class ParenthesesFunDef extends FunDefBase {
    private final int argType;
    ParenthesesFunDef(int argType) {
        super(
            "()",
            "(<Expression>)",
            "Parenthesis enclose an expression and indicate precedence.",
            Syntax.Parentheses,
            argType,
            new int[] {argType});
        this.argType = argType;
    }
    public void unparse(Exp[] args, PrintWriter pw) {
        if (args.length != 1) {
            ExpBase.unparseList(pw, args, "(", ",", ")");
        } else {
            // Don't use parentheses unless necessary. We add parentheses around
            // expressions because we're not sure of operator precedence, so if
            // we're not careful, the parentheses tend to multiply ad infinitum.
            args[0].unparse(pw);
        }
    }

    public Type getResultType(Validator validator, Exp[] args) {
        Util.assertTrue(args.length == 1);
        return args[0].getTypeX();
    }

    public Object evaluate(Evaluator evaluator, Exp[] args) {
        return args[0].evaluate(evaluator);
    }
}

// End ParenthesesFunDef.java
