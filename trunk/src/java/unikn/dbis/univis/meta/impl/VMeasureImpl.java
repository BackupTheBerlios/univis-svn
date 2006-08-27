package unikn.dbis.univis.meta.impl;

import unikn.dbis.univis.meta.VMeasure;
import unikn.dbis.univis.meta.VDataReference;

/**
 * TODO: document me!!!
 * <p/>
 * VMeasureImpl.
 * <p/>
 * User: raedler, weiler
 * Date: 26.08.2006
 * Time: 15:56:51
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.2
 */
public class VMeasureImpl extends VDataReferenceImpl implements VMeasure, VDataReference {

    // The name of the measure.
    private String measure;

    /**
     * Returns the name of the measure.
     *
     * @return The name of the measure.
     */
    public String getMeasure() {
        return measure;
    }

    /**
     * Sets the name of the measure.
     *
     * @param measure The name of the measure.
     */
    public void setMeasure(String measure) {
        this.measure = measure;
    }
}