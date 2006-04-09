package unikn.dbis.univis.meta.impl;

import unikn.dbis.univis.meta.Dimension;
import unikn.dbis.univis.meta.Cube;
import unikn.dbis.univis.meta.DataReference;

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
public class DimensionImpl extends DataReferenceImpl implements Dimension, DataReference {

    // ##############################################################################
    // Interface implementations.
    // ##############################################################################

    // The i18nKey of the label that should be shown in the
    // tree.
    private String i18nKey;

    // Whether the dimension is summable or not.
    private boolean summable;

    // The cubes that supports this dimension.
    private Set<Cube> supportedCubes;
    /**
     * Returns the i18nKey of the label that should be shown in the
     * tree.
     *
     * @return The i18nKey of the label that should be shown in the
     *         tree.
     */
    public String getI18nKey() {
        return i18nKey;
    }

    /**
     * Sets the i18nKey of the label that should be shown in the
     * // tree.
     *
     * @param i18nKey The i18nKey of the label that should be shown in
     *                the tree.
     */
    public void setI18nKey(String i18nKey) {
        this.i18nKey = i18nKey;
    }

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
}