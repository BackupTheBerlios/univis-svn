package unikn.dbis.univis.navigation.tree;

import unikn.dbis.univis.meta.*;
import unikn.dbis.univis.icon.VIconComponent;
import unikn.dbis.univis.icon.VIcons;
import unikn.dbis.univis.icon.VCubeFlagIcon;
import unikn.dbis.univis.icon.VCubeIcon;
import unikn.dbis.univis.hibernate.util.HibernateUtil;

import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.*;
import java.awt.*;

import org.hibernate.SessionFactory;
import org.hibernate.Session;

/**
 * TODO: document me!!!
 * <p/>
 * <code>VTreeCellRenderer</code>.
 * <p/>
 * User: raedler, weiler
 * Date: 11.04.2006
 * Time: 14:39:00
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public class VTreeCellRenderer extends DefaultTreeCellRenderer {

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

        Component rendererComponent = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIManager.getColor("Tree.background"));
        rendererComponent.setBackground(UIManager.getColor("Tree.background"));

        if (value instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            final Object o = node.getUserObject();

            if (o instanceof VDataReference) {
                VDataReference dataReference = (VDataReference) o;
                rendererComponent.setEnabled(dataReference.isEnabled());
            }

            if (o instanceof VDimension) {
                VDimension dimension = (VDimension) o;

                if (dimension.isSummable()) {
                    panel.add(new VIconComponent(VIcons.FILTER), BorderLayout.EAST);
                }

                if (dimension.isDropped()) {
                    rendererComponent.setForeground(Color.LIGHT_GRAY);
                    rendererComponent.setBackground(UIManager.getColor("Tree.background"));
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
                flags.setBackground(UIManager.getColor("Tree.background"));

                for (VCube cube : dimension.getSupportedCubes()) {
                    JLabel flag = new JLabel(new VCubeFlagIcon(cube.getColor()));
                    flag.setVerticalAlignment(JLabel.CENTER);

                    flags.add(flag);
                }
                session.close();

                panel.add(flags, BorderLayout.WEST);
            }
            else if (o instanceof VDiceBox) {
                ((JLabel) rendererComponent).setIcon(VIcons.BRICK);
            }
            else if (o instanceof VCube) {
                ((JLabel) rendererComponent).setIcon(new VCubeIcon(((VCube) o).getColor()));
            }
            else if (o instanceof VClassification) {
                VClassification clazz = (VClassification) o;

                String type = clazz.getType();

                if ("dimension".equals(type)) {
                    ((JLabel) rendererComponent).setIcon(VIcons.CHART_ORGANISATION);
                }
                else if ("measure".equals(type)) {
                    ((JLabel) rendererComponent).setIcon(VIcons.COLOR_SWATCH);
                }
                else if ("function".equals(type)) {
                    ((JLabel) rendererComponent).setIcon(VIcons.BRICKS);
                }
            }
            else if (o instanceof VMeasure || o instanceof VFunction) {
                JLabel label = (JLabel) rendererComponent;

                int preferredHeight = (int) rendererComponent.getPreferredSize().getHeight();

                rendererComponent = new JCheckBox(label.getText());
                rendererComponent.setPreferredSize(new Dimension((int) rendererComponent.getPreferredSize().getWidth() + 1, preferredHeight));
                rendererComponent.setFont(label.getFont());
                rendererComponent.setBackground(label.getBackground());

                if (o instanceof Selectable) {
                    boolean valueSelected = ((Selectable) o).isSelected();

                    ((JCheckBox) rendererComponent).setSelected(valueSelected);
                }
            }
        }

        panel.add(rendererComponent, BorderLayout.CENTER);

        return panel;
    }
}