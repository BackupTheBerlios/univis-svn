package unikn.dbis.univis.meta.impl;

import unikn.dbis.univis.meta.DataReference;

/**
 * TODO: document me!!!
 * <p/>
 * <code>DataReferenceImpl</code>.
 * <p/>
 * User: raedler, weiler
 * Date: 09.04.2006
 * Time: 11:16:56
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public class DataReferenceImpl implements DataReference {

    private Long id;

    // The name of the table that contains the data.
    private String tableName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the name of the table that contains the data.
     *
     * @return The name of the table that contains the data.
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Sets the name of the table that contains the data.
     *
     * @param tableName The name of the table that contains
     *                  the data.
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}