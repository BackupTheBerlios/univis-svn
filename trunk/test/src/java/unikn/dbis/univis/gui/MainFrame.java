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
    private JLabel facts = new JLabel("FACTS");
    private JLabel measures = new JLabel("MEASURES");

    private GridBagConstraints leftConstraints = new GridBagConstraints();

    public MainFrame() {
        setTitle("MainFrameTest");
        init();
        this.pack();
        center();
    }

    public void init() {
        int y = 0;
        leftConstraints.gridx = 0;
        leftConstraints.gridy = y;
        leftConstraints.anchor = GridBagConstraints.NORTHWEST;

        leftPanel.add(facts, leftConstraints);

        leftConstraints.gridy = ++y;
        leftPanel.add(new CubeLabel(Color.RED, "Orders", true), leftConstraints);

        leftConstraints.gridy = ++y;
        leftPanel.add(new CubeLabel(Color.BLUE, "Students", true), leftConstraints);

        leftConstraints.gridy = ++y;
        leftPanel.add(new JTreeTest(), leftConstraints);

        leftConstraints.gridy = ++y;
        leftPanel.add(measures, leftConstraints);

        leftConstraints.gridy = ++y;
        leftPanel.add(new CubeLabel(Color.RED, "Order amount, Euro", false), leftConstraints);

        leftConstraints.gridy = ++y;
        leftPanel.add(new CubeLabel(Color.BLUE, "Student number (cases)", false), leftConstraints);

        leftConstraints.gridy = ++y;
        leftPanel.add(new CubeLabel(Color.BLUE, "Student number (heads)", false), leftConstraints);
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