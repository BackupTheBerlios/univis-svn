/*
//This software is subject to the terms of the Common Public License
//Agreement, available at the following URL:
//http://www.opensource.org/licenses/cpl.html.
//Copyright (C) 2004-2005 TONBELLER AG
//All Rights Reserved.
//You must accept the terms of that agreement to use this software.
*/
package mondrian.rolap.cache;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * a map with soft references that is cleaned up in regular intervals. 
 * <p>
 * There is no contains(key) method because it makes no sense - after 
 * contains() returns true, the garbage collector may remove 
 * the value that was contained. Instead the code should call get() and
 * keep a reference to the value to prevent garbage collection.
 * 
 * @author av
 * @since Nov 3, 2005
 */
public class SoftSmartCache implements SmartCache {

    Map cache = new HashMap();
    ReferenceQueue queue = new ReferenceQueue();

    /**
     * an entry in the cache that contains the key for
     * the cache map to remove the entry when its value
     * has been garbage collected
     * 
     * @author rk
     * @since Nov 7, 2005
     */
    class CacheReference extends SoftReference {
        Object key;

        public CacheReference(Object key, Object value) {
            super(value, queue);
            this.key = key;
        }
        
        public String toString() {
            return String.valueOf(get());
        }
    }

    /* (non-Javadoc)
     * @see mondrian.rolap.cache.SmartCache#put(java.lang.Object, java.lang.Object)
     */
    public synchronized Object put(Object key, Object value) {
        // remove garbage collected entries from cache
        CacheReference ref;
        while ((ref = (CacheReference) queue.poll()) != null) {
            cache.remove(ref.key);
        }

        // put new entry into cache
        ref = new CacheReference(key, value);
        ref = (CacheReference) cache.put(key, ref);
        if (ref != null)
            return ref.get();
        return null;
    }

    /* (non-Javadoc)
     * @see mondrian.rolap.cache.SmartCache#get(java.lang.Object)
     */
    public synchronized Object get(Object key) {
        SoftReference ref = (SoftReference) cache.get(key);
        if (ref == null)
            return null;
        Object value = ref.get();
        if (value == null)
            cache.remove(key);
        return value;
    }

    /* (non-Javadoc)
     * @see mondrian.rolap.cache.SmartCache#clear()
     */
    public void clear() {
        cache.clear();
    }
    
    /* (non-Javadoc)
     * @see mondrian.rolap.cache.SmartCache#size()
     */
    public int size() {
        return cache.size();
    }

}
