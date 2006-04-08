package unikn.dbis.univis.meta.impl;

import unikn.dbis.univis.meta.Dimension;

import javax.persistence.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.*;

import org.hibernate.annotations.GenericGenerator;
import org.apache.commons.collections.CollectionUtils;

/**
 * TODO: document me!!!
 * <p/>
 * <code>DimensionImpl</code>.
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
@Entity
@Table(name = "UNIVIS_DIMENSION")
public class DimensionImpl implements Dimension {

    @Id
    @GeneratedValue
    private Long id;

    private boolean isAbstract;

    private String tableName;

    @ManyToOne(targetEntity = DimensionImpl.class, cascade = CascadeType.ALL)
    private Dimension superDimension;

    @OneToMany(targetEntity = DimensionImpl.class, cascade = CascadeType.ALL)
    private List<Dimension> subDimensions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Dimension getSuperDimension() {
        return superDimension;
    }

    public void setSuperDimension(Dimension superDimension) {
        this.superDimension = superDimension;
    }

    public List<Dimension> getSubDimensions() {
        return subDimensions;
    }

    public void setSubDimensions(List<Dimension> subDimensions) {
        this.subDimensions = subDimensions;
    }

    public void addSubDimension(Dimension subDimension) {
        if (subDimensions == null) {
            subDimensions = new ArrayList<Dimension>();
        }
        subDimensions.add(subDimension);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final DimensionImpl dimension = (DimensionImpl) o;

        if (isAbstract != dimension.isAbstract) return false;
        if (id != null ? !id.equals(dimension.id) : dimension.id != null) return false;
        if (subDimensions != null ? !subDimensions.equals(dimension.subDimensions) : dimension.subDimensions != null)
            return false;
        if (superDimension != null ? !superDimension.equals(dimension.superDimension) : dimension.superDimension != null)
            return false;
        if (tableName != null ? !tableName.equals(dimension.tableName) : dimension.tableName != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (id != null ? id.hashCode() : 0);
        result = 29 * result + (isAbstract ? 1 : 0);
        result = 29 * result + (tableName != null ? tableName.hashCode() : 0);
        result = 29 * result + (superDimension != null ? superDimension.hashCode() : 0);
        result = 29 * result + (subDimensions != null ? subDimensions.hashCode() : 0);
        return result;
    }

    // ##############################################################################
    // TEST
    // ##############################################################################

    /**
     * Returns the child <code>TreeNode</code> at index
     * <code>childIndex</code>.
     */
    public TreeNode getChildAt(int childIndex) {
        return subDimensions.get(childIndex);
    }

    /**
     * Returns the number of children <code>TreeNode</code>s the receiver
     * contains.
     */
    public int getChildCount() {
        return subDimensions.size();
    }

    /**
     * Returns the parent <code>TreeNode</code> of the receiver.
     */
    public TreeNode getParent() {
        return superDimension;
    }

    /**
     * Returns the index of <code>node</code> in the receivers children.
     * If the receiver does not contain <code>node</code>, -1 will be
     * returned.
     */
    public int getIndex(TreeNode node) {
        //noinspection SuspiciousMethodCalls
        return subDimensions.indexOf(node);
    }

    /**
     * Returns true if the receiver allows children.
     */
    public boolean getAllowsChildren() {
        return subDimensions.size() > 0;
    }

    /**
     * Returns true if the receiver is a leaf.
     */
    public boolean isLeaf() {
        return subDimensions.size() <= 0;
    }

    /**
     * Returns the children of the receiver as an <code>Enumeration</code>.
     */
    public Enumeration children() {
        return Collections.enumeration(subDimensions);
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