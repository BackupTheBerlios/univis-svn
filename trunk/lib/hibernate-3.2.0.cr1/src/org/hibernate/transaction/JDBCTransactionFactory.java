//$Id: JDBCTransactionFactory.java 9595 2006-03-10 18:14:21Z steve.ebersole@jboss.com $
package org.hibernate.transaction;

import java.util.Properties;
import java.sql.SQLException;

import org.hibernate.ConnectionReleaseMode;
import org.hibernate.Transaction;
import org.hibernate.HibernateException;
import org.hibernate.jdbc.JDBCContext;

/**
 * Factory for <tt>JDBCTransaction</tt>.
 * @see JDBCTransaction
 * @author Anton van Straaten
 */
public final class JDBCTransactionFactory implements TransactionFactory {

	public ConnectionReleaseMode getDefaultReleaseMode() {
		return ConnectionReleaseMode.AFTER_TRANSACTION;
	}

	public Transaction createTransaction(JDBCContext jdbcContext, Context transactionContext)
	throws HibernateException {
		return new JDBCTransaction( jdbcContext, transactionContext );
	}

	public void configure(Properties props) throws HibernateException {}

	public boolean isTransactionManagerRequired() {
		return false;
	}

	public boolean areCallbacksLocalToHibernateTransactions() {
		return true;
	}

	public boolean isTransactionInProgress(
			JDBCContext jdbcContext,
	        Context transactionContext,
	        Transaction transaction) {
//		try {
//			// for JDBC-based transactions, we only want to return true
//			// here if we (this transaction) are managing the transaction
//			return transaction != null &&
//			       transaction.isActive() &&
//			       !jdbcContext.getConnectionManager().isAutoCommit();
//		}
//		catch ( SQLException e ) {
//			// assume we are in auto-commit!
//			return false;
//		}
		return transaction != null && transaction.isActive();
	}

}
