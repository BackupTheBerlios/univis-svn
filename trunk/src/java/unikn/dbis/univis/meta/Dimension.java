package unikn.dbis.univis.meta;

import javax.swing.tree.TreeNode;
import java.util.List;

/**
 * TODO: document me!!!
 * <p/>
 * <code>Dimension</code>.
 * <p/>
 * User: raedler, weiler
 * Date: 07.04.2006
 * Time: 15:28:03
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public interface Dimension extends TreeNode {

    public boolean isAbstract();

    public String getTableName();

    public Dimension getSuperDimension();

    public List<Dimension> getSubDimensions();
}