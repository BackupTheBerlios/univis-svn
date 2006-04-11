package unikn.dbis.univis.navigation.tree;

import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.Hibernate;
import org.hibernate.Transaction;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.*;

import unikn.dbis.univis.hibernate.util.HibernateUtil;
import unikn.dbis.univis.meta.impl.VDiceBoxImpl;
import unikn.dbis.univis.meta.impl.VHierarchyImpl;
import unikn.dbis.univis.meta.impl.VDimensionImpl;
import unikn.dbis.univis.meta.impl.VDataReferenceImpl;
import unikn.dbis.univis.meta.VDimension;
import unikn.dbis.univis.meta.VDataReference;
import unikn.dbis.univis.meta.VHierarchy;
import unikn.dbis.univis.helper.VTreeHelper;
import unikn.dbis.univis.icon.VIcon;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.*;
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

        setCellRenderer(new DefaultTreeCellRenderer() {
            /**
             * Configures the renderer based on the passed in components.
             * The value is set from messaging the tree with
             * <code>convertValueToText</code>, which ultimately invokes
             * <code>toString</code> on <code>value</code>.
             * The foreground color is set based on the selection and the icon
             * is set based on on leaf and expanded.
             */
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                Component label = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

                boolean summable = false;
                if (value instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                    Object o = node.getUserObject();

                    if (o instanceof VDimension) {
                        summable = ((VDimension) o).isSummable();
                    }
                }

                JPanel panel = new JPanel(new BorderLayout());
                panel.setBackground(Color.WHITE);
                panel.add(new JLabel(tree.convertValueToText(value, selected, expanded, leaf,  row, hasFocus)), BorderLayout.CENTER);

                if (summable) {
                    UVIcon view = new UVIcon(VIcon.VIEW);

                    view.addMouseListener(new MouseAdapter() {
                        /**
                         * Invoked when the mouse has been clicked on a component.
                         */
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            System.out.println("JTreeTest.mouseClicked");
                        }

                        /**
                         * Invoked when a mouse button has been pressed on a component.
                         */
                        @Override
                        public void mousePressed(MouseEvent e) {
                            System.out.println("JTreeTest.mousePressed");
                        }

                        /**
                         * Invoked when a mouse button has been released on a component.
                         */
                        @Override
                        public void mouseReleased(MouseEvent e) {
                            System.out.println("JTreeTest.mouseReleased");
                        }

                        /**
                         * Invoked when the mouse enters a component.
                         */
                        @Override
                        public void mouseEntered(MouseEvent e) {
                            System.out.println("JTreeTest.mouseEntered");
                        }

                        /**
                         * Invoked when the mouse exits a component.
                         */
                        @Override
                        public void mouseExited(MouseEvent e) {
                            System.out.println("JTreeTest.mouseExited");
                        }
                    });

                    panel.add(view, BorderLayout.EAST);
                }

                return panel;
            }
        });

        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();

        /*
        java.util.List l = session.createQuery("from " + VDataReference.class.getName()).list();

        for (Object o : l) {
            System.out.println("CLASS: " + o.getClass());

            if (o instanceof VDimension) {
                System.out.println("SUMM: " + ((VDimension) o).isSummable());
            }
        }
        session.close();
        */

        VDiceBoxImpl diceBox = (VDiceBoxImpl) session.createQuery("from " + VDiceBoxImpl.class.getName() + " where name = 'UniVis Explorer'").uniqueResult();

        DefaultTreeModel model = new DefaultTreeModel(VTreeHelper.createDefaultTree(diceBox));
        setModel(model);

        /*
        for (VHierarchy hierarchy : diceBox.getChildren()) {
            System.out.println("CLASS: " + hierarchy.getDataReference().getClass());
        }
        */

        /*
        setCellRenderer(new TreeCellRenderer() {

            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

                JPanel panel = null;

                if (value instanceof VDimension) {

                    VDimension dimension = (VDimension) value;

                    panel = new JPanel(new GridLayout(1, dimension.getSupportedCubes().size()));

                    for (VCube cube : dimension.getSupportedCubes()) {
                        ReferentialFlag flag = new ReferentialFlag(cube.getColor());
                        flag.setPreferredSize(new Dimension(5, 12));
                        flag.setSize(new Dimension(5, 12));
                        flag.setMaximumSize(new Dimension(5, 12));
                        flag.setMinimumSize(new Dimension(5, 12));
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

        /*
        addMouseListener(new MouseAdapter() {
            /**
             * Invoked when the mouse has been clicked on a component.
             *
            @Override
            public void mouseClicked(MouseEvent e) {

                if (SwingUtilities.isRightMouseButton(e)) {

                    TreePath path = JTreeTest.this.getPathForLocation(e.getX(), e.getY());

                    System.out.println("PARENT: " + path.getParentPath().getLastPathComponent());

                    JTreeTest.this.setSelectionPath(path);

                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) JTreeTest.this.getLastSelectedPathComponent();

                    Object o = node.getUserObject();

                    if (o instanceof VDimension) {

                        VDimension dimension = (VDimension) o;

                        if (dimension.isSummable()) {

                            JPopupMenu menu = new JPopupMenu("Test");

                            System.out.println("TABLE_NAME: " + dimension.getTableName());

                            try {
                                Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/univis", "univis", "UniVis");

                                Statement stmt = connection.createStatement();

                                ResultSet result = stmt.executeQuery("SELECT name FROM " + dimension.getTableName());

                                while (result.next()) {
                                    menu.add(result.getString(1));
                                    System.out.println("TEST: " + result.getString(1));
                                }
                            }
                            catch (SQLException e1) {
                                e1.printStackTrace();
                            }

                            menu.show(JTreeTest.this, e.getX(), e.getY());
                        }
                    }
                }
            }
        });
        */
    }

    public static void main(String[] args) {

        JFrame frame = new JFrame("JTreeTest");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(new JScrollPane(new JTreeTest()));

        frame.setSize(new Dimension(300, 500));
        frame.setVisible(true);
    }
}