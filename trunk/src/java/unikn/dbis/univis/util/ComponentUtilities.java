package unikn.dbis.univis.util;

import java.awt.*;

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
}