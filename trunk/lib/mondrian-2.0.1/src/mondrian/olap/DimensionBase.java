/*
// $Id: //open/mondrian/src/main/mondrian/olap/DimensionBase.java#12 $
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

import mondrian.olap.type.Type;
import mondrian.resource.MondrianResource;

import java.util.List;
import java.util.ArrayList;

/**
 * Abstract implementation for a {@link Dimension}.
 *
 * @author jhyde
 * @since 6 August, 2001
 * @version $Id: //open/mondrian/src/main/mondrian/olap/DimensionBase.java#12 $
 **/
public abstract class DimensionBase
    extends OlapElementBase
    implements Dimension {

    protected final String name;
    protected final String uniqueName;
    protected final String description;
    protected final int globalOrdinal;
    protected Hierarchy[] hierarchies;
    protected DimensionType dimensionType;

    protected DimensionBase(
        String name,
        String uniqueName,
        String description,
        int globalOrdinal,
        DimensionType dimensionType)
    {
        this.name = name;
        this.uniqueName = Util.makeFqName(name);
        this.description = null;
        this.globalOrdinal = globalOrdinal;
        this.dimensionType = dimensionType;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Hierarchy[] getHierarchies() {
        return hierarchies;
    }

    public Hierarchy getHierarchy() {
        return hierarchies[0];
    }

    public Dimension getDimension() {
        return this;
    }

    public int getCategory() {
        return Category.Dimension;
    }

    public Type getTypeX() {
        return new mondrian.olap.type.DimensionType(this);
    }

    public DimensionType getDimensionType() {
        return dimensionType;
    }

    public String getQualifiedName() {
        return MondrianResource.instance().MdxDimensionName.str(getUniqueName());
    }

    public boolean isMeasures() {
        return getUniqueName().equals(MEASURES_UNIQUE_NAME);
    }

    public boolean usesDimension(Dimension dimension) {
        return dimension == this;
    }

    public OlapElement lookupChild(SchemaReader schemaReader, String s) {
        OlapElement oe = lookupHierarchy(s);

        // If the user is looking for [Marital Status].[Marital Status] we
        // should not return oe "Marital Status", because he is
        // looking for level - we can check that by checking of hierarchy and
        // dimension name is the same.
        if ((oe == null) || oe.getName().equalsIgnoreCase(getName()) ) {
            OlapElement oeLevel = getHierarchy().lookupChild(schemaReader, s);
            if (oeLevel != null)
                oe = oeLevel; // level match overrides hierarchy match
        }

        if (getLogger().isDebugEnabled()) {
            StringBuffer buf = new StringBuffer(64);
            buf.append("DimensionBase.lookupChild: ");
            buf.append("name=");
            buf.append(getName());
            buf.append(", childname=");
            buf.append(s);
            if (oe == null) {
                buf.append(" returning null");
            } else {
                buf.append(" returning elementname="+oe.getName());
            }
            getLogger().debug(buf.toString());
        }


        return oe;
    }

    private Hierarchy lookupHierarchy(String s) {
        for (int i = 0; i < hierarchies.length; i++) {
            if (hierarchies[i].getName().equalsIgnoreCase(s))
                return hierarchies[i];
        }
        return null;
    }

//      public Level lookupLevel(NameResolver st, String s)
//      {
//          Hierarchy[] mdxHierarchies = getHierarchies();
//          for (int i = 0; i < mdxHierarchies.length; i++) {
//              Level mdxLevel = mdxHierarchies[i].lookupLevel(st, s);
//              if (mdxLevel != null)
//                  return mdxLevel;
//          }
//          return null;
//      }

//      public Member lookupMember(NameResolver st, String s)
//      {
//          Hierarchy[] mdxHierarchies = getHierarchies();
//          for (int i = 0; i < mdxHierarchies.length; i++) {
//              Member mdxMember = mdxHierarchies[i].lookupMember(st, s);
//              if (mdxMember != null)
//                  return mdxMember;
//          }
//          return null;
//      }

    public Object[] getChildren() {return getHierarchies();}

    protected Object[] getAllowedChildren(CubeAccess cubeAccess) {
        List hierarchyList = new ArrayList();
        Hierarchy[] mdxHierarchies = getHierarchies();
        for (int i = 0; i < mdxHierarchies.length; i++) {
            if (cubeAccess.isHierarchyAllowed(mdxHierarchies[i])) {
                hierarchyList.add(mdxHierarchies[i]);
            }
        }
        return hierarchyList.toArray(new Hierarchy[hierarchyList.size()]);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
    public void childrenAccept(Visitor visitor) {
        Hierarchy[] hierarchies = getHierarchies();
        for (int i = 0; i < hierarchies.length; i++) {
            hierarchies[i].accept(visitor);
        }
    }
}


// End DimensionBase.java
