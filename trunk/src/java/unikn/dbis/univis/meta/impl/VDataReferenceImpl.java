package unikn.dbis.univis.meta.impl;

import unikn.dbis.univis.meta.VDataReference;

import java.util.Set;
import java.util.HashSet;

/**
 * TODO: document me!!!
 * <p/>
 * <code>VDataReferenceImpl</code>.
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
public abstract class VDataReferenceImpl implements VDataReference {

    protected VDataReferenceImpl() {
        children = new HashSet<VDataReference>();
    }

    // ##############################################################################
    // Interface implementations.
    // ##############################################################################

    // The identifier of the data reference object.
    private Long id;

    // The i18n key to allow international application support.
    private String i18nKey;

    // The name of the table that contains the data.
    private String tableName;

    // TODO: document me!!!
    private VDataReference parent;

    // TODO: document me!!!
    private Set<VDataReference> children;

    /**
     * Returns the identifier of the data reference
     * object.
     *
     * @return The identifier of the data reference
     *         object.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the identifier of the data reference object.
     *
     * @param id The identifier of the data reference
     *           object.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the internationalization key to allow
     * international application support.
     *
     * @return The i18n key to allow international application
     *         support.
     */
    public String getI18nKey() {
        return i18nKey;
    }

    /**
     * Sets the internationalization key to allow international
     * application support.
     *
     * @param i18nKey The i18n key to allow international
     *                application support.
     */
    public void setI18nKey(String i18nKey) {
        this.i18nKey = i18nKey;
    }

    /**
     * Returns the name of the table that contains the
     * data.
     *
     * @return The name of the table that contains the
     *         data.
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Sets the name of the table that contains the
     * data.
     *
     * @param tableName The name of the table that contains
     *                  the data.
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * TODO: document me!!!
     *
     * @return
     */
    public VDataReference getParent() {
        return parent;
    }

    /**
     * TODO: document me!!!
     *
     * @param parent
     */
    public void setParent(VDataReference parent) {
        this.parent = parent;
    }

    /**
     * TODO: document me!!!
     *
     * @return
     */
    public Set<VDataReference> getChildren() {
        return children;
    }

    /**
     * TODO: document me!!!
     *
     * @param children
     */
    public void setChildren(Set<VDataReference> children) {
        this.children = children;
    }

    /**
     * Returns the name that should be shown in the tree
     * or anything else.
     *
     * @return The name that should be shown.
     */
    @Override
    public String toString() {

        if (getI18nKey() == null) {
            return super.toString();
        }

        return getI18nKey();
    }
}