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
import unikn.dbis.univis.visualization.chart.ChartType;
import unikn.dbis.univis.visualization.graph.VGraph;
import unikn.dbis.univis.visualization.graph.VGraphCell;
import unikn.dbis.univis.message.MessageResolver;
import unikn.dbis.univis.message.Internationalizable;
import unikn.dbis.univis.message.swing.VLabel;
import unikn.dbis.univis.system.Constants;
import unikn.dbis.univis.images.VImageDummy;
import unikn.dbis.univis.pivot.view.UniViewPanelPivottable;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.event.*;
import java.awt.*;
import java.awt.dnd.DragSource;
import java.awt.dnd.DnDConstants;
import java.util.List;
import java.util.Locale;
import java.sql.Connection;
import java.sql.SQLException;
import java.io.IOException;
import java.io.InputStream;

import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.jgraph.graph.GraphLayoutCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.jdesktop.swingx.IncidentInfo;

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
public class VExplorer extends JFrame implements Internationalizable {

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

        InputStream imageStream = null;
        try {
            imageStream = VImageDummy.class.getResource("splash_screen.png").openStream();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }

        System.out.println("IN: " + imageStream);

        final VSplashScreen splashScreen = new VSplashScreen(imageStream);

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    VExplorer explorer = new VExplorer();
                    explorer.setVisible(true);

