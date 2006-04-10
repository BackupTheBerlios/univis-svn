package unikn.dbis.univis.gui;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeNode;
import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import java.awt.dnd.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.datatransfer.Transferable;
import java.util.*;

// @todo document me!

/**
 * MoiTree.
 * <p/>
 * User: Andreas Weiler
 * Date: 24.11.2005
 * Time: 22:14:38
 *
 * @author Andreas Weiler
 * @version $Id: MyTree.java,v 1.2 2006/01/19 20:18:47 weiler Exp $
 * @since EcoBill 1.0
 */
public class MyTree extends JPanel implements DragGestureListener, DragSourceListener {


    private JTree fakTree;

    public MyTree() {
        //setLayout(new BorderLayout());
        initTree();
        //DragSource -> Initiates drags and creates drag gesture recognizers
        //DragGestureRecognizer -> Fires events in response to drag gestures in a component
        DragSource dragSource = DragSource.getDefaultDragSource();
        dragSource.createDefaultDragGestureRecognizer(
                fakTree, // component where drag originates
                DnDConstants.ACTION_COPY_OR_MOVE, // actions
                this); // drag gesture recognizer

    }

    public DefaultMutableTreeNode rootDim = new DefaultMutableTreeNode("Dimensions");
    public DefaultMutableTreeNode nodeCostCat = new DefaultMutableTreeNode("Cost Categories");
    public DefaultMutableTreeNode nodePersonell = new DefaultMutableTreeNode("Personell");
    public DefaultMutableTreeNode nodeServices = new DefaultMutableTreeNode("Services");
    public DefaultMutableTreeNode nodeMaterials = new DefaultMutableTreeNode("Materials");
    public DefaultMutableTreeNode nodeInfra = new DefaultMutableTreeNode("Infrastructure");
    public DefaultMutableTreeNode nodeMisc = new DefaultMutableTreeNode("Miscellaneous");

    public DefaultMutableTreeNode nodePeriods = new DefaultMutableTreeNode("Periods");
    public DefaultMutableTreeNode nodeYearsCal = new DefaultMutableTreeNode("by year (calendar)");
    public DefaultMutableTreeNode nodeYearsAc = new DefaultMutableTreeNode("by year (academic)");
    public DefaultMutableTreeNode nodeSem = new DefaultMutableTreeNode("by semester");
    public DefaultMutableTreeNode nodeMonth = new DefaultMutableTreeNode("by months");

    public void initTree() {

        nodeCostCat.add(nodePersonell);
        nodeCostCat.add(nodeServices);
        nodeCostCat.add(nodeMaterials);
        nodeCostCat.add(nodeInfra);
        nodeCostCat.add(nodeMisc);

        nodePeriods.add(nodeYearsCal);

        nodeSem.add(nodeMonth);
        nodeYearsAc.add(nodeSem);
        nodePeriods.add(nodeYearsAc);

        rootDim.add(nodeCostCat);
        rootDim.add(nodePeriods);

        fakTree = new JTree(rootDim);

        this.add(fakTree);
    }

    //Fires events in response to drag gestures in a component
    public void dragGestureRecognized(DragGestureEvent e) {

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) fakTree.getLastSelectedPathComponent();
        System.out.println("Object is choosen");
        e.startDrag(DragSource.DefaultCopyDrop, new ObjectTransferable(), this);

    }

    public void dragEnter(DragSourceDragEvent dsde) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void dragOver(DragSourceDragEvent dsde) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void dropActionChanged(DragSourceDragEvent dsde) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void dragExit(DragSourceEvent dse) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void dragDropEnd(DragSourceDropEvent dsde) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}

