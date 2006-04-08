package unikn.dbis.univis.navigation.tree;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * TODO: document me!!!
 * <p/>
 * <code>JTreeTest</code>.
 * <p/>
 * User: raedler, weiler
 * Date: 03.11.2005
 * Time: 14:36:45
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Revision$
 * @since UniVis Explorer 0.1
 */
public class JTreeTest extends JTree {

    public JTreeTest() {

        DefaultTreeCellRenderer cellRenderer = new DefaultTreeCellRenderer();

        cellRenderer.setClosedIcon(new ReferentialFlag(Color.RED));

        this.setCellRenderer(cellRenderer);

        BasicTreeUI treeUI = (BasicTreeUI) this.getUI();

        /*
        this.setCellRenderer(new TreeCellRenderer() {

            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

                JPanel panel = new JPanel(null);

                ReferentialFlag flag1 = new ReferentialFlag(Color.RED);
                ReferentialFlag flag2 = new ReferentialFlag(Color.BLUE);

                flag1.setBounds(0, 1, 5, 13);
                flag2.setBounds(5, 1, 5, 13);

                panel.add(flag1);
                panel.add(flag2);

                panel.setPreferredSize(new Dimension(50, 14));
                panel.setBackground(Color.WHITE);

                JLabel label = new JLabel("Test");
                label.setBounds(15, 1, 40, 14);

                panel.add(label);

                if (selected) {
                    panel.setBackground(Color.BLUE);
                }

                return panel;
            }
        });
        */
    }

    public static void main(String[] args) {

        JFrame frame = new JFrame("src.JTreeTest");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(new JTreeTest());

        frame.pack();
        frame.setVisible(true);
    }
}