package unikn.dbis.univis.explorer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

import unikn.dbis.univis.util.ComponentUtilities;

/**
 * TODO: document me!!!
 * <p/>
 * VSplashScreen.
 * <p/>
 * User: raedler, weiler
 * Date: 28.05.2006
 * Time: 22:30:10
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public class VSplashScreen extends JFrame implements Runnable {


    /**
     * In diesem <code>Log</code> können Fehler, Info oder sonstige Ausgaben erfolgen.
     * Diese Ausgaben können in einem separaten File spezifiziert werden.
     */
    private static final Log LOG = LogFactory.getLog(VExplorer.class);

    /**
     * Ein eigenständiger <code>Thread</code> der während des Ladens der eigentlichen Applikation
     * läuft und sich nach vollständig geladener Anwendung zerstört.
     */
    private Thread thread;

    /**
     * Das anzuzeigende <code>Image</code>.
     */
    private BufferedImage image;

    /**
     * Erzeugt eine neue <code>SplashScreen</code> und sobald das Splashable gesetzt wurde
     * beendet sich die Screen von selbst.
     *
     * @param inputStream Der Dateiname incl. des relativen Pfades des anzuzeigenden Images.
     */
    public VSplashScreen(InputStream inputStream) {

        // Setze die für einen SplashScreen üblichen Einstellungen.
        setUndecorated(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        try {
            image = ImageIO.read(inputStream);
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }

        thread = new Thread(this);
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();

        center();

        setVisible(true);
    }

    /**
     * Zentriert die <code>SplashScreen</code> im sichtbaren Bereich des Bildschirms.
     */
    private void center() {

        // Größe der eingestellten Bildschirmauflösung.
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        double width = screenSize.getWidth();
        double height = screenSize.getHeight();

        width -= image.getWidth();
        height -= image.getHeight();

        setSize(new Dimension(image.getWidth(), image.getHeight()));

        setLocation((int) width / 2, (int) height / 2);
    }

    /**
     * Diese Methode wird nur verwendet um, nach dem Setzen des <code>Splashable</code>, das
     * <code>SplashScreen</code> zu schließen. -> Nur im Fehlerfall.
     *
     * @throws Exception - sollte nicht auftreten.
     */
    public void destroy() throws Exception {
        thread = null;
    }

    /**
     * @see JFrame#paint(java.awt.Graphics)
     */
    public void paint(Graphics g) {
        super.paint(g);
        g.drawImage(image, 0, 0, this);
    }

    /**
     * @see Runnable#run()
     */
    public void run() {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Starting splash screen animation...");
        }

        while (Thread.currentThread() == thread && thread != null) {

            try {
                Thread.sleep(100);
            }
            catch (InterruptedException ie) {
                if (LOG.isErrorEnabled()) {
                    LOG.error(ie.getMessage(), ie);
                }
            }
        }

        dispose();

        if (LOG.isDebugEnabled()) {
            LOG.debug("... splash screen animation finished.");
        }
    }
}