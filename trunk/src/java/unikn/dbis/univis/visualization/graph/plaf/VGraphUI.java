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
import java.awt.*;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import unikn.dbis.univis.visualization.graph.VGraphCell;
import unikn.dbis.univis.visualization.chart.VChartPanel;

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

                    VChartPanel chartPanel = (VChartPanel) cell.getUserObject();

                    if (chartPanel.isShowPopUp()) {

                        LegendItemCollection collect = chartPanel.getChart().getPlot().getLegendItems();
                        JMenu first = new JMenu("Erste");
                        int checker = 0;

                        for (Iterator iter = collect.iterator(); iter.hasNext();) {
                            LegendItem item = (LegendItem) iter.next();
                            checker++;
                            first.add(new JMenuItem(item.getLabel()));
                            if ((checker % 40) == 0) {
                                menu.add(first);
                                first = new JMenu("Weitere");
                            }
                            if (!iter.hasNext()) {
                                menu.add(first);
                            }
                        }
                        menu.show(graph, e.getX(), e.getY());
                    }
                }
            }
        };
    }
}
