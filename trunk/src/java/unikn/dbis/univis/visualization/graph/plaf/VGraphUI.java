package unikn.dbis.univis.visualization.graph.plaf;

import org.jgraph.plaf.basic.BasicGraphUI;

import javax.swing.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

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
        return new MouseListener() {
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

                    System.out.println("cell.getCellId() = " + cell.getCellId());

                    JPopupMenu menu = new JPopupMenu();
                    menu.add(new JButton(cell.getCellId()));
                    menu.show(graph, e.getX(), e.getY());
                }
            }

            /**
             * Invoked when a mouse button has been pressed on a component.
             */
            public void mousePressed(MouseEvent e) {
                System.out.println("VGraphUI.mousePressed");
            }

            /**
             * Invoked when a mouse button has been released on a component.
             */
            public void mouseReleased(MouseEvent e) {
                System.out.println("VGraphUI.mouseReleased");
            }

            /**
             * Invoked when the mouse enters a component.
             */
            public void mouseEntered(MouseEvent e) {
                System.out.println("VGraphUI.mouseEntered");
            }

            /**
             * Invoked when the mouse exits a component.
             */
            public void mouseExited(MouseEvent e) {
                System.out.println("VGraphUI.mouseExited");
            }
        };
    }
}
