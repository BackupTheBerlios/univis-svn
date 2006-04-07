package unikn.dbis.univis.meta;

import java.util.Set;

/**
 * TODO: document me!!!
 * <p/>
 * <code>Cube</code>.
 * User: raedler, weiler
 * Date: 07.04.2006
 * Time: 15:27:44
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public interface Cube {

    public Set<Dimension> getDimensions();
}