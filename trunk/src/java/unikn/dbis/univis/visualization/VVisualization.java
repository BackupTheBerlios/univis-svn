package unikn.dbis.univis.visualization;

import unikn.dbis.univis.dnd.VDataReferenceFlavor;
import unikn.dbis.univis.meta.VDimension;
import unikn.dbis.univis.visualization.graph.VGraph;

import javax.swing.*;
import java.awt.dnd.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.*;
import java.io.IOException;

/**
 * TODO: document me!!!
 * <p/>
 * <code>VVisualization</code>.
 * <p/>
 * User: raedler, weiler
 * Date: 11.04.2006
 * Time: 18:25:13
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public class VVisualization extends JPanel implements DropTargetListener {

    /**
     * Creates a new <code>JPanel</code> with a double buffer
     * and a flow layout.
     */
    public VVisualization(LayoutManager layoutManager) {
        super(layoutManager);
    }

    /**
     * Called while a drag operation is ongoing, when the mouse pointer enters
     * the operable part of the drop site for the <code>DropTarget</code>
     * registered with this listener.
     *
     * @param dtde the <code>DropTargetDragEvent</code>
     */

    public void dragEnter(DropTargetDragEvent dtde) {

    }

    /**
     * Called when a drag operation is ongoing, while the mouse pointer is still
     * over the operable part of the drop site for the <code>DropTarget</code>
     * registered with this listener.
     *
     * @param dtde the <code>DropTargetDragEvent</code>
     */

    public void dragOver(DropTargetDragEvent dtde) {

    }

    /**
     * Called if the user has modified
     * the current drop gesture.
     * <p/>
     *
     * @param dtde the <code>DropTargetDragEvent</code>
     */

    public void dropActionChanged(DropTargetDragEvent dtde) {

    }

    /**
     * Called while a drag operation is ongoing, when the mouse pointer has
     * exited the operable part of the drop site for the
     * <code>DropTarget</code> registered with this listener.
     *
     * @param dte the <code>DropTargetEvent</code>
     */

    public void dragExit(DropTargetEvent dte) {

    }

    /**
     * Called when the drag operation has terminated with a drop on
     * the operable part of the drop site for the <code>DropTarget</code>
     * registered with this listener.
     * <p/>
     * This method is responsible for undertaking
     * the transfer of the data associated with the
     * gesture. The <code>DropTargetDropEvent</code>
     * provides a means to obtain a <code>Transferable</code>
     * object that represents the data object(s) to
     * be transfered.<P>
     * From this method, the <code>DropTargetListener</code>
     * shall accept or reject the drop via the
     * acceptDrop(int dropAction) or rejectDrop() methods of the
     * <code>DropTargetDropEvent</code> parameter.
     * <p/>
     * Subsequent to acceptDrop(), but not before,
     * <code>DropTargetDropEvent</code>'s getTransferable()
     * method may be invoked, and data transfer may be
     * performed via the returned <code>Transferable</code>'s
     * getTransferData() method.
     * <p/>
     * At the completion of a drop, an implementation
     * of this method is required to signal the success/failure
     * of the drop by passing an appropriate
     * <code>boolean</code> to the <code>DropTargetDropEvent</code>'s
     * dropComplete(boolean success) method.
     * <p/>
     * Note: The data transfer should be completed before the call  to the
     * <code>DropTargetDropEvent</code>'s dropComplete(boolean success) method.
     * After that, a call to the getTransferData() method of the
     * <code>Transferable</code> returned by
     * <code>DropTargetDropEvent.getTransferable()</code> is guaranteed to
     * succeed only if the data transfer is local; that is, only if
     * <code>DropTargetDropEvent.isLocalTransfer()</code> returns
     * <code>true</code>. Otherwise, the behavior of the call is
     * implementation-dependent.
     * <p/>
     *
     * @param dtde the <code>DropTargetDropEvent</code>
     */

    public void drop(DropTargetDropEvent dtde) {
        try {
            Object o = dtde.getTransferable().getTransferData(VDataReferenceFlavor.DIMENSION_FLAVOR);

            if (o instanceof VDimension) {
                System.out.println("ECHO: " + ((VDimension) o).getI18nKey());

                add(new JLabel(((VDimension) o).getI18nKey()));
                revalidate();
            }
        }
        catch (UnsupportedFlavorException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}