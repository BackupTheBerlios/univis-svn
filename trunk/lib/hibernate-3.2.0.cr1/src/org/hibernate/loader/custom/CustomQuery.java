//$Id: CustomQuery.java 9636 2006-03-16 14:14:48Z max.andersen@jboss.com $
package org.hibernate.loader.custom;

import java.util.Map;
import java.util.Set;

import org.hibernate.LockMode;
import org.hibernate.loader.CollectionAliases;
import org.hibernate.loader.EntityAliases;
import org.hibernate.type.Type;


/**
 * Extension point allowing any SQL query with named
 * and positional parameters to be executed by Hibernate, 
 * returning managed entities, collections and simple
 * scalar values.
 * 
 * 
 * @author Gavin King
 */
public interface CustomQuery {
	public String getSQL();
	public Set getQuerySpaces();

	/**
	 * Optional, may return null
	 */
	public Map getNamedParameterBindPoints();
	
	public String[] getEntityNames();
	public EntityAliases[] getEntityAliases();
	public CollectionAliases[] getCollectionAliases();
	public LockMode[] getLockModes();
	/**
	 * Optional, may return null
	 */
	public int[] getEntityOwners();
	
	/**
	 * Optional, may return null
	 */
	public int[] getCollectionOwner();
	/**
	 * Optional, may return null
	 */
	public String[] getCollectionRoles();
	
	/**
	 * Optional, may return null
	 */
	public Type[] getScalarTypes();
	/**
	 * Optional, may return null
	 */
	public String[] getScalarColumnAliases();
	/**
	 * Optional, may return null
	 */	
	public String[] getReturnAliases();
	
}
