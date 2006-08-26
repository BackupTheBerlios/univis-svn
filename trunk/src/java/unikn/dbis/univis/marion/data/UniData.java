package unikn.dbis.univis.marion.data;

import java.sql.SQLException;
import java.util.*;

/**
 * UniVis Data Structure
 *
 * @author Christian Gruen
 * @author Marion Herb
 */
public class UniData {
    private String[][] meta_cubes, meta_tables, meta_cube_dimensions, meta_dimensions;

    private HashMap table_nodes;

    private ForeignKeys foreignKeys;

    private Cubes cubes;

    public UniData() {
    }

    /**
     * create tree for specified database
     *
     * @return Returns the loaded Cube[]
     */
    public Cubes loadCubes() {
        PSQL psql = new PSQL();
        meta_cubes = psql.getData("meta_cubes");
        meta_tables = psql.getData("meta_tables");
        meta_cube_dimensions = psql.getData("meta_cube_dimensions");
        meta_dimensions = psql.getData("meta_dimensions");

//        System.out.println("cubes: " + UniUtil.getStringRepOfArrayArray(cubes));
//        System.out.println("tables: "
//                + UniUtil.getStringRepOfArrayArray(tables));
//        System.out.println("cube_dimensions: "
//                + UniUtil.getStringRepOfArrayArray(cube_dimensions));
//        System.out.println("dimensions: "
//                + UniUtil.getStringRepOfArrayArray(dimensions));

        // get foreign key info for all tables
        this.foreignKeys = getForeignKeys(psql);

        // create list of table entries
        table_nodes = new HashMap();
        for (int j = 0; j < meta_tables.length; j++) {
            // Structure Node: ID, tablename, description, rootJoinColumn
            Node node = new Dimension(getInt(meta_tables[j][0]), meta_tables[j][1], meta_tables[j][2]);
            // Structure Hashmap: ID, Node
            // -> node ID in object node and as counter in hashmap...
            table_nodes.put(meta_tables[j][0], node);
        }

        // create main cubes
        // convert to class variable
        this.cubes = new Cubes();
        for (int i = 0; i < meta_cubes.length; i++) {
            // fetches ID from table meta_cubes
            int id = getInt(meta_cubes[i][0]);

            this.cubes.add(new Cube(id, meta_cubes[i][1], meta_cubes[i][2], psql.getDataMeasures(id)));

            // add root nodes
            // passes through cube dimensions for every cube
            for (int j = 0; j < meta_cube_dimensions.length; j++) {
                // if dimension matches cube id
                if (this.cubes.getNode(i).id == getInt(meta_cube_dimensions[j][0])) {
                    Node node = (Node) table_nodes.get(meta_cube_dimensions[j][1]);
                    // now get the foreignKey info to this table and store it in the node
                    ForeignKey foreignKey = this.foreignKeys.getForeignKey(this.cubes.getNode(i).getName(), node.getName());
                    node.setForeignKey(foreignKey);
                    // each table is added to its cube as a node
                    this.cubes.getNode(i).add(node);
                    // recursive add now all dimensions children
                    addChildren(node, node, 1);
                }
            }
        }
        return this.cubes;
    }


    private void addChildren(Node parent, Node root, int l) {
        for (int j = 0; j < meta_dimensions.length; j++) {
            String idRoot = meta_dimensions[j][1];
            String idParent = meta_dimensions[j][2];

            if (root.id == getInt(idRoot) && parent.id == getInt(idParent)) {
                //old: Problem: Attached Nodes appear multiple times if already sub nodes are added
                //Node node = (Node) table_nodes.get(dimensions[j][0]);

                Node deepNode = (Node) table_nodes.get(meta_dimensions[j][0]);
                Node node = new Dimension(deepNode.id, deepNode.name, deepNode.description);

//                for (int k = 0; k < l; k++) {
//                    System.out.print("  ");
//                }
//                System.out.println("addChildren(parent:" + parent.toString() + ", rootid:" + idRoot + ", l:" + l + "):");
//                for (int k = 0; k < l; k++) {
//                    System.out.print("  ");
//                }
//                System.out.println(node.toString(true));

                // now get the foreignKey info to this table and store it in the node
                ForeignKey foreignKey = this.foreignKeys.getForeignKey(root.getName(), node.getName());
                node.setForeignKey(foreignKey);

                parent.add(node);
                addChildren(node, root, l + 1);
            }
        }
    }

    /**
     * converts string to positive integer value
     *
     * @param num string to be converted
     * @return integer value
     */
    private static int getInt(String num) {
        if (num == null)
            return -1;
        int i = 0;
        int l = num.length();
        for (int b = 0; b < l; b++)
            i = i * 10 + num.charAt(b) - '0';
        return i;
    }


    /**
     * @param psql
     * @return Returns a ForeignKey collection
     */
    private ForeignKeys getForeignKeys(PSQL psql) {
        if (psql == null) {
            return null;
        }

        String sql = "SELECT pclass.relname, pg_catalog.pg_get_constraintdef(pc.oid, true) AS consrc";
        sql += " FROM pg_catalog.pg_constraint pc JOIN pg_catalog.pg_class pclass ON pc.conrelid = pclass.oid";
        sql += " WHERE (pclass.relname like 'bluep%' OR pclass.relname like '%cube') AND pc.contype = 'f'";
        sql += " ORDER BY 1";

        ForeignKeys foreignKeys = new ForeignKeys();
        if (psql.execute(sql)) {
            try {
                while (!psql.result.isLast()) {
                    String[] columnValues = psql.next();
                    if (columnValues.length != 2) {
                        throw new IllegalArgumentException("Just two columns expected.");
                    }
                    foreignKeys.add(new ForeignKey(columnValues[0], columnValues[1]));
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
            psql.close();
        }

        return foreignKeys;
    }

    /*
    // Sql to select foreign/primary key
	SELECT
		pc.conname,
		pg_catalog.pg_get_constraintdef(pc.oid, true) AS consrc,
		pc.contype,
		CASE WHEN pc.contype='u' OR pc.contype='p' THEN (
			SELECT
				indisclustered
			FROM
				pg_catalog.pg_depend pd,
				pg_catalog.pg_class pl,
				pg_catalog.pg_index pi
			WHERE
				pd.refclassid=pc.tableoid 
				AND pd.refobjid=pc.oid
				AND pd.objid=pl.oid
				AND pl.oid=pi.indexrelid
		) ELSE
			NULL
		END AS indisclustered
	FROM
		pg_catalog.pg_constraint pc
	WHERE
		pc.conrelid = (SELECT oid FROM pg_catalog.pg_class WHERE relname='bluep_nation'
			AND relnamespace = (SELECT oid FROM pg_catalog.pg_namespace
			WHERE nspname='public'))
	ORDER BY
		1
		
	// simpler...
	SELECT
		pclass.relname,
		pc.conname,
		pg_catalog.pg_get_constraintdef(pc.oid, true) AS consrc,
		pc.contype
	FROM
		pg_catalog.pg_constraint pc JOIN pg_catalog.pg_class pclass ON pc.conrelid = pclass.oid
    WHERE pclass.relname like 'bluep%' AND pc.contype = 'f'
	ORDER BY 1
	*/
}
