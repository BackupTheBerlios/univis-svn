package unikn.dbis.univis.meta;

import java.util.Set;

/**
 * The <code>Dimension</code> is the side of a cube that contains
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
public interface Dimension {

    /**
     * Whether the dimension is a summable dimension which
     * couldn't be used for dragging into the visualization
     * or not.
     *
     * @return Whether the dimension is a summable dimension
     *         or not.
     */
    public boolean isSummable();

    /**
     * Sets whether the dimension is a summable dimension
     * which couldn't be used for dragging into the visualization
     * or not.
     *
     * @param summable Whether the dimension is a summable
     *                 dimension or not.
     */
    public void setSummable(boolean summable);

    /**
     * Returns the cubes that supports this dimension.
     *
     * @return The cubes that supports this dimension.
     */
    public Set<Cube> getSupportedCubes();

    /**
     * Sets the cubes that supports this dimension.
     *
     * @param supportedCubes The cubes that supports this
     *                       dimension.
     */
    public void setSupportedCubes(Set<Cube> supportedCubes);

    /**
     * Returns the level of the dimension shown in the tree.
     *
     * @return The level of the dimension shown in the tree.
     */
    public Integer getLevel();

    /**
     * Sets the level of the dimension shown in the tree.
     *
     * @param level The level of the dimension shown in the
     *              tree.
     */
    public void setLevel(Integer level);

    /**
     * Returns the category of the dimension shown in the
     * tree.
     *
     * @return The category of the dimension shown in the
     *         tree.
     */
    public String getCategory();

    /**
     * Sets the category of the dimension shown in the
     * tree.
     *
     * @param category The category of the dimension shown in
     *                 the tree.
     */
    public void setCategory(String category);
}