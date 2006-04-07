/*
// $Id: //open/mondrian/src/main/mondrian/olap/Position.java#4 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// (C) Copyright 2001-2005 Kana Software, Inc. and others.
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
//
// jhyde, 6 August, 2001
*/

package mondrian.olap;

/**
 * A <code>Position</code> is an item on an {@link Axis}.  It contains
 * one or more {@link Member}s.
 *
 * @author jhyde
 * @since 6 August, 2001
 * @version $Id: //open/mondrian/src/main/mondrian/olap/Position.java#4 $
 **/
public class Position {

    /**
     * public-private: This must be public because it is still accessed in olap.ResultBase
     */
    public final Member[] members;

    protected Position(Member[] members) {
        this.members = members;
    }

    public Member[] getMembers() {
        return members;
    }

    // override Object
    public boolean equals(Object o) {
        if (o instanceof Position) {
            Position other = (Position) o;
            if (other.members.length == this.members.length) {
                for (int i = 0; i < this.members.length; i++) {
                    if (this.members[i] != other.members[i]) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
    // override Object
    public int hashCode() {
        int h = 0;
        for (int i = 0; i < members.length; i++) {
            h = (h << 4) ^ members[i].hashCode();
        }
        return h;
    }
};


// End Position.java
