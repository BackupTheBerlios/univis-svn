/*
//This software is subject to the terms of the Common Public License
//Agreement, available at the following URL:
//http://www.opensource.org/licenses/cpl.html.
//Copyright (C) 2004-2005 TONBELLER AG
//All Rights Reserved.
//You must accept the terms of that agreement to use this software.
*/
package mondrian.rolap;

import mondrian.rolap.sql.TupleConstraint;
import mondrian.rolap.sql.MemberChildrenConstraint;
import mondrian.rolap.sql.SqlQuery;

/**
 * does not restrict the result.
 */
public class DefaultTupleConstraint implements TupleConstraint {

    private static final TupleConstraint instance = new DefaultTupleConstraint();

    /** we have no state, so all instances are equal */
    private static final Object cacheKey = new Object();

    protected DefaultTupleConstraint() {
    }

    public void addConstraint(SqlQuery sqlQuery) {
    }

    public void addLevelConstraint(SqlQuery query, RolapLevel level) {
    }

    public MemberChildrenConstraint getMemberChildrenConstraint(RolapMember parent) {
        return DefaultMemberChildrenConstraint.instance();
    }

    public String toString() {
        return "DefaultTupelConstraint";
    }

    public Object getCacheKey() {
        return cacheKey;
    }

    public static TupleConstraint instance() {
        return instance;
    }

}
