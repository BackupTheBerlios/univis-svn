package unikn.dbis.univis.visualization.graph;

import org.jgraph.JGraph;

import org.jgraph.graph.*;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.*;
import java.awt.dnd.*;
import java.util.Map;

import unikn.dbis.univis.visualization.chart.ChartSample;

import javax.swing.*;

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


    //private OrderedTreeLayoutAlgorithm moen = new OrderedTreeLayoutAlgorithm();

    /**
     * Returns a <code>JGraph</code> with a sample model.
     */
    public VGraph() {


        setModel(model);
        setGraphLayoutCache(cache);
        new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
        //moen.setLayoutOrientation(SwingConstants.VERTICAL);
        setEditable(false);
//        setBendable(false);
        setMoveable(false);
//        setSelectionEnabled(false);
//        setFocusable(false);
//        setEnabled(false);

        //DefaultGraphCell[] cells = new DefaultGraphCell[3];
       cells[0] = createVertex("cell1", 20, 220, 160, 160, false, 500, 1000);


        //System.out.println(cells[0].getChildren());
        //GraphConstants.setBounds(cells[0].getAttributes(), new Rectangle2D.Double(20, 220, 160, 160));
        //GraphConstants.setGradientColor(cells[0].getAttributes(), Color.orange);
        //GraphConstants.setOpaque(cells[0].getAttributes(), true);

//        DefaultPort port0 = new DefaultPort();

//        cells[0].add(port0);
        //cells[1] = new DefaultGraphCell(new String("World"));

        //GraphConstants.setBounds(cells[1].getAttributes(), new Rectangle2D.Double(240, 200, 160, 160));
        //GraphConstants.setResize(cells[0].getAttributes(), false);
        //GraphConstants.setGradientColor(cells[1].getAttributes(), Color.red);
        //GraphConstants.setOpaque(cells[1].getAttributes(), true);

//        DefaultPort port1 = new DefaultPort();

//        cells[1].add(port1);
        //DefaultEdge edge = new DefaultEdge();
        //edge.setSource(cells[0]/*.getChildAt(0)*/);
        //edge.setTarget(cells[1]/*.getChildAt(0)*/);
        //cells[2] = edge;
        //int arrow = GraphConstants.ARROW_CLASSIC;
        //moen.setChildParentDistance(25);
        //moen.setLayoutOrientation(MoenLayoutAlgorithm.LEFT_TO_RIGHT);
        //GraphConstants.setLineEnd(edge.getAttributes(), arrow);
        //GraphConstants.setEndFill(edge.getAttributes(), true);
        this.getGraphLayoutCache().insert(cells);

        //moen.run(this, cells);
    }


    public static DefaultGraphCell createVertex(String name, double x,
                                                double y, double w, double h, boolean raised, int pointx, int pointy) {

        // Create vertex with the given name
        DefaultGraphCell cell = new DefaultGraphCell(new ChartSample());

        // Set bounds
        GraphConstants.setBounds(cell.getAttributes(), new Rectangle2D.Double(
                x, y, w, h));

        // Set fill color
        //if (bg != null) {
        //GraphConstants.setGradientColor(cell.getAttributes(), bg);
        //   GraphConstants.setOpaque(cell.getAttributes(), true);
        //}

        // Set raised border
        if (raised)
            GraphConstants.setBorder(cell.getAttributes(), BorderFactory
                    .createRaisedBevelBorder());
        else
            // Set black border
            GraphConstants.setBorderColor(cell.getAttributes(), Color.black);

        // Add a Floating Port
        cell.addPort(new Point2D.Double(pointx, pointy));
        cell.addPort(new Point2D.Double(500,1000));
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
                this.getGraphLayoutCache().insert(edge3);

                this.getGraphLayoutCache().insert(nextCell);
                //nextCell.setParent(cells[0]);
                //System.out.println(this.getGraphLayoutCache().getCells(false, true, false,false).length);
            }
            // = new JGraphFacade(this, cells , true, true, true, true);
            //moen.run(this, cells);
            layout.run(facade);
            Map nested = facade.createNestedMap(true, true);
            this.getGraphLayoutCache().edit(nested);
            dtde.dropComplete(true);
            check++;
        } else {

            for (int i = 0; i < cache.getNeighbours(cells[0], null, true, true).size(); i++) {
              DefaultGraphCell action = (DefaultGraphCell) cache.getNeighbours(cells[0], null, true, true).get(i);
            //System.out.println(facade.getRoots().get(0));
            //for (int i = 0; i < facade.getVertices().size(); i++) {
              //  System.out.println(facade.getEdges());
                //if (facade.getVertices().iterator().next().equals(facade.getRoots().get(0))) {
                for (int x = 0; x < 3; x++) {
               DefaultGraphCell newCell = createVertex("cellNew", 200, 200, 160, 160, false, 500, 0);
               DefaultEdge edge4 = new DefaultEdge();
                    // Fetch the ports from the new vertices, and connect them with the edge
                edge4.setSource(action.getChildAt(1));
                edge4.setTarget(newCell.getChildAt(0));
                //Set Arrow Style for edge
                GraphConstants.setLineEnd(edge4.getAttributes(), GraphConstants.ARROW_CLASSIC);
                GraphConstants.setEndFill(edge4.getAttributes(), true);
                this.getGraphLayoutCache().insert(edge4);

                this.getGraphLayoutCache().insert(newCell);
                }
            }
               /* else{
                    System.out.println("Hansi");
                }
            }*/
            facade = new JGraphFacade(this, cells , true, true, true, true);
            //moen.run(this, cells);
            layout.run(facade);
            Map nested = facade.createNestedMap(true, true);
            this.getGraphLayoutCache().edit(nested);
            dtde.dropComplete(true);
        }
    }
}
