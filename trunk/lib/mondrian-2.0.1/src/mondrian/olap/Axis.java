/*
// $Id: //open/mondrian/src/main/mondrian/olap/Axis.java#3 $
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
 * A <code>Axis</code> is a component of a {@link Result}.
 *
 * @author jhyde
 * @since 6 August, 2001
 * @version $Id: //open/mondrian/src/main/mondrian/olap/Axis.java#3 $
 **/
public class Axis {
    public final Position[] positions;

    protected Axis(Position[] positions) {
        this.positions = positions;
    }
}

// End Axis.java
