package unikn.dbis.univis.visualization;

import java.awt.*;

/**
 * TODO: document me!!!
 * <p/>
 * Renderable.
 * <p/>
 * User: raedler, weiler
 * Date: 10.05.2006
 * Time: 12:30:30
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public interface Renderable {

    /**
     * Renders a component that could be used
     * to display on screen.
     *
     * @return The component that could be used
     *         to display on screen.
     */
    public Component render();
}