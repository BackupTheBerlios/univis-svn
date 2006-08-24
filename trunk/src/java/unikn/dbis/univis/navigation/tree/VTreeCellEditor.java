package unikn.dbis.univis.navigation.tree;

import unikn.dbis.univis.meta.VDimension;
import unikn.dbis.univis.meta.VCube;
import unikn.dbis.univis.icon.VIconComponent;
import unikn.dbis.univis.icon.VIcons;
import unikn.dbis.univis.icon.VCubeFlagIcon;
import unikn.dbis.univis.hibernate.util.HibernateUtil;

import javax.swing.*;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.util.EventObject;

import org.hibernate.SessionFactory;
import org.hibernate.Session;

/**
 * TODO: document me!!!
 * <p/>
 * <code>VTreeCellEditor</code>.
 * <p/>
 * User: raedler, weiler
 * Date: 18.04.2006
 * Time: 18:19:00
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public class VTreeCellEditor extends AbstractCellEditor implements TreeCellEditor {

    private VTreeCellRenderer renderer;

    public VTreeCellEditor(VTreeCellRenderer renderer) {
        this.renderer = renderer;
    }

    /**
     * Returns the value contained in the editor.
     *
     * @return the value contained in the editor
     */
    public Object getCellEditorValue() {
        return "n/a";
    }

    /**
     * Returns true.
     *
     * @param e an event object
     * @return true
     */
    @Override
    public boolean isCellEditable(EventObject e) {
        if (e instanceof MouseEvent) {
            MouseEvent mouseEvent = (MouseEvent) e;

            if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets an initial <I>value</I> for the editor.  This will cause
     * the editor to stopEditing and lose any partially edited value
     * if the editor is editing when this method is called. <p>
     * <p/>
     * Returns the component that should be added to the client's
     * Component hierarchy.  Once installed in the client's hierarchy
     * this component will then be able to draw and receive user input.
     *
     * @param tree       the JTree that is asking the editor to edit;
     *                   this parameter can be null
     * @param value      the value of the cell to be edited
     * @param isSelected true is the cell is to be renderer with
     *                   selection highlighting
     * @param expanded   true if the node is expanded
     * @param leaf       true if the node is a leaf node
     * @param row        the row index of the node being edited
     * @return the component for editing
     */
    public Component getTreeCellEditorComponent(final JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {

        Component rendererComponent = renderer.getTreeCellRendererComponent(tree, value, isSelected, expanded, leaf, row, false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel(tree.convertValueToText(value, isSelected, expanded, leaf, row, true));
        label.setFont(rendererComponent.getFont());
        label.setForeground(rendererComponent.getForeground());
        label.setBackground(rendererComponent.getBackground());

        panel.add(label, BorderLayout.CENTER);

        if (value instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

            if (node.isLeaf()) {
                label.setIcon(renderer.getLeafIcon());
            }
            else {
                if (expanded) {
                    label.setIcon(renderer.getOpenIcon());
                }
                else {
                    label.setIcon(renderer.getClosedIcon());
                }
            }

            Object o = node.getUserObject();

            if (o instanceof VDimension) {
                VDimension dimension = (VDimension) o;

                if (dimension.isSummable()) {
                    final VIconComponent icon = new VIconComponent(VIcons.FILTER, 8);
                    panel.add(icon, BorderLayout.EAST);
                    icon.addMouseListener(new MouseAdapter() {

                        /**
                         * Invoked when a mouse button has been pressed on a component.
                         */
                        @Override
                        public void mousePressed(MouseEvent e) {
                            VTree vtree = (VTree) tree;
                            MouseEvent converted = SwingUtilities.convertMouseEvent(icon, e, vtree);

                            vtree.showPopupMenu(converted.getPoint());
                        }
                    });
                }

                if (dimension.isDropped()) {
                    label.setForeground(Color.LIGHT_GRAY);
                    //label.setBackground(UIManager.getColor("Tree.background"));
                    label.setBackground(Color.RED);
                }

                SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
                Session session = sessionFactory.openSession();
                session.refresh(dimension);

                int size = dimension.getSupportedCubes().size();
                if (size % 2 == 1) {
                    size = (size / 2) + 1;
                }
                else {
                    size = (size / 2);
                }

                JPanel flags = new JPanel(new GridLayout(2, size));
                flags.setBackground(Color.WHITE);

                for (VCube cube : dimension.getSupportedCubes()) {
                    JLabel flag = new JLabel(new VCubeFlagIcon(cube.getColor()));
                    flag.setVerticalAlignment(JLabel.CENTER);

                    flags.add(flag);
                }
                session.close();

                panel.add(flags, BorderLayout.WEST);
            }
        }

        return panel;
    }
}