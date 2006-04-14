package unikn.dbis.univis.visualization.graph;

import org.jgraph.JGraph;

import org.jgraph.graph.*;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.ChartFactory;

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
import unikn.dbis.univis.visualization.chart.Pie3DChart;
import unikn.dbis.univis.visualization.chart.Bar3DChart;
import unikn.dbis.univis.visualization.item.ChartData;
import unikn.dbis.univis.visualization.item.VisualizationItem;
import unikn.dbis.univis.visualization.item.DefaultVisualizationItem;
import unikn.dbis.univis.dnd.VDataReferenceFlavor;
import unikn.dbis.univis.meta.VDimension;

import javax.swing.*;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.tree.JGraphTreeLayout;

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
    private static ChartData chartData = new ChartData();

    /**
     * Returns a <code>JGraph</code> with a sample model.
     */
    public VGraph() {
        setModel(model);
        setGraphLayoutCache(cache);
        new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
        setEditable(false);
        setMoveable(false);

    }


    public static DefaultGraphCell createVertex(String chartName, double x,
                                                double y, double w, double h, boolean raised) {

        DefaultPieDataset data = new DefaultPieDataset();
        Pie3DChart pie3DChart = new Pie3DChart(data, chartName);
        for (VisualizationItem visualizationItem : chartData.getVisualizationItems()) {
            System.out.println("Name: " + visualizationItem.getName() + " Value: " + visualizationItem.getValue());
            data.setValue(visualizationItem.getName(), visualizationItem.getValue());
            pie3DChart.setName(visualizationItem.getName());
        }
        // Create vertex with the given name
        DefaultGraphCell cell = new DefaultGraphCell(pie3DChart);

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
        cell.addPort(new Point2D.Double(500, 0));
        cell.addPort(new Point2D.Double(500, 1000));
        return cell;
    }

    public void createEdges(DefaultGraphCell source, DefaultGraphCell target) {
        DefaultEdge edge = new DefaultEdge();

        edge.setSource(source.getChildAt(1));
        edge.setTarget(target.getChildAt(0));
        GraphConstants.setLineEnd(edge.getAttributes(), GraphConstants.ARROW_CLASSIC);
        GraphConstants.setEndFill(edge.getAttributes(), true);
        cache.insert(edge);
    }

    public void fillChartData(VDimension vDim) {

        String name = vDim.getI18nKey();
        double value = Math.random();
        VisualizationItem visualizationItem = new DefaultVisualizationItem(name, value);
        chartData.addVisualizationItem(visualizationItem);
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
            //System.out.println("ECHO: " + ((VDimension) o).getI18nKey());
            VDimension vDim = (VDimension) o;

            fillChartData(vDim);

            layout.setAlignment(SwingConstants.CENTER);
            layout.setOrientation(SwingConstants.NORTH);
            if (check == 0) {
                cells[0] = createVertex(vDim.getI18nKey() ,220, 220, 160, 160, false);
                //ChartSample root = (ChartSample) cells[0].getUserObject();
                cells[0].isRoot();

                //root.setIdentify("Root");
                cache.insert(cells);
            } else if (check == 1) {
                int x = 5;
                for (int i = 0; i < x; i++) {

                    DefaultGraphCell nextCell = createVertex(vDim.getI18nKey(),200, 200, 160, 160, false);
                    //ChartSample kids = (ChartSample) nextCell.getUserObject();
                    //kids.setIdentify("Kids");
                    createEdges(cells[0], nextCell);
                    cache.insert(nextCell);
                }
            } else {
                for (int i = 0; i < cache.getNeighbours(cells[0], null, true, true).size(); i++) {
                    DefaultGraphCell action = (DefaultGraphCell) cache.getNeighbours(cells[0], null, true, true).get(i);
                    //ChartSample all = (ChartSample) action.getUserObject();
                    //System.out.println(all.getIdentify());
                    for (int x = 0; x < 3; x++) {
                        DefaultGraphCell newCell = createVertex(vDim.getI18nKey(), 200, 200, 160, 160, false);

                        createEdges(action, newCell);
                        cache.insert(newCell);
                    }
                }
            }
            JGraphFacade facade = new JGraphFacade(this, cells, true, true, true, true);
            layout.run(facade);
            Map nested = facade.createNestedMap(true, true);
            cache.edit(nested);
            check++;
            dtde.dropComplete(true);
        }
    }
}
