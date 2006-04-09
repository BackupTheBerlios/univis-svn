package unikn.dbis.univis.meta.impl;

import unikn.dbis.univis.meta.TreeFresh;
import unikn.dbis.univis.meta.DiceBox;

/**
 * TODO: document me!!!
 * <p/>
 * <code>DiceBoxImpl</code>.
 * <p/>
 * User: raedler, weiler
 * Date: 09.04.2006
 * Time: 01:56:32
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public class DiceBoxImpl extends TreeFresh implements DiceBox {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Clones the tree fresh item to handle unique occurence
     * in the tree to perform tree path actions.
     *
     * @param clone The clone with the settings of the super class.
     * @return The cloned tree fresh item.
     * @throws CloneNotSupportedException This exception occures if the clone
     *                                    couldn't be produced.
     */
    public TreeFresh cloneSpecific(TreeFresh clone) throws CloneNotSupportedException {
        return clone;
    }
}