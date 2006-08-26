package unikn.dbis.univis.pivot.data;

/**
 * @author Marion Herb
 *         <p/>
 *         Dimension sub class
 */
public class Dimension extends Node {

    /**
     * Default constructor
     *
     * @param id
     * @param name
     * @param description
     */
    public Dimension(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

}
