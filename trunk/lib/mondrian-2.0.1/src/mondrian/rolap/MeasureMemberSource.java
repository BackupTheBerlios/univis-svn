/*
// $Id: //open/mondrian/src/main/mondrian/rolap/MeasureMemberSource.java#6 $
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

/**
 * A <code>MeasureMemberSource</code> implements the {@link MemberReader}
 * interface for the special Measures dimension.
 *
 * <p>Usually when a member is added to the context, the resulting SQL
 * statement has extra filters in its WHERE clause, but for members from this
 * source, but this implementation columns are added to the SELECT list.
 *
 * @author jhyde
 * @since 21 December, 2001
 * @version $Id: //open/mondrian/src/main/mondrian/rolap/MeasureMemberSource.java#6 $
 **/
class MeasureMemberSource extends ArrayMemberSource {
    MeasureMemberSource(RolapHierarchy hierarchy, RolapMember[] members) {
        super(hierarchy, members);
    }
}

// End MeasureMemberSource.java
