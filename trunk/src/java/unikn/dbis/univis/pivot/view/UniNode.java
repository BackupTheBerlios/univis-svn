package unikn.dbis.univis.pivot.view;

import javax.swing.tree.*;

import unikn.dbis.univis.pivot.data.Node;

/**
 * @author Marion Herb
 *         <p/>
 *         Node representation for visualizing
 */
public class UniNode extends DefaultMutableTreeNode {
    private Node node;

    /**
     * @param node
     */
    public UniNode(Node node) {
//        super("id=" + node.getId() + " " + node.getName() + " ( " + node.getDescription() +  " )");
        super(node.getDescription()); // wieder einsetzen! MH
        this.node = node;
    }

    /**
     * @return Returns the node object
     */
    public Node getNode() {
        return node;
    }

}
