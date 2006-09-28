package unikn.dbis.univis.navigation.filter;

import javax.swing.*;
import java.awt.*;

/**
 * TODO: document me!!!
 * <p/>
 * <code>FilterPopupMenu</code>.
 * <p/>
 * User: raedler
 * Date: 27.09.2006
 * Time: 14:35:18
 *
 * @author Roman R&auml;dle
 * @version $Revision$
 * @since UniVis Explorer 0.3
 */
public class FilterPopupMenu extends JPopupMenu {

    /**
     * Constructs a <code>JPopupMenu</code> without an "invoker".
     *
     * @param headline
     */
    public FilterPopupMenu(String headline) {

        JLabel header = new JLabel(headline);

        Font font = header.getFont();
        header.setFont(font.deriveFont(Font.BOLD, font.getSize() + 2));

        add(header);
        add(new JPopupMenu.Separator());
    }
}
