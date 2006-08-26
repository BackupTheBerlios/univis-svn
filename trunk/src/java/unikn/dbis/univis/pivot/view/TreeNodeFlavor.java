package unikn.dbis.univis.pivot.view;

import java.awt.datatransfer.*;

/**
 * @author Marion Herb
 */
public class TreeNodeFlavor extends DataFlavor {

    public TreeNodeFlavor() {
        super(javax.swing.tree.TreeNode.class, "TreeNode");
    }

}
