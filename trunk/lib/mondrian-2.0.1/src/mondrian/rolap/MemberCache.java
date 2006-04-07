/*
// $Id: //open/mondrian/src/main/mondrian/rolap/MemberCache.java#7 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// (C) Copyright 2001-2005 Kana Software, Inc. and others.
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
//
// jhyde, 22 December, 2001
*/

package mondrian.rolap;

import java.util.List;

import mondrian.rolap.sql.TupleConstraint;
import mondrian.rolap.sql.MemberChildrenConstraint;

/**
 * A <code>MemberCache</code> can retrieve members based upon their parent and
 * name.
 *
 * @author jhyde
 * @since 22 December, 2001
 * @version $Id: //open/mondrian/src/main/mondrian/rolap/MemberCache.java#7 $
 **/
interface MemberCache {
    /**
     * Creates a key with which to {@link #get} or {@link #put} the {@link
     * RolapMember} with a given parent and key.
     **/
    Object makeKey(RolapMember parent, Object key);

    /**
     * Retrieves the {@link RolapMember} with a given key (created by {@link
     * #makeKey}).
     **/
    RolapMember getMember(Object key);

    /**
     * Replaces the {@link RolapMember} with a given key (created by {@link
     * #makeKey}). Returns the previous member with that key, or null.
     **/
    Object putMember(Object key, RolapMember value);

    /**
     * returns the children of <code>member</code> if they are currently in the cache.
     * Otherwise returns null. The children may be garbage collected as soon as the
     * returned list may be garbage collected.
     * @param parent the parent member
     * @param constraint the condition that was used when the members were fetched. May
     * be null for all members (no constraint)
     */
    List getChildrenFromCache(RolapMember parent, MemberChildrenConstraint constraint);

    /**
     * returns the members of <code>level</code> if they are currently in the cache.
     * Otherwise returns null. The members may be garbage collected as soon as the
     * returned list may be garbage collected.
     * @param level the level whose members should be fetched
     * @param constraint the condition that was used when the members were fetched. May
     * be null for all members (no constraint)
     */
    List getLevelMembersFromCache(RolapLevel level, TupleConstraint constraint);

    /**
     * Registers that the children of <code>member</code> are
     * <code>children</code> (a list of {@link RolapMember}s).
     * @param member the parent member
     * @param constraint the condition that was used when the members were fetched. May
     * be null for all members (no constraint)
     */
    void putChildren(RolapMember member, MemberChildrenConstraint constraint, List children);
}


// End MemberCache.java
