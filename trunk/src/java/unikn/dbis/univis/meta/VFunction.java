/*
 * Copyright 2005-2006 UniVis Explorer development team.
 *
 * This file is part of UniVis Explorer
 * (http://phobos22.inf.uni-konstanz.de/univis).
 *
 * UniVis Explorer is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * Please see COPYING for the complete licence.
 */
package unikn.dbis.univis.meta;

/**
 * TODO: document me!!!
 * <p/>
 * VFunction.
 * <p/>
 * User: raedler, weiler
 * Date: 27.08.2006
 * Time: 23:13:07
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.2
 */
public interface VFunction extends VDataReference, Selectable {

  /**
     * Returns the name of the function.
     *
     * @return The name of the function.
     */
    public String getDefinition();

    /**
     * Sets the name of the function.
     *
     * @param function The name of the function.
     */
    public void setDefinition(String function);
}
