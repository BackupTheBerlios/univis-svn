package unikn.dbis.univis.visualization.graph;

import org.jgraph.graph.*;
import org.jgraph.JGraph;

import javax.swing.*;
import java.awt.geom.Rectangle2D;
import java.awt.*;

public class GraphTest {

    public static void main(String[] args) {
        GraphModel model = new DefaultGraphModel();

        GraphLayoutCache view = new GraphLayoutCache(model, new MyCellViewFactory());


        JGraph graph = new JGraph(model, view);

        DefaultGraphCell[] cells = new DefaultGraphCell[3];

        cells[0] = new DefaultGraphCell(new String("Hello"));

        GraphConstants.setBounds(cells[0].getAttributes(), new Rectangle2D.Double(20, 20, 40, 20));
        GraphConstants.setGradientColor(cells[0].getAttributes(), Color.orange);
        GraphConstants.setOpaque(cells[0].getAttributes(), true);

        DefaultPort port0 = new DefaultPort();

        cells[0].add(port0);
        cells[1] = new DefaultGraphCell(new String("World"));

        GraphConstants.setBounds(cells[1].getAttributes(), new Rectangle2D.Double(140, 140, 40, 20));
        GraphConstants.setGradientColor(cells[1].getAttributes(), Color.red);
        GraphConstants.setOpaque(cells[1].getAttributes(), true);

        DefaultPort port1 = new DefaultPort();

        cells[1].add(port1);

        DefaultEdge edge = new DefaultEdge();
        edge.setSource(cells[0].getChildAt(0));
        edge.setTarget(cells[1].getChildAt(0));
        cells[2] = edge;

        int arrow = GraphConstants.ARROW_CLASSIC;

        GraphConstants.setLineEnd(edge.getAttributes(), arrow);
        GraphConstants.setEndFill(edge.getAttributes(), true);
        graph.getGraphLayoutCache().insert(cells);

        JFrame frame = new JFrame();
        frame.getContentPane().add(new JScrollPane(graph));
        frame.pack();
        frame.setVisible(true);
        System.out.println(Color.RED.getRGB());
    }
}