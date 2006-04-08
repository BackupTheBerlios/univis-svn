package unikn.dbis.univis.gui;

import javax.swing.*;
import java.awt.*;

/**
 * TODO: document me!!!
 * <p/>
 * VisualCube.
 * <p/>
 * User: raedler, weiler
 * Date: 08.04.2006
 * Time: 19:19:30
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public class VisualCube implements Icon {

     private Color cubeColor;

    public VisualCube(Color cubeColor) {
        this.cubeColor = cubeColor;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {

        g.setColor(Color.BLACK);
        g.drawRect(0, 0, 14, 14);

        g.setColor(cubeColor);
        g.fill3DRect(1, 1, 13, 13, true);
    }

    public int getIconWidth() {
        return 15;
    }

    public int getIconHeight() {
        return 15;
    }
}