package unikn.dbis.univis.explorer;

import javax.swing.*;
import java.util.GregorianCalendar;
import java.util.Date;
import java.util.Calendar;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.text.SimpleDateFormat;

/**
 * TODO: document me!!!
 * <p/>
 * VClock.
 * <p/>
 * User: raedler, weiler
 * Date: 24.05.2006
 * Time: 20:19:29
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public class VClock extends JLabel {

    private Date date = new Date();

    public VClock() {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        Timer timer = new Timer(0, new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                date.setTime(System.currentTimeMillis());
                VClock.this.setText(simpleDateFormat.format(date));
            }
        });
        timer.setRepeats(true);
        timer.start();
    }

    public String getDate() {
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("EE, dd.MM.yy");
        return simpleDateFormat2.format(date);
    }
}