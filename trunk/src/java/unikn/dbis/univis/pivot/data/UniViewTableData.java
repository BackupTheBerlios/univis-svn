package unikn.dbis.univis.pivot.data;

import unikn.dbis.univis.meta.VDataReference;
import unikn.dbis.univis.meta.VDimension;

import java.util.Iterator;
import java.util.Vector;

/**
 * @author Marion Herb
 *         <p/>
 *         Pivottable data structure
 */
public class UniViewTableData {

    Vector xAxisNodes = new Vector();
    Vector yAxisNodes = new Vector();
    Vector measureNodes = new Vector();

    int amountCols;

    int groupsInY;
    Vector allJoinTables;
    String pivotTableType;

    // Anzeige für NULL-Values in den Dimensions-Elementen
    static String nullValue = "*NULL*";
    PSQL myPSQL = new PSQL();

    /**
     * get data for Pivottable
     *
     * @param xAxisNodes
     * @param yAxisNodes
     * @param measureNodes
     * @return Returns a Vector with data for pivottable
     */
    public Vector getPivottableData(Vector xAxisNodes, Vector yAxisNodes, Vector measureNodes) {
        this.xAxisNodes = xAxisNodes;
        this.yAxisNodes = yAxisNodes;
        this.measureNodes = measureNodes;

        allJoinTables = buildJoinTables();

//		System.out.println("alljointables: " + new ArrayList(allJoinTables).toString());
//		int j = 0;
//		for (Iterator x = allJoinTables.iterator(); x.hasNext();) {
//			System.out.println("alljointables: " + (j++) + " " + x.next());
//		}

        String sql = new String();
        String sql_select = new String();
        String sql_from = new String();
        String sql_order = new String();

        boolean buildSelect = false;
        boolean buildFrom = false;
        boolean buildOrder = false;

        /* 1. Zusammenbasteln des SQLs: zuerst SELECT incl. CASE-Abfragen ... */
        sql_select = buildSqlSelect();
        if (sql_select != null)
            buildSelect = true;

        /* 2. ... dann FROM-Block inclusive JOINS ... */
        if (buildSelect)
            sql_from = buildSqlFrom();
        if (sql_from != null)
            buildFrom = true;

        /* 3. ... dann ORDER-BY-Block ... */
        if (buildFrom)
            sql_order = buildSqlOrder();
        if (sql_order != null)
            buildOrder = true;

        Vector values = new Vector();
        // now execute SQL and move tupels into Vector for display in JTable
        if (buildSelect && buildFrom && buildOrder) {
            try {
                sql = sql_select + sql_from + sql_order;
                myPSQL.execute(sql);

                Object[] resultTemp;
                while (!myPSQL.result.isLast()) {
                    Vector valuesSingleRow = new Vector();
                    /* numbers as Integers or Doubles will align right automatically */
                    resultTemp = myPSQL.nextAsObject();
                    for (int i = 0; i < resultTemp.length; i++) {
                        valuesSingleRow.addElement(resultTemp[i]);
                    }
                    values.addElement(valuesSingleRow);
                }

            }
            catch (Exception e) {
                System.out.println("PSQL: Could not get data for Pivottable-Data Query!");
                e.printStackTrace();
                return null;
            }
            finally {
                myPSQL.close();
            }
        }
        return values;
    }

    /**
     * Get the header info for pivot table
     *
     * @param xAxisNodes
     * @param yAxisNodes
     * @return Returns String[] of column names
     */
    public Vector getPivottableHeader(Vector xAxisNodes, Vector yAxisNodes) {
        int size = amountCols + groupsInY;
        Vector columnNames = new Vector();

        for (int j = 0; j < groupsInY; j++) {
            columnNames.addElement(((VDataReference) yAxisNodes.get(j)).getTableName());
        }

        try {
            if (!pivotTableType.equals("y_value")) {
                myPSQL.execute("select name from " + ((VDataReference) yAxisNodes.get(0)).getTableName() + ";");
                for (int i = groupsInY; i < size; i++) {
                    columnNames.addElement(new String(myPSQL.next()[0]));
                }
            }
            else {
                columnNames.addElement("SUM");
            }
//			int j = 0;
//			for (Iterator x = columnNames.iterator(); x.hasNext();) {
//			System.out.println("columnNames: " + (j++) + " " + x.next());
//			}
            return columnNames;
        }
        catch (Exception e) {
            System.out.println("PSQL: Could not get data for Pivottable-Header Query.");
            e.printStackTrace();
            return null;
        }
        finally {
            myPSQL.close();
        }
    }

