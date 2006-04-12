package unikn.dbis.univis.visualization.graph;

import org.jgraph.JGraph;
import org.jgraph.graph.*;

import java.awt.geom.Rectangle2D;
import java.awt.*;

import unikn.dbis.univis.visualization.chart.ChartSample;

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
public class VGraph extends JGraph {

    private GraphModel model = new DefaultGraphModel();
    private GraphLayoutCache cache = new GraphLayoutCache(model, new VCellViewFactory());

    /**
     * Returns a <code>JGraph</code> with a sample model.
     */
    public VGraph() {

        setModel(model);
        setGraphLayoutCache(cache);

//        setEditable(false);
//        setBendable(false);
//        setMoveable(false);
//        setSelectionEnabled(false);
//        setFocusable(false);
//        setEnabled(false);

        DefaultGraphCell[] cells = new DefaultGraphCell[3];

        cells[0] = new DefaultGraphCell(new ChartSample());

        GraphConstants.setBounds(cells[0].getAttributes(), new Rectangle2D.Double(20, 20, 160, 160));
        //GraphConstants.setGradientColor(cells[0].getAttributes(), Color.orange);
        //GraphConstants.setOpaque(cells[0].getAttributes(), true);

//        DefaultPort port0 = new DefaultPort();

//        cells[0].add(port0);
        cells[1] = new DefaultGraphCell(new String("World"));

        GraphConstants.setBounds(cells[1].getAttributes(), new Rectangle2D.Double(240, 200, 160, 160));
        GraphConstants.setResize(cells[0].getAttributes(), false);
        //GraphConstants.setGradientColor(cells[1].getAttributes(), Color.red);
        //GraphConstants.setOpaque(cells[1].getAttributes(), true);

//        DefaultPort port1 = new DefaultPort();

//        cells[1].add(port1);

        DefaultEdge edge = new DefaultEdge();
        edge.setSource(cells[0]/*.getChildAt(0)*/);
        edge.setTarget(cells[1]/*.getChildAt(0)*/);
        cells[2] = edge;

        int arrow = GraphConstants.ARROW_CLASSIC;

        GraphConstants.setLineEnd(edge.getAttributes(), arrow);
        GraphConstants.setEndFill(edge.getAttributes(), true);
        cache.insert(cells);
    }
}