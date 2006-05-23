package unikn.dbis.univis.navigation.tree;

import unikn.dbis.univis.meta.VDiceBox;
import unikn.dbis.univis.meta.VDimension;
import unikn.dbis.univis.icon.VIcons;
import unikn.dbis.univis.dnd.VDataReferenceFlavor;
import unikn.dbis.univis.explorer.VExplorer;
import unikn.dbis.univis.sql.dialect.UniVisDialect;
import unikn.dbis.univis.hibernate.util.HibernateUtil;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import java.sql.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.dnd.*;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;

import org.hibernate.sql.QuerySelect;
import org.hibernate.sql.JoinFragment;

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
public class VTree extends JTree implements DragGestureListener {

    /**
     * Returns a <code>JTree</code> with a sample model.
     * The default model used by the tree defines a leaf node as any node
     * without children.
     *
     * @see javax.swing.tree.DefaultTreeModel#asksAllowsChildren
     */
    public VTree(VDiceBox diceBox) {
        super(VTreeHelper.createDefaultTree(diceBox));

        VTreeCellRenderer renderer = new VTreeCellRenderer();
        setCellRenderer(renderer);
        setCellEditor(new VTreeCellEditor(renderer));
        setEditable(true);

        addMouseListener(new MouseAdapter() {
            /**
             * Invoked when a mouse button has been pressed on a component.
             */
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    setSelectionPath(getPathForLocation(e.getX(), e.getY()));
                }
            }
        });
    }

    public void showPopupMenu(int x, int y) {

        QuerySelect querySelect = new QuerySelect(HibernateUtil.getDialect());

        final JPopupMenu popupMenu = new JPopupMenu();

        Object o = getLastSelectedPathComponent();

        if (o instanceof DefaultMutableTreeNode) {

            DefaultMutableTreeNode node = (DefaultMutableTreeNode) o;

            Object userObject = node.getUserObject();

            if (userObject instanceof VDimension) {

                VDimension dimension = (VDimension) userObject;

                try {
                    Connection connection = VExplorer.getConnection();

                    Statement stmt = connection.createStatement();

                    querySelect.addSelectColumn("id", "id");
                    if (dimension.isParentable()) querySelect.addSelectColumn("parent", "parent");
                    querySelect.addSelectColumn("name", "name");
                    querySelect.getJoinFragment().addJoin(dimension.getTableName(), UniVisDialect.generateTableAlias(dimension.getTableName(), 1), new String[]{"parent"}, new String[]{"id"}, JoinFragment.FULL_JOIN);


                    String sql = "SELECT " + dimension.getTableName() + ".id, " + (dimension.isParentable() ? dimension.getTableName() + ".parent, " : "") + dimension.getTableName() + ".name " + getWhere(node);

                    //System.out.println("SQL: " + sql);

                    ResultSet result = stmt.executeQuery(sql);

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
                    }

                    if (hasNoTupel) {
                        JOptionPane.showMessageDialog(VTree.this.getParent().getParent().getParent(), "No items found.", "Error message", JOptionPane.INFORMATION_MESSAGE);
                    }
                    else {
                        JMenuItem view = new JMenuItem("Filter", VIcons.LIGHTNING);
                        view.addActionListener(new ActionListener() {
                            /**
                             * Invoked when an action occurs.
                             */
                            public void actionPerformed(ActionEvent e) {
                                popupMenu.setVisible(false);
                            }
                        });

                        popupMenu.add(view);

                        popupMenu.show(VTree.this, x, y);
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
            }

            //System.out.println("querySelect = " + querySelect.toQueryString());
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
                        else if (o3 instanceof VIdCheckBox.VIdCheckBoxFilter) {
                            selection = ((VIdCheckBox.VIdCheckBoxFilter) o3).getId();
                        }

                        appended = true;

                        if (where.length() > 0) {
                            where.append(" OR ");
                        }
                        else {
                            where.append("(");
                        }
                        where.append(tableName).append(".parent = ").append(selection);
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

    /**
     * A <code>DragGestureRecognizer</code> has detected
     * a platform-dependent drag initiating gesture and
     * is notifying this listener
     * in order for it to initiate the action for the user.
     * <p/>
     *
     * @param dge the <code>DragGestureEvent</code> describing
     *            the gesture that has just occurred
     */
    public void dragGestureRecognized(DragGestureEvent dge) {
        dge.startDrag(DragSource.DefaultMoveDrop, new Transferable() {
            /**
             * Returns an array of DataFlavor objects indicating the flavors the data
             * can be provided in.  The array should be ordered according to preference
             * for providing the data (from most richly descriptive to least descriptive).
             *
             * @return an array of data flavors in which this data can be transferred
             */
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[]{VDataReferenceFlavor.DIMENSION_FLAVOR};
            }

            /**
             * Returns whether or not the specified data flavor is supported for
             * this object.
             *
             * @param flavor the requested flavor for the data
             * @return boolean indicating whether or not the data flavor is supported
             */
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                for (DataFlavor dataFlavor : getTransferDataFlavors()) {
                    if (dataFlavor.match(flavor)) {
                        return true;
                    }
                }
                return false;
            }

            /**
             * Returns an object which represents the data to be transferred.  The class
             * of the object returned is defined by the representation class of the flavor.
             *
             * @param flavor the requested flavor for the data
             * @throws java.awt.datatransfer.UnsupportedFlavorException
             *                             if the requested data flavor is
             *                             not supported.
             * @see java.awt.datatransfer.DataFlavor#getRepresentationClass
             */
            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {

                if (VDataReferenceFlavor.DIMENSION_FLAVOR.match(flavor)) {
                    Object o = getLastSelectedPathComponent();

                    if (o instanceof DefaultMutableTreeNode) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) o;

                        Object userObject = node.getUserObject();

                        if (userObject instanceof VDimension) {
                            return userObject;
                        }
                    }
                }

                throw new UnsupportedFlavorException(flavor);
            }
        });
    }
}