package unikn.dbis.univis.message;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.context.NoSuchMessageException;

import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.util.Locale;

/**
 * TODO: document me!!!
 * <p/>
 * <code>MessageResolver</code>.
 * <p/>
 * User: raedler, weiler
 * Date: 18.05.2006
 * Time: 00:23:19
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public class MessageResolver {

    // The message properties.
    private static ResourceBundleMessageSource bundle = new ResourceBundleMessageSource();

    static {
        bundle.setBasenames(new String[] {
                "unikn.dbis.univis.message.univis",
                "unikn.dbis.univis.message.data-reference"
        });
    }

    private static Locale locale = Locale.getDefault();

    public static void setLocale(Locale locale) {
        MessageResolver.locale = locale;
    }

    /**
     * Returns the message that will be found at parameter
     * key or the key if there isn't a matching key in one
     * of the property files.
     *
     * @param key The key that indicates the message.
     * @return The message or the key if there isn't a matching
     * key in one of the property files.
     */
    public static String getMessage(String key) {
        return bundle.getMessage(key, null, "??? " + key + " ???", locale);
    }
}