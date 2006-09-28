/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2005, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * -----------------------
 * ChartEditorFactory.java
 * -----------------------
 * (C) Copyright 2005, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   ;
 *
 * $Id: ChartEditorFactory.java,v 1.1.2.1 2005/11/28 15:19:32 mungady Exp $
 *
 * Changes
 * -------
 * 28-Nov-2005 : Version 1 (DG);
 *
 */

package org.jfree.chart.editor;

import org.jfree.chart.JFreeChart;

/**
 * A factory for creating new {@link ChartEditor} instances.
 */
public interface ChartEditorFactory {
    
    /**
     * Creates an editor for the given chart.
     * 
     * @param chart  the chart.
     */
    public ChartEditor createEditor(JFreeChart chart);

}
