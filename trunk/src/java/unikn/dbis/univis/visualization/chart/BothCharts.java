package unikn.dbis.univis.visualization.chart;

import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.category.DefaultCategoryDataset;
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
import org.jfree.util.SortOrder;

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

    public DefaultPieDataset data = null;

    public DefaultCategoryDataset dataset = null;

    public JFreeChart chart = null;

    public ChartPanel chartPanel = null;

    public String identify;

    private Font legendFont = new Font("Tahoma", Font.PLAIN, 10);

    private ArrayList l = new ArrayList();

    public BothCharts(String chartName, DefaultPieDataset data, int total) {
        this.data = data;
        chart = ChartFactory.createPieChart3D(chartName, data, true, false, false);
        LegendTitle legend = chart.getLegend();
        if (legend != null) {
            legend.setItemFont(legendFont);
        }
        setSubtitles(total, legend);
        chartPanel = new ChartPanel(chart);
        if (data.getItemCount() >= 50) {
            makeScale();
        }
        startPie3DChart();
    }

    public BothCharts(String chartName, DefaultCategoryDataset dataset, int total, String xAxis) {
        this.dataset = dataset;
        chart = ChartFactory.createBarChart3D(chartName, "", xAxis, dataset, PlotOrientation.HORIZONTAL, true, false, false);
        LegendTitle legend = chart.getLegend();
        if (legend != null) {
            legend.setItemFont(legendFont);
        }
        setSubtitles(total, legend);
        chartPanel = new ChartPanel(chart);
        if (dataset.getColumnCount() >= 50) {
            makeScale();
        }
        startBar3DChart();
    }

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

    public void makeScale() {

        chartPanel.setMinimumDrawHeight(600);
        chartPanel.setMinimumDrawWidth(600);
        chartPanel.scale(new Rectangle2D.Double(0, 0, 300, 300));

    }

    public void setSubtitles(int total, LegendTitle legend) {

        Integer totalHelp = total;
        String totalName = "Total: " + totalHelp.toString();
        TextTitle totalTitle = new TextTitle(totalName);
        l.add(totalTitle);
        l.add(legend);
        chart.setSubtitles(l);

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