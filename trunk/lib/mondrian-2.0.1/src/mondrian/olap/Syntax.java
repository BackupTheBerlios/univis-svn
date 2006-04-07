/*
// $Id: //open/mondrian/src/main/mondrian/olap/Syntax.java#6 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// (C) Copyright 2003-2005 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package mondrian.olap;

import java.io.PrintWriter;

/**
 * Enumerated values describing the syntax of an expression.
 *
 * @author jhyde
 * @since 21 July, 2003
 * @version $Id: //open/mondrian/src/main/mondrian/olap/Syntax.java#6 $
 */
public class Syntax extends EnumeratedValues.BasicValue {
    private Syntax(String name, int ordinal) {
        super(name, ordinal, null);
    }

    /** Expression invoked <code>FUNCTION()</code> or
     * <code>FUNCTION(args)</code>. **/
    public static final Syntax Function = new Syntax("Function", 0) {
        public void unparse(String fun, Exp[] args, PrintWriter pw) {
            if (fun.equals("_Value")) {
                // pseudo function evaluting a tuple value
                if (args[0] instanceof FunCall && ((FunCall)args[0]).isCallToTuple()) {
                    ((FunCall)args[0]).unparse(pw);
                    return;
                }
            }
            ExpBase.unparseList(pw, args, fun + "(", ", ", ")");
        }
    };

    /** Expression invoked as <code>object.PROPERTY</code>. **/
    public static final Syntax Property = new Syntax("Property", 1) {
        public void unparse(String fun, Exp[] args, PrintWriter pw) {
            Util.assertTrue(args.length >= 1);
            args[0].unparse(pw); // 'this'
            pw.print(".");
            pw.print(fun);
        }

        public String getSignature(String name, int returnType, int[] argTypes) {
            // e.g. "<Set>.Current"
            return getTypeDescription(argTypes[0]) + "." + name;
        }
    };

    /** Expression invoked invoked as <code>object.METHOD()</code> or
     * <code>object.METHOD(args)</code>. **/
    public static final Syntax Method = new Syntax("Method", 2) {
        public void unparse(String fun, Exp[] args, PrintWriter pw) {
            Util.assertTrue(args.length >= 1);
            args[0].unparse(pw); // 'this'
            pw.print(".");
            pw.print(fun);
            pw.print("(");
            for (int i = 1; i < args.length; i++) {
                if (i > 1) {
                    pw.print(", ");
                }
                args[i].unparse(pw);
            }
            pw.print(")");
        }

        public String getSignature(String name, int returnType, int[] argTypes) {
            // e.g. "<Member>.Lead(<Numeric Expression>)"
            return (returnType == Category.Unknown ? "" :
                    getTypeDescription(returnType) + " ") +
                getTypeDescription(argTypes[0]) + "." +
                name + "(" + getTypeDescriptionCommaList(argTypes, 1) +
                ")";
        }
    };
    /** Expression invoked as <code>arg OPERATOR arg</code> (like '+' or
     * 'AND') **/
    public static final Syntax Infix = new Syntax("Infix", 3) {
        public void unparse(String fun, Exp[] args, PrintWriter pw) {
            if (needParen(args)) {
                ExpBase.unparseList(pw, args, "(", " " + fun + " ", ")");
            } else {
                ExpBase.unparseList(pw, args, "", " " + fun + " ", "");
            }
        }

        public String getSignature(String name, int returnType, int[] argTypes) {
            // e.g. "<Numeric Expression> / <Numeric Expression>"
            return getTypeDescription(argTypes[0]) + " " + name + " " +
                getTypeDescription(argTypes[1]);
        }
    };

    /** Expression invoked as <code>OPERATOR arg</code> (like unary
     * '-'). **/
    public static final Syntax Prefix = new Syntax("Prefix", 4) {
        public void unparse(String fun, Exp[] args, PrintWriter pw) {
            if (needParen(args)) {
                ExpBase.unparseList(pw, args, "(" + fun + " ", null, ")");
            } else {
                ExpBase.unparseList(pw, args, "" + fun + " ", null, "");
            }
        }

        public String getSignature(String name, int returnType, int[] argTypes) {
            // e.g. "- <Numeric Expression>"
            return name + " " + getTypeDescription(argTypes[0]);
        }
    };

