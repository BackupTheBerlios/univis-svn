package unikn.dbis.univis.meta;

/**
 * The <code>VDataReference</code> contains information about
 * the data of the object that references this object.
 * <p/>
 * User: raedler, weiler
 * Date: 26.08.2006
 * Time: 15:55:56
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.2
 */
public interface VMeasure extends VDataReference {

    /**
     * Returns the name of the measure.
     *
     * @return String
     */
    public String getMeasure();

    /**
     * Sets the name of the measure.
     *
     * @param measure
     */
    public void setMeasure(String measure);


}
