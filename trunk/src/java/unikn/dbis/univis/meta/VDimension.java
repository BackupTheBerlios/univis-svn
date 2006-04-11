package unikn.dbis.univis.meta;

import java.util.Set;

/**
 * The <code>VDimension</code> is the side of a cube that contains
 * informations about the cube cell data.
 * <p/>
 * User: raedler, weiler
 * Date: 07.04.2006
 * Time: 15:28:03
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public interface VDimension extends VDataReference {

    /**
     * Whether the dimension is a summable dimension which
     * couldn't be used for dragging into the visualization
     * or not.
     *
     * @return Whether the dimension is a summable dimension
     *         or not.
     */
    public Boolean isSummable();

    /**
     * Sets whether the dimension is a summable dimension
     * which couldn't be used for dragging into the visualization
     * or not.
     *
     * @param summable Whether the dimension is a summable
     *                 dimension or not.
     */
    public void setSummable(Boolean summable);

    /**
     * Returns whether the dimension contains data for parentable
     * usage or not.
     *
     * @return Whether the dimension contains parentable data or
     *         not.
     */
    public Boolean isParentable();

    /**
     * Sets whether the dimension contains data for parentable
     * usage or not.
     *
     * @param parentable Whether the dimension contains parentable
     *                   data or not.
     */
    public void setParentable(Boolean parentable);

    /**
     * Returns the cubes that supports this dimension.
     *
     * @return The cubes that supports this dimension.
     */
    public Set<VCube> getSupportedCubes();

    /**
     * Sets the cubes that supports this dimension.
     *
     * @param supportedCubes The cubes that supports this
     *                       dimension.
     */
    public void setSupportedCubes(Set<VCube> supportedCubes);

    /**
     * Returns the identifiers of the selected values.
     *
     * @return The identifiers of the selected values.
     */
    public Set<Object> getSelections();

    /**
     * Sets the identifiers of the selected values.
     *
     * @param selections The identifiers of the selected
     *                   values.
     */
    public void setSelections(Set<Object> selections);

    /**
     * Returns whether the selection of the dimension was changed
     * or not.
     *
     * @return Whether the selection of the dimension was changed
     * or not.
     */
    public boolean isSelectionChanged();

    /**
     * Sets whether the selection of the dimension was changed
     * or not.
     *
     * @param selectionChanged Whether the selection of the dimension
     * was changed or not.
     */
    public void setSelectionChanged(boolean selectionChanged);
}