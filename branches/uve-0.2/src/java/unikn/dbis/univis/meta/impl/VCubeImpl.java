package unikn.dbis.univis.meta.impl;

import unikn.dbis.univis.meta.VCube;
import unikn.dbis.univis.meta.VHierarchy;
import unikn.dbis.univis.meta.VDataReference;

import java.awt.*;

/**
 * The <code>VCubeImpl</code> represents a cube of an OLAP
 * datastructure.
 * <p/>
 * User: raedler, weiler
 * Date: 07.04.2006
 * Time: 17:06:33
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @see VCube
 * @since UniVis Explorer 0.1
 */
public class VCubeImpl extends VDataReferenceImpl implements VCube, VDataReference {

    // ##############################################################################
    // Interface implementations.
    // ##############################################################################

    // The color of the cube.
    private Color color;

    // The hierarchy of the cube
    private VHierarchy hierarchy;

    /**
     * Returns the color of the cube.
     *
     * @return The color of the cube.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets the color of the cube.
     *
     * @param color The color of the cube.
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Returns the cube color as a RGB int value.
     *
     * @return The cube color as a RGB int value.
     */
    public int getColorRGB() {
        return color.getRGB();
    }

    /**
     * Sets the cube color of the RGB int value.
     *
     * @param rgb The RGB int value of the cube color.
     */
    public void setColorRGB(int rgb) {
        color = new Color(rgb);
    }

    /**
     * Returns the hierarchy of the cube. The hierarchy
     * contains references to the dimensions.
     *
     * @return The hierarchy of the cube.
     */
    public VHierarchy getHierarchy() {
        return hierarchy;
    }

    /**
     * Sets the hierarchy of the cube. The hierarchy
     * contains references to the dimensions.
     *
     * @param hierarchy The hierarchy of the cube.
     */
    public void setHierarchy(VHierarchy hierarchy) {
        this.hierarchy = hierarchy;
    }
}