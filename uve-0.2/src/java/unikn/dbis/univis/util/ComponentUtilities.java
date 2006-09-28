package unikn.dbis.univis.util;

import unikn.dbis.univis.message.Internationalizable;

import javax.swing.*;
import java.awt.*;

import org.jgraph.JGraph;

/**
 * The <code>ComponentUtilities</code> contains a series of
 * helpful methods to interact/style components.
 * <p/>
 * User: raedler, weiler
 * Date: 11.04.2006
 * Time: 15:52:28
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public class ComponentUtilities {

    /**
     * Centers a component on screen.
     *
     * @param c The component that will be centered.
     */
    public static void centerComponentOnScreen(Component c) {

        // Size of the screen.
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        double width = screenSize.getWidth();
        double height = screenSize.getHeight();

        // Size of the component.
        Dimension size = c.getSize();

        width -= size.getWidth();
        height -= size.getHeight();

        c.setLocation((int) width / 2, (int) height / 2);
    }

    /**
     * A simple minded internationalization change: ask each node in
     * the tree to <code>repaint()</code>.
     */
    public static void repaintComponentTree(Component c) {
        repaintComponentTree0(c);

        // TODO: throws an exception if uncomment JGraph instance.
        if (c instanceof JComponent && !(c instanceof JGraph)) {
            ((JComponent) c).updateUI();
        }

        // Internationalizing of the component.
        if (c instanceof Internationalizable) {
            ((Internationalizable) c).internationalize();
        }

        c.repaint();
    }

    /**
     * A simple minded internationalization change: ask each node in
     * the tree to <code>repaint()</code>.
     * <p/>
     * Recursive calling.
     *
     * @see this#repaintComponentTree(java.awt.Component)
     */
    private static void repaintComponentTree0(Component c) {

        // TODO: throws an exception if uncomment JGraph instance.
        if (c instanceof JComponent && !(c instanceof JGraph)) {
            ((JComponent) c).updateUI();
        }

        // Internationalizing of the component.
        if (c instanceof Internationalizable) {
            ((Internationalizable) c).internationalize();
        }

        Component[] children = null;
        if (c instanceof JMenu) {
            children = ((JMenu) c).getMenuComponents();
        }
        else if (c instanceof Container) {
            children = ((Container) c).getComponents();
        }

        if (children != null) {
            for (Component aChildren : children) {
                repaintComponentTree0(aChildren);
            }
        }

        c.repaint();
    }
}