/*
// $Id: //open/mondrian/src/main/mondrian/olap/Namer.java#2 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// (C) Copyright 2000-2005 Kana Software, Inc. and others.
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
//
*/

package mondrian.olap;

/**
 * Namer contains the methods to retrieve localized attributes
 */
public interface Namer {
    public String getLocalResource(String uName, String defaultValue);
}


// End Namer.java
