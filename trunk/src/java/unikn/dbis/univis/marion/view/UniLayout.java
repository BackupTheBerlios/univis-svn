package unikn.dbis.univis.marion.view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * @author Christian Grün
 */
public class UniLayout {
    static Font FONT = new Font("Tahoma", 0, 12);
    static Font BOLD = new Font("Tahoma", 1, 12);

    /**
     * create new button
     *
     * @param caption button title
     * @param action  component with action listener
     * @return new button
     */
    public static JButton newButton(String caption, ActionListener action) {
        JButton button = new JButton(caption);
        button.setMargin(new Insets(2, 10, 2, 10));
        button.addActionListener(action);
        return button;
    }

    /**
     * create new combo box
     *
     * @param values string array with combobox values
     * @return new combobox
     */
    public static JComboBox newCombo(String[] values) {
        JComboBox combo = new JComboBox(values);
        return combo;
    }

    /**
     * create new label
     *
     * @param text label text
     * @return new combobox
     */
    static JLabel newLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(BOLD);
        label.setBorder(new EmptyBorder(4, 8, 4, 12));
        return label;
    }

    /**
     * Set width of a component, adopting the original
     * component height
     * @param comp component
     * @param width width
     */
    static void setWidth(JComponent comp, int width) {
        comp.setPreferredSize(new Dimension(width, comp.getPreferredSize().height));
    }
}
