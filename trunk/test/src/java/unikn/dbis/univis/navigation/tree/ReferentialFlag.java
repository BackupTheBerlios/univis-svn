package unikn.dbis.univis.navigation.tree;

import javax.swing.*;
import java.awt.*;

/**
 * TODO: document me!!!
 * <p/>
 * <code>ReferentialFlag</code>.
 * <p/>
 * User: raedler
 * Date: 03.11.2005
 * Time: 14:46:40
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Revision$
 * @since UniVis Explorer 0.1
 */
public class ReferentialFlag implements Icon {

    private Color flagColor;

    public ReferentialFlag(Color flagColor) {
        this.flagColor = flagColor;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {

        g.setColor(Color.BLACK);
        g.drawRect(1, 1, 3, 11);

        g.setColor(flagColor);
        g.fillRect(2, 2, 2, 10);
    }

    public int getIconWidth() {
        return 5;
    }

    public int getIconHeight() {
        return 12;
    }

    /*
    /**
     * @@see JComponent#paintComponent(java.awt.Graphics)
     *
    protected void paintComponent(Graphics g) {

        g.setColor(Color.BLACK);
        g.drawRect(1, 1, 3, 11);

        g.setColor(flagColor);
        g.fillRect(2, 2, 2, 10);
    }
    */
}