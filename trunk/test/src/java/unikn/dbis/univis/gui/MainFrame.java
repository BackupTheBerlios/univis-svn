package unikn.dbis.univis.gui;

import unikn.dbis.univis.navigation.tree.JTreeTest;

import javax.swing.*;
import java.awt.*;

/**
 * TODO: document me!!!
 * <p/>
 * MainFrame.
 * <p/>
 * User: raedler, weiler
 * Date: 08.04.2006
 * Time: 18:23:23
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public class MainFrame extends JFrame {

    private JSplitPane overviewSplit = new JSplitPane();
    private JScrollPane treeScroll = new JScrollPane();
    private JPanel rightPanel = new JPanel();
    private JPanel leftPanel = new JPanel(new GridBagLayout());
    private JLabel visual = new JLabel(new Visual());
    private JLabel facts = new JLabel("Facts");
    private JLabel measures = new JLabel("Measures");

    private GridBagConstraints leftConstraints = new GridBagConstraints();

    public MainFrame() {
        setTitle("MainFrameTest");
        init();
        this.pack();
        center();
    }

    public void init() {

        leftConstraints.gridx = 0;
        leftConstraints.gridy = 0;

        leftPanel.add(facts, leftConstraints);

        leftConstraints.gridy = 1;
        leftPanel.add(new JTreeTest(), leftConstraints);

        leftConstraints.gridy = 2;
        leftPanel.add(measures, leftConstraints);
        treeScroll.setViewportView(leftPanel);

        overviewSplit.setLeftComponent(treeScroll);

        rightPanel.add(visual);
        overviewSplit.setRightComponent(rightPanel);

        this.getContentPane().add(overviewSplit);
    }

      /**
     * Zentriert das <code>MainFrame</code> im sichtbaren Bereich des Bildschirms.
     */
    private void center() {

        // Größe der eingestellten Bildschirmauflösung.
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        double width = screenSize.getWidth();
        double height = screenSize.getHeight();

        // Größe des <code>JFrame</code>.
        Dimension frameSize = this.getSize();

        width -= frameSize.getWidth();
        height -= frameSize.getHeight();

        this.setLocation((int) width / 2, (int) height / 2);
    }

     /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new MainFrame().setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}