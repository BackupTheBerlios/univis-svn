package unikn.dbis.univis.visualization.chart;

import org.jfree.data.category.CategoryDataset;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.renderer.category.AreaRenderer;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import unikn.dbis.univis.visualization.Renderable;

/**
 * Created by IntelliJ IDEA.
 * User: Paul Chef
 * Date: 20.05.2006
 * Time: 21:31:11
 * To change this template use File | Settings | File Templates.
 */
public class AreaChart extends AbstractChart<CategoryDataset> implements Renderable {

    // Description of the xAxis.
    private String xAxis;

    // Total amount of the chart.
    private Integer total;

    /**
     * A pie chart with the chart name as name of the chart and
     * the dataset that contains the data of the pie chart.
     *
     * @param chartName Headline of the chart.
     * @param dataset   Dataset of the Chart.
     */
    public AreaChart(String chartName, CategoryDataset dataset, String xAxis) {
        super(chartName, dataset);
        this.xAxis = xAxis;
    }

    /**
     * Returns the JFreeChart.
     *
     * @return JFreeChart.
     */
    protected JFreeChart createChart() {
        return ChartFactory.createAreaChart(getChartName(), "", xAxis, getDataset(), PlotOrientation.VERTICAL, true, false, false);
    }

    /**
     * Returns the total amount of the chart.
     *
     * @return Total amount of the chart.
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
     * Makes the plot.
     */
    protected void plot() {
        CategoryPlot plot = (CategoryPlot) getChart().getPlot();
        CategoryAxis axis = plot.getDomainAxis();
        plot.setForegroundAlpha(0.5f);
        axis.setTickLabelsVisible(false);
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
        AreaRenderer renderer = (AreaRenderer) plot.getRenderer();
        renderer.setLegendItemLabelGenerator(new LabelGenerator(createTotal()));
        plot.setNoDataMessage("No data available");
        plot.getDomainAxis().setLabelFont(getLegendFont());
        plot.getRangeAxis().setLabelFont(getLegendFont());
    }
}
