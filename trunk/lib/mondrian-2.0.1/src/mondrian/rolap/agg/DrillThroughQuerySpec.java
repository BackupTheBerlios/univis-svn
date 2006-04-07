
package mondrian.rolap.agg;

import mondrian.olap.Util;
import mondrian.olap.MondrianDef;
import mondrian.rolap.RolapStar;
import mondrian.rolap.sql.SqlQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

/**
 * Provides the information necessary to generate SQL for a drill-through
 * request.
 * 
 * @author jhyde <a>Richard M. Emberson</a>
 * @version 
 */
class DrillThroughQuerySpec extends AbstractQuerySpec {
    private final CellRequest request;
    private final String[] columnNames;

    public DrillThroughQuerySpec(final CellRequest request) {
        super(request.getMeasure().getStar());
        this.request = request;
        this.columnNames = computeDistinctColumnNames();
    }

    private String[] computeDistinctColumnNames() {
        final List columnNames = new ArrayList();
        final Set columnNameSet = new HashSet();

        final RolapStar.Column[] columns = getColumns();
        for (int i = 0; i < columns.length; i++) {
            RolapStar.Column column = columns[i];
            addColumnName(column, columnNames, columnNameSet);
        }

        addColumnName(request.getMeasure(), columnNames, columnNameSet);

        return (String[]) columnNames.toArray(new String[columnNames.size()]);
    }

    private void addColumnName(final RolapStar.Column column, 
                               final List columnNames, 
                               final Set columnNameSet) {
        String columnName = column.getName();
        if (columnName != null) {
            // nothing
        } else if (column.getExpression() instanceof MondrianDef.Column) {
            columnName = ((MondrianDef.Column) column.getExpression()).name;
        } else {
            columnName = "c" + Integer.toString(columnNames.size());
        }
        // Register the column name, and if it's not unique,
        // generate names until it is.
        for (int j = 0; !columnNameSet.add(columnName); j++) {
            columnName = "x" + Integer.toString(j);
        }
        columnNames.add(columnName);
    }

    public int getMeasureCount() {
        return 1;
    }

    public RolapStar.Measure getMeasure(final int i) {
        Util.assertTrue(i == 0);
        return request.getMeasure();
    }

    public String getMeasureAlias(final int i) {
        Util.assertTrue(i == 0);
        return columnNames[columnNames.length - 1];
    }

    public RolapStar.Column[] getColumns() {
        return request.getColumns();
    }

    public String getColumnAlias(final int i) {
        return columnNames[i];
    }

    public ColumnConstraint[] getConstraints(final int i) {
        final ColumnConstraint constr = 
            (ColumnConstraint) request.getValueList().get(i);
        return (constr == null)
            ? null
            : new ColumnConstraint[] {constr};
    }

    public String generateSqlQuery() {
        SqlQuery sqlQuery = newSqlQuery();

        nonDistinctGenerateSQL(sqlQuery);

        return sqlQuery.toString();
    }
    protected void addMeasure(final int i, final SqlQuery sqlQuery) {
        RolapStar.Measure measure = getMeasure(i);

        Util.assertTrue(measure.getTable() == getStar().getFactTable());
        measure.getTable().addToFrom(sqlQuery, false, true);

        String expr = measure.getExpression(sqlQuery);
        sqlQuery.addSelect(expr, getMeasureAlias(i));
    }
    protected boolean isAggregate() {
        return false;
    }
}
