/*
// $Id: //open/mondrian/src/main/mondrian/jolap/MondrianAttributeFilter.java#5 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// (C) Copyright 2002-2005 Kana Software, Inc. and others.
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
//
// jhyde, Dec 24, 2002
*/
package mondrian.jolap;

import mondrian.olap.*;
import org.omg.java.cwm.objectmodel.core.Attribute;

import javax.olap.OLAPException;
import javax.olap.query.dimensionfilters.AttributeFilter;
import javax.olap.query.enumerations.OperatorType;
import javax.olap.query.enumerations.OperatorTypeEnum;

/**
 * Implementation of {@link AttributeFilter}.
 *
 * @author jhyde
 * @since Dec 24, 2002
 * @version $Id: //open/mondrian/src/main/mondrian/jolap/MondrianAttributeFilter.java#5 $
 **/
class MondrianAttributeFilter extends MondrianDimensionFilter
        implements AttributeFilter {
    private OperatorType op;
    private Object rhs;
    private Attribute attribute;

    public MondrianAttributeFilter(MondrianDimensionStepManager manager) {
        super(manager);
    }

    Exp convert(Exp exp) throws OLAPException {
        return combine(
                exp,
                new FunCall("Filter", new Exp[] {exp, getCondition()}));
    }

    Exp getCondition() throws OLAPException {
        MondrianJolapDimension dimension = (MondrianJolapDimension)
                getDimensionStepManager().getDimensionView().getDimension();
        return new FunCall(
                getFunName(getOp()),
                getFunSyntacticType(getOp()), new Exp[] {
                    new FunCall("CurrentMember",
                            Syntax.Property, new Exp[] {dimension.dimension}
                    ),
                    getExp(rhs)}
        );
    }

    private Exp getExp(Object rhs) {
        if (rhs instanceof String) {
            return Literal.createString((String) rhs);
        } else {
            throw Util.newInternal("Cannot _convert '" + rhs + "' (" +
                    rhs.getClass() + ") to an Exp");
        }
    }

    private String getFunName(OperatorType op) {
        if (op == OperatorTypeEnum.EQ) {
            return "=";
        } else if (op == OperatorTypeEnum.GE) {
            return ">=";
        } else if (op == OperatorTypeEnum.GT) {
            return ">";
        } else if (op == OperatorTypeEnum.LE) {
            return "<=";
        } else if (op == OperatorTypeEnum.LT) {
            return "<";
        } else if (op == OperatorTypeEnum.NE) {
            return "!=";
        } else {
            throw Util.newInternal("Unknown operator type " + op);
        }
    }

    private Syntax getFunSyntacticType(OperatorType op) {
        return Syntax.Infix;
    }

    public OperatorType getOp() throws OLAPException {
        return op;
    }

    public void setOp(OperatorType input) throws OLAPException {
        this.op = input;
    }

    public Object getRhs() throws OLAPException {
        return rhs;
    }

    public void setRhs(Object input) throws OLAPException {
        this.rhs = input;
    }

    public void setAttribute(Attribute input) throws OLAPException {
        this.attribute = input;
    }

    public Attribute getAttribute() throws OLAPException {
        return attribute;
    }
}

// End MondrianAttributeFilter.java
