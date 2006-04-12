package unikn.dbis.univis;

import unikn.dbis.univis.visualization.chart.ChartSample;

import javax.swing.*;
import java.awt.*;

/**
 * TODO: document me!!!
 * <p/>
 * <code>EmptyJFrame</code>.
 * <p/>
 * User: raedler, weiler
 * Date: 12.04.2006
 * Time: 01:00:42
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public class EmptyJFrame extends JFrame {

    public static void main(String[] args) {
        new EmptyJFrame();
    }

    /**
     * Constructs a new frame that is initially invisible.
     * <p/>
     * This constructor sets the component's locale property to the value
     * returned by <code>JComponent.getDefaultLocale</code>.
     *
     * @throws java.awt.HeadlessException if GraphicsEnvironment.isHeadless()
     *                                    returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see java.awt.Component#setSize
     * @see java.awt.Component#setVisible
     * @see javax.swing.JComponent#getDefaultLocale
     */
    public EmptyJFrame() throws HeadlessException {
        getContentPane().add(new ChartSample());

        pack();

        setVisible(true);
    }
}