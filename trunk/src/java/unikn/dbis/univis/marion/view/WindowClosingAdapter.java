package unikn.dbis.univis.marion.view;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Marion Herb
 *         <p/>
 *         Adapter for closing window
 *         No functions at the moment
 */
public class WindowClosingAdapter extends WindowAdapter {
    private boolean exitSystem;

    /**
     * Erzeugt einen WindowClosingAdapter zum Schliessen des Fensters. Ist
     * exitSystem true, wird das komplette Programm beendet.
     *
     * @param exitSystem
     */
    public WindowClosingAdapter(boolean exitSystem) {
        this.exitSystem = exitSystem;
    }

    /**
     * Erzeugt einen WindowClosingAdapter zum Schliessen des Fensters. Das
     * Programm wird nicht beendet.
     */
    public WindowClosingAdapter() {
        this(false);
    }

    /**
     * (non-Javadoc)
     *
     * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
     */
    public void windowClosing(WindowEvent event) {
        event.getWindow().setVisible(false);
        event.getWindow().dispose();
        if (exitSystem) {
            System.exit(0);
        }
    }
}
