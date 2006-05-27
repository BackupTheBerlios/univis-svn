package unikn.dbis.univis.visualization.graph.plaf;

import org.jgraph.plaf.basic.BasicGraphUI;
import org.jfree.chart.*;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.title.Title;

import javax.swing.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import unikn.dbis.univis.visualization.graph.VGraphCell;

/**
 * TODO: document me!!!
 * <p/>
 * <code>VGraphUI</code>.
 * <p/>
 * User: raedler, weiler
 * Date: 22.05.2006
 * Time: 23:02:10
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public class VGraphUI extends BasicGraphUI {

    /**
     * Creates the listener responsible for calling the correct handlers based
     * on mouse events, and to select invidual cells.
     */
    protected MouseListener createMouseListener() {
        return new MouseAdapter() {
            /**
             * Invoked when the mouse button has been clicked (pressed
             * and released) on a component.
             */
            public void mouseClicked(MouseEvent e) {
                System.out.println("VGraphUI.mouseClicked");
                System.out.println("graph.getComponentAt(e.getPoint()) = " + graph.getComponentAt(e.getPoint()));

                Object o = graph.getFirstCellForLocation(e.getX(), e.getY());

                if (o instanceof VGraphCell) {
                    VGraphCell cell = (VGraphCell) o;

                    JPopupMenu menu = new JPopupMenu();

                    ChartPanel chartPanel = (ChartPanel) cell.getUserObject();
                    JFreeChart chart = chartPanel.getChart();

                    LegendItemCollection collect = chart.getPlot().getLegendItems();

                    for (Iterator iter = collect.iterator(); iter.hasNext();) {
                        LegendItem item = (LegendItem) iter.next();
                        menu.add(item.getLabel());
                        System.out.println(item.getLabel());
                    }
                    menu.show(graph, e.getX(), e.getY());
                }
            }
        };
    }
}
