package unikn.dbis.univis.meta;

/**
 * TODO: document me!!!
 * <p/>
 * VFunction.
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
public interface VFunction extends VDataReference {

  /**
     * Returns the name of the function.
     *
     * @return The name of the function.
     */
    public String getFunction();

    /**
     * Sets the name of the function.
     *
     * @param function The name of the function.
     */
    public void setFunction(String function);
}
