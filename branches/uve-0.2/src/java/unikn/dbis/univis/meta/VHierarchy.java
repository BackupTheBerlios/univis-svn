package unikn.dbis.univis.meta;

import javax.swing.tree.TreeNode;
import java.util.List;

/**
 * The <code>VHierarchy</code> represents the hierarchical structure
 * of the OLAP cube.
 * <p/>
 * User: raedler, weiler
 * Date: 09.04.2006
 * Time: 22:27:00
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @see TreeNode
 * @since UniVis Explorer 0.1
 */
public interface VHierarchy /*extends TreeNode*/ {

    /**
     * Returns the information of the hierarchy data using
     * this referenced data object.
     *
     * @return The information about the hierarchy data.
     */
    public VDataReference getDataReference();

    /**
     * Sets the information of the hierarchy data using
     * this referenced data object.
     *
     * @param dataReference The information about the hierarchy
     *                      data.
     */
    public void setDataReference(VDataReference dataReference);

    /**
     * Returns the parental element of this hierarchy element.
     *
     * @return The parental hierarchy element.
     */
    public VHierarchy getParent();

    /**
     * Sets the parental element of this hierarchy element.
     *
     * @param parent The parental hierarchy element.
     */
    public void setParent(VHierarchy parent);

    /**
     * Returns the children that belongs to this hierarchy
     * element.
     *
     * @return The children of this hierarchy element.
     */
    public List<? extends VHierarchy> getChildren();

    /**
     * Sets the children that belongs to this hierarchy
     * element.
     *
     * @param children The children of this hierarchy element.
     */
    public void setChildren(List<? extends VHierarchy> children);

    /**
     * Returns a clone of this hierarchy element.
     *
     * @return The clone of this hierarchy element.
     * @throws CloneNotSupportedException This exception could
     *                                    occure while cloning
     *                                    this element.
     */
    public VHierarchy cloneHierarchy() throws CloneNotSupportedException;
}