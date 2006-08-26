package unikn.dbis.univis.marion.data;

import unikn.dbis.univis.marion.view.NotDraggable;

/**
 * @author Marion Herb
 */
public class Text extends Node implements NotDraggable {

    public Text(String name, String description) {
        this.name = name;
        this.description = description;
    }

}
