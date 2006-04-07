/*
// $Id: //open/mondrian/src/main/mondrian/jolap/MondrianEdgeFilter.java#3 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// (C) Copyright 2002-2005 Kana Software, Inc. and others.
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
//
// jhyde, Dec 26, 2002
*/
package mondrian.jolap;

import javax.olap.OLAPException;
import javax.olap.query.enumerations.EdgeFilterType;

/**
 * Implementation of {@link EdgeFilter}, and also a factory for these.
 *
 * @author jhyde
 * @since Dec 26, 2002
 * @version $Id: //open/mondrian/src/main/mondrian/jolap/MondrianEdgeFilter.java#3 $
 **/
class MondrianEdgeFilter {
    /**
     * Factory method.
     */
    static MondrianEdgeFilter create(EdgeFilterType type) throws OLAPException {
        throw new UnsupportedOperationException("Unknown type " + type);
    }
}

// End MondrianEdgeFilter.java
