package unikn.dbis.univis.hibernate;

import org.hibernate.Transaction;
import org.hibernate.Session;

/**
 * TODO: document me!!!
 * <p/>
 * <code>TransactionCallback</code>.
 * <p/>
 * User: raedler
 * Date: 28.09.2006
 * Time: 14:01:58
 *
 * @author Roman R&auml;dle
 * @version $Revision$
 * @since UniVis Explorer 0.3
 */
public interface TransactionCallback {
    public abstract void execute(Session session, Transaction trx) throws RuntimeException;
}
