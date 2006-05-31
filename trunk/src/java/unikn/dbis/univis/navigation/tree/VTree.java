package unikn.dbis.univis.navigation.tree;

import unikn.dbis.univis.meta.VDiceBox;
import unikn.dbis.univis.meta.VDimension;
import unikn.dbis.univis.icon.VIcons;
import unikn.dbis.univis.dnd.VDataReferenceFlavor;
import unikn.dbis.univis.explorer.VExplorer;
import unikn.dbis.univis.sql.dialect.UniVisDialect;
import unikn.dbis.univis.hibernate.util.HibernateUtil;
import unikn.dbis.univis.message.MessageResolver;

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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
public class VTree extends JTree implements DragSourceListener, DragGestureListener {

    // The logger to log info, error and other occuring messages
    // or exceptions.
    public static final transient Log LOG = LogFactory.getLog(VTree.class);

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

    /**
     * TODO: document me!!!
     *
     * @param x
     * @param y
     */
    public void showPopupMenu(int x, int y) {

        QuerySelect querySelect = new QuerySelect(HibernateUtil.getDialect());

        final VPopupMenu popupMenu = new VPopupMenu(x, y);

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
                        JOptionPane.showMessageDialog(VTree.this.getParent().getParent().getParent(), MessageResolver.getMessage("no_items_found"), MessageResolver.getMessage("error_message"), JOptionPane.ERROR_MESSAGE);
                    }
                    else {
                        JMenuItem view = new JMenuItem(MessageResolver.getMessage("filtering"), VIcons.LIGHTNING);
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
                }
                catch (SQLException sqle) {
                    VExplorer.publishException(sqle);

                    if (LOG.isErrorEnabled()) {
                        LOG.error(sqle.getMessage(), sqle);
                    }
                }
            }

            //System.out.println("querySelect = " + querySelect.toQueryString());
        }
    }

    /**
     * TODO: document me!!!
     *
     * @param node
     * @return _
     */
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

        // Gets the selected tree node.
        Object o = getLastSelectedPathComponent();

        if (o instanceof DefaultMutableTreeNode) {

            // Gets the user object of the tree node.
            Object userObject = ((DefaultMutableTreeNode) o).getUserObject();

            if (userObject instanceof VDimension) {

                final VDimension dimension = (VDimension) userObject;

                // Whether the selected dimension allows drag and drop
                // or not.
                if (dimension.isSummable() && !dimension.isDropped()) {
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
                         *          if the requested data flavor is
                         *          not supported.
                         * @see java.awt.datatransfer.DataFlavor#getRepresentationClass
                         */
                        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
                            if (VDataReferenceFlavor.DIMENSION_FLAVOR.match(flavor)) {
                                return dimension;
                            }
                            throw new UnsupportedFlavorException(flavor);
                        }
                    });
                }
                else {
                    if (LOG.isInfoEnabled()) {
                        LOG.info("The tree node isn't summable or has been drag and dropped in the past.");
                    }
                }
            }
        }
    }

    /**
     * This method is invoked to signify that the Drag and Drop
     * operation is complete. The getDropSuccess() method of
     * the <code>DragSourceDropEvent</code> can be used to
     * determine the termination state. The getDropAction() method
     * returns the operation that the drop site selected
     * to apply to the Drop operation. Once this method is complete, the
     * current <code>DragSourceContext</code> and
     * associated resources become invalid.
     *
     * @param dsde the <code>DragSourceDropEvent</code>
     */
    public void dragDropEnd(DragSourceDropEvent dsde) {
        if (dsde.getDropSuccess()) {

            if (LOG.isDebugEnabled()) {
                LOG.debug("Drag and drop finished successfuly. The tree UI will be updated.");
            }

            updateUI();
        }
    }

    /**
     * Called as the cursor's hotspot exits a platform-dependent drop site.
     * This method is invoked when any of the following conditions are true:
     * <UL>
     * <LI>The cursor's hotspot no longer intersects the operable part
     * of the drop site associated with the previous dragEnter() invocation.
     * </UL>
     * OR
     * <UL>
     * <LI>The drop site associated with the previous dragEnter() invocation
     * is no longer active.
     * </UL>
     * OR
     * <UL>
     * <LI> The drop site associated with the previous dragEnter() invocation
     * has rejected the drag.
     * </UL>
     *
     * @param dse the <code>DragSourceEvent</code>
     */
    public void dragExit(DragSourceEvent dse) {
        // empty
    }

    /**
     * Called when the user has modified the drop gesture.
     * This method is invoked when the state of the input
     * device(s) that the user is interacting with changes.
     * Such devices are typically the mouse buttons or keyboard
     * modifiers that the user is interacting with.
     *
     * @param dsde the <code>DragSourceDragEvent</code>
     */
    public void dropActionChanged(DragSourceDragEvent dsde) {
        // empty
    }

    /**
     * Called as the cursor's hotspot moves over a platform-dependent drop site.
     * This method is invoked when all the following conditions are true:
     * <UL>
     * <LI>The cursor's hotspot has moved, but still intersects the
     * operable part of the drop site associated with the previous
     * dragEnter() invocation.
     * <LI>The drop site is still active.
     * <LI>The drop site accepts the drag.
     * </UL>
     *
     * @param dsde the <code>DragSourceDragEvent</code>
     */
    public void dragOver(DragSourceDragEvent dsde) {
        // empty
    }

    /**
     * Called as the cursor's hotspot enters a platform-dependent drop site.
     * This method is invoked when all the following conditions are true:
     * <UL>
     * <LI>The cursor's hotspot enters the operable part of a platform-
     * dependent drop site.
     * <LI>The drop site is active.
     * <LI>The drop site accepts the drag.
     * </UL>
     *
     * @param dsde the <code>DragSourceDragEvent</code>
     */
    public void dragEnter(DragSourceDragEvent dsde) {
        // empty
    }
}