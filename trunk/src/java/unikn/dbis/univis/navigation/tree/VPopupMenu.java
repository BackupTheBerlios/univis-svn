package unikn.dbis.univis.navigation.tree;

import javax.swing.*;
import java.awt.*;

/**
 * TODO: document me!!!
 * <p/>
 * <code>VPopupMenu</code>.
 * <p/>
 * User: raedler, weiler
 * Date: 23.05.2006
 * Time: 20:17:15
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public class VPopupMenu extends JPopupMenu {

    private int x;
    private int y;

    /**
     * Constructs a <code>JPopupMenu</code> without an "invoker".
     */
    public VPopupMenu(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Displays the popup menu at the position x,y in the coordinate
     * space of the component invoker.
     *
     * @param invoker the component in whose space the popup menu is to appear
     */
    public void show(Component invoker) {
        super.show(invoker, x, y);
    }
}
