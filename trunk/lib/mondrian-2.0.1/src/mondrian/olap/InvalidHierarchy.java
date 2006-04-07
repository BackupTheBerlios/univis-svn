/*
//This software is subject to the terms of the Common Public License
//Agreement, available at the following URL:
//http://www.opensource.org/licenses/cpl.html.
//Copyright (C) 2004-2005 TONBELLER AG
//All Rights Reserved.
//You must accept the terms of that agreement to use this software.
//
//$Id: //open/mondrian/src/main/mondrian/olap/InvalidHierarchy.java#1 $
*/
package mondrian.olap;

/**
 * this Throwable will indicate, that the Cube is invalid
 * because there is an hierarchy with no members
 */
public class InvalidHierarchy extends MondrianException {
    public InvalidHierarchy(String message) {
        super(message);
    }
}
