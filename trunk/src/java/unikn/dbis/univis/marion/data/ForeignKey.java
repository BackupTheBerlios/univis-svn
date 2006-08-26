/*
 * Created on 11.05.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package unikn.dbis.univis.marion.data;

/**
 * @author Marion Herb
 */
public class ForeignKey {

    private String consrc, table, column, refTable, refColumn;


    /**
     * Default constructor
     *
     * @param table
     * @param consrc
     */
    public ForeignKey(String table, String consrc) {
        this.table = table;
        this.consrc = consrc;
        create(consrc);
    }


    private void create(String consrc) {
        if (consrc == null) {
            return;
        }
        int pos = consrc.toLowerCase().indexOf("foreign key");
        if (pos == -1) {
            return;
        }

        int posStart = consrc.indexOf("(");
        int posEnd = consrc.indexOf(")");
        this.column = consrc.substring(posStart + 1, posEnd);

        pos = consrc.toLowerCase().indexOf("references");
        if (pos == -1) {
            return;
        }

        String rest = consrc.substring(pos, consrc.length());
        posStart = rest.toLowerCase().indexOf("references");
        posEnd = rest.indexOf("(");
        this.refTable = rest.substring(posStart + "references".length(), posEnd).trim();

        rest = rest.substring(posEnd - 1, rest.length());
        posStart = rest.indexOf("(");
        posEnd = rest.indexOf(")");
        this.refColumn = rest.substring(posStart + 1, posEnd);
    }

    /**
     * (non-Javadoc)
     *
     * @see Object#toString()
     */
    public String toString() {
//        return "[" + this.consrc + ": " + this.column + " => " + this.refTable + "(" + this.refColumn + ")";
        return this.table + "(" + this.column + ") => " + this.refTable + "(" + this.refColumn + ")";
    }

    /**
     * @return Returns the column.
     */
    public String getColumn() {
        return column;
    }

    /**
     * @return Returns the consrc.
     */
    public String getConsrc() {
        return consrc;
    }

    /**
     * @return Returns the refColumn.
     */
    public String getRefColumn() {
        return refColumn;
    }

    /**
     * @return Returns the refTable.
     */
    public String getRefTable() {
        return refTable;
    }

    /**
     * @return Returns the table.
     */
    public String getTable() {
        return table;
    }

}
