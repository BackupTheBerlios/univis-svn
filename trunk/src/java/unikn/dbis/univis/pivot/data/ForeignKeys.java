/*
 * Created on 12.05.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package unikn.dbis.univis.pivot.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

/**
 * @author Marion Herb
 */
public class ForeignKeys extends Vector {

    /**
     * Default constructor
     */
    public ForeignKeys() {
        super();

    }

    /**
     * @param i
     * @return Returns object at pos i
     */
    public ForeignKey getForeignKey(int i) {
        return (ForeignKey) super.get(i);
    }

    /**
     * @param rootTable
     * @param referencedTable
     * @return Returns object for this rootTable and referencedTable
     */
    public ForeignKey getForeignKey(String rootTable, String referencedTable) {
        for (Iterator iter = this.iterator(); iter.hasNext();) {
            ForeignKey element = (ForeignKey) iter.next();
            if (element.getTable().equalsIgnoreCase(rootTable)
                    && element.getRefTable().equalsIgnoreCase(referencedTable)) {
                return element;
            }
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see java.util.Vector#toString()
     */
    public String toString() {
        return new ArrayList(this).toString();
    }

}
