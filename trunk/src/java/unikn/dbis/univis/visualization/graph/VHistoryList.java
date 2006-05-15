package unikn.dbis.univis.visualization.graph;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * TODO: document me!!!
 * <p/>
 * <code>VHistoryList</code>.
 * <p/>
 * User: raedler, weiler
 * Date: 15.05.2006
 * Time: 17:06:14
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public class VHistoryList<T> {

    private List<T> current = new ArrayList<T>();
    private List<List<T>> history = new ArrayList<List<T>>();

    public void add(T o) {
        current.add(o);
    }

    public void get(int index) {
        current.get(index);
    }

    public List<T> getLastOfHistory() {
        if (history.size() > 0) {
            return history.get(history.size() - 1);
        }

        throw new IllegalStateException("The history doesn't contain elements.");
    }

    public List<T> getCurrent() {
        return current;
    }

    public void historize() {
        history.add(current);
        current = new ArrayList<T>();
    }

    public void historyBack() {
        current = history.remove(history.size() - 1);
    }
}
