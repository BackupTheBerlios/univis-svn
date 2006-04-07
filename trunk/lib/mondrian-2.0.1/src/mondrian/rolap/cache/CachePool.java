/*
// $Id: //open/mondrian/src/main/mondrian/rolap/cache/CachePool.java#6 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// (C) Copyright 2004-2005 Julian Hyde and others.
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package mondrian.rolap.cache;

import mondrian.rolap.RolapSchema;

/**
 * A <code>CachePool</code> manages the objects in a collection of
 * caches.
 *
 * @author av
 */
public class CachePool {

    /** The singleton. **/
    private static CachePool instance = new CachePool();

    /** Returns the singleton. **/
    public static CachePool instance() {
        return instance;
    }

    private CachePool() {
    }

    public void flush() {
        RolapSchema.clearCache();
    }

}

// End CachePool.java
