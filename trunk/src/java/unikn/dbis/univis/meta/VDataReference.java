package unikn.dbis.univis.meta;

import java.util.Set;

/**
 * The <code>VDataReference</code> contains information about
 * the data of the object that references this object.
 * <p/>
 * User: raedler, weiler
 * Date: 09.04.2006
 * Time: 11:00:32
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public interface VDataReference {

    /**
     * Returns the internationalization key to allow
     * international application support.
     *
     * @return The i18n key to allow international application
     *         support.
     */
    public String getI18nKey();

    /**
     * Sets the internationalization key to allow international
     * application support.
     *
     * @param i18nKey The i18n key to allow international
     *                application support.
     */
    public void setI18nKey(String i18nKey);

    /**
     * Returns the join attribute.
     *
     * @return The join attribute to join tables.
     */
    public String getJoinable();

    /**
     * Sets the joinable attribute.
     *
     * @param joinable The joinable key shows the join
     *                 attribute.
     */
    public void setJoinable(String joinable);

    /**
     * Returns the name of the table that contains the
     * data.
     *
     * @return The name of the table that contains the
     *         data.
     */
    public String getTableName();

    /**
     * Sets the name of the table that contains the
     * data.
     *
     * @param tableName The name of the table that contains
     *                  the data.
     */
    public void setTableName(String tableName);

    /**
     * TODO: document me!!!
     *
     * @return
     */
    public VDataReference getParent();

    /**
     * TODO: document me!!!
     *
     * @param parent
     */
    public void setParent(VDataReference parent);

    /**
     * TODO: document me!!!
     *
     * @return
     */
    public Set<VDataReference> getChildren();

    /**
     * TODO: document me!!!
     *
     * @param children
     */
    public void setChildren(Set<VDataReference> children);
}