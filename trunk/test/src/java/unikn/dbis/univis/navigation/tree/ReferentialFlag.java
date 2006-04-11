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
public class ReferentialFlag extends JComponent {

    private Color flagColor;

    public ReferentialFlag(Color flagColor) {
        this.flagColor = flagColor;
    }

    public void paintComponent(Graphics g) {

        g.setColor(Color.BLACK);
        g.drawRect(1, 1, 3, 11);

        g.setColor(flagColor);
        g.fillRect(2, 2, 2, 10);
    }

    public int getWidth() {
        return 5;
    }

    public int getHeight() {
        return 12;
    }

    /**
     * If the <code>preferredSize</code> has been set to a
     * non-<code>null</code> value just returns it.
     * If the UI delegate's <code>getPreferredSize</code>
     * method returns a non <code>null</code> value then return that;
     * otherwise defer to the component's layout manager.
     *
     * @return the value of the <code>preferredSize</code> property
     * @see #setPreferredSize
     * @see javax.swing.plaf.ComponentUI
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(getWidth(), getHeight());
    }

    /**
     * Returns the size of this component in the form of a
     * <code>VDimension</code> object. The <code>height</code>
     * field of the <code>VDimension</code> object contains
     * this component's height, and the <code>width</code>
     * field of the <code>VDimension</code> object contains
     * this component's width.
     *
     * @return a <code>VDimension</code> object that indicates the
     *         size of this component
     * @see #setSize
     * @since JDK1.1
     */
    @Override
    public Dimension getSize() {
        return new Dimension(getWidth(), getHeight());
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