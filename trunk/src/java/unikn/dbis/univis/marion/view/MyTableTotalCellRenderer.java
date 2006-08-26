package unikn.dbis.univis.marion.view;

import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * @author Marion Herb
 *         <p/>
 *         This renderer extends a component.
 *         It is used each time a cell must be displayed.
 */
public class MyTableTotalCellRenderer extends DefaultTableCellRenderer {

    /**
     * Default number format.
     */
    protected NumberFormat format = new DecimalFormat("#,###");

    public MyTableTotalCellRenderer() {
        super();
        setBackground(Color.LIGHT_GRAY);
    }

    /**
     * Sets component's text.
     *
     * @param value cell value.
     */
    protected void setValue(Object value) {
        Font defaultFont = UIManager.getFont("Table.font");
        Font totalFont = new Font(defaultFont.getName(), defaultFont.getStyle() + Font.BOLD, defaultFont.getSize());
        setFont(totalFont);

        if (value == null) {
            super.setValue(value);
        }
        else {
            if (value instanceof Number) {
                setHorizontalAlignment(JLabel.RIGHT);
                setText(format.format(value));
            }
            else {
                setText(value.toString());
            }
        }
    }

}
