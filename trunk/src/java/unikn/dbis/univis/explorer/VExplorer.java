package unikn.dbis.univis.explorer;

import unikn.dbis.univis.navigation.tree.VTree;
import unikn.dbis.univis.meta.impl.VDiceBoxImpl;
import unikn.dbis.univis.meta.VDiceBox;
import unikn.dbis.univis.hibernate.util.HibernateUtil;
import unikn.dbis.univis.icon.VIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import org.hibernate.SessionFactory;
import org.hibernate.Session;

/**
 * TODO: document me!!!
 * <p/>
 * <code>VExplorer</code>.
 * <p/>
 * User: raedler, weiler
 * Date: 10.04.2006
 * Time: 17:06:14
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public class VExplorer extends JFrame {

    /**
     * Starting the univis explorer.
     */
    public static void main(String args[]) {

        /*
        try {
            UIManager.setLookAndFeel(new com.sun.java.swing.plaf.windows.WindowsLookAndFeel());
        }
        catch (UnsupportedLookAndFeelException ulafe) {
            ulafe.printStackTrace();
        }
        */

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new VExplorer().setVisible(true);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private JMenuBar menubar = new JMenuBar();

    private JToolBar toolbar = new JToolBar();

    private JSplitPane split = new JSplitPane();

    private VTree tree;

    /**
     * Constructs a new frame that is initially invisible.
     * <p/>
     * This constructor sets the component's locale property to the value
     * returned by <code>JComponent.getDefaultLocale</code>.
     *
     * @throws java.awt.HeadlessException if GraphicsEnvironment.isHeadless()
     *                                    returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see java.awt.Component#setSize
     * @see java.awt.Component#setVisible
     * @see javax.swing.JComponent#getDefaultLocale
     */
    public VExplorer() throws HeadlessException {
        super("UniVis Explorer 0.1 - (c) 2005 a.d. - DBIS, University of Konstanz");

        split.setDividerLocation(300);

        initMenubar();
        initToolbar();
        initNavigation();
        initVisualization();

        Container container = getContentPane();
        container.add(toolbar, BorderLayout.NORTH);
        container.add(split, BorderLayout.CENTER);

        setPreferredSize(new Dimension(800, 600));
        setSize(new Dimension(800, 600));
    }

    private void initMenubar() {

        JMenu file = new JMenu("File");

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(new ActionListener() {

            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        file.add(exit);

        menubar.add(file);

        setJMenuBar(menubar);
    }

    private void initToolbar() {

        JButton refresh = new JButton(VIcon.REFRESH);
        refresh.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                Runnable runnable = new Runnable() {

                    /**
                     * When an object implementing interface <code>Runnable</code> is used
                     * to create a thread, starting the thread causes the object's
                     * <code>run</code> method to be called in that separately executing
                     * thread.
                     * <p/>
                     * The general contract of the method <code>run</code> is that it may
                     * take any action whatsoever.
                     *
                     * @see Thread#run()
                     */
                    public void run() {

                        System.out.println("ECHO2");

                        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
                        Session session = sessionFactory.openSession();
                        VDiceBox diceBox = (VDiceBox) session.createQuery("from " + VDiceBoxImpl.class.getName() + " where name = 'UniVis Explorer'").uniqueResult();
                        tree.refresh(diceBox);
                        session.close();
                    }
                };

                System.out.println("ECHO1");

                SwingUtilities.invokeLater(runnable);

                System.out.println("ECHO3");
            }
        });

        toolbar.add(refresh);
    }

    private void initNavigation() {

        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        VDiceBox diceBox = (VDiceBox) session.createQuery("from " + VDiceBoxImpl.class.getName() + " where name = 'UniVis Explorer'").uniqueResult();
        tree = new VTree(diceBox);
        session.close();

        split.setLeftComponent(new JScrollPane(tree));
    }

    private void initVisualization() {

        JPanel visualization = new JPanel();
        visualization.setBackground(Color.WHITE);

        split.setRightComponent(visualization);
    }
}