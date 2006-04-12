package unikn.dbis.univis.util;

import java.awt.*;

/**
 * TODO: document me!!!
 * <p/>
 * <code>ComponentUtilities</code>.
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
     * TODO: document me!!!
     *
     * @param c
     */
    public static void centerComponentOnScreen(Component c) {

        // Größe der eingestellten Bildschirmauflösung.
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        double width = screenSize.getWidth();
        double height = screenSize.getHeight();

        // Größe des <code>JFrame</code>.
        Dimension size = c.getSize();

        width -= size.getWidth();
        height -= size.getHeight();

        c.setLocation((int) width / 2, (int) height / 2);
    }
}