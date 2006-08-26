package unikn.dbis.univis.pivot.data;

import java.util.ArrayList;
import java.util.Vector;

/**
 * @author Marion Herb
 *         <p/>
 *         Nodes collection
 */
public class Nodes extends Vector {

    /**
     * Default constructor
     */
    public Nodes() {
        super();
    }

    /**
     * Adds a node to the collection
     *
     * @param node
     */
    public void add(Node node) {
        super.add(node);
    }

    /**
     * @param i
     * @return Returns node on pos i
     */
    public Node getNode(int i) {
        return (Node) super.get(i);
    }

    /**
     * Flat search a node in this collection
     *
     * @param id
     * @return Returns a node if found, else returns null
     */
    public Node findNode(int id) {
        for (int i = 0; i < super.size(); i++) {
            Node node = getNode(i);
            if (node != null && node.getId() == id)
                return node;
        }

        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see java.util.Vector#toString()
     */
    public String toString() {
        return new ArrayList(this).toString();
    }

    /**
     * @return Returns a comma-separated list of node descriptions
     */
    public String createText() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < super.size(); i++) {
            Node node = getNode(i);
            if (node != null) {
                sb.append(node.getDescription());
            }
            else {
                sb.append("null");
            }
            if (i < super.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }


}
