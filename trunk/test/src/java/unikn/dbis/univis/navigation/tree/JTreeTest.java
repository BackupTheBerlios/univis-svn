package unikn.dbis.univis.navigation.tree;

import org.hibernate.SessionFactory;
import org.hibernate.Session;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import unikn.dbis.univis.hibernate.util.HibernateUtil;
import unikn.dbis.univis.meta.impl.CubeImpl;
import unikn.dbis.univis.meta.impl.DiceBoxImpl;
import unikn.dbis.univis.meta.TreeFresh;
import unikn.dbis.univis.meta.Dimension;
import unikn.dbis.univis.meta.Cube;

import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

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

        try {
            Class.forName("org.postgresql.Driver");
        }
        catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }

        DefaultTreeCellRenderer cellRenderer = new DefaultTreeCellRenderer();

        //cellRenderer.setClosedIcon(new ReferentialFlag(Color.RED));

        //setCellRenderer(cellRenderer);

        BasicTreeUI treeUI = (BasicTreeUI) getUI();

        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();

        TreeFresh cube = (TreeFresh) session.createQuery("from " + DiceBoxImpl.class.getName() + " where i18nKey = 'UniVis Explorer'").uniqueResult();
        DefaultTreeModel model = new DefaultTreeModel(cube);
        setModel(model);
        setDragEnabled(true);
        setEditable(true);
        setLargeModel(true);

        /*
        setCellRenderer(new TreeCellRenderer() {

            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

                JPanel panel = null;

                if (value instanceof Dimension) {

                    Dimension dimension = (Dimension) value;

                    panel = new JPanel(new GridLayout(1, dimension.getSupportedCubes().size()));

                    for (Cube cube : dimension.getSupportedCubes()) {
                        ReferentialFlag flag = new ReferentialFlag(cube.getColor());
                        flag.setPreferredSize(new java.awt.Dimension(5, 12));
                        flag.setSize(new java.awt.Dimension(5, 12));
                        flag.setMaximumSize(new java.awt.Dimension(5, 12));
                        flag.setMinimumSize(new java.awt.Dimension(5, 12));
                        panel.add(flag);
                    }
                }

                if (panel == null) {
                    panel = new JPanel();
                }

                panel.add(new JLabel(value.toString()));

                if (selected) {
                    panel.setBackground(Color.BLUE);
                }

                return panel;
            }
        });
        */

        addMouseListener(new MouseAdapter() {
            /**
             * Invoked when the mouse has been clicked on a component.
             */
            @Override
            public void mouseClicked(MouseEvent e) {

                if (SwingUtilities.isRightMouseButton(e)) {

                    TreeFresh treeFresh = (TreeFresh) JTreeTest.this.getLastSelectedPathComponent();

                    try {
                        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/univis", "univis", "UniVis");

                        Statement stmt = connection.createStatement();

                        ResultSet result = stmt.executeQuery("SELECT * FROM " + treeFresh.getTableName());

                        while (result.next()) {
                            System.out.println("TEST: " + result.getString(1));
                        }
                    }
                    catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    public static void main(String[] args) {

        JFrame frame = new JFrame("JTreeTest");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(new JTreeTest());

        frame.setSize(new java.awt.Dimension(300, 500));
        frame.setVisible(true);
    }
}