package unikn.dbis.univis.meta;

import javax.swing.tree.TreeNode;
import java.util.List;
import java.util.Enumeration;
import java.util.Collections;

/**
 * The <code>TreeFresh</code> is the abstract super class that each
 * element of the involved tree cube structure have to extend of.
 * <p/>
 * User: raedler, weiler
 * Date: 08.04.2006
 * Time: 20:20:36
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @see TreeNode
 * @see Cloneable
 * @since UniVis Explorer 0.1
 */
public class TreeFresh<T extends TreeFresh> implements TreeNode, Cloneable {

    // The (unique) identifier of the tree fresh item.
    private Long id;

    // TODO: document me!!!
    private DataReference dataReference;

    // The children of the tree fresh item.
    private List<TreeFresh> children;

    /**
     * Returns the (unique) identifier of the tree fresh
     * item.
     *
     * @return The (unique) identifier of the tree fresh
     *         item.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the (unique) identifier of the tree fresh item.
     *
     * @param id he (unique) identifier of the tree fresh
     *           item.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * TODO: document me!!!
     *
     * @return
     */
    public DataReference getDataReference() {
        return dataReference;
    }

    /**
     * TODO: document me!!!
     *
     * @param dataReference
     */
    public void setDataReference(DataReference dataReference) {
        this.dataReference = dataReference;
    }

    /**
     * Returns the children of the tree fresh item.
     *
     * @return The children of the tree fresh item.
     */
    public List<TreeFresh> getChildren() {
        return children;
    }

    /**
     * Sets the children of the tree fresh item.
     *
     * @param children The children of the tree fresh
     *                 item.
     */
    public void setChildren(List<TreeFresh> children) {
        this.children = children;
    }

    // ##############################################################################
    // Abstract methods.
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
    //public abstract T cloneSpecific(T clone) throws CloneNotSupportedException;

    // ##############################################################################
    // Interface implementations.
    // ##############################################################################

    /**
     * Returns the child <code>TreeNode</code> at index
     * <code>childIndex</code>.
     */
    public TreeNode getChildAt(int childIndex) {
        TreeNode treeNode = children.get(childIndex);

        if (treeNode instanceof TreeFresh) {
            try {
                return ((TreeFresh) treeNode).clone();
            }
            catch (CloneNotSupportedException cnse) {
                cnse.printStackTrace();
            }
        }

        return treeNode;
    }

    /**
     * Returns the number of children <code>TreeNode</code>s the receiver
     * contains.
     */
    public int getChildCount() {
        return children.size();
    }

    /**
     * Returns the parent <code>TreeNode</code> of the receiver.
     */
    public TreeNode getParent() {
        return null;
    }

    /**
     * Returns the index of <code>node</code> in the receivers children.
     * If the receiver does not contain <code>node</code>, -1 will be
     * returned.
     */
    public int getIndex(TreeNode node) {
        //noinspection SuspiciousMethodCalls
        return children.indexOf(node);
    }

    /**
     * Returns true if the receiver allows children.
     */
    public boolean getAllowsChildren() {
        return children.size() > 0;
    }

    /**
     * Returns true if the receiver is a leaf.
     */
    public boolean isLeaf() {
        return children.size() <= 0;
    }

    /**
     * Returns the children of the receiver as an <code>Enumeration</code>.
     */
    public Enumeration children() {
        return Collections.enumeration(children);
    }

    // ##############################################################################
    // Override methods of super classes.
    // ##############################################################################

    /**
     * Clones the tree fresh item to handle unique occurence
     * in the tree to perform tree path actions.
     *
     * @return The cloned tree fresh item.
     * @throws CloneNotSupportedException
     */
    @Override
    @SuppressWarnings({"CloneDoesntCallSuperClone"})
    public TreeFresh clone() throws CloneNotSupportedException {

        TreeFresh treeFresh = null;
        try {
            treeFresh = getClass().newInstance();
        }
        catch (InstantiationException ie) {
            ie.printStackTrace();
        }
        catch (IllegalAccessException iae) {
            iae.printStackTrace();
        }

        if (treeFresh == null) {
            throw new CloneNotSupportedException("Couldn't instantiate an object of the class [\"" + getClass().getName() + "\"].");
        }

        treeFresh.setId(id);
        treeFresh.setChildren(children);
        treeFresh.setDataReference(dataReference);

        //noinspection unchecked
        return treeFresh;//cloneSpecific((T) treeFresh);
    }

    /**
     * Returns a string representation of the object. In general, the
     * <code>toString</code> method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p/>
     * The <code>toString</code> method for class <code>Object</code>
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `<code>@</code>', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {

        if (getDataReference() != null) {
            return getDataReference().getTableName();
        }

        return super.toString();
    }
}