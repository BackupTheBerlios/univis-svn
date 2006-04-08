package unikn.dbis.univis.gui;

import javax.swing.*;
import java.awt.*;

/**
 * TODO: document me!!!
 * <p/>
 * Visual.
 * <p/>
 * User: raedler, weiler
 * Date: 08.04.2006
 * Time: 18:38:08
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public class Visual implements Icon {


    public Visual() {
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {

        g.setColor(Color.GREEN);
        g.fillOval(0, 0, 300, 300);

    }

    public int getIconWidth() {
        return 300;
    }

    public int getIconHeight() {
        return 300;
    }
}