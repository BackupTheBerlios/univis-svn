package unikn.dbis.univis.visualization.graph;

import unikn.dbis.univis.icon.VIcons;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

/**
 * TODO: document me!!!
 * <p/>
 * <code>VHintButton</code>.
 * <p/>
 * User: raedler, weiler
 * Date: 04.07.2006
 * Time: 23:53:54
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public class VHintButton extends JButton {

    /**
     * Creates a button with an icon.
     *
     * @param icon the Icon image to display on the button
     */
    public VHintButton(Icon icon) {
        super(icon);

        setBackground(Color.WHITE);
        setBorderPainted(false);

        setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
    }
}