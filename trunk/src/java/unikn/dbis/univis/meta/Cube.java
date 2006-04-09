package unikn.dbis.univis.meta;

import java.awt.*;

/**
 * The <code>Cube</code> contains information about the OLAP cube.
 * <p/>
 * User: raedler, weiler
 * Date: 07.04.2006
 * Time: 15:27:44
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public interface Cube {

    /**
     * Returns the color of the cube.
     *
     * @return The color of the cube.
     */
    public Color getColor();

    /**
     * Sets the color of the cube.
     *
     * @param color The color of the cube.
     */
    public void setColor(Color color);

    /**
     * TODO: document me!!!
     *
     * @return
     */
    public TreeFresh getHierarchy();

    /**
     * TODO: document me!!!
     *
     * @param hierarchy
     */
    public void setHierarchy(TreeFresh hierarchy);
}