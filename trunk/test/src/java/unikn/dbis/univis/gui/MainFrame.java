package unikn.dbis.univis.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Transferable;


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
    private JScrollPane graphScroll = new JScrollPane();
    private JPanel rightPanel = new JPanel();
    private JPanel leftPanel = new JPanel(new GridBagLayout());
    private JLabel facts = new JLabel("FACTS");
    private JLabel measures = new JLabel("MEASURES");
    private Font tahoma = new Font("Tahoma", Font.BOLD, 11);
    private GuiGraph guiGraph = new GuiGraph();

    public Transferable tr;

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

        facts.setFont(tahoma);
        leftPanel.add(facts, leftConstraints);

        leftConstraints.gridy = ++y;
        leftPanel.add(new CubeLabel(Color.RED, "Orders", true, 21, 22, 10), leftConstraints);

        leftConstraints.gridy = ++y;
        leftPanel.add(new CubeLabel(Color.BLUE, "Students", true, 21, 22, 10), leftConstraints);

        leftConstraints.gridy = ++y;
        leftPanel.add(new JLabel(new VisualLine()), leftConstraints);

        leftConstraints.gridy = ++y;
        leftPanel.add(new MyTree(), leftConstraints);

        leftConstraints.gridy = ++y;
        leftPanel.add(new JLabel(new VisualLine()), leftConstraints);

        measures.setFont(tahoma);
        leftConstraints.gridy = ++y;
        leftPanel.add(measures, leftConstraints);

        leftConstraints.gridy = ++y;
        leftPanel.add(new CubeLabel(Color.RED, "Order amount, Euro", false, 11, 11, 2), leftConstraints);

        leftConstraints.gridy = ++y;
        leftPanel.add(new CubeLabel(Color.BLUE, "Student number (cases)", false, 11, 11, 2), leftConstraints);

        leftConstraints.gridy = ++y;
        leftPanel.add(new CubeLabel(Color.BLUE, "Student number (heads)", false, 11, 11, 2), leftConstraints);

        leftConstraints.gridy = ++y;
        Font measureFont = new Font("Serif", Font.ITALIC, 11);
        JLabel measure = new JLabel("Define new measure", new NewMeasureIcon(), 0);
        measure.setFont(measureFont);
        leftPanel.add(measure, leftConstraints);

        treeScroll.setViewportView(leftPanel);

        overviewSplit.setLeftComponent(treeScroll);

        rightPanel.add(guiGraph);
        graphScroll.setViewportView(rightPanel);
        overviewSplit.setRightComponent(graphScroll);

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
                   UIManager.setLookAndFeel(new com.sun.java.swing.plaf.windows.WindowsLookAndFeel());
                   new MainFrame().setVisible(true);
               } catch (Exception e) {
                   e.printStackTrace();
               }
           }
       });
   }


}