package unikn.dbis.univis.explorer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

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
public class VSplashScreen implements Runnable {


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

    private InputStream imageStream;

    /**
     * Erzeugt eine neue <code>SplashScreen</code> und sobald das Splashable gesetzt wurde
     * beendet sich die Screen von selbst.
     *
     * @param inputStream Der Dateiname incl. des relativen Pfades des anzuzeigenden Images.
     */
    public VSplashScreen(InputStream inputStream) {

        this.imageStream = inputStream;

        thread = new Thread(this);
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
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
     * @see Runnable#run()
     */
    public void run() {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Starting splash screen animation...");
        }

        JFrame frame = new JFrame() {

            /**
             * Das anzuzeigende <code>Image</code>.
             */
            private BufferedImage image;

            /**
             * Called by the constructors to init the <code>JWindow</code> properly.
             */
            @Override
            protected void frameInit() {
                try {
                    image = ImageIO.read(imageStream);
                }
                catch (IOException ioe) {
                    ioe.printStackTrace();
                }

                super.frameInit();

                center();

                setVisible(true);
            }



            /**
             * Paints each of the components in this container.
             *
             * @param g the graphics context.
             * @see java.awt.Component#paint
             * @see java.awt.Component#paintAll
             */
            @Override
            public void paint(Graphics g) {
                g.drawImage(image, 0, 0, this);
                super.paint(g);
            }

            /**
             * Zentriert die <code>SplashScreen</code> im sichtbaren Bereich des Bildschirms.
             */
            private void center() {

                // Größe der eingestellten Bildschirmauflösung.
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

                double width = screenSize.getWidth();
                double height = screenSize.getHeight();

                width -= image.getWidth(this);
                height -= image.getHeight(this);

                setSize(new Dimension(image.getWidth(this), image.getHeight(this)));

                setLocation((int) width / 2, (int) height / 2);
            }
        };

        // Setze die für einen SplashScreen üblichen Einstellungen.
        //frame.setUndecorated(true);
        //frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        while (Thread.currentThread() == thread && thread != null) {

            try {
                frame.repaint();
                Thread.sleep(100);
            }
            catch (InterruptedException ie) {
                if (LOG.isErrorEnabled()) {
                    LOG.error(ie.getMessage(), ie);
                }
            }
        }

        frame.dispose();

        if (LOG.isDebugEnabled()) {
            LOG.debug("... splash screen animation finished.");
        }
    }
}