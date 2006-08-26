package unikn.dbis.univis.marion.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

/**
 * @author Marion Herb
 */
public abstract class UniViewPanel extends JPanel {

    public UniViewPanel() {
        setBorder(new EtchedBorder());
        setPreferredSize(new Dimension(1000, 700));
        setLayout(new BorderLayout());
        setBackground(Color.white);
    }

}
