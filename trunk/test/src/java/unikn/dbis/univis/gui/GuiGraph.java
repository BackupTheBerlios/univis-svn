package unikn.dbis.univis.gui;

import org.jgraph.JGraph;
import org.jgraph.graph.*;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.geom.Rectangle2D;

/**
 * TODO: document me!!!
 * <p/>
 * GuiGraph.
 * <p/>
 * User: raedler, weiler
 * Date: 09.04.2006
 * Time: 21:05:33
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public class GuiGraph extends JGraph implements DropTargetListener {

// Construct Model and Graph
    GraphModel model = new DefaultGraphModel();

    public GuiGraph() {

    new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, this);

    this.setModel(model);

    // Control-drag should clone selection
    //this.setCloneable(true);

    // Enable edit without final RETURN keystroke
    this.setInvokesStopCellEditing(true);

    // When over a cell, jump to its default port (we only have one, anyway)
    this.setJumpToDefaultPort(true);

    // Insert all three cells in one call, so we need an array to store them
    DefaultGraphCell[] cells = new DefaultGraphCell[5];

    // Create Alle Länder Vertex
    cells[0] = createVertex("Alle Länder", 20, 220, 100, 100, Color.GREEN, false);

    // Create Deutschland Vertex
    cells[1] = createVertex("Deutschland", 120, 20, 100, 100, Color.ORANGE, true);

       // Create Ausland Vertex
    cells[2] = createVertex("Ausland", 120, 420, 100, 100, Color.ORANGE, true);

    // Create Edge
    DefaultEdge edge1 = new DefaultEdge();
    // Fetch the ports from the new vertices, and connect them with the edge
    edge1.setSource(cells[0].getChildAt(0));
    edge1.setTarget(cells[1].getChildAt(0));
    cells[3] = edge1;

    // Set Arrow Style for edge
    int arrow1 = GraphConstants.ARROW_CLASSIC;
    GraphConstants.setLineEnd(edge1.getAttributes(), arrow1);
    GraphConstants.setEndFill(edge1.getAttributes(), true);

        // Create Edge
    DefaultEdge edge2 = new DefaultEdge();
    // Fetch the ports from the new vertices, and connect them with the edge
    edge2.setSource(cells[0].getChildAt(0));
    edge2.setTarget(cells[2].getChildAt(0));
    cells[4] = edge2;

       DefaultGraphCell peter = new DefaultGraphCell();
       // peter.


    // Set Arrow Style for edge
    int arrow2 = GraphConstants.ARROW_CLASSIC;
    GraphConstants.setLineEnd(edge2.getAttributes(), arrow2);
    GraphConstants.setEndFill(edge2.getAttributes(), true);

    // Insert the cells via the cache, so they get selected
    this.getGraphLayoutCache().insert(cells);
}

public static DefaultGraphCell createVertex(String name, double x,
                                            double y, double w, double h, Color bg, boolean raised) {

    // Create vertex with the given name
    DefaultGraphCell cell = new DefaultGraphCell(name);

    // Set bounds
    GraphConstants.setBounds(cell.getAttributes(), new Rectangle2D.Double(
            x, y, w, h));

    // Set fill color
    if (bg != null) {
        GraphConstants.setGradientColor(cell.getAttributes(), bg);
        GraphConstants.setOpaque(cell.getAttributes(), true);
    }

    // Set raised border
    if (raised)
        GraphConstants.setBorder(cell.getAttributes(), BorderFactory
                .createRaisedBevelBorder());
    else
        // Set black border
        GraphConstants.setBorderColor(cell.getAttributes(), Color.black);

    // Add a Floating Port
    cell.addPort();

    return cell;
}

    public void dragEnter(DropTargetDragEvent dtde) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void dragOver(DropTargetDragEvent dtde) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void dropActionChanged(DropTargetDragEvent dtde) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void dragExit(DropTargetEvent dte) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void drop(DropTargetDropEvent dtde) {
        System.out.println("Object is Entered");//To change body of implemented methods use File | Settings | File Templates.
    }
}