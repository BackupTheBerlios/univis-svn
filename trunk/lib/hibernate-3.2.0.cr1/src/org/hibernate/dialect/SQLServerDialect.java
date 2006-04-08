//$Id: SQLServerDialect.java 9328 2006-02-23 17:32:47Z steveebersole $
package org.hibernate.dialect;

import java.sql.Types;
import java.util.List;
import java.util.ArrayList;

import org.hibernate.Hibernate;
import org.hibernate.LockMode;
import org.hibernate.QueryException;
import org.hibernate.engine.Mapping;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.type.Type;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.function.SQLFunction;

/**
 * A dialect for Microsoft SQL Server 2000
 * @author Gavin King
 */
public class SQLServerDialect extends SybaseDialect {

	public SQLServerDialect() {
		registerColumnType( Types.VARBINARY, "image" );
		registerColumnType( Types.VARBINARY, 8000, "varbinary($l)" );

		registerFunction( "second", new SQLFunctionTemplate(Hibernate.INTEGER, "datepart(second, ?1)") );
		registerFunction( "minute", new SQLFunctionTemplate(Hibernate.INTEGER, "datepart(minute, ?1)") );
		registerFunction( "hour", new SQLFunctionTemplate(Hibernate.INTEGER, "datepart(hour, ?1)") );
		registerFunction( "locate", new StandardSQLFunction("charindex", Hibernate.INTEGER) );

		registerFunction( "extract", new SQLFunctionTemplate( Hibernate.INTEGER, "datepart(?1, ?3)" ) );
		registerFunction( "trim", new TrimFunction() );
		registerFunction( "mod", new SQLFunctionTemplate( Hibernate.INTEGER, "?1 % ?2" ) );
		registerFunction( "bit_length", new SQLFunctionTemplate( Hibernate.INTEGER, "datalength(?1) * 8" ) );

		registerKeyword("top");
	}

	public String getNoColumnsInsertString() {
		return "default values";
	}

	static int getAfterSelectInsertPoint(String sql) {
		int selectIndex = sql.toLowerCase().indexOf( "select" );
		final int selectDistinctIndex = sql.toLowerCase().indexOf( "select distinct" );
		return selectIndex + ( selectDistinctIndex == selectIndex ? 15 : 6 );
	}

	public String getLimitString(String querySelect, int offset, int limit) {
		if ( offset > 0 ) {
			throw new UnsupportedOperationException( "sql server has no offset" );
		}
		return new StringBuffer( querySelect.length()+8 )
			.append(querySelect)
			.insert( getAfterSelectInsertPoint(querySelect), " top " + limit )
			.toString();
	}

	/**
	 * Use <tt>insert table(...) values(...) select SCOPE_IDENTITY()</tt>
	 */
	public String appendIdentitySelectToInsert(String insertSQL) {
		return insertSQL + " select scope_identity()";
	}

	public boolean supportsLimit() {
		return true;
	}

	public boolean useMaxForLimit() {
		return true;
	}

	public boolean supportsLimitOffset() {
		return false;
	}

	public boolean supportsVariableLimit() {
		return false;
	}

	public char closeQuote() {
		return ']';
	}

	public char openQuote() {
		return '[';
	}

	public String appendLockHint(LockMode mode, String tableName) {
		if ( mode.greaterThan(LockMode.READ) ) {
			// does this need holdlock also? : return tableName + " with (updlock, rowlock, holdlock)";
			return tableName + " with (updlock, rowlock)";
		}
		else {
			return tableName;
		}
	}

	public String getSelectGUIDString() {
		return "select newid()";
	}

	// The current_timestamp is more accurate, but only known to be supported
	// in SQL Server 7.0 and later (i.e., Sybase not known to support it at all)
	public String getCurrentTimestampSelectString() {
		return "select current_timestamp";
	}

	/**
	 * A SQLServer-specific version of the ANSI-SQL trim function as SQLServer
	 * does not support such a thing.
	 */
	public static class TrimFunction implements SQLFunction {
		private static final SQLFunction LEADING_SPACE_TRIM = new SQLFunctionTemplate( Hibernate.STRING, "ltrim( ?1 )");
		private static final SQLFunction TRAILING_SPACE_TRIM = new SQLFunctionTemplate( Hibernate.STRING, "rtrim( ?1 )");
		private static final SQLFunction BOTH_SPACE_TRIM = new SQLFunctionTemplate( Hibernate.STRING, "ltrim( rtrim( ?1 ) )");
		private static final SQLFunction BOTH_SPACE_TRIM_FROM = new SQLFunctionTemplate( Hibernate.STRING, "ltrim( rtrim( ?2 ) )");

