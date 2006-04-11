package unikn.dbis.univis.navigation.tree;

import unikn.dbis.univis.meta.VDiceBox;
import unikn.dbis.univis.meta.VDimension;
import unikn.dbis.univis.helper.VTreeHelper;
import unikn.dbis.univis.icon.VIcon;
import unikn.dbis.univis.icon.VIconComponent;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.sql.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;

/**
 * TODO: document me!!!
 * <p/>
 * <code>VTree</code>.
 * <p/>
 * User: raedler, weiler
 * Date: 10.04.2006
 * Time: 17:13:54
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public class VTree extends JTree {

    static {
        try {
            Class.forName("org.postgresql.Driver");
        }
        catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
    }

    /**
     * Returns a <code>JTree</code> with a sample model.
     * The default model used by the tree defines a leaf node as any node
     * without children.
     *
     * @see javax.swing.tree.DefaultTreeModel#asksAllowsChildren
     */
    public VTree(VDiceBox diceBox) {
        super(VTreeHelper.createDefaultTree(diceBox));

        setCellRenderer(new DefaultTreeCellRenderer() {

            /**
             * Sets the value of the current tree cell to <code>value</code>.
             * If <code>selected</code> is true, the cell will be drawn as if
             * selected. If <code>expanded</code> is true the node is currently
             * expanded and if <code>leaf</code> is true the node represets a
             * leaf and if <code>hasFocus</code> is true the node currently has
             * focus. <code>tree</code> is the <code>JTree</code> the receiver is being
             * configured for.  Returns the <code>Component</code> that the renderer
             * uses to draw the value.
             *
             * @return the <code>Component</code> that the renderer uses to draw the value
             */
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

                boolean summable = false;
                if (value instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                    Object o = node.getUserObject();

                    if (o instanceof VDimension) {
                        summable = ((VDimension) o).isSummable();
                    }
                }

                Component label = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

                JPanel panel = new JPanel(new BorderLayout());
                panel.setBackground(Color.WHITE);
                panel.add(label, BorderLayout.CENTER);

                if (summable) {
                    panel.add(VIconComponent.VIEW, BorderLayout.EAST);
                }

                return panel;
            }
        });

        addMouseListener(new MouseAdapter() {

            /**
             * Invoked when the mouse has been clicked on a component.
             */
            @Override
            public void mouseClicked(MouseEvent e) {

                System.out.println("SOURCE: " + e.getSource());

                if (SwingUtilities.isRightMouseButton(e)) {
                    setSelectionPath(getPathForLocation(e.getX(), e.getY()));

                    showPopupMenu(e.getX(), e.getY());
                }
            }
        });
    }

    private void showPopupMenu(int x, int y) {

        final JPopupMenu popupMenu = new JPopupMenu();

        Object o = getLastSelectedPathComponent();

        if (o instanceof DefaultMutableTreeNode) {

            DefaultMutableTreeNode node = (DefaultMutableTreeNode) o;

            Object userObject = node.getUserObject();

            if (userObject instanceof VDimension) {

                VDimension dimension = (VDimension) userObject;

                try {
                    Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/univis", "univis", "UniVis");

                    Statement stmt = connection.createStatement();

                    String sql = "SELECT " + dimension.getTableName() + ".id, " + (dimension.isParentable() ? dimension.getTableName() + ".parent, " : "") + dimension.getTableName() + ".name " + getWhere(node);

                    System.out.println("SQL: " + sql);

                    ResultSet result = stmt.executeQuery(sql);

                    //Set<Long> selections = new HashSet<Long>();
                    boolean hasNoTupel = true;
                    while (result.next()) {
                        hasNoTupel = false;

                        VIdCheckBox checkBox;
                        if (dimension.isParentable()) {
                            checkBox = new VIdCheckBox(dimension, result.getLong(1), result.getLong(2), result.getString(3));
                        }
                        else {
                            checkBox = new VIdCheckBox(dimension, result.getLong(1), result.getString(2));
                        }
                        popupMenu.add(checkBox);

                        //selections.add(result.getLong(1));
                    }

                    if (hasNoTupel) {
                        JOptionPane.showMessageDialog(VTree.this.getParent().getParent().getParent(), "No items found.", "Error message", JOptionPane.INFORMATION_MESSAGE);
                    }
                    else {
                        JButton view = new JButton(VIcon.VIEW);
                        view.addActionListener(new ActionListener() {
                            /**
                             * Invoked when an action occurs.
                             */
                            public void actionPerformed(ActionEvent e) {
                                popupMenu.setVisible(false);
                            }
                        });

                        popupMenu.add(view);
                    }

                    /*
                    System.out.println("DIM1: " + dimension.getSelections());

                    dimension.getSelections().retainAll(selections);

                    System.out.println("DIM2: " + dimension.getSelections());
                    */
                }
                catch (SQLException sqle) {
                    JOptionPane.showMessageDialog(VTree.this.getParent().getParent().getParent(), sqle.getMessage(), "Error message", JOptionPane.ERROR_MESSAGE);
                    sqle.printStackTrace();
                }

                popupMenu.show(VTree.this, x, y);
            }
        }
    }

    public String getWhere(DefaultMutableTreeNode node) {

        Object o = node.getUserObject();

        if (o instanceof VDimension) {
            VDimension dimension = (VDimension) o;

            String tableName = dimension.getTableName();

            StringBuffer from = new StringBuffer("FROM " + tableName + " AS " + tableName);
            StringBuffer where = new StringBuffer();

            boolean test = true;
            while (dimension.isParentable() && test) {

                node = (DefaultMutableTreeNode) node.getParent();

                Object o2 = node.getUserObject();

                if (o2 instanceof VDimension) {
                    dimension = (VDimension) o2;

                    boolean appended = false;

                    for (Object o3 : dimension.getSelections()) {

                        Long selection = -1L;

                        if (o3 instanceof Long) {
                            selection = (Long) o3;
                        }
                        else if (o3 instanceof VIdCheckBox.Parentor) {
                            selection = ((VIdCheckBox.Parentor) o3).getId();
                        }

                        appended = true;

                        if (where.length() > 0) {
                            where.append(" OR ");
                        }
                        else {
                            where.append("(");
                        }
                        where.append(tableName + ".parent = ").append(selection);
                    }

                    if (appended) {
                        where.append(")");
                    }
                }
                else {
                    test = false;
                }
            }

            if (where.length() > 0) {
                where.insert(0, " WHERE ");
            }

            return from.toString() + where.toString();
        }

        return "";
    }

    public void refresh(VDiceBox diceBox) {
        setModel(new DefaultTreeModel(VTreeHelper.createDefaultTree(diceBox)));
    }
}