package unikn.dbis.univis.visualization;

import java.awt.*;

/**
 * TODO: document me!!!
 * <p/>
 * AbstractRenderer.
 * <p/>
 * User: raedler, weiler
 * Date: 10.05.2006
 * Time: 12:51:29
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public abstract class AbstractRenderer implements Renderable {

    // The rendered component or null.
    private Component component;

    /**
     * Renders a component that could be used
     * to display on screen.
     *
     * @return The component that could be used
     *         to display on screen.
     */
    public Component render() {
        if (component == null) {
            component = renderComponent();
        }
        return component;
    }

    /**
     * Renders the component that should be displayed
     * at screen.
     *
     * @return The component that should be displayed
     *         at screen.
     */
    public abstract Component renderComponent();
}