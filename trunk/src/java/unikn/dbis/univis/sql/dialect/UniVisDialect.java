package unikn.dbis.univis.sql.dialect;

import org.hibernate.dialect.Dialect;

/**
 * TODO: document me!!!
 * <p/>
 * <code>UniVisDialect</code>.
 * <p/>
 * User: raedler, weiler
 * Date: 10.04.2006
 * Time: 01:37:14
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public abstract class UniVisDialect extends Dialect {
    public abstract String getSelect();

    public static String generateTableAlias(String rootAlias, int tableNumber) {
        if (tableNumber == 0) return rootAlias;
        StringBuffer buf = new StringBuffer().append(rootAlias);
        if (!rootAlias.endsWith("_")) buf.append('_');
        return buf.append(tableNumber).append('_').toString();
    }

    public void test() {
        //super.createOuterJoinFragment().addCrossJoin();
    }
}
