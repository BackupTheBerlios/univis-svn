package unikn.dbis.univis.meta;

/**
 * TODO: document me!!!
 * <p/>
 * <code>Filterable</code>.
 * <p/>
 * User: raedler, weiler
 * Date: 29.08.2006
 * Time: 00:26:21
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Revision$
 * @since UniVis Explorer 0.2
 */
public interface Selectable {

    /**
     * Returns whether the object is selected or not.
     *
     * @return Whether the object is selected or not.
     */
    public boolean isSelected();

    /**
     * Sets whether the object should be selected or not.
     *
     * @param selected Whether the object is selected or not.
     */
    public void setSelected(boolean selected);
}