    /*
      * SQL-Teil bauen welcher alle Spalten definiert: also alle Gruppierungen,
      * und anschliessend Platzhalter für alle Spalten in X-Richtung
      * Sonderfall: wenn kein Element für X-Achse gegeben -> in X-Achse wird nur die
      * Summe für die Gruppierung auf der Y-Achse geschrieben, also nur 1 Spalte
      */
    private String buildSqlSelect() {

        // Zuallererst Festlegen des Typs der Pivottabelle
        String pivotTableTypes[] = {"x_y_value", "x_value", "y_value"};
        int groupedXValues = xAxisNodes.isEmpty() ? 0 : xAxisNodes.size();
        int groupedYValues = groupsInY;
        if ((groupedYValues > 0) && (groupedXValues > 0)) {
            pivotTableType = pivotTableTypes[0]; // x_y_value => Werte auf x- und Y-Achse
        }
        else if (groupedYValues == 0) {
            pivotTableType = pivotTableTypes[1]; // x_value => Werte nur auf X-Achse (momentan nur 1 Knoten mgl.)
        }
        else if (groupedXValues == 0) {
            pivotTableType = pivotTableTypes[2]; // y_value => Werte nur auf Y-Achse
        }

        StringBuffer select = new StringBuffer("");

        /* abbrechen wenn keine Werte auf X-Achse denn dann wird lediglich ein
           * Subselect benötigt, und kein Mergen verschiedener Selects
           */
        if (pivotTableType.equals("y_value")) {
            return "";
        } // besser NULL aber nicht möglich

        select.append("SELECT \n");

        int instancesOnXAxis;

        /* momentan noch nur 1 Element */
        String tmpTable = ((VDataReference) yAxisNodes.get(0)).getTableName();
        instancesOnXAxis = getAmountOfTableRows(tmpTable);

        amountCols = instancesOnXAxis;
        // bei mehr als x Spalten wird abgebrochen -> entspricht Subselects -> tatsächliche Grenze??
        if (amountCols > 150) {
            System.out.println(amountCols + " Columns are too many for X-Axis! Please transform your query.");
            return null;
        }
        /*
           * outer loop runs through elements displayed in x-axis ->
           * instancesOnXAxis + groupedYValues
           */
        for (int i = 0; i < (instancesOnXAxis + groupedYValues); i++) {
            if (i != 0) {
                select.append(",\n");
            }

            select.append("CASE ");

            /* inner loop runs through subselect-joins -> instancesOnXAxis */
            if (i < groupedYValues) {
                for (int j = 0; j < instancesOnXAxis; j++) {
                    select.append("\n\tWHEN not t" + j + ".y" + i
                            + " isnull THEN t" + j + ".y" + i);
                }
                select.append("\n");
                select.append("END");
                select.append(" AS y" + i);
            }
            else {
                select.append("WHEN \"y" + i + "\"");
                select.append(" isnull THEN 0 ELSE \"y" + i + "\" ");
                select.append("END");
            }
        }
        select.append("\n\n");

        return select.toString();
    }

