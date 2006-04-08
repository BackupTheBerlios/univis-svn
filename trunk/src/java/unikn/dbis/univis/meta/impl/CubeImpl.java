package unikn.dbis.univis.meta.impl;

import unikn.dbis.univis.meta.Cube;
import unikn.dbis.univis.meta.Dimension;

import javax.persistence.*;
import javax.swing.tree.TreeNode;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Collections;

/**
 * TODO: document me!!!
 * <p/>
 * <code>CubeImpl</code>.
 * <p/>
 * User: raedler, weiler
 * Date: 07.04.2006
 * Time: 17:06:33
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @see Cube
 * @since UniVis Explorer 0.1
 */
@Entity
@Table(name = "UNIVIS_CUBE")
public class CubeImpl implements Cube {

    @Id
    @GeneratedValue
    private Long id;

    private String tableName;

    @OneToMany(targetEntity = DimensionImpl.class, cascade = CascadeType.ALL)
    private List<Dimension> dimensions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the name of the table that contains the cube
     * data.
     *
     * @return The name of the table that contains the cube
     *         data.
     * @see unikn.dbis.univis.meta.Cube#getTableName()
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Sets the name of the table that contains the cube
     * data.
     *
     * @param tableName The name of the table that contains
     *                  the cube data.
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * Returns the top dimensions of the cube. A dimension equals
     * a side of the cube. See OLAP Cube for more details.<br/>
     * The <code>CubeImpl</code> is annotated by ejb annotations to
     * perform CRUD operations on the cube.
     *
     * @return The top dimensions of the cube.
     * @see unikn.dbis.univis.meta.Cube#getDimensions()
     */
    public List<Dimension> getDimensions() {
        return null;
    }

    /**
     * Sets the top dimensions of the cube.
     *
     * @param dimensions The top dimensions of the cube.
     * @see unikn.dbis.univis.meta.impl.CubeImpl#getDimensions()
     */
    public void setDimensions(List<Dimension> dimensions) {
        this.dimensions = dimensions;
    }

    /**
     * Adds a dimension at the last position of the cube.
     *
     * @param dimension One of the top dimensions of the cube.
     * @see unikn.dbis.univis.meta.impl.CubeImpl#getDimensions()
     */
    public void addDimension(Dimension dimension) {
        if (dimensions == null) {
            dimensions = new ArrayList<Dimension>();
        }
        dimensions.add(dimension);
    }

    // ##############################################################################
    // TEST
    // ##############################################################################

    /**
     * Returns the child <code>TreeNode</code> at index
     * <code>childIndex</code>.
     */
    public TreeNode getChildAt(int childIndex) {
        return dimensions.get(childIndex);
    }

    /**
     * Returns the number of children <code>TreeNode</code>s the receiver
     * contains.
     */
    public int getChildCount() {
        return dimensions.size();
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
        return dimensions.indexOf(node);
    }

    /**
     * Returns true if the receiver allows children.
     */
    public boolean getAllowsChildren() {
        return true;
    }

    /**
     * Returns true if the receiver is a leaf.
     */
    public boolean isLeaf() {
        return dimensions.size() <= 0;
    }

    /**
     * Returns the children of the receiver as an <code>Enumeration</code>.
     */
    public Enumeration children() {
        return Collections.enumeration(dimensions);
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
        return tableName;
    }
}