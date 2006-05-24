package unikn.dbis.univis.meta.impl;

import unikn.dbis.univis.meta.VDimension;
import unikn.dbis.univis.meta.VCube;
import unikn.dbis.univis.meta.VDataReference;
import unikn.dbis.univis.meta.Filterable;

import java.util.*;

/**
 * The <code>VDimensionImpl</code> represents a VDimension of an
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
public class VDimensionImpl extends VDataReferenceImpl implements VDimension, VDataReference {

    public VDimensionImpl() {
        selections = new HashSet<Filterable>();
    }

    // ##############################################################################
    // Interface implementations.
    // ##############################################################################

    // Whether the dimension is summable or not.
    private Boolean summable;

    // Whether the dimension contains parentable data or not.
    private Boolean parentable;

    // Whether the dimension is visible or unvisible.
    private Boolean visible;

    // The identifiers of the selected values.
    private Set<Filterable> selections;

    // Whether the selection of the dimension was changed or not.
    private boolean selectionChanged;

    // The cubes that supports this dimension.
    private Set<VCube> supportedCubes;


    /**
     * Whether the dimension is a summable dimension which
     * could be used for dragging into the visualization
     * or not.
     *
     * @return Whether the dimension is a summable dimension
     *         or not.
     */
    public Boolean isSummable() {
        return summable;
    }

    /**
     * Sets whether the dimension is a summable dimension
     * which could be used for dragging into the visualization
     * or not.
     *
     * @param summable Whether the dimension is a summable
     *                 dimension or not.
     */
    public void setSummable(Boolean summable) {
        this.summable = summable;
    }

    /**
     * Returns whether the dimension contains data for parentable
     * usage or not.
     *
     * @return Whether the dimension contains parentable data or
     *         not.
     */
    public Boolean isParentable() {
        return parentable;
    }

    /**
     * Sets whether the dimension contains data for parentable
     * usage or not.
     *
     * @param parentable Whether the dimension contains parentable
     *                   data or not.
     */
    public void setParentable(Boolean parentable) {
        this.parentable = parentable;
    }

    /**
     * Returns whether the dimension is visible or unvisible.
     *
     * @return Whether the dimension is visible or unvisible.
     */
    public Boolean isVisible() {
        return visible;
    }

    /**
     * Sets whether the dimension is visible or unvisible.
     *
     * @param visible Whether the dimension is visible or unvisible.
     */
    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    /**
     * Returns the identifiers of the selected values.
     *
     * @return The identifiers of the selected values.
     */
    public Set<Filterable> getSelections() {
        return selections;
    }

    /**
     * Sets the identifiers of the selected values.
     *
     * @param selections The identifiers of the selected
     *                   values.
     */
    public void setSelections(Set<Filterable> selections) {
        this.selections = selections;
    }

    /**
     * Returns whether the selection of the dimension was changed
     * or not.
     *
     * @return Whether the selection of the dimension was changed
     *         or not.
     */
    public boolean isSelectionChanged() {
        return selectionChanged;
    }

    /**
     * Sets whether the selection of the dimension was changed
     * or not.
     *
     * @param selectionChanged Whether the selection of the dimension
     *                         was changed or not.
     */
    public void setSelectionChanged(boolean selectionChanged) {
        this.selectionChanged = selectionChanged;
    }

    /**
     * Returns the cubes that supports this dimension.
     *
     * @return The cubes that supports this dimension.
     */
    public Set<VCube> getSupportedCubes() {
        return supportedCubes;
    }

    /**
     * Sets the cubes that supports this dimension.
     *
     * @param supportedCubes The cubes that supports this
     *                       dimension.
     */
    public void setSupportedCubes(Set<VCube> supportedCubes) {
        this.supportedCubes = supportedCubes;
    }
}