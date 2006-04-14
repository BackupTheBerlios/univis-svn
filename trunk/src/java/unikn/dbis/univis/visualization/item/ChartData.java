package unikn.dbis.univis.visualization.item;

import java.util.List;
import java.util.LinkedList;

/**
 * TODO: document me!!!
 * <p/>
 * <code>ChartData</code>.
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
public class ChartData {

    private String headline;

    private List<VisualizationItem> visualizationItems = new LinkedList<VisualizationItem>();

    private String treePath;

    private String nodeName;

    public ChartData() {
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public void addVisualizationItem(VisualizationItem visualizationItem) {
        visualizationItems.add(visualizationItem);
    }

    public List<VisualizationItem> getVisualizationItems() {
        return visualizationItems;
    }

    public void setVisualizationItems(List<VisualizationItem> visualizationItems) {
        this.visualizationItems = visualizationItems;
    }

    public String getTreePath() {
        return treePath;
    }

    public void setTreePath(String treePath) {
        this.treePath = treePath;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

}
