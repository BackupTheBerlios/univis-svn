package unikn.dbis.univis.pivot.data;

import unikn.dbis.univis.pivot.view.NotDraggable;

/**
 * @author Marion Herb
 *         <p/>
 *         Cube sub class of Node
 */
public class Cube extends Node implements NotDraggable {

    private Measures measures;

    /**
     * Default constructor
     *
     * @param id
     * @param name
     * @param description
     */
    public Cube(int id, String name, String description, Measures measures) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.measures = measures;
    }

    /* overrides Nodes tostring() method because cubes may have measures associated */
    public String toString() {
        return super.toString() + " Measure in Cube " + measures.toString();
    }

    /**
     * @return Returns first sub node
     */
    public Node getRootNode() {
        if (this.descendants != null && this.descendants.size() == 1) {
            return this.descendants.getNode(0);
        }
        else {
            throw new IllegalStateException("More than one descendant found for cube node: " + this.toString());
        }
    }

    /**
     * @return Returns the measures.
     */
    public Measures getMeasures() {
        return measures;
    }

}
