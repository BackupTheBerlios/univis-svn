package unikn.dbis.univis.marion.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.*;

import unikn.dbis.univis.marion.data.Cubes;
import unikn.dbis.univis.marion.data.Cube; // wegen ComboCube


/**
 * @author Marion Herb
 *         <p/>
 *         This class creates a panel to visualize the tree
 */
public class UniTreePanel extends JPanel implements ActionListener {

    private JTree tree;

    private ScrollBarPanel bar;

    private Cubes cubes;

    /**
     * Default constructor
     */
    public UniTreePanel() {
        setBorder(new BevelBorder(BevelBorder.LOWERED));
        setPreferredSize(new java.awt.Dimension(300, 400));
        setLayout(new BorderLayout());
        setBackground(Color.white);

        bar = new ScrollBarPanel(this);
        add(bar, BorderLayout.EAST);
    }


    /**
     * tree panel constructor
     *
     * @param cubes
     */
    public UniTreePanel(Cubes cubes) {
        this();
        this.cubes = cubes;
        createTree(cubes.getSelected());
    }


    /**
     * create new tree from specified cube
     */
    private void createTree(Cube selectedCube) {
        if (tree != null)
            remove(tree);
        tree = new UniTree(this.cubes, selectedCube);
        add(tree);
        validate();
        repaint();
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
//        System.out.println(this.getClass().getName() + " fired!");
        if (e.getSource().getClass().equals(JComboBox.class)) {
            Object o = ((JComboBox) e.getSource()).getSelectedItem();
            createTree(((ComboCube) o).getCube());
        }
    }
}
