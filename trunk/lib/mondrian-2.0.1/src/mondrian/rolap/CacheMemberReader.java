/*
// $Id: //open/mondrian/src/main/mondrian/rolap/CacheMemberReader.java#20 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// (C) Copyright 2001-2005 Kana Software, Inc. and others.
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
//
// jhyde, 21 December, 2001
*/

package mondrian.rolap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mondrian.olap.Util;
import mondrian.olap.Evaluator;
import mondrian.rolap.TupleReader.MemberBuilder;
import mondrian.rolap.sql.TupleConstraint;
import mondrian.rolap.sql.MemberChildrenConstraint;

/**
 * <code>CacheMemberReader</code> implements {@link MemberReader} by reading
 * from a pre-populated array of {@link mondrian.olap.Member}s.
 *
 * <p>Note: CacheMemberReader can not handle ragged hierarchies. (HR
 * Tests fail if {@link SmartMemberReader} is replaced with
 * CacheMemberReader).
 *
 * @author jhyde
 * @since 21 December, 2001
 * @version $Id: //open/mondrian/src/main/mondrian/rolap/CacheMemberReader.java#20 $
 */
class CacheMemberReader implements MemberReader, MemberCache {
    private final MemberSource source;
    private final RolapMember[] members;
    /** Maps a {@link MemberKey} to a {@link RolapMember}. **/
    private final Map mapKeyToMember;

    CacheMemberReader(MemberSource source) {
        this.source = source;
        if (false) {
            // we don't want the reader to write back to our cache
            Util.discard(source.setCache(this));
        }
        this.mapKeyToMember = new HashMap();
        this.members = source.getMembers();
        for (int i = 0; i < members.length; i++) {
            members[i].setOrdinal(i);
        }
    }

    // implement MemberReader
    public RolapHierarchy getHierarchy() {
        return source.getHierarchy();
    }

    public boolean setCache(MemberCache cache) {
        // we do not support cache writeback -- we must be masters of our
        // own cache
        return false;
    }

    // implement MemberReader
    public RolapMember[] getMembers() {
        return members;
    }

    // implement MemberCache
    public Object makeKey(RolapMember parent, Object key) {
        return new MemberKey(parent, key);
    }

    // implement MemberCache
    public RolapMember getMember(Object key) {
        return (RolapMember) mapKeyToMember.get(key);
    }

    // implement MemberCache
    public Object putMember(Object key, RolapMember value) {
        return mapKeyToMember.put(key, value);
    }

    // don't need to implement this MemberCache method because we're never
    // used in a context where it is needed
    public void putChildren(RolapMember member, MemberChildrenConstraint constraint, List children) {
        throw new UnsupportedOperationException();
    }

    // don't need to implement this MemberCache method because we're never
    // used in a context where it is needed
    public List getChildrenFromCache(RolapMember member, MemberChildrenConstraint constraint) {
        return null;
    }

    // don't need to implement this MemberCache method because we're never
    // used in a context where it is needed
    public List getLevelMembersFromCache(RolapLevel level, TupleConstraint constraint) {
        return null;
    }

    public RolapMember lookupMember(String[] uniqueNameParts,
                                    boolean failIfNotFound) {
        return RolapUtil.lookupMember(this, uniqueNameParts, failIfNotFound);
    }

    public List getRootMembers() {
        List list = new ArrayList();
        for (int i = 0; i < members.length; i++) {
            if (members[i].getParentUniqueName() == null) {
                list.add(members[i]);
            }
        }
        return list;
    }

    public List getMembersInLevel(RolapLevel level,
                                  int startOrdinal,
                                  int endOrdinal) {
        List list = new ArrayList();
        int levelDepth = level.getDepth();
        for (int i = 0; i < members.length; i++) {
            RolapMember member = members[i];
            if ((member.getLevel().getDepth() == levelDepth) &&
                (startOrdinal <= member.getOrdinal()) &&
                (member.getOrdinal() < endOrdinal)) {

                list.add(members[i]);
            }
        }
        return list;
    }

    public List getMembersInLevel(RolapLevel level, int startOrdinal, int endOrdinal, TupleConstraint constraint) {
        return getMembersInLevel(level, startOrdinal, endOrdinal);
    }

    public void getMemberChildren(RolapMember parentMember, List children) {
        for (int i = 0; i < members.length; i++) {
            RolapMember member = members[i];
            if (member.getParentMember() == parentMember) {
                children.add(member);
            }
        }
    }

    public void getMemberChildren(RolapMember member, List children, MemberChildrenConstraint constraint) {
        getMemberChildren(member, children);
    }

    public void getMemberChildren(List parentMembers, List children) {
        for (int i = 0; i < members.length; i++) {
            RolapMember member = members[i];
            if (parentMembers.contains(member.getParentMember())) {
                children.add(member);
            }
        }
    }

    public void getMemberChildren(List parentMembers, List children, MemberChildrenConstraint constraint) {
        getMemberChildren(parentMembers, children);
    }

    public RolapMember getLeadMember(RolapMember member, int n) {
        if (n >= 0) {
            for (int ordinal = member.getOrdinal(); ordinal < members.length;
                 ordinal++) {
                if ((members[ordinal].getLevel() == member.getLevel()) &&
                    (n-- == 0)) {

                    return members[ordinal];
                }
            }
            return (RolapMember) member.getHierarchy().getNullMember();

        } else {
            for (int ordinal = member.getOrdinal(); ordinal >= 0; ordinal--) {
                if ((members[ordinal].getLevel() == member.getLevel()) &&
                    (n++ == 0)) {
                    return members[ordinal];
                }
            }
            return (RolapMember) member.getHierarchy().getNullMember();
        }
    }

    public void getMemberRange(RolapLevel level,
                               RolapMember startMember,
                               RolapMember endMember,
                               List list) {
        Util.assertPrecondition(startMember != null, "startMember != null");
        Util.assertPrecondition(endMember != null, "endMember != null");
        Util.assertPrecondition(startMember.getLevel() == endMember.getLevel(),
                "startMember.getLevel() == endMember.getLevel()");

        for (int i = startMember.getOrdinal(); i <= endMember.getOrdinal(); i++) {
            if (members[i].getLevel() == endMember.getLevel()) {
                list.add(members[i]);
            }
        }
    }

    public int getMemberCount() {
        return members.length;
    }

    public int compare(RolapMember m1, RolapMember m2, boolean siblingsAreEqual) {
        if (m1 == m2) {
            return 0;
        }
        if (siblingsAreEqual &&
            (m1.getParentMember() == m2.getParentMember())) {
            return 0;
        }
        Util.assertTrue(members[m1.getOrdinal()] == m1);
        Util.assertTrue(members[m2.getOrdinal()] == m2);

        return (m1.getOrdinal() < m2.getOrdinal()) ? -1 : 1;
    }

    public MemberBuilder getMemberBuilder() {
        return null;
    }

}

// End CacheMemberReader.java
