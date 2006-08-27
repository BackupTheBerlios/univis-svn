package unikn.dbis.univis.meta.impl;

import unikn.dbis.univis.meta.VFunction;
import unikn.dbis.univis.meta.VDataReference;

/**
 * TODO: document me!!!
 * <p/>
 * VFunctionImpl.
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
public class VFunctionImpl extends VDataReferenceImpl implements VFunction, VDataReference {

    // The name of the function.
    private String function;

    /**
     * Returns the name of the function.
     *
     * @return The name of the function.
     */
    public String getFunction() {
        return function;
    }

    /**
     * Sets the name of the function.
     *
     * @param function The name of the function.
     */
    public void setFunction(String function) {
        this.function = function;
    }
}
