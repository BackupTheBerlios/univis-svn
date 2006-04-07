/*
// $Id: //open/mondrian/src/main/mondrian/rolap/DelegatingMemberReader.java#8 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// (C) Copyright 2003-2005 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
//
// jhyde, Feb 26, 2003
*/
package mondrian.rolap;

import java.util.List;

import mondrian.rolap.TupleReader.MemberBuilder;
import mondrian.rolap.sql.TupleConstraint;
import mondrian.rolap.sql.MemberChildrenConstraint;
import mondrian.olap.Evaluator;

/**
 * A <code>DelegatingMemberReader</code> is a {@link MemberReader} which
 * redirects all method calls to an underlying {@link MemberReader}.
 *
 * @author jhyde
 * @since Feb 26, 2003
 * @version $Id: //open/mondrian/src/main/mondrian/rolap/DelegatingMemberReader.java#8 $
 **/
class DelegatingMemberReader implements MemberReader {
    protected final MemberReader memberReader;

    DelegatingMemberReader(MemberReader memberReader) {
        this.memberReader = memberReader;
    }

    public RolapMember getLeadMember(RolapMember member, int n) {
        return memberReader.getLeadMember(member, n);
    }

    public List getMembersInLevel(RolapLevel level,
                                  int startOrdinal,
                                  int endOrdinal) {
        return memberReader.getMembersInLevel(level, startOrdinal, endOrdinal);
    }

    public void getMemberRange(RolapLevel level,
                               RolapMember startMember,
                               RolapMember endMember,
                               List list) {
        memberReader.getMemberRange(level, startMember, endMember, list);
    }

    public int compare(RolapMember m1, RolapMember m2, boolean siblingsAreEqual) {
        return memberReader.compare(m1, m2, siblingsAreEqual);
    }

    public RolapHierarchy getHierarchy() {
        return memberReader.getHierarchy();
    }

    public boolean setCache(MemberCache cache) {
        return memberReader.setCache(cache);
    }

    public RolapMember[] getMembers() {
        return memberReader.getMembers();
    }

    public List getRootMembers() {
        return memberReader.getRootMembers();
    }

    public void getMemberChildren(RolapMember parentMember, List children) {
        memberReader.getMemberChildren(parentMember, children);
    }

    public void getMemberChildren(List parentMembers, List children) {
        memberReader.getMemberChildren(parentMembers, children);
    }

    public int getMemberCount() {
        return memberReader.getMemberCount();
    }

    public RolapMember lookupMember(String[] uniqueNameParts,
                                    boolean failIfNotFound) {
        return memberReader.lookupMember(uniqueNameParts, failIfNotFound);
    }

    public void getMemberChildren(RolapMember member, List children, MemberChildrenConstraint constraint) {
        memberReader.getMemberChildren(member, children, constraint);
    }

    public void getMemberChildren(List parentMembers, List children, MemberChildrenConstraint constraint) {
        memberReader.getMemberChildren(parentMembers, children, constraint);
    }

    public List getMembersInLevel(RolapLevel level, int startOrdinal, int endOrdinal, TupleConstraint constraint) {
        return memberReader.getMembersInLevel(level, startOrdinal, endOrdinal, constraint);
    }

    public MemberBuilder getMemberBuilder() {
        return memberReader.getMemberBuilder();
    }
}

// End DelegatingMemberReader.java
