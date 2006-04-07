/*
// $Id: //open/mondrian/src/main/mondrian/rolap/agg/QuerySpec.java#1 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// (C) Copyright 2001-2005 Kana Software, Inc. and others.
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
//
// jhyde, 30 August, 2001
*/

package mondrian.rolap.agg;

import mondrian.rolap.RolapStar;
import mondrian.rolap.sql.SqlQuery;

/**
 * Contains the information necessary to generate a SQL statement to
 * retrieve a set of cells.
 *
 * @author jhyde <a>Richard M. Emberson</a>
 */
public interface QuerySpec {
    RolapStar getStar();
    int getMeasureCount();
    RolapStar.Measure getMeasure(int i);
    String getMeasureAlias(int i);
    RolapStar.Column[] getColumns();
    String getColumnAlias(int i);
    ColumnConstraint[] getConstraints(int i);

    String generateSqlQuery();
}