                    if (splashScreen != null) {
                        splashScreen.destroy();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // The logger to log info, error and other occuring messages
    // or exceptions.
    public static final transient Log LOG = LogFactory.getLog(VExplorer.class);

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
    private JPanel underStatePanel = new JPanel(new BorderLayout());
    private JPanel statePanel = new JPanel(new GridBagLayout());
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
    private JPopupMenu settingsMenu = new JPopupMenu();
    private JPopupMenu functionsMenu = new JPopupMenu();

    private JCheckBoxMenuItem resize = new JCheckBoxMenuItem(VIcons.ARROW_INOUT);
    private JRadioButtonMenuItem german = new JRadioButtonMenuItem(VIcons.FLAG_DE);
    private JRadioButtonMenuItem english = new JRadioButtonMenuItem(VIcons.FLAG_EN);
    private JRadioButtonMenuItem barChartHorizontal = new JRadioButtonMenuItem(VIcons.CHART_BAR_HORIZONTAL);
    private JRadioButtonMenuItem barChartVertical = new JRadioButtonMenuItem(VIcons.CHART_BAR_VERTICAL);
    private JRadioButtonMenuItem pieChart = new JRadioButtonMenuItem(VIcons.CHART_PIE);
    private JRadioButtonMenuItem areaChart = new JRadioButtonMenuItem(VIcons.CHART_AREA);
    private JRadioButtonMenuItem ringChart = new JRadioButtonMenuItem(VIcons.CHART_RING);
    private JRadioButtonMenuItem pivotTable = new JRadioButtonMenuItem(VIcons.TABLE);
    private JRadioButtonMenuItem heads = new JRadioButtonMenuItem(VIcons.STUDENTS_CASES);
    private JRadioButtonMenuItem cases = new JRadioButtonMenuItem(VIcons.STUDENTS_HEADS);
    private JRadioButtonMenuItem amount = new JRadioButtonMenuItem(VIcons.EURO);

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
    private JButton settings = new JButton(VIcons.APPLICATION_FORM_EDIT);
    private JButton rotateClockwise = new JButton(VIcons.SHAPE_ROTATE_CLOCKWISE);
    private JButton rotateAnticlockwise = new JButton(VIcons.SHAPE_ROTATE_ANTICLOCKWISE);
    private JMenuItem newMeasure = new JMenuItem(VIcons.NEW_MEASURE);
    private JMenuItem newLanguage = new JMenuItem(VIcons.NEW_WORLD);

    private String measureMessage;
    private String measureTitle;
    private String languageMessage;
    private String languageTitle;

    private VLabel measureLabel;
    private VLabel whatMeasureLabel;
    private VLabel chartLabel;
    private VLabel whatChartLabel;
    private VLabel languageLabel;
    private JLabel whatLanguageLabel;
    private VLabel dateLabel;
    private JLabel whatDateLabel;
    private VLabel timeLabel;
    private VClock whatTimeLabel;
    private JPanel complete;

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
        super("UniVis Explorer 0.2 - (c) 2005-2006 a.d. - DBIS, University of Konstanz");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 600));
        setSize(new Dimension(800, 600));

        // Centers the explorer on screen.
        setLocationRelativeTo(null);

        split.setDividerLocation(300);
        split.setOneTouchExpandable(true);

        initToolbar();
        initStatePanel();
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

        ComponentUtilities.repaintComponentTree(this);
    }

    private void initStatePanel() {

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        chartLabel = new VLabel(Constants.CHART);
        whatChartLabel = new VLabel(Constants.BAR_CHART_HORIZONTAL);
        complete = new JPanel();
        complete.add(chartLabel);
        complete.add(whatChartLabel);
        statePanel.add(complete, gbc);

        measureLabel = new VLabel(Constants.MEASURE);
        whatMeasureLabel = new VLabel(Constants.HEADS);
        complete = new JPanel();
        complete.add(measureLabel);
        complete.add(whatMeasureLabel);
        ++gbc.gridy;
        statePanel.add(complete, gbc);

        languageLabel = new VLabel(Constants.LANGUAGE);
        whatLanguageLabel = new JLabel(Locale.GERMAN.getDisplayName());
        complete = new JPanel();
        complete.add(languageLabel);
        complete.add(whatLanguageLabel);
        ++gbc.gridy;
        statePanel.add(complete, gbc);

        timeLabel = new VLabel(Constants.TIME);
        whatTimeLabel = new VClock();
        complete = new JPanel();
        complete.add(timeLabel);
        complete.add(whatTimeLabel);
        ++gbc.gridy;
        statePanel.add(complete, gbc);

        dateLabel = new VLabel(Constants.DATE);
        whatDateLabel = new JLabel(whatTimeLabel.getDate());
        complete = new JPanel();
        complete.add(dateLabel);
        complete.add(whatDateLabel);
        ++gbc.gridy;
        statePanel.add(complete, gbc);

        TitledBorder border = BorderFactory.createTitledBorder("Status");

        underStatePanel.setBorder(border);
        underStatePanel.add(statePanel, BorderLayout.WEST);
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
        makeSettingsMenu();
        makeLanguageMenu();

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

        settings.addMouseListener(new MouseAdapter() {

            /**
             * Invoked when the mouse has been clicked on a component.
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                settingsMenu.show(settings, 0, settings.getHeight());
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
                tree.updateUI();
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

        rotateClockwise.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if ((SwingConstants.NORTH) == graph.getLayoutOrientation()) {
                    graph.setLayoutOrientation(SwingConstants.EAST);
                }
                else if ((SwingConstants.EAST) == graph.getLayoutOrientation()) {
                    graph.setLayoutOrientation(SwingConstants.SOUTH);
                }
                else if ((SwingConstants.SOUTH) == graph.getLayoutOrientation()) {
                    graph.setLayoutOrientation(SwingConstants.WEST);
                }
                else if ((SwingConstants.WEST) == graph.getLayoutOrientation()) {
                    graph.setLayoutOrientation(SwingConstants.NORTH);
                }

                rotateGraph();
            }
        });

        rotateAnticlockwise.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if ((SwingConstants.NORTH) == graph.getLayoutOrientation()) {
                    graph.setLayoutOrientation(SwingConstants.WEST);
                }
                else if ((SwingConstants.WEST) == graph.getLayoutOrientation()) {
                    graph.setLayoutOrientation(SwingConstants.SOUTH);
                }
                else if ((SwingConstants.SOUTH) == graph.getLayoutOrientation()) {
                    graph.setLayoutOrientation(SwingConstants.EAST);
                }
                else if ((SwingConstants.EAST) == graph.getLayoutOrientation()) {
                    graph.setLayoutOrientation(SwingConstants.NORTH);
                }

                rotateGraph();
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

        undo.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (e.getSource().equals(undo)) {
                    graph.undoCells();
                    tree.updateUI();
                }
            }
        });

        toolbar.add(refresh);
        toolbar.add(undo);
        toolbar.add(redo);
        toolbar.add(delete);
        toolbar.addSeparator();
        toolbar.add(charts);
        toolbar.add(measures);
        toolbar.add(zoomIn);
        toolbar.add(zoomOut);
        toolbar.add(rotateClockwise);
        toolbar.add(rotateAnticlockwise);
        toolbar.addSeparator();
        toolbar.add(settings);
        toolbar.addSeparator();
        toolbar.add(languages);
        toolbar.addSeparator();
        toolbar.add(exit);
    }

    private void makeChartsMenu() {

        makeActionListenerCharts(barChartHorizontal, ChartType.BAR_CHART_HORIZONTAL, "barChartHorizontal");
        makeActionListenerCharts(barChartVertical, ChartType.BAR_CHART_VERTICAL, "barChartVertical");
        makeActionListenerCharts(pieChart, ChartType.PIE_CHART, "pieChart");
        makeActionListenerCharts(areaChart, ChartType.AREA_CHART, "areaChart");
        makeActionListenerCharts(ringChart, ChartType.RING_CHART, "ringChart");

        pivotTable.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                whatChartLabel.setI18NKey("pivottable");
                UniViewPanelPivottable pivot = new UniViewPanelPivottable();
                split.setRightComponent(pivot);

            }
        });

        ButtonGroup charts = new ButtonGroup();
        barChartHorizontal.setSelected(true);
        charts.add(barChartHorizontal);
        charts.add(barChartVertical);
        charts.add(pieChart);
        charts.add(areaChart);
        charts.add(ringChart);
        charts.add(pivotTable);

        chartsMenu.add(barChartHorizontal);
        chartsMenu.add(barChartVertical);
        chartsMenu.add(pieChart);
        chartsMenu.add(areaChart);
        chartsMenu.add(ringChart);
        chartsMenu.add(pivotTable);
    }

    private void makeMeasuresMenu() {


        makeActionListenerMeasures(heads, "sos_cube", "SUM(koepfe)", "Studenten", "heads");
        makeActionListenerMeasures(cases, "sos_cube", "SUM(faelle)", "Studenten", "cases");
        makeActionListenerMeasures(amount, "cob_busa_cube", "SUM(betrag)", "Betraege", "amount");

        newMeasure.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(VExplorer.this.getContentPane(), measureMessage, measureTitle, 1);
            }
        });


        ButtonGroup measuresGroup = new ButtonGroup();

        heads.setSelected(true);
        measuresGroup.add(heads);
        measuresGroup.add(cases);
        measuresGroup.add(amount);

        measuresMenu.add(heads);
        measuresMenu.add(cases);
        measuresMenu.add(amount);
        measuresMenu.add(newMeasure);
    }

    private void makeSettingsMenu() {

        resize.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                graph.setMoveable(!graph.isMoveable());
                ComponentUtilities.repaintComponentTree(VExplorer.this.graph);
            }
        });

        settingsMenu.add(resize);
    }

    private void makeLanguageMenu() {

        german.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Locale locale = Locale.GERMAN;
                MessageResolver.setLocale(locale);
                whatLanguageLabel.setText(locale.getDisplayName(locale));
                ComponentUtilities.repaintComponentTree(VExplorer.this);
            }
        });

        english.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Locale locale = Locale.ENGLISH;
                MessageResolver.setLocale(locale);
                whatLanguageLabel.setText(locale.getDisplayName(locale));
                ComponentUtilities.repaintComponentTree(VExplorer.this);
            }
        });

        newLanguage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(VExplorer.this.getContentPane(), languageMessage, languageTitle, 1);
            }
        });

        final ButtonGroup languageGroup = new ButtonGroup();
        german.setSelected(true);
        languageGroup.add(german);
        languageGroup.add(english);
        languageMenu.add(german);
        languageMenu.add(english);
        languageMenu.add(newLanguage);
    }

    private void initNavigation() {

        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();

        VDiceBox diceBox = (VDiceBox) session.createQuery("from " + VDiceBoxImpl.class.getName() + " where name = 'UniVis Explorer'").uniqueResult();
        tree = new VTree(diceBox);

        session.close();

        navigation.add(new JScrollPane(tree), BorderLayout.CENTER);
        navigation.add(underStatePanel, BorderLayout.SOUTH);

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
        dragSource.addDragSourceListener(tree);
        dragSource.createDefaultDragGestureRecognizer(tree, DnDConstants.ACTION_COPY, tree);
    }

    public static void publishException(Exception e) {
        System.out.println("REMOVED BECAUSE SHOULDN'T BE STATIC EXPLORER");
        //IncidentInfo info = new IncidentInfo("Header", e.getMessage(), "asdfa\nasdfasdf\nasdfsadf\n\nsadfa", e);
        //JXErrorDialog.showDialog(explorer, info);
        //new VDialog(explorer, e);
    }

    /**
     * @param radioButtonMenuItem Item which gets the Listener.
     * @param cube                String which is need to set.
     * @param measureName         String which is need to set.
     */
    public void makeActionListenerMeasures(final JRadioButtonMenuItem radioButtonMenuItem, final String cube, final String measureName, final String xAxisName, final String i18NKey) {

        radioButtonMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (e.getSource().equals(radioButtonMenuItem)) {

                    graph.getQueryHistory().setCubeAttribute(measureName);
                    graph.getQueryHistory().setCubeName(cube);
                    graph.setxAxis(xAxisName);
                    whatMeasureLabel.setI18NKey(i18NKey);

                }
            }
        });
    }

    /**
     * @param radioButtonMenuItem Item which gets the Listener.
     * @param chartType           The type of the current chart.
     */
    public void makeActionListenerCharts(final JRadioButtonMenuItem radioButtonMenuItem, final ChartType chartType, final String i18NKey) {

        radioButtonMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                graph.setChartType(chartType);
                whatChartLabel.setI18NKey(i18NKey);
            }
        });
    }

    public void rotateGraph() {
        GraphLayoutCache cache = graph.getGraphLayoutCache();
        Object cells[] = cache.getCells(false, true, false, false);
        Object edges[] = cache.getCells(false, false, false, true);

        graph.reloadGraph();
        cache.remove(edges);

        for (Object cell1 : cells) {

            VGraphCell cell = (VGraphCell) cell1;
            if (!cell.toString().equals("root")) {
                graph.createEdges(cell, cell.getCellId());
            }
        }
    }

    public void internationalize() {
        // Sets the text for the MenuItems.
        german.setText(MessageResolver.getMessage(Constants.GERMAN));
        english.setText(MessageResolver.getMessage(Constants.ENGLISH));
        barChartHorizontal.setText(MessageResolver.getMessage(Constants.BAR_CHART));
        barChartVertical.setText(MessageResolver.getMessage(Constants.BAR_CHART));
        pieChart.setText(MessageResolver.getMessage(Constants.PIE_CHART));
        areaChart.setText(MessageResolver.getMessage(Constants.AREA_CHART));
        ringChart.setText(MessageResolver.getMessage(Constants.RING_CHART));
        pivotTable.setText(MessageResolver.getMessage(Constants.PIVOT_TABLE));
        heads.setText(MessageResolver.getMessage(Constants.HEADS));
        cases.setText(MessageResolver.getMessage(Constants.CASES));
        amount.setText(MessageResolver.getMessage(Constants.AMOUNT));
        resize.setText(MessageResolver.getMessage(Constants.GRAPH_EDITABLE));

        // Sets the tooltip for the Buttons.
        settings.setToolTipText(MessageResolver.getMessage(Constants.SETTINGS_TOOLTIP));
        resize.setToolTipText(MessageResolver.getMessage(Constants.GRAPH_EDITABLE_TOOLTIP));
        refresh.setToolTipText(MessageResolver.getMessage(Constants.REFRESH_TOOLTIP));
        undo.setToolTipText(MessageResolver.getMessage(Constants.UNDO_TOOLTIP));
        redo.setToolTipText(MessageResolver.getMessage(Constants.REDO_TOOLTIP));
        delete.setToolTipText(MessageResolver.getMessage(Constants.DELETE_TOOLTIP));
        charts.setToolTipText(MessageResolver.getMessage(Constants.CHARTS_TOOLTIP));
        measures.setToolTipText(MessageResolver.getMessage(Constants.MEASURES_TOOLTIP));
        newMeasure.setText(MessageResolver.getMessage(Constants.NEW_MEASURE));
        newMeasure.setToolTipText(MessageResolver.getMessage(Constants.NEW_MEASURE_TOOLTIP));
        newLanguage.setText(MessageResolver.getMessage(Constants.NEW_LANGUAGE));
        newLanguage.setToolTipText(MessageResolver.getMessage(Constants.NEW_LANGUAGE));
        languages.setToolTipText(MessageResolver.getMessage(Constants.LANGUAGES_TOOLTIP));
        exit.setToolTipText(MessageResolver.getMessage(Constants.EXIT_TOOLTIP));
        zoomIn.setToolTipText(MessageResolver.getMessage(Constants.ZOOM_IN_TOOLTIP));
        zoomOut.setToolTipText(MessageResolver.getMessage(Constants.ZOOM_OUT_TOOLTIP));
        rotateClockwise.setToolTipText(MessageResolver.getMessage(Constants.ROTATE_CLOCKWISE_TOOLTIP));
        rotateAnticlockwise.setToolTipText(MessageResolver.getMessage(Constants.ROTATE_ANTICLOCKWISE_TOOLTIP));
        measureMessage = MessageResolver.getMessage(Constants.MEASURE_MESSAGE);
        measureTitle = MessageResolver.getMessage(Constants.NEW_MEASURE);
        languageMessage = MessageResolver.getMessage(Constants.NEW_LANGUAGE_MESSAGE);
        languageTitle = MessageResolver.getMessage(Constants.NEW_LANGUAGE);
    }
}