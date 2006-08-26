package unikn.dbis.univis.pivot.data;

/**
 * @author Marion Herb
 *         <p/>
 *         Container to hold data (the cubes) and its state (selection)
 */
public class Cubes extends Nodes {

    private Cube selected;


    /**
     * Default constructor
     */
    public Cubes() {
        super();
    }


    /**
     * @return Returns the selected.
     */
    public Cube getSelected() {
        return selected;
    }

    /**
     * @param selected The selected to set.
     */
    public void setSelected(Cube selected) {
        this.selected = selected;
        System.out.println("new selected Cube: " + this.selected.toString());
    }

}
