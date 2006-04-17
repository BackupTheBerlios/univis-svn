package unikn.dbis.univis.visualization.graph;

import org.jgraph.JGraph;

import org.jgraph.graph.*;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.*;
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
    private int dimensionCount;
    public Transferable tr;
    private Stack dimensionStack = new Stack();
    private DefaultCategoryDataset barDataSet;
    private DefaultPieDataset pieDataSet;
    private String chartName;
    private String chartCheck = "barChart";
    private int cellsize = 300;

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

    public DefaultGraphCell createVertex(double x,
                                         double y, boolean raised) {

        DefaultGraphCell cell = new DefaultGraphCell();
        if (chartCheck.equals("barChart")) {
            BothCharts bar3DChart = new BothCharts(chartName, barDataSet);
            // Create vertex with the given name
            cell = new DefaultGraphCell(bar3DChart);
        } else {
            BothCharts pie3DChart = new BothCharts(chartName, pieDataSet);
            // Create vertex with the given name
            cell = new DefaultGraphCell(pie3DChart);
        }
        // Set bounds
        GraphConstants.setBounds(cell.getAttributes(), new Rectangle2D.Double(
                x, y, cellsize, cellsize));
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

    public void fillChartData(ResultSet result) throws SQLException {

        double width = this.getWidth();
        double height = this.getHeight();
        width -= cellsize;
        height -= cellsize;

        layout.setAlignment(SwingConstants.CENTER);
        layout.setOrientation(SwingConstants.NORTH);
        if (dimensionCount == 0) {
            if (chartCheck.equals("barChart")) {
                barDataSet = new DefaultCategoryDataset();
                while (result.next()) {
                    barDataSet.addValue(result.getDouble(2), result.getString(1), "");
                }
            }
            if (chartCheck.equals("pieChart")) {
                pieDataSet = new DefaultPieDataset();
                while (result.next()) {
                    pieDataSet.setValue(result.getString(1), result.getDouble(2));
                }
            }
            cells[0] = createVertex(((int) width / 2), ((int) height / 2), false);
            cells[0].isRoot();
            cache.insert(cells);
        } else if (dimensionCount == 1) {
            for (int i = 0; i < 5; i++) {

                DefaultGraphCell nextCell = createVertex(200, 200, false);
                createEdges(cells[0], nextCell);
                cache.insert(nextCell);
            }
        } else if (dimensionCount == 2) {
            for (int i = 0; i < cache.getNeighbours(cells[0], null, true, true).size(); i++) {
                DefaultGraphCell action = (DefaultGraphCell) cache.getNeighbours(cells[0], null, true, true).get(i);
                if (i == 2) {
                    for (int x = 0; x < 3; x++) {
                        DefaultGraphCell newCell = createVertex(200, 200, false);
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

        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "univis", "UniVis");
        Statement stmt = connection.createStatement();
        String sql = "";


        if (dimensionCount == 0) {
            dimensionStack.push(vDim);
        } else if (dimensionCount == 1) {
            if (!dimensionStack.isEmpty()) {
                VDimension vOld = (VDimension) dimensionStack.pop();
                sql = "SELECT gender, dim_land.name, sum FROM dim_land, (SELECT bluep_geschlecht.name AS gender, sos_cube.nation AS nation, SUM(koepfe) AS sum FROM bluep_geschlecht INNER JOIN sos_cube ON bluep_geschlecht.id = sos_cube.geschlecht GROUP BY bluep_geschlecht.name, sos_cube.nation) AS first WHERE dim_land.id = first.nation GROUP BY dim_land.name, first.gender, first.sum";
            }
        }

        if (!vDim.isParentable() && !vDim.isSummable()) {
            sql = "SELECT " + vDim.getTableName() + ".name, " + "SUM(koepfe) FROM " + vDim.getTableName() + " INNER JOIN sos_cube ON "
                    + vDim.getTableName() + ".id " + " = sos_cube." + vDim.getJoinable() + " GROUP BY " + vDim.getTableName() + ".name";

        } else if (vDim.isParentable() && !vDim.isSummable()) {

            String tableName = vDim.getTableName();
            String dataName = "bluep_nation";

            sql = "SELECT " + tableName + ".name, sum FROM (SELECT " + dataName + "." + vDim.getJoinable() +
                    " AS name, SUM(koepfe) AS sum FROM " + dataName + " INNER JOIN sos_cube ON " + dataName + ".id = sos_cube." +
                    dataName.substring(6) + " GROUP BY " + dataName + "." + vDim.getJoinable() + ") AS complete, " + tableName +
                    " WHERE complete.name = " + tableName + ".id";
        }
        System.out.println("SQL: " + sql);
        ResultSet result = stmt.executeQuery(sql);
        chartName = vDim.getI18nKey();
        fillChartData(result);
        connection.close();
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

        final JCheckBoxMenuItem barChart = new JCheckBoxMenuItem("BarChart");
        barChart.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (e.getSource().equals(barChart))

                    chartCheck = "barChart";
            }
        });

        final JCheckBoxMenuItem pieChart = new JCheckBoxMenuItem("PieChart");
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

        visualization.add(barChart);
        visualization.add(pieChart);
    }
}
