package unikn.dbis.univis.icon;

import javax.swing.*;
import java.awt.*;

/**
 * TODO: document me!!!
 * <p/>
 * <code>VCubeFlagIcon</code>.
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
public class VCubeFlagIcon implements Icon {

    private Color flagColor;

    public VCubeFlagIcon(Color flagColor) {
        this.flagColor = flagColor;
    }

    /**
     * Draw the icon at the specified location.  Icon implementations
     * may use the Component argument to get properties useful for
     * painting, e.g. the foreground or background color.
     */
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(flagColor);
        g.fill3DRect(x, y, 7, 7, true);

        g.setColor(Color.BLACK);
        g.draw3DRect(x, y, 7, 7, true);
    }

    /**
     * Returns the icon's width.
     *
     * @return an int specifying the fixed width of the icon.
     */
    public int getIconWidth() {
        return 10;
    }

    /**
     * Returns the icon's height.
     *
     * @return an int specifying the fixed height of the icon.
     */
    public int getIconHeight() {
        return 10;
    }
}