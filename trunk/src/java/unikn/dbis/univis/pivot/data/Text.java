package unikn.dbis.univis.pivot.data;

import unikn.dbis.univis.pivot.view.NotDraggable;

/**
 * @author Marion Herb
 */
public class Text extends Node implements NotDraggable {

    public Text(String name, String description) {
        this.name = name;
        this.description = description;
    }

}
