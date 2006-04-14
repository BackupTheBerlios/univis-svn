package unikn.dbis.univis.visualization.item;

/**
 * TODO: document me!!!
 * <p/>
 * <code>VGraph</code>.
 * <p/>
 * User: raedler, weiler
 * Date: 12.04.2006
 * Time: 00:02:10
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public class DefaultVisualizationItem implements VisualizationItem {

    private double value;
    private String name;

    public DefaultVisualizationItem() {

    }

    public DefaultVisualizationItem(String name, double value) {

        this.name = name;
        this.value = value;

    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
