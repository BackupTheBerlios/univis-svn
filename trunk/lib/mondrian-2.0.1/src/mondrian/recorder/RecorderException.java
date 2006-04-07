/*
// $Id: //open/mondrian/src/main/mondrian/recorder/RecorderException.java#1 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2005-2005 Julian Hyde and others.
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package mondrian.recorder;

import mondrian.olap.MondrianException;

/**
 * Exception thrown by MessageRecorder when too many errors have been
 * reported.
 *
 * @author <a>Richard M. Emberson</a>
 * @version $Id: //open/mondrian/src/main/mondrian/recorder/RecorderException.java#1 $
 */
public final class RecorderException extends MondrianException {
     protected RecorderException(String msg) {
        super(msg);
    }
}

// End RecorderException.java