    /*
      * Subselects generieren
      */
    private String buildSqlFrom() {

        int groupedYValues = groupsInY;

        // aneinanderhängen der Subselects entweder mit FULL JOIN (wenn Werte auf Y-Achse vorhanden)
        // oder Konkatenation (wenn nur Werte auf X-Achse)
        String attached = new String();
//		if (groupedYValues > 0) { 
        if (pivotTableType.equals("x_y_value")) {
            attached = "\nFULL JOIN\n";
        }
        else if (pivotTableType.equals("x_value")) {
            attached = "\n, \n";
        }

        StringBuffer from = new StringBuffer();

        try {

            int upperBorder = 1; // damit Schleife 1x durchlaufen wird
            // nur wenn mind. 1 Wert auf X-Achse ist kann man seine Instanzen holen
            // also Ausnahme bei Anfrage bei nur Knote auf Y-Achse, dann ist per default upperBorder=-1
            if (!pivotTableType.equals("y_value")) {
                String xDimTables = ((VDataReference) yAxisNodes.get(0)).getTableName(); // Abschluesse...
                upperBorder = getAmountOfTableRows(xDimTables);
                myPSQL.execute("select name from " + xDimTables + ";");
                from.append("FROM ");
            }

            String xDimTable = new String();

            /* loop through elements on x-axis for creating the subselects */
            for (int instanceOnX = 0; instanceOnX < upperBorder; instanceOnX++) {
                /* Loop for which instance? e.g. "mit Bachelorabschluss", "I"... */
                if (!pivotTableType.equals("y_value")) {
                    xDimTable = myPSQL.next()[0];
                    from.append("\n( ");
                }

                from.append("SELECT\n");
                /* loop through elements on y-axis */
                /* SELECT elements */
                String fromTable = new String();
                Vector uniqueY = buildUniqueNodes(yAxisNodes);

                int j;
                for (j = 0; j < groupedYValues; j++) {
                    fromTable = ((VDataReference) uniqueY.get(j)).getTableName();

//					if (!pivotTableType.equals("y_value")) {
                    from.append("CASE WHEN ");
//					}
                    from.append(fromTable + ".name");
//					if (!pivotTableType.equals("y_value")) {
                    from.append(" isnull THEN '" + nullValue + "' ELSE "
                            + fromTable + ".name");
                    from.append(" END AS y" + j);
//					}
                    from.append(",\n");
                }

                /*
                     * um Measure CASE WHEN-Abfrage: denn bei nur Wert auf Y-Achse kann es vorkommen
                     * z.B. wenn man HZP angezeigt haben will, dass 1. Wert in Spalte NULL ist,
                     * dann kracht es bei getValueAt() von EnvelopeTableModell der auf NULL trifft
                     * "CASE WHEN sum(SOS_CUBE.koepfe) isnull THEN '*NULL*' ELSE sum(SOS_CUBE.koepfe) END"
                     */
                for (Iterator iter = measureNodes.iterator(); iter.hasNext();) {
                    VDataReference element = (VDataReference) iter.next();
                    String cubePlusMeasure = ((VDataReference) allJoinTables.get(0)).getTableName() + "." + element.getTableName();
                    if (pivotTableType.equals("y_value")) {
                        from.append("CASE WHEN ");
                    }
                    from.append("sum(" + cubePlusMeasure + ")");
                    if (pivotTableType.equals("y_value")) {
                        from.append(" isnull THEN 0 ELSE sum(" + cubePlusMeasure + ") END");
                        //					from.append("\"y" + (instanceOnX+j) + "\" -- " + xDimTable);
                    }
                    from.append(" AS \"y" + (instanceOnX + j) + "\"");
                    if (iter.hasNext()) from.append(",");
                    from.append("\n");
                }

                /*
                     * JOINS
                     */
                from.append("FROM " + ((VDataReference) allJoinTables.get(0)).getTableName() + "\n");

                /*
                     * Loop through all needed tables in order to join them all to
                     * the fact table, important to first join all Blueps to fact table
                     * and after that the dimensions because otherwise syntax errors appear
                     */
                // 1 because first element was cube, already taken out
                for (int k = 1; k < allJoinTables.size(); k++) {
                    VDataReference node = (VDataReference) allJoinTables.get(k);
                    from.append("FULL JOIN " + node.getTableName());
                    from.append(" ON ( " + node.getTableName() + ".id");
                    from.append(" = ");
                    // Joins always carried out with root

                    //TODO: from.append(node.getJoinable().getTable() + ".id");
                    from.append(" ) " + "\n");
                }

                /* WHERE */
                if (!pivotTableType.equals("y_value")) {
                    from.append("WHERE " + ((VDataReference) xAxisNodes.get(0)).getTableName()
                            + ".name = '" + xDimTable + "'\n");
                }

                /* GROUP BY */
                if ((pivotTableType.equals("x_y_value")) || (pivotTableType.equals("y_value"))) {
                    from.append("GROUP BY ");
                    for (int l = 0; l < groupedYValues; l++) {
                        from.append(((VDataReference) uniqueY.get(l)).getTableName() + ".name");
                        if (l < (groupedYValues - 1))
                            from.append(", ");
                    }
                }
                if (!pivotTableType.equals("y_value")) {
                    from.append("\n ) AS t" + instanceOnX + "\n");
                }
                // wenn Werte auf Y-Achse:
                /* subselects need a join-criteria */
//				if (groupedYValues > 0) {
                if (pivotTableType.equals("x_y_value")) {
                    if (instanceOnX > 0) {
                        from.append("\nON (");
                        /* loop through elements on yAxis */
                        for (int outer = 0; outer < groupedYValues; outer++) {
                            from.append("\nCASE");
                            /* loop through amount of subselects */
                            for (int inner = 0; inner < instanceOnX; inner++) {
                                from.append("\n\tWHEN not t" + inner + ".y" + outer
                                        + " isnull THEN ");
                                from.append("t" + inner + ".y" + outer);
                            }
                            from.append("\nEND = t" + instanceOnX + ".y" + outer + "\n");
                            if (outer < (groupedYValues - 1))
                                from.append("AND ");
                        }
                        from.append(")\n");
                    }
                }

                // aneinanderhängen der Subselects
                // entweder mit "FULL JOIN" oder ","
                if (instanceOnX != (upperBorder - 1))
                    from.append(attached);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("PSQL: Could not execute From Query.");
            return null;
        }
        finally {
            myPSQL.close();
        }
        return from.toString();
    }

    /*
      * Order-Klausel generieren
      */
    private String buildSqlOrder() {

        StringBuffer orderBy = new StringBuffer();
        // order by nicht nötig bei Werten nur in X-Achse
        if (!pivotTableType.equals("x_value")) {
            orderBy.append("\nORDER BY ");
            for (int i = 0; i < groupsInY; i++) {
                if (i != 0)
                    orderBy.append(", ");
                orderBy.append("y" + i);
            }
        }
        return orderBy.append(";").toString();
    }

    /*
      * Eliminiert Duplikate in Nodes
      * Bedeutung: wenn Node mehrmals gedroppt wird, findet trotzdem nur einmalige
      * Gruppierung danach statt
      */
    private Vector buildUniqueNodes(Vector axisNodes) {
        int groupedYValues = yAxisNodes.size();
        Vector uniqueNodes = new Vector();

        for (int i = 0; i < groupedYValues; i++) {
            if (!uniqueNodes.contains(axisNodes.elementAt(i)))
                uniqueNodes.add(axisNodes.elementAt(i));
        }
        return uniqueNodes;
    }

    /*
      * which ones are needed? connecting fact table with all tables in x- as
      * well as in y-axis, as well as all bluep-tables on the way down to
      * those dim-tables
      */
    private Vector buildJoinTables() {
        int nodesOnXAxis = xAxisNodes.size();
        int nodesOnYAxis = yAxisNodes.size();
        Vector joinNodes = new Vector();

        // zuerst Faktentabelle in Vektor
        // man nehme zufaelligen Knoten aus einem Vektor und holt Cube dazu
        // -> es wird momentan nur 1 Cube angezeigt und keine Schneidung mehrerer

        // wichtig: alle Blueps jeweils vor ihren Dims einsortieren
        // first all entries from x-axis ...
        if (!xAxisNodes.isEmpty()) { // -> es exisitieren Werte in X-Achse
            //TODO: joinNodes.add(((VDimension) xAxisNodes.get(0)).getSupportedCubes().getCube());
            for (int i = 0; i < nodesOnXAxis; i++) {
                VDataReference node = (VDataReference) xAxisNodes.get(i);
                // if not already inserted
                if (!joinNodes.contains(node)) {
                    VDataReference rootNode = ((VDimension) node).getBlueprint();
                    // test if rootnode is Cube or not
                    // if not add root node as well
                    if (!joinNodes.contains(rootNode)) {
                        joinNodes.add(rootNode);
                    }
                    joinNodes.add(node);
                }
            }
            groupsInY = 0;
        }

        // ... then all entries from y-axis
        if (!yAxisNodes.isEmpty()) { // -> es exisitieren Werte in Y-Achse
            // nur Cube adden falls noch keiner drin!
            if (xAxisNodes.isEmpty()) {
                // TODO joinNodes.add(yAxisNodes.get(0).getCube());
            }
            for (int i = 0; i < nodesOnYAxis; i++) {
                VDataReference node = (VDataReference) yAxisNodes.get(i);
                // if not already inserted
                if (!joinNodes.contains(node)) {
                    VDataReference rootNode = ((VDimension) node).getBlueprint();
                    // test if rootnode is Cube or not
                    // if not add root node as well
                    if (!joinNodes.contains(rootNode)) {
                        joinNodes.add(rootNode);
                    }
                    joinNodes.addElement(node);
                    groupsInY ++;
                }
            }
        }
//		System.out.println("groupsInY " + groupsInY);
        return joinNodes;
    }

    /*
      * Methode holt Anzahl der Dimensions-Elemente in X-Achse also Anzahl Spalten
      */
    private int getAmountOfTableRows(String tmpTable) {
        int i = -1;
        try {
            myPSQL.execute("select count(*) from " + tmpTable + ";");
            myPSQL.result.next();
            i = myPSQL.result.getInt(1);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out
                    .println("PSQL: Could not retrieve amount of table rows.");
        }
        finally {
            myPSQL.close();
        }
        return i;
    }
}
