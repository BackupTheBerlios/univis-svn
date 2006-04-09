package unikn.dbis.univis.meta.impl;

import unikn.dbis.univis.meta.Dimension;
import unikn.dbis.univis.meta.Cube;
import unikn.dbis.univis.meta.TreeFresh;

import java.util.*;

/**
 * The <code>DimensionImpl</code> represents a Dimension of an
 * OLAP cube.
 * <p/>
 * User: raedler, weiler
 * Date: 07.04.2006
 * Time: 17:08:08
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public class DimensionImpl extends TreeFresh<DimensionImpl> implements Dimension {

    // ##############################################################################
    // Interface implementations.
    // ##############################################################################

    // Whether the dimension is summable or not.
    private boolean summable;

    // The cubes that supports this dimension.
    private Set<Cube> supportedCubes;

    // The level of the dimension shown in the tree.
    private Integer level;

    // The category of the dimension shown in the tree.
    private String category;

    /**
     * Whether the dimension is a summable dimension which
     * couldn't be used for dragging into the visualization
     * or not.
     *
     * @return Whether the dimension is a summable dimension
     *         or not.
     */
    public boolean isSummable() {
        return summable;
    }

    /**
     * Sets whether the dimension is a summable dimension
     * which couldn't be used for dragging into the visualization
     * or not.
     *
     * @param summable Whether the dimension is a summable
     *                 dimension or not.
     */
    public void setSummable(boolean summable) {
        this.summable = summable;
    }

    /**
     * Returns the cubes that supports this dimension.
     *
     * @return The cubes that supports this dimension.
     */
    public Set<Cube> getSupportedCubes() {
        return supportedCubes;
    }

    /**
     * Sets the cubes that supports this dimension.
     *
     * @param supportedCubes The cubes that supports this
     *                       dimension.
     */
    public void setSupportedCubes(Set<Cube> supportedCubes) {
        this.supportedCubes = supportedCubes;
    }

    /**
     * Returns the level of the dimension shown in the tree.
     *
     * @return The level of the dimension shown in the tree.
     */
    public Integer getLevel() {
        return level;
    }

    /**
     * Sets the level of the dimension shown in the tree.
     *
     * @param level The level of the dimension shown in the
     *              tree.
     */
    public void setLevel(Integer level) {
        this.level = level;
    }

    /**
     * Returns the category of the dimension shown in the
     * tree.
     *
     * @return The category of the dimension shown in the
     *         tree.
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the category of the dimension shown in the
     * tree.
     *
     * @param category The category of the dimension shown in
     *                 the tree.
     */
    public void setCategory(String category) {
        this.category = category;
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
    public DimensionImpl cloneSpecific(DimensionImpl clone) throws CloneNotSupportedException {
        clone.setSummable(summable);
        clone.setSupportedCubes(supportedCubes);

        return clone;
    }
}