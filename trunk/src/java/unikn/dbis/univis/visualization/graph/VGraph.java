package unikn.dbis.univis.visualization.graph;

import org.jgraph.JGraph;
import org.jgraph.graph.*;

import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.AbstractDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.CategoryDataset;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.event.*;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.*;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
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
public class VGraph implements DropTargetListener {

    /*
     * Different Objects for the graph.
     */
    public JGraph graph = new JGraph();
    private GraphModel model = new DefaultGraphModel();
    private GraphLayoutCache cache = new GraphLayoutCache(model, new VCellViewFactory());
    private JGraphTreeLayout layout = new JGraphTreeLayout();
    private VGraphCell root = null;

    private List<VGraphCell> cellList = new ArrayList<VGraphCell>();

    private List<VGraphCell> tempCellList = null;

    /*
     * Object Transferable for Drag & Drop.
     */
    public Transferable tr;

    /*
     * Datasets for creating Charts.
     */
    private AbstractDataset dataset;

    /*
     * Strings for different topics.
     */
    private String chartCheck = "barChart";
    private String xAxis = "Studenten";
    private boolean isRoot = true;

    /*
     * Int values for different topics.
     */
    private int cellsize = 300;

    /*
     *  VQuery
     */
    private VQuery vquery = new VQuery();

    /**
     * Standard Constructor
     */
    public VGraph() {
        graph.setModel(model);
        graph.setGraphLayoutCache(cache);
        new DropTarget(graph, DnDConstants.ACTION_COPY_OR_MOVE, this);
        graph.setEditable(false);
        graph.setMoveable(false);
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

        return cell;
    }

