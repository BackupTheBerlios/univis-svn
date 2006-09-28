package unikn.dbis.univis.visualization.chart;

import org.jfree.data.general.PieDataset;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.RingPlot;
import org.jfree.util.Rotation;
import unikn.dbis.univis.visualization.Renderable;

/**
 * TODO: document me!!!
 * <p/>
 * RingChart.
 * <p/>
 * User: raedler, weiler
 * Date: 21.05.2006
 * Time: 22:31:06
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public class RingChart extends AbstractChart<PieDataset> implements Renderable {

    // Total amount of the chart.
    private Integer total;

    /**
     * A pie chart with the chart name as name of the chart and
     * the dataset that contains the data of the pie chart.
     *
     * @param chartName Headline of the chart.
     * @param dataset   Dataset of the Chart.
     */
    public RingChart(String chartName, PieDataset dataset) {
        super(chartName, dataset);
    }

    /**
     * Returns the JFreeChart.
     *
     * @return JFreeChart.
     */
    protected JFreeChart createChart() {
        return ChartFactory.createRingChart(getChartName(), getDataset(), true, false, false);
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
        for (int i = getDataset().getItemCount() - 1; i >= 0; i--) {
            total += getDataset().getValue(i).intValue();
        }
        return total;
    }

    /**
     * Makes the plot.
     */
    protected void plot() {
        RingPlot plot = (RingPlot) getChart().getPlot();
        plot.setStartAngle(290);
        plot.setDirection(Rotation.CLOCKWISE);
        plot.setForegroundAlpha(0.5f);
        plot.setNoDataMessage("No data available");
        plot.setLabelGenerator(null);
        plot.setLegendLabelGenerator(new LabelGenerator(createTotal()));
    }

    /**
     * Gets the number of Items in the chart.
     */
    protected int getItemsCount() {
        return getDataset().getItemCount();
    }
}