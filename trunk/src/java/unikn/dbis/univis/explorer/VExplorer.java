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
import java.awt.event.*;
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
import org.jgraph.graph.GraphLayoutCache;

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
                    explorer = new VExplorer();
                    explorer.setVisible(true);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static VExplorer explorer;

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = HibernateUtil.getSessionFactory().openSession().connection();
        }
        connection.commit();

        return connection;
    }

    private JToolBar toolbar = new JToolBar();

    private JSplitPane split = new JSplitPane();

    private JPanel navigation = new JPanel(new BorderLayout());
    private VVisualization visualization = new VVisualization(new BorderLayout());

    private VTree tree;

    private VGraph graph = new VGraph();
    private JScrollPane graphScrollPane;

    private Point startSelection;
    private Point endSelection;
    private Rectangle selection;

    private JPopupMenu chartsMenu = new JPopupMenu();
    private JPopupMenu measuresMenu = new JPopupMenu();

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
        super("UniVis Explorer 0.1 - (c) 2005-2006 a.d. - DBIS, University of Konstanz");

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

        graph.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                System.out.println("VExplorer.mousePressed");
                startSelection = e.getPoint();
            }

            public void mouseReleased(MouseEvent e) {
                System.out.println("VExplorer.mouseReleased");
                endSelection = e.getPoint();

                selection = new Rectangle((int) startSelection.getX(), (int) startSelection.getY(), (int) (endSelection.getX() - startSelection.getX()), (int) (endSelection.getY() - startSelection.getY()));

                graphScrollPane.scrollRectToVisible(selection);
                graph.scrollRectToVisible(selection);

                graphScrollPane.getViewport().setViewPosition(selection.getLocation());
                graphScrollPane.repaint();

                //graph.getGraph().setScale(selection.getWidth() / graphScrollPane.getViewport().getSize().getWidth());

                System.out.println("SIZE: " + graphScrollPane.getViewport().getSize());
                System.out.println("selection = " + selection);
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
        JButton delete = new JButton(VIcons.DELETE);
        final JButton chartsButton = new JButton(VIcons.CHART);
        final JButton measureButton = new JButton(VIcons.MEASURE);
        JButton exit = new JButton(VIcons.EXIT);
        JButton zoomIn = new JButton(VIcons.ZOOM_IN);
        JButton zoomOut = new JButton(VIcons.ZOOM_OUT);
        JButton layout = new JButton(VIcons.LAYOUT);

        makeChartsMenu();
        makeMeasuresMenu();

        chartsButton.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent evt) {
                chartsMenu.show(chartsButton, 0, chartsButton.getHeight());
            }
        });

        measureButton.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent evt) {
                measuresMenu.show(measureButton, 0, measureButton.getHeight());
            }
        });

        delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GraphLayoutCache cache = graph.getGraphLayoutCache();
                cache.remove(cache.getCells(true, true, true, true), true, true);
                graph.setRoot(true);
                graph.getQueryHistory().reset();
                graph.getCellHistory().reset();
            }
        });

        zoomIn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                graph.zoomIn();
            }
        });

        zoomOut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                graph.zoomOut();
            }
        });

        layout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (graph.getLayoutOrientation() == SwingConstants.NORTH) {
                    graph.setLayoutOrientation(SwingConstants.WEST);
                }
                else {
                    graph.setLayoutOrientation(SwingConstants.NORTH);
                }
                graph.reloadGraph();
            }
        });

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
        toolbar.add(zoomIn);
        toolbar.add(zoomOut);
        toolbar.add(layout);
    }

    private void makeChartsMenu() {

        JCheckBoxMenuItem barChart = new JCheckBoxMenuItem("BarChart", VIcons.BARCHART);
        JCheckBoxMenuItem pieChart = new JCheckBoxMenuItem("PieChart", VIcons.PIECHART);

        makeActionListenerCharts(barChart, "barChart");
        makeActionListenerCharts(pieChart, "pieChart");
        ButtonGroup charts = new ButtonGroup();
        barChart.setState(true);
        pieChart.setState(false);
        charts.add(barChart);
        charts.add(pieChart);

        chartsMenu.add(barChart);
        chartsMenu.add(pieChart);
    }

    private void makeMeasuresMenu() {

        JCheckBoxMenuItem heads = new JCheckBoxMenuItem("Koepfe (Studenten)", VIcons.USERK);
        JCheckBoxMenuItem cases = new JCheckBoxMenuItem("Faelle (Studenten)", VIcons.USERF);
        JCheckBoxMenuItem amount = new JCheckBoxMenuItem("Betrag (Kosten)", VIcons.EURO);

        makeActionListenerMeasures(heads, "sos_cube", "SUM(koepfe)", "Studenten");
        makeActionListenerMeasures(cases, "sos_cube", "SUM(faelle)", "Studenten");
        makeActionListenerMeasures(amount, "cob_busa_cube", "SUM(betrag)", "Betraege");

        ButtonGroup measuresGroup = new ButtonGroup();
        heads.setState(true);
        cases.setState(false);
        amount.setState(false);
        measuresGroup.add(heads);
        measuresGroup.add(cases);
        measuresGroup.add(amount);

        measuresMenu.add(heads);
        measuresMenu.add(cases);
        measuresMenu.add(amount);
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

        graphScrollPane = new JScrollPane(graph);

        visualization.setBackground(Color.WHITE);
        visualization.add(graphScrollPane, BorderLayout.CENTER);

        split.setRightComponent(visualization);
    }

    private void initDragAndDrop() {
        DragSource dragSource = DragSource.getDefaultDragSource();
        dragSource.createDefaultDragGestureRecognizer(tree, DnDConstants.ACTION_COPY, tree);

        graph.setDropTarget(new DropTarget(graph, graph));
    }

    public static void publishException(Exception e) {
        JOptionPane.showMessageDialog(explorer, e.getMessage(), "Exception: " + e.getClass().getName(), JOptionPane.ERROR_MESSAGE);
    }

    /**
     * @param checkBoxMenuItem Item which gets the Listener.
     * @param cube             String which is need to set.
     * @param measureName      String which is need to set.
     */
    public void makeActionListenerMeasures(final JCheckBoxMenuItem checkBoxMenuItem, final String cube, final String measureName, final String xAxisName) {

        checkBoxMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (e.getSource().equals(checkBoxMenuItem)) {

                    graph.getQueryHistory().setCubeAttribute(measureName);
                    graph.getQueryHistory().setCubeName(cube);
                    graph.setxAxis(xAxisName);
                }
            }
        });
    }

    /**
     * @param checkBoxMenuItem Item which gets the Listener.
     * @param chartName        String which is need to set.
     */
    public void makeActionListenerCharts(final JCheckBoxMenuItem checkBoxMenuItem, final String chartName) {

        checkBoxMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                graph.setChartCheck(chartName);
            }
        });
    }
}