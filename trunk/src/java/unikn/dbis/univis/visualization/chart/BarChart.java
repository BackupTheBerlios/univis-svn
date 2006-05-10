package unikn.dbis.univis.visualization.chart;

import org.jfree.data.category.CategoryDataset;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.CategoryPlot;
import unikn.dbis.univis.visualization.Renderable;

/**
 * TODO: document me!!!
 * <p/>
 * BarChart.
 * <p/>
 * User: raedler, weiler
 * Date: 10.05.2006
 * Time: 15:14:42
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public class BarChart extends AbstractChart<CategoryDataset> implements Renderable {

    // Description of the xAxis.
    private String xAxis;

    // Total amount of the chart.
    private Integer total;

    /**
     * A bar chart with the chart name as name of the chart and
     * the dataset that contains the data of the pie chart.
     *
     * @param chartName Headline of the chart.
     * @param dataset   Dataset of the Chart.
     */
    public BarChart(String chartName, CategoryDataset dataset, String xAxis) {
        super(chartName, dataset);
        this.xAxis = xAxis;
    }

    /**
     * @return JFreeChart as BarChart3D.
     */
    protected JFreeChart createChart() {
        return ChartFactory.createBarChart3D(getChartName(), "", xAxis, getDataset(), PlotOrientation.HORIZONTAL, true, false, false);
    }

    /**
     * @return total amount of the chart.
     */
    protected int createTotal() {

        if (total != null) {
            return total;
        }

        total = 0;
        for (int i = getDataset().getRowCount() - 1; i >= 0; i--) {
            total += getDataset().getValue(i, 0).intValue();
        }
        return total;
    }


    /**
     * starts the Bar3DChart.
     */
    public void plot() {

        CategoryPlot plot = (CategoryPlot) getChart().getPlot();
        CategoryAxis axis = plot.getDomainAxis();
        axis.setTickLabelsVisible(false);
        BarRenderer3D renderer = (BarRenderer3D) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        renderer.setLegendItemLabelGenerator(new BarLabelGenerator(createTotal()));
        plot.setNoDataMessage("No data available");
        plot.getDomainAxis().setLabelFont(getLegendFont());
        plot.getRangeAxis().setLabelFont(getLegendFont());
    }
}