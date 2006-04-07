/*
// $Id: //open/mondrian/src/main/mondrian/olap/type/SetType.java#1 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// (C) Copyright 2005-2005 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package mondrian.olap.type;

import mondrian.olap.Dimension;
import mondrian.olap.Hierarchy;
import mondrian.olap.Level;

/**
 * Set type.
 *
 * @author jhyde
 * @since Feb 17, 2005
 * @version $Id: //open/mondrian/src/main/mondrian/olap/type/SetType.java#1 $
 */
public class SetType implements Type {

    private final Type elementType;

    /**
     * Creates a type representing a set of elements of a given type.
     *
     * @param elementType The type of the elements in the set, or null if not
     *   known
     */
    public SetType(Type elementType) {
        this.elementType = elementType;
    }

    /**
     * Returns the type of the elements of this set.
     */
    public Type getElementType() {
        return elementType;
    }

    public boolean usesDimension(Dimension dimension) {
        return elementType != null &&
                elementType.usesDimension(dimension);
    }

    public Hierarchy getHierarchy() {
        return elementType == null ? null :
                elementType.getHierarchy();
    }

    public Level getLevel() {
        return elementType == null ? null :
                elementType.getLevel();
    }
}

// End SetType.java
