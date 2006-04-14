package unikn.dbis.univis.visualization.graph;

import org.jgraph.JGraph;

import org.jgraph.graph.*;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.util.Map;
import java.io.IOException;

import unikn.dbis.univis.visualization.chart.ChartSample;
import unikn.dbis.univis.dnd.VDataReferenceFlavor;
import unikn.dbis.univis.meta.VDimension;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.JGraphLayout;
import com.jgraph.layout.tree.JGraphTreeLayout;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;

/**
 * TODO: document me!!!
 * <p/>
 * <code>VGraph</code>.
 * <p/>
 * User: raedler, weiler
 * Date: 12.04.2006
 * Time: 00:02:10
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public class VGraph extends JGraph implements DropTargetListener {

    GraphModel model = new DefaultGraphModel();
    GraphLayoutCache cache = new GraphLayoutCache(model, new VCellViewFactory());
    // Insert all three cells in one call, so we need an array to store them
    private DefaultGraphCell[] cells = new DefaultGraphCell[1];
    private JGraphTreeLayout layout = new JGraphTreeLayout();
    private int check;
    public Transferable tr;

    /**
     * Returns a <code>JGraph</code> with a sample model.
     */
    public VGraph() {


        setModel(model);
        setGraphLayoutCache(cache);
        new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
        setEditable(false);
        setMoveable(false);

        //DefaultGraphCell[] cells = new DefaultGraphCell[3];
        cells[0] = createVertex("cell1", 20, 220, 160, 160, false, 500, 1000);
        cache.insert(cells);

    }


    public static DefaultGraphCell createVertex(String name, double x,
                                                double y, double w, double h, boolean raised, int pointx, int pointy) {

        // Create vertex with the given name
        DefaultGraphCell cell = new DefaultGraphCell(new ChartSample());

        // Set bounds
        GraphConstants.setBounds(cell.getAttributes(), new Rectangle2D.Double(
                x, y, w, h));

        // Set raised border
        if (raised)
            GraphConstants.setBorder(cell.getAttributes(), BorderFactory
                    .createRaisedBevelBorder());
        else
            // Set black border
            GraphConstants.setBorderColor(cell.getAttributes(), Color.black);

        // Add a Floating Port
        cell.addPort(new Point2D.Double(pointx, pointy));
        cell.addPort(new Point2D.Double(500, 1000));
        return cell;
    }

    public void dragEnter(DropTargetDragEvent dtde) {

    }

    public void dragOver(DropTargetDragEvent dtde) {

    }

    public void dropActionChanged(DropTargetDragEvent dtde) {

    }

    public void dragExit(DropTargetEvent dte) {

    }

    public void drop(DropTargetDropEvent dtde) {
        Object o = null;
        try {
            o = dtde.getTransferable().getTransferData(VDataReferenceFlavor.DIMENSION_FLAVOR);
        } catch (UnsupportedFlavorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (o instanceof VDimension) {
            System.out.println("ECHO: " + ((VDimension) o).getI18nKey());
            layout.setAlignment(SwingConstants.TOP);
            layout.setOrientation(SwingConstants.NORTH);
            JGraphFacade facade = new JGraphFacade(this, cells, true, true, true, true);
            if (check == 0) {
                int x = 5;
                for (int i = 0; i < x; i++) {

                    DefaultGraphCell nextCell = createVertex("cell2", 200, 200, 160, 160, false, 500, 0);
                    DefaultEdge edge3 = new DefaultEdge();
                    // Fetch the ports from the new vertices, and connect them with the edge
                    edge3.setSource(cells[0].getChildAt(0));
                    edge3.setTarget(nextCell.getChildAt(0));
                    //Set Arrow Style for edge
                    GraphConstants.setLineEnd(edge3.getAttributes(), GraphConstants.ARROW_CLASSIC);
                    GraphConstants.setEndFill(edge3.getAttributes(), true);
                    cache.insert(edge3);

                    cache.insert(nextCell);
                }
                layout.run(facade);
                Map nested = facade.createNestedMap(true, true);
                cache.edit(nested);
                dtde.dropComplete(true);
                check++;
            } else {
                for (int i = 0; i < cache.getNeighbours(cells[0], null, true, true).size(); i++) {
                    DefaultGraphCell action = (DefaultGraphCell) cache.getNeighbours(cells[0], null, true, true).get(i);

                    for (int x = 0; x < 3; x++) {
                        DefaultGraphCell newCell = createVertex("cellNew", 200, 200, 160, 160, false, 500, 0);
                        DefaultEdge edge4 = new DefaultEdge();
                        // Fetch the ports from the new vertices, and connect them with the edge
                        edge4.setSource(action.getChildAt(1));
                        edge4.setTarget(newCell.getChildAt(0));
                        //Set Arrow Style for edge
                        GraphConstants.setLineEnd(edge4.getAttributes(), GraphConstants.ARROW_CLASSIC);
                        GraphConstants.setEndFill(edge4.getAttributes(), true);
                        cache.insert(edge4);

                        cache.insert(newCell);
                    }
                }
                facade = new JGraphFacade(this, cells, true, true, true, true);
                layout.run(facade);
                Map nested = facade.createNestedMap(true, true);
                cache.edit(nested);
                dtde.dropComplete(true);
            }

        }
    }
}
