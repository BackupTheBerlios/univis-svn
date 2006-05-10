package unikn.dbis.univis.sql;

import unikn.dbis.univis.meta.VDimension;

/**
 * TODO: document me!!!
 * <p/>
 * VQuery.
 * <p/>
 * User: raedler, weiler
 * Date: 10.05.2006
 * Time: 10:05:27
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public class VQuery {

    private String cubeAttribute = "SUM(koepfe)";
    private boolean root = true;
    private StringBuilder from = new StringBuilder(" FROM ");
    private StringBuilder where = new StringBuilder(" WHERE ");
    private StringBuilder group = new StringBuilder(" GROUP BY ");
    private StringBuilder order = new StringBuilder(" ORDER BY ");
    private String cubeName = "sos_cube";
    private StringBuilder select = new StringBuilder("SELECT " + cubeAttribute);
    private StringBuilder concatenation = new StringBuilder();

    private String lastTableName;


    public void setCubeAttribute(String cubeAttribute) {
        this.cubeAttribute = cubeAttribute;
    }

    public void setCubeName(String cubeName) {
        this.cubeName = cubeName;
    }

    public String createChartQuery(VDimension vDim, VDimension bluep) {

        String bluepName = bluep.getTableName();
        String tableName = vDim.getTableName();


        if (root) {
            root = false;

            if (!vDim.equals(bluep)) {

                from.append(cubeName).append(", ").append(tableName).append(", ").append(bluepName);
                where.append(tableName).append(".id = ").append(bluepName).append(".").append(vDim.getJoinable()).append(" AND ").append(bluepName).append(".id = ").append(cubeName).append(".").append(bluep.getJoinable());
            }
            else {
                from.append(cubeName).append(", ").append(tableName);
                where.append(tableName).append(".id = ").append(cubeName).append(".").append(vDim.getJoinable());
            }
        }
        else {

            if (!vDim.equals(bluep)) {
                from.append(", ").append(bluepName).append(", ").append(tableName);
                where.append(" AND ").append(tableName).append(".id = ").append(bluepName).append(".").append(vDim.getJoinable()).append(" AND ").append(bluepName).append(".id = ").append(cubeName).append(".").append(bluep.getJoinable());
            }
            else {
                from.append(", ").append(tableName);
                where.append(" AND ").append(tableName).append(".id = ").append(cubeName).append(".").append(vDim.getJoinable());
            }
            group.append(", ");
            order.append(", ");
        }

        group.append(tableName).append(".name");
        order.append(tableName).append(".name");

        if (concatenation.length() > 0) {
            concatenation.append(" || '_' || ");
        }

        if (lastTableName != null && !"".equals(lastTableName)) {
            concatenation.append(lastTableName).append(".name");
        }

        lastTableName = tableName;

        select.append(", ").append(tableName).append(".name");

        StringBuilder sql = new StringBuilder();
        sql.append(select);

        if (concatenation.length() > 0) {
            sql.append(", ").append(concatenation);
        }

        sql.append(from).append(where).append(group).append(order);

        System.out.println(sql.toString());
        return sql.toString();
    }

    public void reset() {
        root = true;
        from = new StringBuilder(" FROM ");
        where = new StringBuilder(" WHERE ");
        group = new StringBuilder(" GROUP BY ");
        order = new StringBuilder(" ORDER BY ");
        select = new StringBuilder("SELECT " + cubeAttribute);
        concatenation = new StringBuilder();
        lastTableName = "";
    }

    public void undo() {

    }
}