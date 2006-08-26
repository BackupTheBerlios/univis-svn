package view;

import unikn.dbis.univis.marion.view.UniTreePanel;
import unikn.dbis.univis.marion.view.UniViewPanel;
import unikn.dbis.univis.marion.view.UniFilterPanel;
import unikn.dbis.univis.marion.view.UniViewPanelPivottable;
import unikn.dbis.univis.marion.data.Cubes;
import unikn.dbis.univis.marion.data.UniData;

import java.awt.*;
import java.util.Iterator;

import javax.swing.*;

/**
 * @author Marion Herb
 * @author Christian Grün
 *         <p/>
 *         Main frame with a panel on the left (tree) and on the right (view)
 */
public class UniVis extends JFrame {
    static final String TITLE = "UniVis Explorer - (c)2006 a.d. - DBIS, University of Konstanz";
    Cursor DEFAULT, WAIT;

    UniTreePanel treePanel;
    UniFilterPanel filterPanel;
    UniViewPanel viewPanel;

    // Daten
    UniData data = new UniData();
    Cubes cubes = data.loadCubes();


    /**
     * constructor of the main frame
     */
    public UniVis() {
        super(TITLE);
        DEFAULT = new Cursor(Cursor.DEFAULT_CURSOR);
        WAIT = new Cursor(Cursor.WAIT_CURSOR);

        // set system specific look & feel
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container comp = getContentPane();
        comp.setLayout(new BorderLayout());

        // order is important since filterPanel defines what treePanel should show
        this.filterPanel = new UniFilterPanel(this.cubes);
        this.treePanel = new UniTreePanel(this.cubes);
        this.filterPanel.getCombo().addActionListener(this.treePanel);

        // create split panel
        JPanel left = new JPanel();
        left.setLayout(new BorderLayout());
        left.add(filterPanel, BorderLayout.NORTH);
        left.add(treePanel, BorderLayout.CENTER);

        /* call subclass UniViewPanelPivottable instead of UniViewPanel */
        this.viewPanel = new UniViewPanelPivottable();
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, viewPanel);
        split.setDividerSize(5);
        comp.add(split, BorderLayout.CENTER);
//		this.addWindowListener(new WindowClosingAdapter(true));

        pack();
        setLocation(0, 0);
        setVisible(true);
    }

    /**
     * starter method
     *
     * @param args standard arguments
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            System.out.println("////");
            e.printStackTrace();
        }

        new UniVis();
    }
}
