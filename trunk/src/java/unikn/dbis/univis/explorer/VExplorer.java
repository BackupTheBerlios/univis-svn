package unikn.dbis.univis.explorer;

import unikn.dbis.univis.navigation.tree.VTree;
import unikn.dbis.univis.meta.impl.VDiceBoxImpl;
import unikn.dbis.univis.meta.impl.VCubeImpl;
import unikn.dbis.univis.meta.VDiceBox;
import unikn.dbis.univis.meta.VCube;
import unikn.dbis.univis.hibernate.util.HibernateUtil;
import unikn.dbis.univis.icon.VIcons;
import unikn.dbis.univis.icon.VCubeIcon;
import unikn.dbis.univis.util.ComponentUtilities;
import unikn.dbis.univis.visualization.VVisualization;
import unikn.dbis.univis.visualization.graph.VGraph;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.*;
import java.awt.dnd.DragSource;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.util.List;
import java.sql.Connection;
import java.sql.SQLException;

import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.connection.ConnectionProviderFactory;

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

    private static Connection connection;

    static {
        connection = HibernateUtil.getSessionFactory().openSession().connection();
    }

    public static Connection getConnection() {
        return connection;
    }

    private JToolBar toolbar = new JToolBar();

    private JSplitPane split = new JSplitPane();

    private JPanel navigation = new JPanel(new BorderLayout());
    private VVisualization visualization = new VVisualization(new BorderLayout());

    private VTree tree;

    private VGraph graph = new VGraph();

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

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 600));
        setSize(new Dimension(800, 600));
        ComponentUtilities.centerComponentOnScreen(this);

        split.setDividerLocation(300);

        initToolbar();
        initNavigation();
        initVisualization();
        initDragAndDrop();

        Container container = getContentPane();
        container.add(toolbar, BorderLayout.NORTH);
        container.add(split, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {

            /**
             * Invoked when a window is in the process of being closed.
             * The close operation can be overridden at this point.
             */
            @Override
            public void windowClosing(WindowEvent e) {
                if (connection != null) {
                    try {
                        connection.commit();
                        connection.close();
                    }
                    catch (SQLException sqle) {
                        sqle.printStackTrace();
                    }
                }
            }
        });
    }

    private void initToolbar() {

        JButton refresh = new JButton(VIcons.REFRESH);
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

        final JButton undo = new JButton(VIcons.UNDO);
        JButton redo = new JButton(VIcons.REDO);
        JButton delete = graph.createDeleteButton();
        JButton chartsButton = graph.createChartsButton();
        JButton measureButton = graph.createMeasureButton();
        JButton exit = new JButton(VIcons.EXIT);

        exit.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        toolbar.add(refresh);
        toolbar.add(undo);

        undo.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (e.getSource().equals(undo)) {

                    graph.undoCells();
                }
            }
        });

        toolbar.add(redo);
        toolbar.add(delete);
        toolbar.add(chartsButton);
        toolbar.add(measureButton);
        toolbar.add(exit);
    }

    private void initNavigation() {

        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        VDiceBox diceBox = (VDiceBox) session.createQuery("from " + VDiceBoxImpl.class.getName() + " where name = 'UniVis Explorer'").uniqueResult();
        tree = new VTree(diceBox);

        //noinspection unchecked
        List<VCube> cubes = session.createQuery("from " + VCubeImpl.class.getName()).list();

        JPanel facts = new JPanel(new GridLayout(cubes.size(), 1));
        for (VCube cube : cubes) {
            facts.add(new JLabel(cube.getI18nKey(), new VCubeIcon(cube.getColor()), JLabel.LEFT));
        }

        session.close();

        JPanel measures = new JPanel(/*new GridLayout(?, ?)*/);
        measures.add(new JLabel("MEASURES not yet defined"));

        navigation.add(facts, BorderLayout.NORTH);
        navigation.add(new JScrollPane(tree), BorderLayout.CENTER);
        navigation.add(measures, BorderLayout.SOUTH);

        split.setLeftComponent(navigation);
    }

    private void initVisualization() {

        visualization.setBackground(Color.WHITE);
        visualization.add(new JScrollPane(graph.getGraph()), BorderLayout.CENTER);

        split.setRightComponent(visualization);
    }

    private void initDragAndDrop() {
        DragSource dragSource = DragSource.getDefaultDragSource();
        dragSource.createDefaultDragGestureRecognizer(tree, DnDConstants.ACTION_COPY_OR_MOVE, tree);

        visualization.setDropTarget(new DropTarget(visualization, visualization));
    }
}