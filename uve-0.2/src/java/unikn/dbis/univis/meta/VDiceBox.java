package unikn.dbis.univis.meta;

/**
 * The <code>VDiceBox</code> contains the cubes.
 * <p/>
 * User: raedler, weiler
 * Date: 09.04.2006
 * Time: 01:57:53
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 * @see VHierarchy
 */
public interface VDiceBox extends VHierarchy {

    /**
     * Returns the name of the dice box.
     *
     * @return The name of the dice box.
     */
    public String getName();

    /**
     * Sets the name of the dice box.
     *
     * @param name The name of the dice box.
     */
    public void setName(String name);

    /**
     * Returns the name of the dice box which contains the
     * cubes.
     *
     * @return The name of the dice box.
     */
    public String toString();
}