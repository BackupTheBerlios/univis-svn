package unikn.dbis.univis.visualization.graph;

import org.jgraph.JGraph;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.graph.*;

import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.AbstractDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.CategoryDataset;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.event.*;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.*;
import java.util.Map;
import java.io.IOException;
import java.sql.*;

import unikn.dbis.univis.visualization.chart.PieChart;
import unikn.dbis.univis.visualization.chart.BarChart;
import unikn.dbis.univis.visualization.chart.AbstractChart;
import unikn.dbis.univis.dnd.VDataReferenceFlavor;
import unikn.dbis.univis.meta.VDimension;
import unikn.dbis.univis.meta.VDataReference;
import unikn.dbis.univis.icon.VIcons;
import unikn.dbis.univis.explorer.VExplorer;
import unikn.dbis.univis.sql.VQuery;

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

    // The logger to log info, error and other occuring messages
    // or exceptions.
    public static final transient Log LOG = LogFactory.getLog(VGraph.class);

    // Different Objects for the graph.
    private GraphModel model = new DefaultGraphModel();
    private GraphLayoutCache cache = new GraphLayoutCache(model, new VCellViewFactory());
    private JGraphFacade facade = new JGraphFacade(cache);
    private JGraphTreeLayout layout = new JGraphTreeLayout();
    private VGraphCell root = null;

    private VHistoryList<VGraphCell> cellHistory = new VHistoryList<VGraphCell>();

    // Datasets for creating Charts.
    private AbstractDataset dataset;

    // Strings for different topics.
    private String chartCheck = "barChart";
    private String rootHeadLine = "";
    private String xAxis = "Studenten";
    private boolean isRoot = true;

    // Int values for different topics.
    private int cellsize = 300;

    // The <code>VQuery</code> to get the sql statements to perform
    // exploring.
    private VQuery queryHistory = new VQuery();


    /**
     * Standard Constructor
     */
    public VGraph() {

        new DropTarget(this, DnDConstants.ACTION_COPY, this);
        this.setModel(model);
        this.setGraphLayoutCache(cache);
        this.setEditable(false);
        this.setMoveable(false);
        layout.setOrientation(SwingConstants.NORTH);
    }

    /**
     * @return a DefaultGraphCell
     */
    public VGraphCell createVertex(String chartName, String id) {

        AbstractChart chart;
        if (chartCheck.equals("barChart")) {
            chart = new BarChart(chartName, (CategoryDataset) dataset, xAxis);
        }
        else {
            chart = new PieChart(chartName, (PieDataset) dataset);
        }

        // Create vertex with the given name
        VGraphCell cell = new VGraphCell(chart);
        cell.setCellId(id);

        // Set bounds
        GraphConstants.setBounds(cell.getAttributes(), new Rectangle2D.Double(chart.getX(), chart.getY(), cellsize, cellsize));

        // Add a Floating Port
        cell.addPort(new Point2D.Double(500, 0));
        cell.addPort(new Point2D.Double(500, 1000));
        cell.addPort(new Point2D.Double(0, 500));
        cell.addPort(new Point2D.Double(1000, 500));

        return cell;
    }

    /**
     * @param source : source where the edge is starting.
     * @param target : target where the edge is ending.
     */
    public void createEdges(VGraphCell source, VGraphCell target) {
        DefaultEdge edge = new DefaultEdge();
        if (layout.getOrientation() == SwingConstants.NORTH) {
            edge.setSource(source.getChildAt(1));
            edge.setTarget(target.getChildAt(0));
        }
        else {
            edge.setSource(source.getChildAt(3));
            edge.setTarget(target.getChildAt(2));
        }
        GraphConstants.setLineEnd(edge.getAttributes(), GraphConstants.ARROW_CLASSIC);
        GraphConstants.setEndFill(edge.getAttributes(), true);
        cache.insert(edge);
    }

    private void createEdges(VGraphCell target, String targetId) {

        String sourceId = "";
        if (targetId.lastIndexOf("_") != -1) {
            sourceId = targetId.substring(0, targetId.lastIndexOf("_"));
        }

        VGraphCell source = root;
        for (VGraphCell cell : cellHistory.getLastOfHistory()) {
            if (sourceId.equals(cell.getCellId())) {
                source = cell;
                break;
            }
        }

        /*
        System.out.println("TARGET_ID: " + targetId);
        System.out.println("SOURCE_ID: " + sourceId);
        System.out.println("CELL_ID: " + source);
        System.out.println("TEMP: " + tempCellList);
        */

        createEdges(source, target);
    }

    /**
     * @param result : the given resultset from the sql action.
     * @throws SQLException
     */
    public void fillChartData(ResultSet result) throws SQLException {

        layout.setAlignment(SwingConstants.CENTER);

        ResultSetMetaData data = result.getMetaData();
        int idPos = data.getColumnCount();
        int namePos = idPos - 1;
        int bufferPos = namePos - 1;

        if (isRoot) {
            cellHistory.historize();

            if (chartCheck.equals("barChart")) {
                dataset = new DefaultCategoryDataset();

                while (result.next()) {
                    ((DefaultCategoryDataset) dataset).addValue(result.getInt(1), result.getString(namePos + 1), "");
                }
            }
            else {
                dataset = new DefaultPieDataset();

                while (result.next()) {
                    ((DefaultPieDataset) dataset).setValue(result.getString(namePos + 1), result.getInt(1));
                }
            }

            root = createVertex(rootHeadLine, "");
            cache.insert(root);
            cellHistory.add(root);
            isRoot = false;
        }
        else {
            cellHistory.historize();

            String buffer = "";
            if (chartCheck.equals("barChart")) {
                while (result.next()) {

                    String currentValue = result.getString(idPos);

                    if (!buffer.equals(currentValue)) {
                        dataset = new DefaultCategoryDataset();

                        VGraphCell nextCell = createVertex(result.getString(bufferPos), result.getString(idPos));
                        createEdges(nextCell, result.getString(idPos));
                        cache.insert(nextCell);
                        cellHistory.add(nextCell);
                    }

                    ((DefaultCategoryDataset) dataset).addValue(result.getInt(1), result.getString(namePos), "");

                    buffer = currentValue;
                }
            }
            else {
                while (result.next()) {

                    String currentValue = result.getString(idPos);

                    LOG.info(result.getString(2));

                    if (!buffer.equals(currentValue)) {
                        dataset = new DefaultPieDataset();

                        VGraphCell nextCell = createVertex(result.getString(bufferPos), result.getString(idPos));
                        createEdges(nextCell, result.getString(idPos));
                        cache.insert(nextCell);
                        cellHistory.add(nextCell);
                    }

                    ((DefaultPieDataset) dataset).setValue(result.getString(namePos), result.getInt(1));

                    buffer = currentValue;
                }
            }
        }
        layout.run(facade);
        Map nested = facade.createNestedMap(true, true);
        cache.edit(nested);
    }

    /**
     * @param vDim : the given VDimension from the drag & drop.
     * @throws SQLException
     */
    public void connection(VDimension vDim) throws SQLException {

        Connection connection = VExplorer.getConnection();

        Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

        VDimension bluep = searchBluep(vDim);

        String sql = queryHistory.createChartQuery(vDim, bluep);

        ResultSet result;
        try {
            result = stmt.executeQuery(sql);

            rootHeadLine = vDim.getI18nKey();
            fillChartData(result);
        }
        catch (SQLException sqle) {
            queryHistory.historyBack();
            VExplorer.publishException(sqle);
            if (LOG.isErrorEnabled()) {
                LOG.error(sqle.getMessage(), sqle);
            }
        }
    }

    /**
     * @param vDim VDimension from the drag & drop.
     * @return VDimension which is the bluep of the Dimension
     */
    public VDimension searchBluep(VDimension vDim) {

        if (vDim.getChildren().size() == 0) {
            return vDim;
        }

        VDimension dimension = null;
        for (VDataReference vDataReference : vDim.getChildren()) {
            dimension = searchBluep((VDimension) vDataReference);
        }

        return dimension;
    }

    /**
     * @param dtde
     */
    public void dragEnter(DropTargetDragEvent dtde) {
    }

    /**
     * @param dtde
     */
    public void dragOver(DropTargetDragEvent dtde) {
    }

    /**
     * @param dtde
     */
    public void dropActionChanged(DropTargetDragEvent dtde) {
    }

    /**
     * @param dte
     */
    public void dragExit(DropTargetEvent dte) {
    }

    /**
     * Action when a <code>VDimension<code> is dropped into the
     * graph.
     *
     * @param dtde The drop target event.
     */
    public void drop(DropTargetDropEvent dtde) {

        Object o = null;
        try {
            o = dtde.getTransferable().getTransferData(VDataReferenceFlavor.DIMENSION_FLAVOR);
        }
        catch (UnsupportedFlavorException ufe) {
            dtde.rejectDrop();
            dtde.dropComplete(false);
            if (LOG.isErrorEnabled()) {
                LOG.error(ufe.getMessage(), ufe);
            }
        }
        catch (IOException ioe) {
            dtde.rejectDrop();
            dtde.dropComplete(false);
            if (LOG.isErrorEnabled()) {
                LOG.error(ioe.getMessage(), ioe);
            }
        }
        if (o instanceof VDimension) {
            VDimension vDim = (VDimension) o;

            try {
                dtde.acceptDrop(DnDConstants.ACTION_COPY);
                connection(vDim);
            }
            catch (SQLException sqle) {
                dtde.rejectDrop();
                dtde.dropComplete(false);
                if (LOG.isErrorEnabled()) {
                    LOG.error(sqle.getMessage(), sqle);
                }
            }
        }
        dtde.dropComplete(true);
    }

    public void undoCells() {
        cache.remove(cellHistory.getCurrent().toArray(), true, true);
        cellHistory.historyBack();
        queryHistory.historyBack();

        if (queryHistory.isEmpty()) {
            isRoot = true;
        }

        reloadGraph();

    }

    public void reloadGraph() {
        layout.run(facade);
        Map nested = facade.createNestedMap(true, true);
        cache.edit(nested);
    }

    private double zoomScale = 1.0;

    public void zoomIn() {
        this.setScale(zoomScale += 0.05);
    }

    public void zoomOut() {
        this.setScale(zoomScale -= 0.05);
    }

    public void setLayoutOrientation(int orientation) {
        layout.setOrientation(orientation);
    }

    public int getLayoutOrientation() {
        return layout.getOrientation();
    }

    public VQuery getQueryHistory() {
        return queryHistory;
    }

    public VHistoryList<VGraphCell> getCellHistory() {
        return cellHistory;
    }

    public void setxAxis(String xAxis) {
        this.xAxis = xAxis;
    }

    public void setChartCheck(String chartCheck) {
        this.chartCheck = chartCheck;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }
}
