package unikn.dbis.univis.dataexchange.schemaimport;

/**
 * TODO: document me!!!
 * <p/>
 * <code>SchemaImportException</code>.
 * <p/>
 * User: raedler
 * Date: 27.09.2006
 * Time: 23:03:51
 *
 * @author Roman R&auml;dle
 * @version $Revision$
 * @since UniVis Explorer 0.3
 */
public class SchemaImportException extends RuntimeException {

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public SchemaImportException(String message) {
        super(message);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * <code>cause</code> is <i>not</i> automatically incorporated in
     * this runtime exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method).  (A <tt>null</tt> value is
     *                permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     * @since 1.4
     */
    public SchemaImportException(String message, Throwable cause) {
        super(message, cause);
    }
}
