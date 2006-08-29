package unikn.dbis.univis.sql;

/**
 * TODO: document me!!!
 * <p/>
 * <code>CubeChangeException</code>.
 * <p/>
 * User: raedler, weiler
 * Date: 29.08.2006
 * Time: 12:53:24
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Revision$
 * @since UniVis Explorer 0.2
 */
public class CubeChangeException extends Exception {
    
    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public CubeChangeException(String message) {
        super(message);
    }
}
