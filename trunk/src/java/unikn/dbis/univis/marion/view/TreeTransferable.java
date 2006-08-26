package unikn.dbis.univis.marion.view;

import java.awt.datatransfer.*;

import javax.swing.tree.TreeNode;

/**
 * @author Marion Herb
 */
public class TreeTransferable implements Transferable {

    // This is a list of the flavors we know how to work with
    private static DataFlavor[] flavors = {new TreeNodeFlavor(),
            DataFlavor.stringFlavor};

    // the data itself
    private TreeNode data = null;


    /**
     * @param data a TreeNode (eg. MutableTreeNode)
     */
    public TreeTransferable(TreeNode data) {
        this.data = data;
    }


    /**
     * @param df the flavor type desired for the data. Acceptable value is
     *           DataFlavor.stringFlavor.
     * @return if df is DataFlavor.stringFlavor, returns a raw string containing
     *         an Ant element name.
     */
    public Object getTransferData(DataFlavor df) {
        if (df == null)
            return null;
        else if (df instanceof TreeNodeFlavor)
            return data;
        else if (df.equals(DataFlavor.stringFlavor))
            return data.toString();
        else
            return null;
    }

    /**
     * @return an array containing a single ElementFlavor.
     */
    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    /**
     * @param df the flavor to check
     * @return true if df is an ElementFlavor
     */
    public boolean isDataFlavorSupported(DataFlavor df) {
        if (df == null)
            return false;
        for (int i = 0; i < flavors.length; i++) {
            if (df.equals(flavors[i])) {
                return true;
            }
        }
        return false;
    }
}
