package unikn.dbis.univis.message;

import javax.swing.*;

/**
 * The <code>VLabel</code> extends the <code>JLabel</code> and
 * provides an easy way to internationlize the content of the
 * label.
 * <p/>
 * User: raedler, weiler
 * Date: 23.05.2006
 * Time: 19:37:02
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public class VLabel extends JLabel implements Internationalizable {

    // The i18n key to internationlize the label text.
    private String i18nKey;

    /**
     * Creates a <code>JLabel</code> instance with the specified
     * i18n key to resolve the internationalized message to display
     * as text on label, image, and horizontal alignment.
     * The label is centered vertically in its display area.
     * The text is on the trailing edge of the image.
     *
     * @param i18nKey             The i18n key.
     * @param icon                The image to be displayed by the label.
     * @param horizontalAlignment One of the following constants
     *                            defined in <code>SwingConstants</code>:
     *                            <code>LEFT</code>,
     *                            <code>CENTER</code>,
     *                            <code>RIGHT</code>,
     *                            <code>LEADING</code> or
     *                            <code>TRAILING</code>.
     */
    public VLabel(String i18nKey, Icon icon, int horizontalAlignment) {
        super(MessageResolver.getMessage(i18nKey), icon, horizontalAlignment);
        this.i18nKey = i18nKey;
    }

    /**
     * Creates a <code>JLabel</code> instance with the specified
     * i18n key to resolve the internationalized message to display
     * as text on label and horizontal alignment.
     * The label is centered vertically in its display area.
     *
     * @param i18nKey             The i18n key.
     * @param horizontalAlignment One of the following constants
     *                            defined in <code>SwingConstants</code>:
     *                            <code>LEFT</code>,
     *                            <code>CENTER</code>,
     *                            <code>RIGHT</code>,
     *                            <code>LEADING</code> or
     *                            <code>TRAILING</code>.
     */
    public VLabel(String i18nKey, int horizontalAlignment) {
        super(MessageResolver.getMessage(i18nKey), horizontalAlignment);
        this.i18nKey = i18nKey;
    }

    /**
     * Creates a <code>JLabel</code> instance with the specified i18n key
     * to resolve the internationalized message to display as text on label.
     * The label is aligned against the leading edge of its display area,
     * and centered vertically.
     *
     * @param i18nKey The i18n key.
     */
    public VLabel(String i18nKey) {
        super(MessageResolver.getMessage(i18nKey));
        this.i18nKey = i18nKey;
    }

    /**
     * Creates a <code>JLabel</code> instance with the specified
     * image and horizontal alignment.
     * The label is centered vertically in its display area.
     *
     * @param image               The image to be displayed by the label.
     * @param horizontalAlignment One of the following constants
     *                            defined in <code>SwingConstants</code>:
     *                            <code>LEFT</code>,
     *                            <code>CENTER</code>,
     *                            <code>RIGHT</code>,
     *                            <code>LEADING</code> or
     *                            <code>TRAILING</code>.
     */
    public VLabel(Icon image, int horizontalAlignment) {
        super(image, horizontalAlignment);
    }

    /**
     * Creates a <code>JLabel</code> instance with the specified image.
     * The label is centered vertically and horizontally
     * in its display area.
     *
     * @param image The image to be displayed by the label.
     */
    public VLabel(Icon image) {
        super(image);
    }

    /**
     * Creates a <code>JLabel</code> instance with
     * no image and with an empty string for the title.
     * The label is centered vertically
     * in its display area.
     * The label's contents, once set, will be displayed on the leading edge
     * of the label's display area.
     */
    public VLabel() {
    }

    /**
     * Sets the i18n key to resolve the internationalized
     * message.
     *
     * @param i18nKey The i18n key.
     */
    public void setI18NKey(String i18nKey) {
        this.i18nKey = i18nKey;
    }

    public void internationalize() {
        setText(MessageResolver.getMessage(i18nKey));
    }
}