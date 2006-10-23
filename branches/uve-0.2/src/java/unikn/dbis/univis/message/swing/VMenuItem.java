package unikn.dbis.univis.message.swing;

import unikn.dbis.univis.message.MessageResolver;
import unikn.dbis.univis.message.Internationalizable;

import javax.swing.*;

/**
 * TODO: document me!!!
 * <p/>
 * <code>VMenuItem</code>.
 * <p/>
 * User: raedler, weiler
 * Date: 28.08.2006
 * Time: 19:37:02
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Revision$
 * @since UniVis Explorer 0.2
 */
public class VMenuItem extends JMenuItem implements Internationalizable {

    private String i18nKey;

    /**
     * @param i18nKey
     */
    public VMenuItem(String i18nKey) {
        super(MessageResolver.getMessage(i18nKey));
        this.i18nKey = i18nKey;
    }

    public void internationalize() {
        setText(MessageResolver.getMessage(i18nKey));
    }
}
