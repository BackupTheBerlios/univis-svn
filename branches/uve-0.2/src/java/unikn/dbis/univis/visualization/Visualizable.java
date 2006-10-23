package unikn.dbis.univis.visualization;

/**
 * TODO: document me!!!
 * <p/>
 * <code>Visualizable</code>.
 * <p/>
 * User: raedler, weiler
 * Date: 30.08.2006
 * Time: 00:43:35
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Revision$
 * @since UniVis Explorer 0.2
 */
public interface Visualizable {
    public void clear();
    public void undo();
    public void redo();
    public void zoomIn();
    public void zoomOut();
    public void rotateRight();
    public void rotateLeft();
    public void setMoveable(boolean movable);
    public boolean isMoveable();
}
