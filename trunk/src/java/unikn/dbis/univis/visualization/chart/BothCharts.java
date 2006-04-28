package unikn.dbis.univis.visualization.chart;

import org.jfree.data.general.PieDataset;
import org.jfree.data.category.CategoryDataset;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.util.Rotation;

import javax.swing.*;
import java.awt.geom.Rectangle2D;
import java.awt.*;
import java.util.ArrayList;

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

    /*
    * Chart itself.
    */
    public JFreeChart chart = null;

    /*
     * ChartPanel on which is the chart.
     */
    public ChartPanel chartPanel = null;

    /*
     * Font.
     */
    private Font legendFont = new Font("Tahoma", Font.PLAIN, 10);

    /*
     * ArrayList for the subtitles.
     */
    private ArrayList l = new ArrayList();

    /**
     * @param chartName : Headline of the chart.
     * @param dataset      : Dataset of the Chart.
     * @param total     : total Amount of the Chart.
     */
    public BothCharts(String chartName, PieDataset dataset, int total) {
        chart = ChartFactory.createPieChart3D(chartName, dataset, true, false, false);
        LegendTitle legend = chart.getLegend();
        if (legend != null) {
            legend.setItemFont(legendFont);
        }
        setSubtitles(total, legend);
        chartPanel = new ChartPanel(chart);
        if (dataset.getItemCount() >= 50) {
            makeScale();
        }
        startPie3DChart();
    }

    /**
     * @param chartName : Headline of the chart.
     * @param dataset   : Dataset of the chart.
     * @param total     : total Amount of the chart.
     * @param xAxis     : Description of the xAxis on the Barchart.
     */
    public BothCharts(String chartName, CategoryDataset dataset, int total, String xAxis) {
        chart = ChartFactory.createBarChart3D(chartName, "", xAxis, dataset, PlotOrientation.HORIZONTAL, true, false, false);
        LegendTitle legend = chart.getLegend();
        if (legend != null) {
            legend.setItemFont(legendFont);
        }
        setSubtitles(total, legend);
        chartPanel = new ChartPanel(chart);

        if (dataset.getRowCount() >= 50) {
            makeScale();
        }
        startBar3DChart();
    }

    /**
     * starts the Pie3DChart.
     */
    public void startPie3DChart() {

        PiePlot3D plot = (PiePlot3D) chart.getPlot();
        plot.setStartAngle(290);
        plot.setDirection(Rotation.CLOCKWISE);
        plot.setForegroundAlpha(0.5f);
        plot.setNoDataMessage("No data available");
        plot.setLabelGenerator(null);
        setPreferredSize(new Dimension(300, 300));
        setLayout(new BorderLayout());
        add(chartPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    /**
     * starts the Bar3DChart.
     */
    public void startBar3DChart() {

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryAxis axis = plot.getDomainAxis();
        axis.setTickLabelsVisible(false);
        BarRenderer3D renderer = (BarRenderer3D) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        plot.setNoDataMessage("No data available");
        plot.getDomainAxis().setLabelFont(legendFont);
        plot.getRangeAxis().setLabelFont(legendFont);
        setPreferredSize(new Dimension(300, 300));
        setLayout(new BorderLayout());
        add(chartPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    /**
     * scales the charts which have too much values.
     */
    public void makeScale() {

        chartPanel.setMinimumDrawHeight(600);
        chartPanel.setMinimumDrawWidth(600);
        chartPanel.scale(new Rectangle2D.Double(0, 0, 300, 300));
    }

    /**
     * Sets the subtitels and the legend.
     *
     * @param total  : total amount of the chart.
     * @param legend : the legend of the chart
     */
    public void setSubtitles(int total, LegendTitle legend) {

        Integer totalHelp = total;
        String totalName = "Total: " + totalHelp.toString();
        TextTitle totalTitle = new TextTitle(totalName);
        l.add(totalTitle);
        l.add(legend);
        chart.setSubtitles(l);
    }
}