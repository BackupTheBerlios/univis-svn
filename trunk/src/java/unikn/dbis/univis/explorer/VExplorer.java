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
import unikn.dbis.univis.message.MessageResolver;
import unikn.dbis.univis.system.Constants;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.dnd.DragSource;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.util.List;
import java.util.Locale;
import java.sql.Connection;
import java.sql.SQLException;

import org.hibernate.SessionFactory;
import org.hibernate.Session;
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
    private JPopupMenu languageMenu = new JPopupMenu();
    private JPopupMenu layoutMenu = new JPopupMenu();

    private JCheckBoxMenuItem german = new JCheckBoxMenuItem(VIcons.FLAG_DE);
    private JCheckBoxMenuItem english = new JCheckBoxMenuItem(VIcons.FLAG_EN);
    private JCheckBoxMenuItem barChart1 = new JCheckBoxMenuItem(VIcons.CHART_BAR_HORIZONTAL);
    private JCheckBoxMenuItem barChart2 = new JCheckBoxMenuItem(VIcons.CHART_BAR_VERTICAL);
    private JCheckBoxMenuItem pieChart = new JCheckBoxMenuItem(VIcons.CHART_PIE);
    private JCheckBoxMenuItem areaChart = new JCheckBoxMenuItem(VIcons.CHART_AREA);
    private JCheckBoxMenuItem heads = new JCheckBoxMenuItem(VIcons.USERK);
    private JCheckBoxMenuItem cases = new JCheckBoxMenuItem(VIcons.USERF);
    private JCheckBoxMenuItem amount = new JCheckBoxMenuItem(VIcons.EURO);
    private JCheckBoxMenuItem layoutVertical = new JCheckBoxMenuItem(VIcons.VERTICAL_LAYOUT);
    private JCheckBoxMenuItem layoutHorizontal = new JCheckBoxMenuItem(VIcons.HORIZONTAL_LAYOUT);

    private JButton refresh = new JButton(VIcons.ARROW_REFRESH);
    private JButton undo = new JButton(VIcons.ARROW_UNDO);
    private JButton redo = new JButton(VIcons.ARROW_REDO);
    private JButton delete = new JButton(VIcons.DELETE);
    private JButton charts = new JButton(VIcons.CHART);
    private JButton measures = new JButton(VIcons.MEASURE);
    private JButton languages = new JButton(VIcons.WORLD);
    private JButton exit = new JButton(VIcons.EXIT);
    private JButton zoomIn = new JButton(VIcons.ZOOM_IN);
    private JButton zoomOut = new JButton(VIcons.ZOOM_OUT);
    private JButton layout = new JButton(VIcons.SHAPE_ROTATE_CLOCKWISE);
    private JMenuItem newMeasure = new JMenuItem(VIcons.NEW_MEASURE);

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
        reinitI18();
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

        /*graph.addMouseListener(new MouseAdapter() {

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
        });*/
    }

    private void initToolbar() {

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


        makeChartsMenu();
        makeMeasuresMenu();
        makeLanguageMenu();
        makeLayoutMenu();

        charts.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent evt) {
                chartsMenu.show(charts, 0, charts.getHeight());
            }
        });

        measures.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent evt) {
                measuresMenu.show(measures, 0, measures.getHeight());
            }
        });

        languages.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent evt) {
                languageMenu.show(languages, 0, languages.getHeight());
            }
        });

        delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                graph.reset();
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

        layout.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent evt) {
                layoutMenu.show(layout, 0, layout.getHeight());
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
        toolbar.addSeparator();
        toolbar.add(charts);
        toolbar.add(measures);
        toolbar.add(zoomIn);
        toolbar.add(zoomOut);
        toolbar.add(layout);
        toolbar.add(languages);
        toolbar.addSeparator();
        toolbar.add(exit);
    }

    private void makeChartsMenu() {

        makeActionListenerCharts(barChart1, "barChart", "Vertical");
        makeActionListenerCharts(barChart2, "barChart", "Horizontal");
        makeActionListenerCharts(pieChart, "pieChart", "");
        makeActionListenerCharts(areaChart, "areaChart", "");
        ButtonGroup charts = new ButtonGroup();
        barChart1.setState(true);
        barChart2.setState(false);
        pieChart.setState(false);
        areaChart.setState(false);
        charts.add(barChart1);
        charts.add(barChart2);
        charts.add(pieChart);
        charts.add(areaChart);

        chartsMenu.add(barChart1);
        chartsMenu.add(barChart2);
        chartsMenu.add(pieChart);
        chartsMenu.add(areaChart);
    }

    private void makeMeasuresMenu() {


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
        measuresMenu.add(newMeasure);
    }

    private void makeLanguageMenu() {

        german.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MessageResolver.setLocale(Locale.GERMAN);
                reinitI18();
                ComponentUtilities.repaintComponentTree(VExplorer.this);
            }
        });

        english.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MessageResolver.setLocale(Locale.ENGLISH);
                reinitI18();
                ComponentUtilities.repaintComponentTree(VExplorer.this);
            }
        });

        ButtonGroup languageGroup = new ButtonGroup();
        german.setState(true);
        english.setState(false);
        languageGroup.add(german);
        languageGroup.add(english);

        languageMenu.add(german);
        languageMenu.add(english);
    }

    private void makeLayoutMenu() {
        layoutVertical.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                graph.setLayoutOrientation(SwingConstants.NORTH);
                graph.reloadGraph();
            }
        });

        layoutHorizontal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                graph.setLayoutOrientation(SwingConstants.WEST);
                graph.reloadGraph();
            }
        });

        ButtonGroup layoutGroup = new ButtonGroup();
        layoutVertical.setState(true);
        layoutHorizontal.setState(false);
        layoutGroup.add(layoutVertical);
        layoutGroup.add(layoutHorizontal);

        layoutMenu.add(layoutVertical);
        layoutMenu.add(layoutHorizontal);
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

        navigation.add(facts, BorderLayout.NORTH);
        navigation.add(new JScrollPane(tree), BorderLayout.CENTER);

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
    public void makeActionListenerCharts(final JCheckBoxMenuItem checkBoxMenuItem, final String chartName, final String barChartOrientation) {

        checkBoxMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                graph.setChartCheck(chartName);
                graph.setBarChartOrientation(barChartOrientation);
            }
        });
    }

    public void reinitI18() {
        // Sets the text for the MenuItems.
        german.setText(MessageResolver.getMessage(Constants.GERMAN));
        english.setText(MessageResolver.getMessage(Constants.ENGLISH));
        barChart1.setText(MessageResolver.getMessage(Constants.BAR_CHART));
        barChart2.setText(MessageResolver.getMessage(Constants.BAR_CHART));
        pieChart.setText(MessageResolver.getMessage(Constants.PIE_CHART));
        areaChart.setText(MessageResolver.getMessage(Constants.AREA_CHART));
        heads.setText(MessageResolver.getMessage(Constants.HEADS));
        cases.setText(MessageResolver.getMessage(Constants.CASES));
        amount.setText(MessageResolver.getMessage(Constants.AMOUNT));
        layoutVertical.setText(MessageResolver.getMessage(Constants.LAYOUT_VERTICAL));
        layoutHorizontal.setText(MessageResolver.getMessage(Constants.LAYOUT_HORIZONTAL));

        // Sets the tooltip for the Buttons.
        refresh.setToolTipText(MessageResolver.getMessage(Constants.REFRESH_TOOLTIP));
        undo.setToolTipText(MessageResolver.getMessage(Constants.UNDO_TOOLTIP));
        redo.setToolTipText(MessageResolver.getMessage(Constants.REDO_TOOLTIP));
        delete.setToolTipText(MessageResolver.getMessage(Constants.DELETE_TOOLTIP));
        charts.setToolTipText(MessageResolver.getMessage(Constants.CHARTS_TOOLTIP));
        measures.setToolTipText(MessageResolver.getMessage(Constants.MEASURES_TOOLTIP));
        newMeasure.setText(MessageResolver.getMessage(Constants.NEW_MEASURE));
        newMeasure.setToolTipText(MessageResolver.getMessage(Constants.NEW_MEASURE_TOOLTIP));
        languages.setToolTipText(MessageResolver.getMessage(Constants.LANGUAGES_TOOLTIP));
        exit.setToolTipText(MessageResolver.getMessage(Constants.EXIT_TOOLTIP));
        zoomIn.setToolTipText(MessageResolver.getMessage(Constants.ZOOM_IN_TOOLTIP));
        zoomOut.setToolTipText(MessageResolver.getMessage(Constants.ZOOM_OUT_TOOLTIP));
        layout.setToolTipText(MessageResolver.getMessage(Constants.LAYOUT_TOOLTIP));
    }
}