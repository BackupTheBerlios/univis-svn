//$Id: OptimisticTreeCache.java 9310 2006-02-21 14:11:28Z steveebersole $
package org.hibernate.cache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Comparator;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.cache.Fqn;
import org.jboss.cache.optimistic.DataVersion;
import org.jboss.cache.config.Option;
import org.jboss.cache.lock.TimeoutException;

/**
 * Represents a particular region within the given JBossCache TreeCache
 * utilizing TreeCache's optimistic locking capabilities.
 *
 * @see OptimisticTreeCacheProvider for more details
 *
 * @author Steve Ebersole
 */
public class OptimisticTreeCache implements OptimisticCache {

	// todo : eventually merge this with TreeCache and just add optional opt-lock support there.

	private static final Log log = LogFactory.getLog( OptimisticTreeCache.class);

	private static final String ITEM = "item";

	private org.jboss.cache.TreeCache cache;
	private final String regionName;
	private final String userRegionName;
	private OptimisticCacheSource source;

	public OptimisticTreeCache(org.jboss.cache.TreeCache cache, String regionName)
	throws CacheException {
		this.cache = cache;
		userRegionName = regionName;
		this.regionName = regionName.replace('.', '/');
	}


	// OptimisticCache impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public void setSource(OptimisticCacheSource source) {
		this.source = source;
	}

	public void writeInsert(Object key, Object value, Object currentVersion) {
		writeUpdate( key, value, currentVersion, null );
	}

	public void writeUpdate(Object key, Object value, Object currentVersion, Object previousVersion) {
		try {
			Option option = null;
			if ( source != null ) {
				if ( source.isVersioned() ) {
					option = new Option();
					option.setDataVersion(
							new DataVersionAdapter(
									currentVersion,
							        previousVersion,
							        source.getVersionComparator()
							)
					);
				}
			}
			cache.put( new Fqn( new Object[] { regionName, key } ), ITEM, value, option );
		}
		catch (Exception e) {
			throw new CacheException(e);
		}
	}

	public void writeLoad(Object key, Object value, Object currentVersion) {
		try {
			Option option = new Option();
			option.setFailSilently( true );
			cache.remove( new Fqn( new Object[] { regionName, key } ), "ITEM", option );
			if ( source != null ) {
				if ( source.isVersioned() ) {
					option.setDataVersion(
							new DataVersionAdapter(
									currentVersion,
									null,
									source.getVersionComparator()
							)
					);
				}
			}
			cache.put( new Fqn( new Object[] { regionName, key } ), ITEM, value, option );
		}
		catch (Exception e) {
			throw new CacheException(e);
		}
	}


	// Cache impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public Object get(Object key) throws CacheException {
		try {
			Option option = new Option();
			option.setFailSilently( true );
			return cache.get( new Fqn( new Object[] { regionName, key } ), ITEM, option );
		}
		catch (Exception e) {
			throw new CacheException(e);
		}
	}

	public Object read(Object key) throws CacheException {
		try {
			return cache.get( new Fqn( new Object[] { regionName, key } ), ITEM );
		}
		catch (Exception e) {
			throw new CacheException(e);
		}
	}

	public void update(Object key, Object value) throws CacheException {
		try {
			cache.put( new Fqn( new Object[] { regionName, key } ), ITEM, value );
		}
		catch (Exception e) {
			throw new CacheException(e);
		}
	}

	public void put(Object key, Object value) throws CacheException {
		try {
			// do the put outside the scope of the JTA txn
			Option option = new Option();
			option.setFailSilently( true );
			cache.put( new Fqn( new Object[] { regionName, key } ), ITEM, value, option );
		}
		catch (TimeoutException te) {
			//ignore!
			log.debug("ignoring write lock acquisition failure");
		}
		catch (Exception e) {
			throw new CacheException(e);
		}
	}

	public void remove(Object key) throws CacheException {
		try {
			cache.remove( new Fqn( new Object[] { regionName, key } ) );
		}
		catch (Exception e) {
			throw new CacheException(e);
		}
	}

	public void clear() throws CacheException {
		try {
			cache.remove( new Fqn(regionName) );
		}
		catch (Exception e) {
			throw new CacheException(e);
		}
	}

	public void destroy() throws CacheException {
		try {
			Option option = new Option();
			option.setCacheModeLocal( true );
			cache.remove( new Fqn( regionName ), option );
		}
		catch( Exception e ) {
			throw new CacheException( e );
		}
	}

	public void lock(Object key) throws CacheException {
		throw new UnsupportedOperationException("TreeCache is a fully transactional cache" + regionName);
	}

	public void unlock(Object key) throws CacheException {
		throw new UnsupportedOperationException("TreeCache is a fully transactional cache: " + regionName);
	}

	public long nextTimestamp() {
		return System.currentTimeMillis() / 100;
	}

	public int getTimeout() {
		return 600; //60 seconds
	}

	public String getRegionName() {
		return userRegionName;
	}

	public long getSizeInMemory() {
		return -1;
	}

	public long getElementCountInMemory() {
		try {
			Set children = cache.getChildrenNames( new Fqn(regionName) );
			return children == null ? 0 : children.size();
		}
		catch (Exception e) {
			throw new CacheException(e);
		}
	}

	public long getElementCountOnDisk() {
		return 0;
	}

	public Map toMap() {
		try {
			Map result = new HashMap();
			Set childrenNames = cache.getChildrenNames( new Fqn(regionName) );
			if (childrenNames != null) {
				Iterator iter = childrenNames.iterator();
				while ( iter.hasNext() ) {
					Object key = iter.next();
					result.put(
							key,
					        cache.get( new Fqn( new Object[] { regionName, key } ), ITEM )
						);
				}
			}
			return result;
		}
		catch (Exception e) {
			throw new CacheException(e);
		}
	}

	public String toString() {
		return "OptimisticTreeCache(" + userRegionName + ')';
	}

	public static class DataVersionAdapter implements DataVersion {
		private final Object currentVersion;
		private final Object previousVersion;
		private final Comparator versionComparator;

		public DataVersionAdapter(Object currentVersion, Object previousVersion, Comparator versionComparator) {
			this.currentVersion = currentVersion;
			this.previousVersion = previousVersion;
			this.versionComparator = versionComparator;
		}

		public boolean newerThan(DataVersion dataVersion) {
			if ( previousVersion == null ) {
				log.warn( "Unexpected optimistic lock check on inserted data" );
			}
			Object other = ( ( DataVersionAdapter ) dataVersion ).currentVersion;
			return versionComparator.compare( previousVersion, other ) > 1;
		}
	}
}
