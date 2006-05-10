package unikn.dbis.univis.visualization.graph;

import org.jgraph.graph.DefaultGraphCell;
import unikn.dbis.univis.visualization.Renderable;
import unikn.dbis.univis.visualization.AbstractRenderer;

/**
 * TODO: document me!!!
 * <p/>
 * VGraphCell.
 * <p/>
 * User: raedler, weiler
 * Date: 10.05.2006
 * Time: 11:23:21
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public class VGraphCell extends DefaultGraphCell {

    private String cellId;

    /**
     * Creates an empty cell.
     */
    public VGraphCell() {
        // empty
    }

    /**
     * Creates a graph cell and initializes it with the specified user object.
     *
     * @param userObject an Object provided by the user that constitutes
     *                   the cell's data
     */
    public VGraphCell(Object userObject) {
        super(userObject);
    }

    /**
     * Returns this node's user object.
     *
     * @return the Object stored at this node by the user
     * @see #setUserObject
     * @see #toString
     */
    public Object getUserObject() {
        Object o = super.getUserObject();

        if (o instanceof Renderable) {
            return ((Renderable) o).render();
        }

        return o;
    }

    public String getCellId() {
        return cellId;
    }

    public void setCellId(String cellId) {
        this.cellId = cellId;
    }

    /**
     * Returns the result of sending <code>toString()</code> to this node's
     * user object, or null if this node has no user object.
     *
     * @see #getUserObject
     */
    public String toString() {
        return cellId;
    }
}