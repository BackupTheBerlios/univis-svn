package unikn.dbis.univis.meta.impl;

import unikn.dbis.univis.meta.Cube;
import unikn.dbis.univis.meta.TreeFresh;

import java.awt.*;

/**
 * The <code>CubeImpl</code> represents a cube of an OLAP
 * datastructure.
 * <p/>
 * User: raedler, weiler
 * Date: 07.04.2006
 * Time: 17:06:33
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @see Cube
 * @since UniVis Explorer 0.1
 */
public class CubeImpl extends TreeFresh<CubeImpl> implements Cube {

    // ##############################################################################
    // Interface implementations.
    // ##############################################################################

    // The color of the cube.
    private Color color;

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

    // ##############################################################################
    // Abstract method implementations.
    // ##############################################################################

    /**
     * Clones the tree fresh item to handle unique occurence
     * in the tree to perform tree path actions.
     *
     * @param clone The clone with the settings of the super class.
     * @return The cloned tree fresh item.
     * @throws CloneNotSupportedException This exception occures if the clone
     *                                    couldn't be produced.
     */
    public CubeImpl cloneSpecific(CubeImpl clone) throws CloneNotSupportedException {

        if (color == null) {
            throw new CloneNotSupportedException("Couldn't clone CubeImpl object because the color of the cube isn't set.");
        }

        clone.setColor(color);

        return clone;
    }
}