package unikn.dbis.univis.visualization.chart;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;

import javax.swing.*;

/**
 * TODO: document me!!!
 * <p/>
 * <code>Bar3DChart</code>.
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
public class Bar3DChart extends JPanel {

    public DefaultCategoryDataset dataset = null;

    public JFreeChart chart = null;

    public ChartPanel chartPanel = null;

    public String identify;

    public void startChart() {

        CategoryPlot plot = (CategoryPlot) chart.getPlot();

        CategoryAxis axis = plot.getDomainAxis();

        axis.setTickLabelsVisible(false);
        
        BarRenderer3D renderer = (BarRenderer3D) plot.getRenderer();
        renderer.setDrawBarOutline(false);

        add(chartPanel);
        setVisible(true);

    }

    public DefaultCategoryDataset getDataset() {
        return dataset;
    }

    public void setDataset(DefaultCategoryDataset dataset) {
        this.dataset = dataset;
    }

    public void setChart(JFreeChart chart) {
        this.chart = chart;
        chartPanel = new ChartPanel(chart, 400, 350, 500, 500, 500, 500, false, false, false, false ,false ,false);
        add(chartPanel);
    }

    public void removeChart() {
        remove(chartPanel);
    }

    public String getIdentify() {
        return identify;
    }

    public void setIdentify(String identify) {
        this.identify = identify;
    }
}