    /**
     * @param source : source where the edge is starting.
     * @param target : target where the edge is ending.
     */
    public void createEdges(VGraphCell source, VGraphCell target) {
        DefaultEdge edge = new DefaultEdge();
        edge.setSource(source.getChildAt(1));
        edge.setTarget(target.getChildAt(0));
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
        for (VGraphCell cell : tempCellList) {
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
        layout.setOrientation(SwingConstants.NORTH);

        ResultSetMetaData data = result.getMetaData();
        int idPos = data.getColumnCount();
        int namePos = idPos - 1;
        int bufferPos = namePos - 1;

        if (isRoot) {
            tempCellList = new ArrayList<VGraphCell>(cellList);
            cellList.clear();

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

            root = createVertex("ROOT_NODE", "");
            cache.insert(root);
            cellList.add(root);
            isRoot = false;
        }
        else {
            tempCellList = new ArrayList<VGraphCell>(cellList);
            cellList.clear();
            String buffer = "";
            if (chartCheck.equals("barChart")) {
                while (result.next()) {

                    String currentValue = result.getString(bufferPos);

                    if (!buffer.equals(currentValue)) {
                        dataset = new DefaultCategoryDataset();

                        VGraphCell nextCell = createVertex(result.getString(bufferPos), result.getString(idPos));
                        createEdges(nextCell, result.getString(idPos));
                        cache.insert(nextCell);
                        cellList.add(nextCell);
                    }

                    ((DefaultCategoryDataset) dataset).addValue(result.getInt(1), result.getString(namePos), "");

                    buffer = currentValue;
                }
            }
            else {
                while (result.next()) {

                    String currentValue = result.getString(bufferPos);

                    System.out.println(result.getString(2));

                    if (!buffer.equals(currentValue)) {
                        dataset = new DefaultPieDataset();

                        VGraphCell nextCell = createVertex(result.getString(bufferPos), result.getString(idPos));
                        createEdges(nextCell, result.getString(idPos));
                        cache.insert(nextCell);
                        cellList.add(nextCell);
                    }

                    ((DefaultPieDataset) dataset).setValue(result.getString(namePos), result.getInt(1));

                    buffer = currentValue;
                }
            }
        }
        JGraphFacade facade = new JGraphFacade(cache);
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

        ResultSet result = stmt.executeQuery(vquery.createChartQuery(vDim, bluep));
        //chartName = vDim.getI18nKey();
        fillChartData(result);
        connection.close();
    }

    /**
     * @param vDim : VDimension from the drag & drop.
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
     * Action when a VDimension is dropt into the graph.
     *
     * @param dtde
     */
    public void drop(DropTargetDropEvent dtde) {
        Object o = null;

        try {
            o = dtde.getTransferable().getTransferData(VDataReferenceFlavor.DIMENSION_FLAVOR);
        }
        catch (UnsupportedFlavorException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if (o instanceof VDimension) {
            //System.out.println("ECHO: " + ((VDimension) o).getI18nKey());
            VDimension vDim = (VDimension) o;

            try {
                connection(vDim);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        dtde.dropComplete(true);
    }

    /**
     * @param checkBoxMenuItem : Item which gets the Listener.
     * @param chartName        : String which is need to set.
     */
    public void makeActionListenerCharts(final JCheckBoxMenuItem checkBoxMenuItem, final String chartName) {

        checkBoxMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (e.getSource().equals(checkBoxMenuItem))

                    chartCheck = chartName;
            }
        });
    }

    /**
     * @param checkBoxMenuItem : Item which gets the Listener.
     * @param cube             : String which is need to set.
     * @param measureName      : String which is need to set.
     */
    public void makeActionListenerMeasures(final JCheckBoxMenuItem checkBoxMenuItem, final String cube, final String measureName, final String xAxisName) {

        checkBoxMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (e.getSource().equals(checkBoxMenuItem)) {

                    vquery.setCubeAttribute(measureName);
                    vquery.setCubeName(cube);
                    xAxis = xAxisName;
                }
            }
        });
    }

    /**
     * Returns the DeleteButton for deleting the whole graph.
     *
     * @return The DeleteButton.
     */
    public JButton createDeleteButton() {

        final JButton delete = new JButton(VIcons.DELETE);

        delete.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (e.getSource().equals(delete)) {

                    cache.remove(cache.getCells(true, true, true, true), true, true);
                    vquery.clear();
                }
            }
        });

        return delete;
    }


    /**
     * Returns the Chartsbutton for choosing the sort of the chart.
     *
     * @return The ChartsButton.
     */
    public JButton createChartsButton() {
        final JButton chartsButton = new JButton(VIcons.CHART);

        final JPopupMenu pop = new JPopupMenu();

        JCheckBoxMenuItem barChart = new JCheckBoxMenuItem("BarChart", VIcons.BARCHART);
        JCheckBoxMenuItem pieChart = new JCheckBoxMenuItem("PieChart", VIcons.PIECHART);

        makeActionListenerCharts(barChart, "barChart");
        makeActionListenerCharts(pieChart, "pieChart");
        ButtonGroup charts = new ButtonGroup();
        barChart.setState(true);
        pieChart.setState(false);
        charts.add(barChart);
        charts.add(pieChart);

        pop.add(barChart);
        pop.add(pieChart);

        chartsButton.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent evt) {
                pop.show(chartsButton, 0, chartsButton.getHeight());
            }

        });

        return chartsButton;
    }


    /**
     * Returns the MeasureButton for selecting the measure.
     *
     * @return MeasureButton for selectin the measure.
     */
    public JButton createMeasureButton() {
        final JButton measureButton = new JButton(VIcons.MEASURE);

        final JPopupMenu pop = new JPopupMenu();

        JCheckBoxMenuItem heads = new JCheckBoxMenuItem("StudentenKöpfe", VIcons.USERK);
        JCheckBoxMenuItem cases = new JCheckBoxMenuItem("StudentenFälle", VIcons.USERF);
        JCheckBoxMenuItem amount = new JCheckBoxMenuItem("KostenBetrag", VIcons.EURO);

        makeActionListenerMeasures(heads, "sos_cube", "SUM(koepfe", "Studenten");
        makeActionListenerMeasures(cases, "sos_cube", "SUM(faelle)", "Studenten");
        makeActionListenerMeasures(amount, "cob_busa_cube", "SUM(betrag)", "Beträge");

        ButtonGroup measuresGroup = new ButtonGroup();
        heads.setState(true);
        cases.setState(false);
        amount.setState(false);
        measuresGroup.add(heads);
        measuresGroup.add(cases);
        measuresGroup.add(amount);

        measureButton.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent evt) {
                pop.show(measureButton, 0, measureButton.getHeight());
            }

        });

        pop.add(heads);
        pop.add(cases);
        pop.add(amount);

        return measureButton;

    }

    /**
     * Returns the complete JGraph.
     *
     * @return The complete JGraph.
     */
    public JGraph getGraph() {
        return graph;
    }
}
