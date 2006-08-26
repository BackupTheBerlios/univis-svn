package unikn.dbis.univis.marion.data;

/**
 * @author Marion Herb
 *         <p/>
 *         Defines an abstract Node
 */
public abstract class Node {

    protected Nodes descendants = new Nodes();

    protected Node parent;

    protected String name;

    protected String description;

    protected int id;

    protected ForeignKey foreignKey;


    /**
     * @param node
     */
    public void add(Node node) {
        this.descendants.add(node);
        node.parent = this;
    }

    /**
     * Searches for a sub node of this node
     *
     * @param id
     * @return Returns the node or null if node was not found
     */
    public Node findNode(int id) {
        Node node = null;
        // bin ich es?
        if (this.id == id) {
            return this;
        }

        // wenn nicht, dann mit dem ersten Kind das gleiche Spiel von vorne...
        for (int i = 0; i < this.descendants.size(); i++) {
            Node child = this.descendants.getNode(i);
            if (child != null) {
                node = child.findNode(id);
                if (node != null) {
                    break;
                }
            }
        }
        return node;
    }

    /**
     * This method climbs up in the node hierarchy till there is no more parent
     * node of the same type and returns this node
     *
     * @return Returns the root node or this node if no parent exists
     */
    public Node getRoot() {
        return getRoot(0);
    }

    private Node getRoot(int level) {
        Node parent = this.getParent();
        if (parent != null && parent.getClass().equals(this.getClass())) {
            return parent.getRoot(level + 1);
        }
        else {
            if (level > 0) {
                return this;
            }
            else {
                return parent;
            }
        }
    }

    public Node getCube() {
        Node parent = this.getParent();
        if (parent != null && parent.getClass().equals(this.getClass())) {
            return parent.getCube();
        }
        else { // parent ist andere Klasse also Cube gefunden
            return parent;
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see Object#toString()
     */
    public String toString() {
        return this.getClass().getName() + " [" + id + ": " + name + "/"
                + description + "] Foreign Key [" + foreignKey + "]";
    }

    /**
     * @param deep if true, return a deep representation
     * @return Returns a String representation of this node
     */
    public String toString(boolean deep) {
        StringBuffer sb = new StringBuffer();
        sb.append(this.toString(true, 1));
        return sb.toString();
    }

    private String toString(boolean deep, int level) {
        StringBuffer sb = new StringBuffer();
        sb.append(this.toString() + "\n");
        for (int i = 0; i < this.descendants.size(); i++) {
            Node child = this.descendants.getNode(i);
            if (child != null) {
                for (int j = 0; j < level; j++) {
                    sb.append("  ");
                }
                sb.append("Level " + level + "; Child " + (i + 1) + ": ");
                sb.append(child.toString(true, level + 1));
            }
        }
        return sb.toString();
    }

    /**
     * @return Returns the descendants.
     */
    public Nodes getDescendants() {
        return descendants;
    }

    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return Returns the id.
     */
    public int getId() {
        return id;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return Returns the parent.
     */
    public Node getParent() {
        return parent;
    }

    /**
     * @return Returns the foreignKey.
     */
    public ForeignKey getForeignKey() {
        return foreignKey;
    }

    /**
     * @param foreignKey The foreignKey to set.
     */
    public void setForeignKey(ForeignKey foreignKey) {
        this.foreignKey = foreignKey;
    }
}
