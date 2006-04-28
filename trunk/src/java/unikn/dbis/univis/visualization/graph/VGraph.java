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
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.util.Map;
import java.io.IOException;
import java.sql.*;

import unikn.dbis.univis.visualization.chart.BothCharts;
import unikn.dbis.univis.dnd.VDataReferenceFlavor;
import unikn.dbis.univis.meta.VDimension;
import unikn.dbis.univis.icon.VIcons;
import unikn.dbis.univis.explorer.VExplorer;

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

    /*
     * Different Objects for the graph.
     */
    GraphModel model = new DefaultGraphModel();
    GraphLayoutCache cache = new GraphLayoutCache(model, new VCellViewFactory());
    private DefaultGraphCell[] cells = new DefaultGraphCell[1];
    private JGraphTreeLayout layout = new JGraphTreeLayout();

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
    private String chartName;
    private String chartCheck = "barChart";
    private String sql = "";
    private String select = "";
    private String from = "";
    private String where = "";
    private String and = "";
    private String group = "";
    private String order = "";
    private String cubeName = "sos_cube";
    private String cubeAttribute = "SUM(koepfe)";
    private String xAxis = "Studenten";

    /*
     * Int values for different topics.
     */
    private int dimensionCount = 0;
    private int cellsize = 300;
    private int whichRowA = 0;
    private int whichRowB = 0;

    /**
     * Standard Constructor
     */
    public VGraph() {
        setModel(model);
        setGraphLayoutCache(cache);
        new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
        setEditable(false);
        setMoveable(false);
    }

    /**
     * @param total : total amount of all chartvalues.
     * @return a DefaultGraphCell
     */
    public DefaultGraphCell createVertex(int total) {

        BothCharts chart;
        if (chartCheck.equals("barChart")) {
            chart = new BothCharts(chartName, (DefaultCategoryDataset) dataset, total, xAxis);
        }
        else {
            chart = new BothCharts(chartName, (DefaultPieDataset) dataset, total);
        }

        // Create vertex with the given name
        DefaultGraphCell cell = new DefaultGraphCell(chart);

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
    public void createEdges(DefaultGraphCell source, DefaultGraphCell target) {
        DefaultEdge edge = new DefaultEdge();

        //source.getAttributes().applyValue()

        edge.setSource(source.getChildAt(1));
        edge.setTarget(target.getChildAt(0));
        GraphConstants.setLineEnd(edge.getAttributes(), GraphConstants.ARROW_CLASSIC);
        GraphConstants.setEndFill(edge.getAttributes(), true);
        cache.insert(edge);
    }

    /**
     * @param result : the given resultset from the sql action.
     * @throws SQLException
     */
    public void fillChartData(ResultSet result) throws SQLException {

        double width = this.getWidth();
        double height = this.getHeight();
        width -= cellsize;
        height -= cellsize;
        int total = 0;

        layout.setAlignment(SwingConstants.CENTER);
        layout.setOrientation(SwingConstants.NORTH);

        if (dimensionCount == 0) {
            if (chartCheck.equals("barChart")) {
                dataset = new DefaultCategoryDataset();
                while (result.next()) {
                    ((DefaultCategoryDataset) dataset).addValue(result.getInt(2), result.getString(1), "");
                    total = total + result.getInt(2);
                }
            }
            else {
                dataset = new DefaultPieDataset();
                while (result.next()) {
                    ((DefaultPieDataset) dataset).setValue(result.getString(1), result.getInt(2));
                    total = total + result.getInt(2);
                }
            }

            cells[0] = createVertex(total);
            cells[0].isRoot();
            cache.insert(cells);
        }
        else if (dimensionCount == 1) {
            String buffer = "";
            if (chartCheck.equals("barChart")) {
                dataset = new DefaultCategoryDataset();
                while (result.next()) {
                    if (result.isFirst()) {
                        buffer = result.getString(whichRowA);
                        chartName = buffer;
                    }
                    if (!buffer.equals(result.getString(whichRowA))) {
                        DefaultGraphCell nextCell = createVertex(total);
                        createEdges(cells[0], nextCell);
                        cache.insert(nextCell);
                        dataset = new DefaultCategoryDataset();
                        total = 0;
                        chartName = result.getString(whichRowA);
                        ((DefaultCategoryDataset) dataset).addValue(result.getInt(2), result.getString(whichRowB), "");
                        total = total + result.getInt(2);
                    }
                    else {
                        ((DefaultCategoryDataset) dataset).addValue(result.getInt(2), result.getString(whichRowB), "");
                        total = total + result.getInt(2);
                    }
                    buffer = result.getString(whichRowA);
                    if (result.isLast()) {
                        DefaultGraphCell nextCell = createVertex(total);
                        createEdges(cells[0], nextCell);
                        cache.insert(nextCell);
                    }
                }
            }
            else {
                dataset = new DefaultPieDataset();
                while (result.next()) {
                    if (result.isFirst()) {
                        buffer = result.getString(whichRowA);
                        chartName = buffer;
                    }
                    if (!buffer.equals(result.getString(whichRowA))) {
                        DefaultGraphCell nextCell = createVertex(total);
                        createEdges(cells[0], nextCell);
                        cache.insert(nextCell);
                        dataset = new DefaultPieDataset();
                        total = 0;
                        chartName = result.getString(whichRowA);
                        ((DefaultPieDataset) dataset).setValue(result.getString(whichRowB), result.getInt(2));
                        total = total + result.getInt(2);
                    }
                    else {
                        ((DefaultPieDataset) dataset).setValue(result.getString(whichRowB), result.getInt(2));
                        total = total + result.getInt(2);
                    }
                    buffer = result.getString(whichRowA);
                    if (result.isLast()) {
                        DefaultGraphCell nextCell = createVertex(total);
                        createEdges(cells[0], nextCell);
                        cache.insert(nextCell);
                    }
                }
            }
        }
        else if (dimensionCount == 2) {
            for (int i = 0; i < cache.getNeighbours(cells[0], null, true, true).size(); i++) {
                DefaultGraphCell action = (DefaultGraphCell) cache.getNeighbours(cells[0], null, true, true).get(i);
                if (i == 2) {
                    for (int x = 0; x < 3; x++) {
                        DefaultGraphCell newCell = createVertex(total);
                        createEdges(action, newCell);
                        cache.insert(newCell);
                    }
                }
            }
        }
        JGraphFacade facade = new JGraphFacade(this, cells, true, true, true, true);
        layout.run(facade);
        Map nested = facade.createNestedMap(true, true);
        cache.edit(nested);
        dimensionCount++;
    }

    /**
     * @param vDim : the given VDimension from the drag & drop.
     * @throws SQLException
     */
    public void connection(VDimension vDim) throws SQLException {

        Connection connection = VExplorer.getConnection();
        Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

        if (dimensionCount == 0) {
            String tableName = vDim.getTableName();
            if (vDim.isParentable()) {
                VDimension bluep = searchBluep(vDim);
                String dataName = bluep.getTableName();
                from = " FROM " + tableName + ", " + dataName + ", " + cubeName;
                where = " WHERE " + tableName + ".id = " + dataName + "." + vDim.getJoinable();
                and = " AND " + dataName + ".id = " + cubeName + "." + bluep.getJoinable();
                order = " ORDER BY " + tableName + ".name";
            }
            else {
                from = " FROM " + tableName + ", " + cubeName;
                where = " WHERE " + tableName + ".id = " + cubeName + "." + vDim.getJoinable();
                and = "";
            }
            select = "SELECT " + tableName + ".name, " + cubeAttribute;
            group = " GROUP BY " + tableName + ".name";

            sql = select + from + where + and + group;

        }
        else if (dimensionCount == 1) {
            String tableName = vDim.getTableName();
            if (vDim.isParentable()) {
                whichRowA = 3;
                whichRowB = 1;
                VDimension bluep = searchBluep(vDim);
                String dataName = bluep.getTableName();
                from = from + ", " + tableName + ", " + dataName;
                and = and + " AND " + dataName + "." + vDim.getJoinable() + " = " + tableName + ".id" +
                        " AND " + cubeName + "." + bluep.getJoinable() + " = " + dataName + ".id";
                order = " ORDER BY " + tableName + ".name";

            }
            else {
                whichRowA = 1;
                whichRowB = 3;
                from = from + ", " + tableName;
                and = and + " AND " + cubeName + "." + vDim.getJoinable() + " = " + tableName + ".id";
            }
            select = select + ", " + tableName + ".name";
            group = group + ", " + tableName + ".name";

            sql = select + from + where + and + group + order;
        }

        System.out.println("SQL: " + sql);
        ResultSet result = stmt.executeQuery(sql);
        chartName = vDim.getI18nKey();
        fillChartData(result);
        connection.close();
    }

    /**
     * @param vDim : VDimension from the drag & drop.
     * @return VDimension which is the bluep of the Dimension
     */
    public VDimension searchBluep(VDimension vDim) {

        VDimension parent = (VDimension) vDim.getParent();

        while (parent.isParentable()) {
            parent = (VDimension) parent.getParent();
        }
        return parent;
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
     * @param visualization : JMenu which is created.
     */
    public void createVisualizationSetts(JMenu visualization) {

        JCheckBoxMenuItem barChart = new JCheckBoxMenuItem("BarChart", VIcons.BARCHART);
        JCheckBoxMenuItem pieChart = new JCheckBoxMenuItem("PieChart", VIcons.PIECHART);

        makeActionListenerCharts(barChart, "barChart");
        makeActionListenerCharts(pieChart, "pieChart");
        ButtonGroup charts = new ButtonGroup();
        barChart.setState(true);
        pieChart.setState(false);
        charts.add(barChart);
        charts.add(pieChart);

        JMenu chartsMenu = new JMenu("Charts");
        chartsMenu.add(barChart);
        chartsMenu.add(pieChart);

        JMenu measuresMenu = new JMenu("Measures");
        JCheckBoxMenuItem heads = new JCheckBoxMenuItem("StudentenKöpfe");
        JCheckBoxMenuItem cases = new JCheckBoxMenuItem("StudentenFälle");
        JCheckBoxMenuItem amount = new JCheckBoxMenuItem("KostenBetrag");

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
        measuresMenu.add(heads);
        measuresMenu.add(cases);
        measuresMenu.add(amount);
        visualization.add(chartsMenu);
        visualization.add(measuresMenu);
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
                if (e.getSource().equals(checkBoxMenuItem))

                    cubeName = cube;
                cubeAttribute = measureName;
                xAxis = xAxisName;
            }
        });
    }
}
