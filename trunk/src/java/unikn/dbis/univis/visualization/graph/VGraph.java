package unikn.dbis.univis.visualization.graph;

import org.jgraph.JGraph;
import org.jgraph.layout.MoenLayoutAlgorithm;
import org.jgraph.graph.*;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.*;
import java.awt.dnd.*;

import unikn.dbis.univis.visualization.chart.ChartSample;

import javax.swing.*;

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

    private int arrow = GraphConstants.ARROW_CLASSIC;
    private MoenLayoutAlgorithm moen = new MoenLayoutAlgorithm();
    /**
     * Returns a <code>JGraph</code> with a sample model.
     */
    public VGraph() {

        setModel(model);
        setGraphLayoutCache(cache);
        new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
        //moen.setLayoutOrientation(SwingConstants.VERTICAL);
//        setEditable(false);
//        setBendable(false);
//        setMoveable(false);
//        setSelectionEnabled(false);
//        setFocusable(false);
//        setEnabled(false);

        //DefaultGraphCell[] cells = new DefaultGraphCell[3];

        cells[0] = createVertex("cell1", 20, 220, 160, 160, false, 1000, 500);

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

        //GraphConstants.setLineEnd(edge.getAttributes(), arrow);
        //GraphConstants.setEndFill(edge.getAttributes(), true);
        this.getGraphLayoutCache().insert(cells);
        moen.run(this, cells);
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
        cell.addPort(new Point2D.Double(pointx,pointy));

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

        DefaultGraphCell nextCell = createVertex("cell2",200, 200, 160, 160, false, 1, 500);
        //GraphConstants.setBounds(nextCell.getAttributes(), new Rectangle2D.Double(200, 120, 160, 160));

        DefaultGraphCell nextCell1 = createVertex("cell3", 350, 200, 160, 160, false, 1, 500);
        //GraphConstants.setBounds(nextCell1.getAttributes(), new Rectangle2D.Double(200, 320, 160, 160));
        // Create Edge
        DefaultEdge edge3 = new DefaultEdge();
        // Fetch the ports from the new vertices, and connect them with the edge
        edge3.setSource(cells[0].getChildAt(0));
        edge3.setTarget(nextCell.getChildAt(0));
             //Set Arrow Style for edge
        GraphConstants.setLineEnd(edge3.getAttributes(), arrow);
        GraphConstants.setEndFill(edge3.getAttributes(), true);
        this.getGraphLayoutCache().insert(edge3);



        // Create Edge
        DefaultEdge edge4 = new DefaultEdge();
        // Fetch the ports from the new vertices, and connect them with the edge
        edge4.setSource(cells[0].getChildAt(0));
        edge4.setTarget(nextCell1.getChildAt(0));
             //Set Arrow Style for edge
        GraphConstants.setLineEnd(edge4.getAttributes(), arrow);
        GraphConstants.setEndFill(edge4.getAttributes(), true);
        this.getGraphLayoutCache().insert(edge4);


        this.getGraphLayoutCache().insert(nextCell);
        this.getGraphLayoutCache().insert(nextCell1);

        moen.run(this, cells);

        dtde.dropComplete(true);
    }
}