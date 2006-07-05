package unikn.dbis.univis.visualization.chart;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;

import javax.swing.*;
import java.util.Collection;

/**
 * TODO: document me!!!
 * <p/>
 * <code>VChartPanel</code>.
 * <p/>
 * User: raedler, weiler
 * Date: 25.05.2006
 * Time: 00:03:09
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public class VChartPanel extends ChartPanel {

    // The legend items of the chart panel.
    private Collection<LegendItem> legendItems;

    // The boolean value if PopUp is needed.
    private boolean showPopUp = true;

    /**
     * Constructs a panel that displays the specified chart.
     *
     * @param chart the chart.
     */
    public VChartPanel(JFreeChart chart) {
        super(chart);
    }

    /**
     * Constructs a panel containing a chart.
     *
     * @param chart     the chart.
     * @param useBuffer a flag controlling whether or not an off-screen buffer
     *                  is used.
     */
    public VChartPanel(JFreeChart chart, boolean useBuffer) {
        super(chart, useBuffer);
    }

    /**
     * Returns the legend items of the chart panal.
     *
     * @return The legend items.
     */
    public Collection<LegendItem> getLegendItems() {
        return legendItems;
    }

    /**
     * Sets the legend items of the chart panel.
     *
     * @param legendItems The legend items.
     */
    public void setLegendItems(Collection<LegendItem> legendItems) {
        this.legendItems = legendItems;
    }


    /**
     * Returns the boolean value if Popup is needed.
     *
     * @return boolean Boolean Value if Popup is needed.
     */
    public boolean isShowPopUp() {
        return showPopUp;
    }

    /**
     * Sets the boolean value if popup is needed.
     *
     * @param showPopUp The Boolean value
     */
    public void setShowPopUp(boolean showPopUp) {
        this.showPopUp = showPopUp;
    }

    /**
     * Creates a popup menu for the panel.
     *
     * @param properties include a menu item for the chart property editor.
     * @param save       include a menu item for saving the chart.
     * @param print      include a menu item for printing the chart.
     * @param zoom       include menu items for zooming.
     * @return The popup menu.
     */
    @Override
    public JPopupMenu createPopupMenu(boolean properties, boolean save, boolean print, boolean zoom) {
        return super.createPopupMenu(properties, save, print, zoom);
    }
}