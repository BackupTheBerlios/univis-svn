package unikn.dbis.univis.visualization.chart;

import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.util.Rotation;

import javax.swing.*;
import java.awt.*;

/**
 * TODO: document me!!!
 * <p/>
 * BothCharts.
 * <p/>
 * User: raedler, weiler
 * Date: 14.04.2006
 * Time: 23:14:52
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public class BothCharts extends JPanel {

    public DefaultPieDataset data = null;

    public DefaultCategoryDataset dataset = null;

    public JFreeChart chart = null;

    public ChartPanel chartPanel = null;

    public String identify;

    public BothCharts(String chartName, DefaultPieDataset data) {
        this.data = data;
        chart = ChartFactory.createPieChart3D(chartName, data, false, false, false);

        chartPanel = new ChartPanel(chart, 150, 150, 100, 80, 200, 160, true, true, true, true, true, true);
        add(chartPanel);

        startPie3DChart();
    }

    public BothCharts(String chartName, DefaultCategoryDataset dataset) {
        this.dataset = dataset;
        chart = ChartFactory.createBarChart3D(chartName,"", "", dataset, PlotOrientation.HORIZONTAL, true, true, false);

        chartPanel = new ChartPanel(chart, 150, 150, 100, 80, 200, 160, true, true, true, true, true, true);
        add(chartPanel);

        startBar3DChart();
    }

    public void startPie3DChart() {

        PiePlot3D plot = (PiePlot3D) chart.getPlot();
        plot.setStartAngle(290);
        plot.setDirection(Rotation.CLOCKWISE);
        plot.setForegroundAlpha(0.5f);
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        plot.setNoDataMessage("No data available");
        plot.setCircular(false);
        plot.setLabelGap(0.02);

        add(chartPanel);
        setVisible(true);

    }

    public void startBar3DChart() {

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryAxis axis = plot.getDomainAxis();
        axis.setTickLabelsVisible(false);
        BarRenderer3D renderer = (BarRenderer3D) plot.getRenderer();
        renderer.setDrawBarOutline(false);

        add(chartPanel);
        setVisible(true);

    }

    public DefaultPieDataset getData() {
        return data;
    }

    public void setData(DefaultPieDataset data) {
        this.data = data;
    }

    public DefaultCategoryDataset getDataset() {
        return dataset;
    }

    public void setDataset(DefaultCategoryDataset dataset) {
        this.dataset = dataset;
    }

    public String getIdentify() {
        return identify;
    }

    public void setIdentify(String identify) {
        this.identify = identify;
    }
}