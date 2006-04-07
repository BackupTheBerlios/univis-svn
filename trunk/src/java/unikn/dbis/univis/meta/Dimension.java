package unikn.dbis.univis.meta;

import java.util.Set;

/**
 * TODO: document me!!!
 * <p/>
 * <code>Dimension</code>.
 * User: raedler, weiler
 * Date: 07.04.2006
 * Time: 15:28:03
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public interface Dimension {

    public boolean isAbstract();

    public String getTableName();

    public Dimension getSuperDimension();

    public Set<Dimension> getSubDimensions();
}