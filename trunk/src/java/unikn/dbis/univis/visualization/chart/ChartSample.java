package unikn.dbis.univis.visualization.chart;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.util.Rotation;
import org.jfree.util.TableOrder;

import javax.swing.*;
import java.awt.*;

/**
 * TODO: document me!!!
 * <p/>
 * <code>ChartSample</code>.
 * <p/>
 * User: raedler, weiler
 * Date: 12.04.2006
 * Time: 00:52:50
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public class ChartSample extends JPanel {

    public DefaultPieDataset data = null;

    public JFreeChart chart = null;

    public ChartPanel chartPanel = null;

    /**
     * Creates a new <code>JPanel</code> with a double buffer
     * and a flow layout.
     */
    public ChartSample() {

        data = new DefaultPieDataset();
        data.setValue("abc", 3.);
        data.setValue("cde", 14.3);
        data.setValue("efg", 8.4);
        data.setValue("hij", 3.4);

        chart = ChartFactory.createPieChart3D("DEMO CHART", data, false, false, false);

        chartPanel = new ChartPanel(chart, 150, 150, 100, 80, 200, 160, true, true, true, true, true, true);
        add(chartPanel);

        startChart();
    }

    public void startChart() {

        PiePlot3D plot = (PiePlot3D) chart.getPlot();
        plot.setLabelLinksVisible(false);

        plot.setForegroundAlpha(0.5f);

        add(chartPanel);

    }
}