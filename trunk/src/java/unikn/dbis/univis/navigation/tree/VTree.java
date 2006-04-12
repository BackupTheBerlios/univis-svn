package unikn.dbis.univis.navigation.tree;

import unikn.dbis.univis.meta.VDiceBox;
import unikn.dbis.univis.meta.VDimension;
import unikn.dbis.univis.icon.VIcon;
import unikn.dbis.univis.dnd.VDataReferenceFlavor;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.sql.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.dnd.*;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.*;
import java.io.IOException;

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
public class VTree extends JTree implements DragGestureListener, DragSourceListener {

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

        setCellRenderer(new VTreeCellRenderer());

        addMouseListener(new MouseAdapter() {

            /**
             * Invoked when the mouse has been clicked on a component.
             */
            @Override
            public void mouseClicked(MouseEvent e) {

                for (Component c : getComponents()) {
                    if (c instanceof Container) {
                        for (Component c1 : ((Container) c).getComponents()) {

                            if (c1 instanceof Container) {
                                for (Component c2 : ((Container) c1).getComponents()) {
                                    //System.out.println("C: " + c2.getClass());
                                }
                            }
                        }
                    }
                }

                if (SwingUtilities.isRightMouseButton(e)) {
                    setSelectionPath(getPathForLocation(e.getX(), e.getY()));

                    showPopupMenu(e.getX(), e.getY());
                }
            }
        });

        setDragEnabled(true);
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

                        //popupMenu.add(view);

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
        System.out.println("VTree.dragGestureRecognized");

        dge.startDrag(DragSource.DefaultCopyDrop, new Transferable() {
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
                return false;
            }

            /**
             * Returns an object which represents the data to be transferred.  The class
             * of the object returned is defined by the representation class of the flavor.
             *
             * @param flavor the requested flavor for the data
             * @throws java.io.IOException if the data is no longer available
             *                             in the requested flavor.
             * @throws java.awt.datatransfer.UnsupportedFlavorException
             *                             if the requested data flavor is
             *                             not supported.
             * @see java.awt.datatransfer.DataFlavor#getRepresentationClass
             */
            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {

                if (VDataReferenceFlavor.DIMENSION_FLAVOR.equals(flavor)) {
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
        System.out.println("VTree.dragEnter");
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
        System.out.println("VTree.dragOver");
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
        System.out.println("VTree.dropActionChanged");
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
        System.out.println("VTree.dragExit");
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
        System.out.println("VTree.dragDropEnd");
    }
}