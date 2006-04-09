package unikn.dbis.univis.meta;

/**
 * TODO: document me!!!
 * <p/>
 * <code>DataReference</code>.
 * <p/>
 * User: raedler, weiler
 * Date: 09.04.2006
 * Time: 11:00:32
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public interface DataReference {
    public String getTableName();
    public void setTableName(String tableName);
}