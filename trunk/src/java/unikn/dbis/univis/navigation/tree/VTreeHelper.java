package unikn.dbis.univis.navigation.tree;

import unikn.dbis.univis.meta.VHierarchy;
import unikn.dbis.univis.meta.VDataReference;
import unikn.dbis.univis.meta.VDimension;
import unikn.dbis.univis.meta.VDiceBox;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.DefaultMutableTreeNode;

import org.hibernate.Hibernate;

/**
 * TODO: document me!!!
 * <p/>
 * <code>VTreeHelper</code>.
 * <p/>
 * User: raedler, weiler
 * Date: 10.04.2006
 * Time: 16:38:04
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public class VTreeHelper {

    public static MutableTreeNode createDefaultTree(VDiceBox diceBox) {

        MutableTreeNode root = new DefaultMutableTreeNode(diceBox.getName());

        int index = 0;
        for (VHierarchy child : diceBox.getChildren()) {
            createDefaultTree(root, child, index++);
        }

        return root;
    }

    private static MutableTreeNode createDefaultTree(MutableTreeNode parent, VHierarchy hierarchy, int index) {

        MutableTreeNode node = new DefaultMutableTreeNode();

        VDataReference dataReference = hierarchy.getDataReference();
        dataReference.setParent(hierarchy.getParent().getDataReference());

        if (dataReference.getParent() != null) {
            dataReference.getParent().getChildren().add(dataReference);
        }

        node.setUserObject(dataReference);

        parent.insert(node, index);

        int i = 0;
        for (VHierarchy child : hierarchy.getChildren()) {
            createDefaultTree(node, child, i++);
        }

        return parent;
    }
}