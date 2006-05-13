package unikn.dbis.univis.sql;

import unikn.dbis.univis.meta.VDimension;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Stack;

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

    // The logger to log info, error and other occuring messages
    // or exceptions.
    public static final transient Log LOG = LogFactory.getLog(VQuery.class);

    private Stack<VQueryStatement> history;

    private String cubeName = "sos_cube";
    private String cubeAttribute = "SUM(koepfe)";

    /**
     *
     */
    public VQuery() {
        history = new Stack<VQueryStatement>();
    }

    /**
     * @param dimension
     * @param blueprint
     * @return
     */
    public String createChartQuery(VDimension dimension, VDimension blueprint) {

        // The current query statement that will be used to create
        // cube browsing.
        VQueryStatement statement;

        if (history.isEmpty()) {
            // Create initial query statement if history is empty.
            statement = new VQueryStatement();
        }
        else {
            // Create a new statement for history and querying.
            statement = new VQueryStatement(history.peek());
        }

        // Push the new statement onto the stack.
        history.push(statement);

        // Return the chart query.
        return statement.createChartQuery(dimension, blueprint);
    }

    public void setCubeAttribute(String cubeAttribute) {
        this.cubeAttribute = cubeAttribute;
    }

    public void setCubeName(String cubeName) {
        this.cubeName = cubeName;
    }

    /**
     * Resets the query to initial state.
     */
    public void reset() {
        // Clears the query history.
        history.clear();
    }

    /**
     * Removes the last query statement from the
     * history.
     */
    public void historyBack() {
        // Remove the top query element of the stack.
        history.pop();
    }

    private final class VQueryStatement {

        private VQueryStatement parent;

        private StringBuilder select;
        private StringBuilder from;
        private StringBuilder where;
        private StringBuilder group;
        private StringBuilder order;
        private StringBuilder concatenation;
        private String lastTableName;

        private VQueryStatement() {
            from = new StringBuilder(" FROM ");
            where = new StringBuilder(" WHERE ");
            group = new StringBuilder(" GROUP BY ");
            order = new StringBuilder(" ORDER BY ");
            select = new StringBuilder("SELECT " + cubeAttribute);
            concatenation = new StringBuilder();
        }

        private VQueryStatement(VQueryStatement parent) {
            this.parent = parent;

            select = parent.select;
            from = parent.from;
            where = parent.where;
            group = parent.group;
            order = parent.order;
            concatenation = parent.concatenation;
            lastTableName = parent.lastTableName;
        }

        /**
         * @param vDim
         * @param bluep
         * @return
         */
        public String createChartQuery(VDimension vDim, VDimension bluep) {

            String bluepName = bluep.getTableName();
            String tableName = vDim.getTableName();

            if (history.size() <= 1) {

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

            if (LOG.isDebugEnabled()) {
                LOG.debug(sql.toString());
            }

            return sql.toString();
        }
    }
}