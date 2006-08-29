package unikn.dbis.univis.message.swing;

import unikn.dbis.univis.message.Internationalizable;
import unikn.dbis.univis.message.MessageResolver;

import javax.swing.*;

/**
 * TODO: document me!!!
 * <p/>
 * <code>VMenuItem</code>.
 * <p/>
 * User: raedler, weiler
 * Date: 28.08.2006
 * Time: 19:52:13
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Revision$
 * @since UniVis Explorer 0.2
 */
public class VMenu extends JMenu implements Internationalizable {

    private String i18nKey;

    /**
     * @param i18nKey
     */
    public VMenu(String i18nKey) {
        super(MessageResolver.getMessage(i18nKey));
        this.i18nKey = i18nKey;
    }

    public void internationalize() {
        setText(MessageResolver.getMessage(i18nKey));
    }
}
