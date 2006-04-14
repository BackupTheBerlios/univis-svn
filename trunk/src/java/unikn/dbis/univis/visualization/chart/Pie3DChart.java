package unikn.dbis.univis.visualization.chart;

import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.util.Rotation;

import javax.swing.*;
import java.awt.*;

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
public class Pie3DChart extends JPanel {

    public DefaultPieDataset data = null;

    public JFreeChart chart = null;

    public ChartPanel chartPanel = null;

    public String identify;

    public Pie3DChart(DefaultPieDataset data, String chartName) {
        this.data = data;

        chart = ChartFactory.createPieChart3D(chartName, data, false, false, false);

        chartPanel = new ChartPanel(chart, 150, 150, 100, 80, 200, 160, true, true, true, true, true, true);
        add(chartPanel);

        startChart();
    }

    public void startChart() {

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

    public DefaultPieDataset getData() {
        return data;
    }

    public void setData(DefaultPieDataset data) {
        this.data = data;
    }

    public String getIdentify() {
        return identify;
    }

    public void setIdentify(String identify) {
        this.identify = identify;
    }
}
