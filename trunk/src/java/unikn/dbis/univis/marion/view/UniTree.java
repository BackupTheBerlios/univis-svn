package view;

import unikn.dbis.univis.marion.data.*;
import unikn.dbis.univis.marion.view.UniNode;
import unikn.dbis.univis.marion.view.NotDraggable;
import unikn.dbis.univis.marion.view.TreeTransferable;

import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.util.Iterator;

import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * @author Marion Herb
 * @author Christian Gruen
 */
public class UniTree extends JTree implements DragGestureListener,
        DragSourceListener {

    public UniTree(Cubes cubes, Cube selectedCube) {
        super(createRoot(cubes, selectedCube));
        setBorder(new EmptyBorder(4, 8, 4, 8));

        //DragSource -> Initiates drags and creates drag gesture recognizers
        //DragGestureRecognizer -> Fires events in response to drag gestures in
        // a component
        DragSource dragSource = DragSource.getDefaultDragSource();
        dragSource.createDefaultDragGestureRecognizer(this, // component where
                // drag originates
                DnDConstants.ACTION_COPY_OR_MOVE, // actions
                this); // drag gesture recognizer
    }

    private static UniNode createRoot(Cubes cubes, Cube selectedCube) {
        UniNode root = new UniNode(new Text("ROOT", "UniVis"));
        for (Iterator iter = cubes.iterator(); iter.hasNext();) {
            Cube cube = (Cube) iter.next();
            if (selectedCube != null && !cube.equals(selectedCube)) {
                continue;
            }
            // add Cube like Studierendenstatistik
            UniNode cubeNode = new UniNode(cube);
            root.add(cubeNode);
            // add "DIMENSIONS"
            UniNode dimNode = new UniNode(new Text("DIMENSIONS", "DIMENSIONS"));
            cubeNode.add(dimNode);
            // add "MEASURES"
            UniNode measureNode = new UniNode(new Text("MEASURES", "MEASURES"));
            cubeNode.add(measureNode);

            // add Dimension Nodes
            createNodes(dimNode, cube, 1);

            // add Measure Nodes
            Measures measures = cube.getMeasures();
            for (Iterator iterator = measures.iterator(); iterator.hasNext();) {
                Measure element = (Measure) iterator.next();
                measureNode.add(new UniNode(element));
            }
        }
        return root;
    }

    private static void createNodes(UniNode parent, Node node, int l) {
        Nodes descendants = node.getDescendants();
        for (Iterator iter = descendants.iterator(); iter.hasNext();) {
            Node element = (Node) iter.next();
            UniNode uniNode = new UniNode(element);
            parent.add(uniNode);
            createNodes(uniNode, element, l + 1);
        }
    }

    //Fires events in response to drag gestures in a component
    public void dragGestureRecognized(DragGestureEvent e) {
        if (!this.isSelectionEmpty()) {
            //get selected node
            DefaultMutableTreeNode treeNode = getTreeNode(e.getDragOrigin());
            if (treeNode == null) {
                return;
            }
            // dont allow drag of cube nodes
            if (((UniNode) treeNode).getNode() instanceof NotDraggable) {
                return;
            }
            Transferable transferable = new TreeTransferable(treeNode);
            e.startDrag(DragSource.DefaultCopyDrop, // cursor
                    transferable, // mechanism for transferring data to and from
                    // a JComponent
                    this); // drag source listener}
        }
    }

    public void dragEnter(DragSourceDragEvent dsde) {
    }

    public void dragOver(DragSourceDragEvent dsde) {
    }

    public void dropActionChanged(DragSourceDragEvent dsde) {
    }

    public void dragDropEnd(DragSourceDropEvent dsde) {
    }

    public void dragExit(DragSourceEvent dse) {
    }

    protected DefaultMutableTreeNode getTreeNode(Point p) {
        TreePath path = this.getPathForLocation(p.x, p.y);
        if (path != null) {
            return (DefaultMutableTreeNode) path.getLastPathComponent();
        }
        else
            return null;
    }
}
