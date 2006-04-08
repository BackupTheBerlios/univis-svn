package unikn.dbis.univis.meta;

import javax.swing.tree.TreeNode;
import java.util.List;

/**
 * TODO: document me!!!
 * <p/>
 * <code>Cube</code>.
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
public interface Cube extends TreeNode {

    /**
     * Returns the name of the table that contains the cube
     * data.
     *
     * @return The name of the table that contains the cube
     *         data.
     */
    public String getTableName();

    /**
     * Returns the top dimensions of the cube. A dimension
     * equals a side of the cube. See OLAP Cube for more
     * details.
     *
     * @return The top dimensions of the cube.
     */
    public List<Dimension> getDimensions();
}