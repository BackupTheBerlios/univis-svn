//$Id: JTATransactionFactory.java 9601 2006-03-11 18:17:43Z epbernard $
package org.hibernate.transaction;

import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.ConnectionReleaseMode;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.hibernate.TransactionException;
import org.hibernate.jdbc.JDBCContext;
import org.hibernate.cfg.Environment;
import org.hibernate.util.NamingHelper;
import org.hibernate.util.JTAHelper;

/**
 * Factory for <tt>JTATransaction</tt>.
 *
 * @see JTATransaction
 * @author Gavin King
 */
public class JTATransactionFactory implements TransactionFactory {

	private static final Log log = LogFactory.getLog(JTATransactionFactory.class);
	private static final String DEFAULT_USER_TRANSACTION_NAME = "java:comp/UserTransaction";

	protected InitialContext context;
	protected String utName;

	public void configure(Properties props) throws HibernateException {
		try {
			context = NamingHelper.getInitialContext(props);
		}
		catch (NamingException ne) {
			log.error("Could not obtain initial context", ne);
			throw new HibernateException( "Could not obtain initial context", ne );
		}

		utName = props.getProperty(Environment.USER_TRANSACTION);

		if (utName==null) {
			TransactionManagerLookup lookup = TransactionManagerLookupFactory.getTransactionManagerLookup(props);
			if (lookup!=null) utName = lookup.getUserTransactionName();
		}

		if (utName==null) utName = DEFAULT_USER_TRANSACTION_NAME;
	}

	public Transaction createTransaction(JDBCContext jdbcContext, Context transactionContext)
	throws HibernateException {
		return new JTATransaction(context, utName, jdbcContext, transactionContext);
	}

	public ConnectionReleaseMode getDefaultReleaseMode() {
		return ConnectionReleaseMode.AFTER_STATEMENT;
	}

	public boolean isTransactionManagerRequired() {
		return false;
	}

	public boolean areCallbacksLocalToHibernateTransactions() {
		return false;
	}

	public boolean isTransactionInProgress(
			JDBCContext jdbcContext,
	        Context transactionContext,
	        Transaction transaction) {
		try {
			UserTransaction ut;
			if ( transaction != null ) {
				ut = ( ( JTATransaction ) transaction ).getUserTransaction();
			}
			else {
				try {
					ut = ( UserTransaction ) context.lookup( utName );
				}
				catch ( NamingException e ) {
					throw new TransactionException( "Unable to locate UserTrasnaction to check status" );
				}
			}
			return ut != null && JTAHelper.isInProgress( ut.getStatus() );
		}
		catch( SystemException se ) {
			throw new TransactionException( "Unable to check transaction status", se );
		}
	}

}
