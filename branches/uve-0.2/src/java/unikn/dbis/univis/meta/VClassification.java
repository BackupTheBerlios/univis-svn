package unikn.dbis.univis.meta;

/**
 * TODO: document me!!!
 * <p/>
 * VClassification.
 * <p/>
 * User: raedler, weiler
 * Date: 27.08.2006
 * Time: 23:13:07
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Revision$
 * @since UniVis Explorer 0.2
 */
public interface VClassification extends VDataReference {

    /**
     * Returns the type of the class.
     *
     * @return The type of the class.
     */
    public String getType();

    /**
     * Sets the type of the class.
     *
     * @param type The type of the class.
     */
    public void setType(String type);
}