package unikn.dbis.univis.meta;

/**
 * TODO: document me!!!
 * <p/>
 * <code>VMetaHelper</code>.
 * <p/>
 * User: raedler, weiler
 * Date: 05.07.2006
 * Time: 13:14:45
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public class VMetaHelper {

    /**
     * Returns true if the parent of the current data reference is a
     * <code>VDimension</code> else returns false.
     *
     * @param dataReference The data reference that gets the recursiceRemove.
     * @return Whether the parent is a <code>VDimension</code> or not.
     */
    public static boolean isParentADimension(VDataReference dataReference) {
        return dataReference.getParent() instanceof VDimension;
    }
}