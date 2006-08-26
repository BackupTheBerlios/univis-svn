package unikn.dbis.univis.pivot.view;

import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.UIManager;

import table.model.renderer.NumberRenderer;

/**
 * @author Marion Herb
 *         <p/>
 *         This renderer extends a number component.
 *         It is used each time a cell must be displayed.
 */
public class MyNumberRenderer extends NumberRenderer {

    public MyNumberRenderer() {
        this(false);
        // TODO Auto-generated constructor stub
    }

    public MyNumberRenderer(boolean showGroupText) {
        super(showGroupText);
        super.setFormat(new DecimalFormat("#,###"));

        super.setGroupBackgrounds(new Color[]{Color.WHITE,
                new Color(225, 225, 225), new Color(200, 200, 200),
                new Color(175, 175, 175), new Color(150, 150, 150),
                new Color(125, 125, 125)});

        Font defaultFont = UIManager.getFont("Table.font");
        //Font subTotalFont = new Font(defaultFont.getName(), defaultFont.getStyle() + Font.ITALIC + Font.BOLD, defaultFont.getSize());
        Font totalFont = new Font(defaultFont.getName(), defaultFont.getStyle() + Font.BOLD, defaultFont.getSize());
        super.setGroupFonts(new Font[]{defaultFont, totalFont, totalFont, totalFont, totalFont, totalFont});
    }

    /**
     * Sets component's text.
     *
     * @param value cell value.
     */
    protected void setValue(Object value) {
        super.setValue(value);

        if (value != null) {
            if (value instanceof String) {
                setHorizontalAlignment(JLabel.LEFT);
            }
        }
    }

}
