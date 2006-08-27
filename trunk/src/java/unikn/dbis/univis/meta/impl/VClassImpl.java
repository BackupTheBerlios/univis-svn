package unikn.dbis.univis.meta.impl;

import unikn.dbis.univis.meta.VClass;
import unikn.dbis.univis.meta.VDataReference;

/**
 * TODO: document me!!!
 * <p/>
 * VClassImpl.
 * <p/>
 * User: raedler, weiler
 * Date: 27.08.2006
 * Time: 23:14:11
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Revision$
 * @since UniVis Explorer 0.2
 */
public class VClassImpl extends VDataReferenceImpl implements VClass, VDataReference {

    // The type of the class.
    private String type;

    /**
     * Returns the type of the class.
     *
     * @return The type of the class.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the class.
     *
     * @param type The type of the class.
     */
    public void setType(String type) {
        this.type = type;
    }
}