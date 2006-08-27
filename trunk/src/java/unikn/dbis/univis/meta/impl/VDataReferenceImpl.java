package unikn.dbis.univis.meta.impl;

import unikn.dbis.univis.meta.VDataReference;
import unikn.dbis.univis.message.MessageResolver;

import java.util.Set;
import java.util.HashSet;
import java.util.LinkedHashSet;

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

    // The joinable attribute.
    private String joinable;

    // The name of the table that contains the data.
    private String tableName;

    // TODO: document me!!!
    private VDataReference parent;

    // TODO: document me!!!
    private Set<VDataReference> children;

    // Whether the data reference is enabled or not.
    private boolean enabled = true;

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
     * Sets the joinable attribute.
     *
     * @param joinable The joinable key shows the join
     *                 attribute.
     */
    public void setJoinable(String joinable) {
        this.joinable = joinable;
    }

    /**
     * Returns the join attribute.
     *
     * @return The join attribute to join tables.
     */
    public String getJoinable() {
        return joinable;
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
     * Returns whether the data reference is enabled
     * or not.
     *
     * @return Whether the data reference is enabled
     *         or not.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether the data reference is enabled
     * or not.
     *
     * @param enabled Whether the data reference is enabled
     *                or not.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Returns the decendants of this data reference.
     *
     * @return The decendants of this data reference within a
     *         <code>Set</code>.
     */
    public Set<VDataReference> getDescendants() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the name that will be shown as tree label or
     * anywhere else on the frontend.
     *
     * @return The label that will be displayed on the
     *         frontend.
     */
    @Override
    public String toString() {
        return MessageResolver.getMessage("data_reference." + getI18nKey());
    }
}