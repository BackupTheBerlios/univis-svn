/*
// $Id: //open/mondrian/src/main/mondrian/jolap/MondrianSegment.java#3 $
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

import javax.olap.OLAPException;
import javax.olap.query.querycoremodel.DimensionStepManager;
import javax.olap.query.querycoremodel.EdgeView;
import javax.olap.query.querycoremodel.Segment;
import java.util.Collection;

/**
 * Implementation of {@link Segment}.
 *
 * <p>This class is <em>not</em> related to {@link mondrian.rolap.agg.Segment}.
 *
 * @author jhyde
 * @since Dec 24, 2002
 * @version $Id: //open/mondrian/src/main/mondrian/jolap/MondrianSegment.java#3 $
 **/
class MondrianSegment extends RefObjectSupport implements Segment {
    RelationshipList dimensionStepManager = new RelationshipList(Meta.dimensionStepManager);

    static abstract class Meta {
        static Relationship dimensionStepManager = new Relationship(MondrianSegment.class, "dimensionStepManager", DimensionStepManager.class);
    }

    public MondrianSegment() {
    }

    public EdgeView getEdgeView() throws OLAPException {
        throw new UnsupportedOperationException();
    }

    public Collection getDimensionStepManager() throws OLAPException {
        return dimensionStepManager;
    }

    public String getName() throws OLAPException {
        throw new UnsupportedOperationException();
    }

    public void setName(String input) throws OLAPException {
        throw new UnsupportedOperationException();
    }

    public String getId() throws OLAPException {
        throw new UnsupportedOperationException();
    }

    public void setId(String input) throws OLAPException {
        throw new UnsupportedOperationException();
    }

    public void setEdgeView(EdgeView value) throws OLAPException {
        throw new UnsupportedOperationException();
    }
}

// End MondrianSegment.java