		private static final SQLFunction LEADING_TRIM = new SQLFunctionTemplate( Hibernate.STRING, "replace( replace( rtrim( replace( replace( ?1, ' ', '${space}$' ), ?2, ' ' ) ), ' ', ?2 ), '${space}$', ' ' )" );
		private static final SQLFunction TRAILING_TRIM = new SQLFunctionTemplate( Hibernate.STRING, "replace( replace( ltrim( replace( replace( ?1, ' ', '${space}$' ), ?2, ' ' ) ), ' ', ?2 ), '${space}$', ' ' )" );
		private static final SQLFunction BOTH_TRIM = new SQLFunctionTemplate( Hibernate.STRING, "replace( replace( ltrim( rtrim( replace( replace( ?1, ' ', '${space}$' ), ?2, ' ' ) ) ), ' ', ?2 ), '${space}$', ' ' )" );

		public Type getReturnType(Type columnType, Mapping mapping) throws QueryException {
			return Hibernate.STRING;
		}

		public boolean hasArguments() {
			return true;
		}

		public boolean hasParenthesesIfNoArguments() {
			return false;
		}

		public String render(List args, SessionFactoryImplementor factory) throws QueryException {
			// according to both the ANSI-SQL and EJB3 specs, trim can either take
			// exactly one parameter or a variable number of parameters between 1 and 4.
			// from the SQL spec:
			//
			// <trim function> ::=
			//      TRIM <left paren> <trim operands> <right paren>
			//
			// <trim operands> ::=
			//      [ [ <trim specification> ] [ <trim character> ] FROM ] <trim source>
			//
			// <trim specification> ::=
			//      LEADING
			//      | TRAILING
			//      | BOTH
			//
			// If only <trim specification> is omitted, BOTH is assumed;
			// if <trim character> is omitted, space is assumed
			if ( args.size() == 1 ) {
				// we have the form: trim(trimSource)
				//      so we trim leading and trailing spaces
				return BOTH_SPACE_TRIM.render( args, factory );
			}
			else if ( "from".equalsIgnoreCase( ( String ) args.get( 0 ) ) ) {
				// we have the form: trim(from trimSource).
				//      This is functionally equivalent to trim(trimSource)
				return BOTH_SPACE_TRIM_FROM.render( args, factory );
			}
			else {
				// otherwise, a trim-specification and/or a trim-character
				// have been specified;  we need to decide which options
				// are present and "do the right thing"
				boolean leading = true;         // should leading trim-characters be trimmed?
				boolean trailing = true;        // should trailing trim-characters be trimmed?
				String trimCharacter = null;    // the trim-character
				String trimSource = null;       // the trim-source

				// potentialTrimCharacterArgIndex = 1 assumes that a
				// trim-specification has been specified.  we handle the
				// exception to that explicitly
				int potentialTrimCharacterArgIndex = 1;
				String firstArg = ( String ) args.get( 0 );
				if ( "leading".equalsIgnoreCase( firstArg ) ) {
					trailing = false;
				}
				else if ( "trailing".equalsIgnoreCase( firstArg ) ) {
					leading = false;
				}
				else if ( "both".equalsIgnoreCase( firstArg ) ) {
				}
				else {
					potentialTrimCharacterArgIndex = 0;
				}

				String potentialTrimCharacter = ( String ) args.get( potentialTrimCharacterArgIndex );
				if ( "from".equalsIgnoreCase( potentialTrimCharacter ) ) {
					trimCharacter = "' '";
					trimSource = ( String ) args.get( potentialTrimCharacterArgIndex + 1 );
				}
				else if ( potentialTrimCharacterArgIndex + 1 >= args.size() ) {
					trimCharacter = "' '";
					trimSource = potentialTrimCharacter;
				}
				else {
					trimCharacter = potentialTrimCharacter;
					if ( "from".equalsIgnoreCase( ( String ) args.get( potentialTrimCharacterArgIndex + 1 ) ) ) {
						trimSource = ( String ) args.get( potentialTrimCharacterArgIndex + 2 );
					}
					else {
						trimSource = ( String ) args.get( potentialTrimCharacterArgIndex + 1 );
					}
				}

				List argsToUse = null;
				argsToUse = new ArrayList();
				argsToUse.add( trimSource );
				argsToUse.add( trimCharacter );

				if ( trimCharacter.equals( "' '" ) ) {
					if ( leading && trailing ) {
						return BOTH_SPACE_TRIM.render( argsToUse, factory );
					}
					else if ( leading ) {
						return LEADING_SPACE_TRIM.render( argsToUse, factory );
					}
					else {
						return TRAILING_SPACE_TRIM.render( argsToUse, factory );
					}
				}
				else {
					if ( leading && trailing ) {
						return BOTH_TRIM.render( argsToUse, factory );
					}
					else if ( leading ) {
						return LEADING_TRIM.render( argsToUse, factory );
					}
					else {
						return TRAILING_TRIM.render( argsToUse, factory );
					}
				}
			}
		}
	}
}