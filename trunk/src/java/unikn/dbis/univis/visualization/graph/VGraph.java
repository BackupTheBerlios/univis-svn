package unikn.dbis.univis.visualization.graph;

import org.jgraph.JGraph;

import org.jgraph.graph.*;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.util.Map;
import java.util.Stack;
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

    GraphModel model = new DefaultGraphModel();
    GraphLayoutCache cache = new GraphLayoutCache(model, new VCellViewFactory());
    private DefaultGraphCell[] cells = new DefaultGraphCell[1];
    private JGraphTreeLayout layout = new JGraphTreeLayout();


    public Transferable tr;

    private DefaultCategoryDataset barDataSet;
    private DefaultPieDataset pieDataSet;

    private String chartName;
    private String chartCheck = "barChart";

    private String sql = "";
    private String select = "";
    private String from = "";
    private String where = "";
    private String and = "";
    private String group = "";
    private String order = "";

    private String xAxis = "Studenten";

    private int dimensionCount = 0;
    private int cellsize = 300;

    private int whichRowA = 0;
    private int whichRowB = 0;

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

    public DefaultGraphCell createVertex(double x, double y, Object dataSet, int total) {

        DefaultGraphCell cell = new DefaultGraphCell();
        if (chartCheck.equals("barChart")) {
            BothCharts bar3DChart = new BothCharts(chartName, (DefaultCategoryDataset) dataSet, total, xAxis);
            // Create vertex with the given name
            cell = new DefaultGraphCell(bar3DChart);
        } else {
            BothCharts pie3DChart = new BothCharts(chartName, (DefaultPieDataset) dataSet, total);
            // Create vertex with the given name
            cell = new DefaultGraphCell(pie3DChart);
        }
        // Set bounds
        GraphConstants.setBounds(cell.getAttributes(), new Rectangle2D.Double(
                x, y, cellsize, cellsize));

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
                barDataSet = new DefaultCategoryDataset();
                while (result.next()) {
                    barDataSet.addValue(result.getInt(2), result.getString(1), "");
                    total = total + result.getInt(2);
                }
                cells[0] = createVertex((int) (width / 2), (int) (height / 2), barDataSet, total);
                cells[0].isRoot();
                cache.insert(cells);
            } else {
                pieDataSet = new DefaultPieDataset();
                while (result.next()) {
                    pieDataSet.setValue(result.getString(1), result.getInt(2));
                    total = total + result.getInt(2);
                }
                cells[0] = createVertex((int) (width / 2), (int) (height / 2), pieDataSet, total);
                cells[0].isRoot();
                cache.insert(cells);
            }


        } else if (dimensionCount == 1) {
            String buffer = "";
            if (chartCheck.equals("barChart")) {
                barDataSet = new DefaultCategoryDataset();
                while (result.next()) {
                    if (result.isFirst()) {
                        buffer = result.getString(whichRowA);
                        chartName = buffer;
                    }
                    if (!buffer.equals(result.getString(whichRowA))) {
                        DefaultGraphCell nextCell = createVertex(200, 200, barDataSet, total);
                        createEdges(cells[0], nextCell);
                        cache.insert(nextCell);
                        barDataSet = new DefaultCategoryDataset();
                        total = 0;
                        chartName = result.getString(whichRowA);
                        barDataSet.addValue(result.getInt(2), result.getString(whichRowB), "");
                        total = total + result.getInt(2);
                    } else {
                        barDataSet.addValue(result.getInt(2), result.getString(whichRowB), "");
                        total = total + result.getInt(2);
                    }
                    buffer = result.getString(whichRowA);
                    if (result.isLast()) {
                        DefaultGraphCell nextCell = createVertex(200, 200, barDataSet, total);
                        createEdges(cells[0], nextCell);
                        cache.insert(nextCell);
                    }
                }
            } else {
                pieDataSet = new DefaultPieDataset();
                while (result.next()) {
                    if (result.isFirst()) {
                        buffer = result.getString(whichRowA);
                        chartName = buffer;
                    }
                    if (!buffer.equals(result.getString(whichRowA))) {
                        DefaultGraphCell nextCell = createVertex(200, 200, pieDataSet, total);
                        createEdges(cells[0], nextCell);
                        cache.insert(nextCell);
                        pieDataSet = new DefaultPieDataset();
                        total = 0;
                        chartName = result.getString(whichRowA);
                        pieDataSet.setValue(result.getString(whichRowB), result.getInt(2));
                        total = total + result.getInt(2);
                    } else {
                        pieDataSet.setValue(result.getString(whichRowB), result.getInt(2));
                        total = total + result.getInt(2);
                    }
                    buffer = result.getString(whichRowA);
                    if (result.isLast()) {
                        DefaultGraphCell nextCell = createVertex(200, 200, pieDataSet, total);
                        createEdges(cells[0], nextCell);
                        cache.insert(nextCell);
                    }
                }
            }
        } else if (dimensionCount == 2) {
            for (int i = 0; i < cache.getNeighbours(cells[0], null, true, true).size(); i++) {
                DefaultGraphCell action = (DefaultGraphCell) cache.getNeighbours(cells[0], null, true, true).get(i);
                if (i == 2) {
                    for (int x = 0; x < 3; x++) {
                        DefaultGraphCell newCell = createVertex(200, 200, pieDataSet, total);
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

    public void connection(VDimension vDim) throws SQLException {

        Connection connection = VExplorer.getConnection();
        Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

        if (dimensionCount == 0) {
            String tableName = vDim.getTableName();
            if (vDim.isParentable()) {
                VDimension bluep = searchBluep(vDim);
                String dataName = bluep.getTableName();
                from = " FROM " + tableName + ", " + dataName + ", sos_cube";
                where = " WHERE " + tableName + ".id = " + dataName + "." + vDim.getJoinable();
                and = " AND " + dataName + ".id = sos_cube." + bluep.getJoinable();
            } else {
                from = " FROM " + tableName + ", " + "sos_cube";
                where = " WHERE " + tableName + ".id = " + "sos_cube." + vDim.getJoinable();
                and = "";
            }
            select = "SELECT " + tableName + ".name, SUM(koepfe)";
            group = " GROUP BY " + tableName + ".name";

            sql = select + from + where + and + group;

        } else if (dimensionCount == 1) {
            String tableName = vDim.getTableName();
            if (vDim.isParentable()) {
                whichRowA = 3;
                whichRowB = 1;
                VDimension bluep = searchBluep(vDim);
                String dataName = bluep.getTableName();
                from = from + ", " + tableName + ", " + dataName;
                and = and + " AND " + dataName + "." + vDim.getJoinable() + " = " + tableName + ".id" +
                        " AND sos_cube." + bluep.getJoinable() + " = " + dataName + ".id";

            } else {
                whichRowA = 1;
                whichRowB = 3;
                from = from + ", " + tableName;
                and = and + " AND sos_cube." + vDim.getJoinable() + " = " + tableName + ".id";
            }
            select = select + ", " + tableName + ".name";
            group = group + ", " + tableName + ".name";
            order = " ORDER BY dim_kontinent.name";

            sql = select + from + where + and + group + order;
        }

        System.out.println("SQL: " + sql);
        //System.out.println("Select: " + select);
        ResultSet result = stmt.executeQuery(sql);
        chartName = vDim.getI18nKey();
        fillChartData(result);
        connection.close();
    }

    public VDimension searchBluep(VDimension vDim) {

        VDimension parent = (VDimension) vDim.getParent();

        while (parent.isParentable()) {
            parent = (VDimension) parent.getParent();
        }
        return parent;
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

            try {
                connection(vDim);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        dtde.dropComplete(true);
    }

    public void createVisualizationSetts(JMenu visualization) {

        final JCheckBoxMenuItem barChart = new JCheckBoxMenuItem("BarChart", VIcons.BARCHART);
        barChart.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (e.getSource().equals(barChart))

                    chartCheck = "barChart";
            }
        });

        final JCheckBoxMenuItem pieChart = new JCheckBoxMenuItem("PieChart", VIcons.PIECHART);
        pieChart.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (e.getSource().equals(pieChart))

                    chartCheck = "pieChart";
            }
        });

        ButtonGroup charts = new ButtonGroup();
        barChart.setState(true);
        pieChart.setState(false);
        charts.add(barChart);
        charts.add(pieChart);

        JMenu chartsMenu = new JMenu("Charts");
        chartsMenu.add(barChart);
        chartsMenu.add(pieChart);

        JMenu measuresMenu = new JMenu("Measures");
        final JCheckBoxMenuItem heads = new JCheckBoxMenuItem("StudentenKöpfe");
        final JCheckBoxMenuItem cases = new JCheckBoxMenuItem("StudentenFälle");
        final JCheckBoxMenuItem amount = new JCheckBoxMenuItem("KostenBetrag");

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
}