    /** Expression invoked as <code>{ARG,...}</code>, that is, the set
     * construction operator. **/
    public static final Syntax Braces = new Syntax("Braces", 5) {
        public String getSignature(String name, int returnType, int[] argTypes) {
            return "{" + getTypeDescriptionCommaList(argTypes, 0) + "}";
        }
    };

    /** Expression invoked as <code>(ARG)</code> or
     * <code>(ARG,...)</code>; that is, parentheses for grouping
     * expressions, and the tuple construction operator. **/
    public static final Syntax Parentheses = new Syntax("Parentheses", 6) {
        public String getSignature(String name, int returnType, int[] argTypes) {
            return "(" + getTypeDescriptionCommaList(argTypes, 0) + ")";
        }
    };

    /** Expression invoked as <code>CASE ... END</code>. **/
    public static final Syntax Case = new Syntax("Case", 7) {
        public void unparse(String fun, Exp[] args, PrintWriter pw) {
            pw.print("CASE");
            int j = 0;
            if (fun.equals("_CaseTest")) {
                pw.print(" ");
                args[j++].unparse(pw);
            } else {
                Util.assertTrue(fun.equals("_CaseMatch"));
            }
            int clauseCount = (args.length - j) / 2;
            for (int i = 0; i < clauseCount; i++) {
                pw.print(" WHEN ");
                args[j++].unparse(pw);
                pw.print(" THEN ");
                args[j++].unparse(pw);
            }
            if (j < args.length) {
                pw.print(" ELSE ");
                args[j++].unparse(pw);
            }
            Util.assertTrue(j == args.length);
            pw.print(" END");
        }

        public String getSignature(String name, int returnType, int[] argTypes) {
            String s = getTypeDescription(argTypes[0]);
            if (argTypes[0] == Category.Logical) {
                return "CASE WHEN " + s + " THEN <Expression> ... END";
            } else {
                return "CASE " + s + " WHEN " + s + " THEN <Expression> ... END";
            }
        }
    };
    /** Expression generated by the Mondrian system which cannot be
     * specified syntactically. **/
    public static final Syntax Internal = new Syntax("Internal", 8);
    /** Expression invoked <code>object.&PROPERTY</code>
     * (a variant of {@link #Property}). */
    public static final Syntax QuotedProperty = new Syntax("Quoted property", Property.ordinal | 0x100);
    /** Expression invoked <code>object.[&PROPERTY]</code>
     * (a variant of {@link #Property}). */
    public static final Syntax AmpersandQuotedProperty = new Syntax("Ampersand-quoted property", Property.ordinal | 0x200);

    private static final int mask = 0xFF;

    /**
     * Converts an ordinal value into a {@link Syntax}. Returns null if not
     * found.
     */
    public static Syntax get(int ordinal) {
        return (Syntax) enumeration.getValue(ordinal & mask);
    }

    /**
     * List of all valid {@link Syntax} objects.
     */
    public static final EnumeratedValues enumeration = new EnumeratedValues(
            new Syntax[] {
                Function, Property, Method, Infix,
                Prefix, Braces, Parentheses, Case,
                Internal});

    /**
     * Converts a call to a function of this syntax into source code.
     */
    public void unparse(String fun, Exp[] args, PrintWriter pw) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a description of the signature of a function call, for
     * example, "CoalesceEmpty(<Numeric Expression>, <String Expression>)".
     **/
    public String getSignature(String name, int returnType, int[] argTypes) {
        // e.g. "StripCalculatedMembers(<Set>)"
        return (returnType == Category.Unknown ? "" :
                getTypeDescription(returnType) + " ") +
            name + "(" + getTypeDescriptionCommaList(argTypes, 0) +
            ")";
    }

    private static boolean needParen(Exp[] args) {
        return !(args.length == 1 &&
                args[0] instanceof FunCall &&
                ((FunCall) args[0]).getSyntax() == Syntax.Parentheses);
    }

    private static String getTypeDescription(int type) {
        return "<" + Category.instance.getDescription(type & Category.Mask) + ">";
    }

    private static String getTypeDescriptionCommaList(int[] types, int start) {
        int initialSize = (types.length - start) * 16;
        StringBuffer sb = new StringBuffer(initialSize > 0 ? initialSize : 16);
        for (int i = start; i < types.length; i++) {
            if (i > start) {
                sb.append(", ");
            }
            sb.append("<")
                    .append(Category.instance.getDescription(types[i] & Category.Mask))
                    .append(">");
        }
        return sb.toString();
    }
}

// End Syntax.java
