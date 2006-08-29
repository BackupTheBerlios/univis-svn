package unikn.dbis.univis.meta.impl;

import unikn.dbis.univis.meta.VFunction;
import unikn.dbis.univis.meta.VDataReference;
import unikn.dbis.univis.meta.Selectable;

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
public class VFunctionImpl extends VDataReferenceImpl implements VFunction, Selectable, VDataReference {

    // The name of the function.
    private String function;

    // Whether the function is selected or not.
    private boolean selected;

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

    /**
     * Returns whether the object is selected or not.
     *
     * @return Whether the object is selected or not.
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Sets whether the object should be selected or not.
     *
     * @param selected Whether the object is selected or not.
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
