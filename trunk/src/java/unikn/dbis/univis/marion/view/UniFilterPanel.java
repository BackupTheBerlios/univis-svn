package unikn.dbis.univis.marion.view;

import unikn.dbis.univis.marion.data.Cubes;
import unikn.dbis.univis.marion.data.Cube;

import java.awt.event.*;
import java.util.Iterator;

import javax.swing.border.*;
import javax.swing.*;

/**
 * @author Marion Herb
 *         <p/>
 *         Select Measures
 *         Later going to be implementing with further Meta Table
 */
public class UniFilterPanel extends JPanel implements ActionListener {
    /*
     * Structure MEASURES:
     * 1st Array: columnnames existing in cubes
     * 2nd Array: displaynames in application
     * 3rd Array: corresponding cubes
     */
    final static String[][] MEASURES = {
            {"koepfe", "faelle", "betrag"},
            {"Koepfe [sos_cube]", "Faelle [sos_cube]",
                    "Betrag [cob_busa_cube]"},
            {"sos_cube", "sos_cube", "cob_busa_cube"}};

    static int index;

    private JComboBox combo;

    private Cubes cubes;


    public UniFilterPanel() {
        combo = new JComboBox();
        combo.addActionListener(this);

        UniLayout.setWidth(combo, 170);

        setBorder(new EmptyBorder(8, 8, 8, 8));
        setLayout(new TableLayout(3, 2));
        add(UniLayout.newLabel("Select Cube"));
        add(combo);
    }


    /**
     * @param cubes
     */
    public UniFilterPanel(Cubes cubes) {
        this();
        this.cubes = cubes;
        for (Iterator iter = cubes.iterator(); iter.hasNext();) {
            Cube element = (Cube) iter.next();
            this.combo.addItem(new ComboCube(element));
        }
    }

    /* welche Measure angezeigt */
    public String getMeasure() {
        if (index == -1)
            return null;
        else
            return MEASURES[0][index];
    }

    public String getMeasureInCube() {
        if (index == -1)
            return null;
        else
            return MEASURES[2][index];
    }


    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.combo) {
            if (this.cubes != null && this.combo.getSelectedItem() instanceof ComboCube) {
                this.cubes.setSelected(((ComboCube) this.combo.getSelectedItem()).getCube());
            }
        }
    }

    /**
     * @return Returns the combo.
     */
    public JComboBox getCombo() {
        return combo;
    }
}


class ComboCube {
    private Cube cube;

    /**
     * @param cube
     */
    public ComboCube(Cube cube) {
        this.cube = cube;
    }

    public String toString() {
        if (this.cube != null) {
            return this.cube.getDescription();
        }
        return null;
    }

    /**
     * @return Returns the cube.
     */
    public Cube getCube() {
        return cube;
    }
}
