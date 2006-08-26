package unikn.dbis.univis.marion.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableColumn;

import table.model.renderer.SimpleRenderer;
import unikn.dbis.univis.marion.data.UniViewTableData;
import unikn.dbis.univis.dnd.VDataReferenceFlavor;
import unikn.dbis.univis.meta.VDimension;
import unikn.dbis.univis.meta.VMeasure;
import unikn.dbis.univis.meta.VCube;

/**
 * @author Marion Herb
 *         <p/>
 *         Organizes Pivottable Panel with areas to drop measures and nodes for X-/Y-Axis
 */
public class UniViewPanelPivottable extends UniViewPanel implements DropTargetListener {

    // SQL neu generieren und ausgeführen und Pivottabelle neu zeichnen? 
    boolean selectionChanged = false;

    JPanel panelFirstRow;
    JPanel panelSecondRow;
    JPanel panelThirdRow;
    JTextArea dropAreaColX;
    JTextArea dropAreaRowY;
    JTextArea dropAreaMeasure;

    TableSorter jTableSorter;
    JTable jPivotTable;

    JScrollPane jScrollPane;
    JPanel panelButtons;

    JButton bStart;
    JButton bStartDataView;
    JButton bReset;

    JScrollBar bar;

    String textInXYBox = "Drop Element(s)!";
    String textInMeasureBox = "Drop Measure!";

    Vector xAxisNodes = new Vector();
    Vector yAxisNodes = new Vector();
    Vector measureNodes = new Vector();

    UniViewTableData uniData;

    public UniViewPanelPivottable() {
        super();

        /* *** First Row *** */
        JLabel jlabelMeasure = new JLabel("Measure:");
        JLabel jlabelX = new JLabel("X-Axis Elements:");
        JLabel forVerticalSpace = new JLabel(" ");
        panelFirstRow = new JPanel(new TableLayout(3, 2, 3, 3));

        dropAreaMeasure = new JTextArea(textInMeasureBox);
        dropAreaMeasure.setToolTipText(textInMeasureBox);
        dropAreaMeasure.setForeground(Color.LIGHT_GRAY);
        dropAreaMeasure.setEditable(false);
        dropAreaMeasure.setPreferredSize(new Dimension(100, 20));
        dropAreaMeasure.setBorder(BorderFactory.createEtchedBorder(Color.WHITE, Color.LIGHT_GRAY));

        dropAreaColX = new JTextArea(textInXYBox);
        dropAreaColX.setToolTipText(textInXYBox);
        dropAreaColX.setForeground(Color.LIGHT_GRAY);
        dropAreaColX.setEditable(false);
        dropAreaColX.setPreferredSize(new Dimension(400, 20));
        dropAreaColX.setBorder(BorderFactory.createEtchedBorder(Color.WHITE, Color.LIGHT_GRAY));

        panelFirstRow.add(jlabelMeasure);
        panelFirstRow.add(jlabelX);
        panelFirstRow.add(dropAreaMeasure);
        panelFirstRow.add(dropAreaColX);
        panelFirstRow.add(forVerticalSpace);
        add(panelFirstRow, BorderLayout.NORTH);

        /* *** Second Row *** */
        JLabel jlabelY1 = new JLabel("Y-Axis");
        JLabel jlabelY2 = new JLabel("Elements:");
        panelSecondRow = new JPanel(new TableLayout(1, 2, 3, 3));

        dropAreaRowY = new JTextArea(textInXYBox);
        dropAreaRowY.setToolTipText(textInXYBox);
        dropAreaRowY.setForeground(Color.LIGHT_GRAY);
        dropAreaRowY.setEditable(false);
        dropAreaRowY.setLineWrap(true);
        dropAreaRowY.setWrapStyleWord(true);
        dropAreaRowY.setPreferredSize(new Dimension(100, 400));
        dropAreaRowY.setBorder(BorderFactory.createEtchedBorder(Color.WHITE, Color.LIGHT_GRAY));

        JPanel panelY = new JPanel(new TableLayout(3, 1, 1, 1));
        panelY.add(jlabelY1);
        panelY.add(jlabelY2);
        panelY.add(dropAreaRowY);
        panelSecondRow.add(panelY);
        add(panelSecondRow, BorderLayout.WEST);

        /* *** Third Row *** */
//        bStart = UniLayout.newButton("Create Pivot-Table View", al_createTable);
        bStartDataView = UniLayout.newButton("Create Only-Data View", al_dataViewTable);
        bReset = UniLayout.newButton("Reset View", al_reset);
        panelButtons = new JPanel();
        // 1 Row, 3 Columns
        panelButtons.setLayout(new TableLayout(1, 4));
        panelButtons.setBackground(Color.WHITE);
//        panelButtons.add(bStart);
        panelButtons.add(bStartDataView);
        panelButtons.add(bReset);
        add(panelButtons, BorderLayout.SOUTH);

        new DropTarget(dropAreaColX, DnDConstants.ACTION_COPY_OR_MOVE, this);
        new DropTarget(dropAreaRowY, DnDConstants.ACTION_COPY_OR_MOVE, this);
        new DropTarget(dropAreaMeasure, DnDConstants.ACTION_COPY_OR_MOVE, this);
    }

    /*
     * DropTargetListener -> Handles drop target events including the drop
     * itself
     */
    public void drop(DropTargetDropEvent dtde) {
        Object o = null;
        try {
            o = dtde.getTransferable().getTransferData(VDataReferenceFlavor.DIMENSION_FLAVOR);
        }
        catch (UnsupportedFlavorException ufe) {
            dtde.rejectDrop();
            dtde.dropComplete(false);
            /*if (LOG.isErrorEnabled()) {
               LOG.error(ufe.getMessage(), ufe);
           } */
        }
        catch (IOException ioe) {
            dtde.rejectDrop();
            dtde.dropComplete(false);
            /*if (LOG.isErrorEnabled()) {
               LOG.error(ioe.getMessage(), ioe);
           } */
        }
        if (o instanceof VDimension) {
            VDimension dimension = (VDimension) o;
            action(dimension, dtde);
            dtde.dropComplete(true);
        }
    }

    public void action(VDimension dimension, DropTargetDropEvent dtde) {
// in welche Component wurde gedroppt?
//        String eventTarget = new String(e.getSource().toString());
//        String eventTargetFieldColumns = new String(textfieldColumnsX
//                .getDropTarget().toString());
//        String eventTargetFieldRows = new String(textareaRowsY.getDropTarget()
//                .toString());

        // hinzufuegen in entsprechenden Vector
        if (dtde.getSource() == dropAreaColX.getDropTarget()) {
//            System.out.println("Measure?" + (dimension instanceof Measure) + "/size<1?" + ((xAxisNodes.size() < 1)));
            if (dimension instanceof VMeasure) {
                System.err.println("Drop of Measure not allowed.");
                selectionChanged = false;
            }
            else if (xAxisNodes.size() < 1) { // noch kein X-Achsen-Element spezifiziert
                selectionChanged = true;
                boolean rootWithChildren = dimension.getBlueprint().getClass().equals(VCube.class) && dimension.getDescendants().size() > 0;

                System.out.println("GUI: dropped as column value [" + dimension.toString() + "] Root: " + dimension.getBlueprint().toString());

                // wenn der Node der RootNode ist (BLUEP...) und er hat noch
                // Kinderknoten, dann soll nicht dieser, sondern seine Kinderknoten
                // gedropped werden
                if (rootWithChildren) {
                    /*
                    Vector descendants = dimension.getDescendants();
                    for (Iterator iter = descendants.iterator(); iter.hasNext();) {
                        Node child = (Node) iter.next();
                        xAxisNodes.add( child );
                    }
                    */
                }
                else {
                    xAxisNodes.add(dimension);
                }
                // anzeigen in Box
                dropAreaColX.setForeground(Color.BLACK);
                dropAreaColX.setText(xAxisNodes.toString());

            }
            else { // es wurde schon X-Achsen-Element festgelegt --> momentan nur 1 ausgewertet
                System.err.println("Currently only one Element for X-Axis allowed.");
                selectionChanged = false;
            }

        }
        else if (dtde.getSource() == dropAreaRowY.getDropTarget()) {
            if (dimension instanceof VMeasure) {
                System.err.println("Drop of Measure not allowed.");
                selectionChanged = false;
            }
            else {
                if (!yAxisNodes.contains(dimension)) { // nur wenn noch nicht enthalten
                    boolean rootWithChildren = dimension.getBlueprint().getClass().equals(VCube.class) && dimension.getDescendants().size() > 0;
                    selectionChanged = true;

                    System.out.println("GUI: dropped as row value [" + dimension.toString() + "] Root: " + dimension.getBlueprint().toString());

                    if (rootWithChildren) {
                        /*
                        Nodes descendants = dimension.getDescendants();
                        for (Iterator iter = descendants.iterator(); iter.hasNext();) {
                            Node child = (Node) iter.next();
                            yAxisNodes.add(child);
                        }
                        */
                    }
                    else {
                        yAxisNodes.add(dimension);
                    }
                    // anzeigen in Box
                    dropAreaRowY.setForeground(Color.BLACK);
                    dropAreaRowY.setText(yAxisNodes.toString());
                }
                else {
                    selectionChanged = false;
                }
            }

        }
        else if (dtde.getSource() == dropAreaMeasure.getDropTarget()) {
            if (!(dimension instanceof VMeasure)) {
                System.err.println("Drop of Dimension not allowed.");
                selectionChanged = false;
            }
            else if (measureNodes.size() < 1) {  // noch kein Measure spezifiziert
                selectionChanged = true;
                System.out.println("GUI: dropped as measure value [" + dimension.toString());
                measureNodes.add(dimension);
                dropAreaMeasure.setForeground(Color.BLACK);
                dropAreaMeasure.setText(measureNodes.toString());
            }
            else { // es wurde schon Measure festgelegt --> momentan nur 1 ausgewertet
                System.err.println("Currently only one Measure allowed.");
                selectionChanged = false;
            }

        }
        else {
            System.out.println("Wrong eventTarget: " + dimension.getTableName());
        }

        /*
           * Zeichnen wenn ausreichend viele Werte spezifiziert
           * ausreichend bedeutet:
           *    - genau 1 Measure plus
           *    - genau 1 Wert in x-Achse oder beliebig viele in y-Achse
           * Es wird auch nur neu gezeichnet wenn eine Änderung eingetreten
           */

        boolean anyMeasure = (measureNodes.size() == 1);
        boolean anyNode = (xAxisNodes.size() == 1) || (yAxisNodes.size() > 0); //t t -> t, t f -> t, f t -> t, f f -> f

//        System.out.println("---" + anyMeasure + " " + anyNode + " " + selectionChanged + " ==> " + (anyMeasure && anyNode && selectionChanged));

        // Check if table has to be drawn:
        if (anyMeasure && anyNode && selectionChanged) {
            createPivotTable(false);
        }

    }

    public void dragEnter(DropTargetDragEvent dtde) {
    }

    public void dragOver(DropTargetDragEvent dtde) {
    }

    public void dropActionChanged(DropTargetDragEvent dtde) {
    }

    public void dragExit(DropTargetEvent dte) {
    }

    /*
     * boolean flag legt fest welche Ansicht gezeigt wird 
     * Entweder Pivottabelle oder Tabelle ohne Totalling 
     */
    private void createPivotTable(boolean dataOnly) {

        try {
            remove(jScrollPane);
        }
        catch (Exception e) {
            // TODO: handle exception
        }

        // JTable anlegen mit Daten sowie Ueberschriften
        uniData = new UniViewTableData();
        Vector vectorData = this.uniData.getPivottableData(xAxisNodes, yAxisNodes, measureNodes);
        Vector vectorHeader = this.uniData.getPivottableHeader(xAxisNodes, yAxisNodes);

        // Sind denn ueberhaupt Daten zurueckgekommen?
        boolean dataGiven = ((vectorData != null) && (vectorData.size() > 0));
        boolean headerDataGiven = ((vectorHeader != null && vectorHeader.size() > 0));

        if (!dataGiven) {
            System.out.println("keine Daten!");
        }
        if (!headerDataGiven) {
            System.out.println("keine Ueberschriften!");
        }
        if (dataGiven & headerDataGiven) {

            TableModel modelOrigin = new TableModel(vectorData, vectorHeader);

            if (dataOnly) {

                jTableSorter = new TableSorter(modelOrigin);
                jPivotTable = new JTable(jTableSorter);
                jTableSorter.setTableHeader(jPivotTable.getTableHeader());

            }
            else {

                MyEnvelopeTableModel modelEnv = new MyEnvelopeTableModel(modelOrigin);
                jPivotTable = new JTable(modelEnv);
                modelEnv.setTableHeader(jPivotTable.getTableHeader());

            }

            SimpleRenderer renderer = new MyNumberRenderer(false);
            jPivotTable.setDefaultRenderer(String.class, renderer);
            // reine Number-Klasse hat Doubles nicht gefaerbt!
//            jPivotTable.setDefaultRenderer(Number.class, renderer);
            jPivotTable.setDefaultRenderer(Integer.class, renderer);
            jPivotTable.setDefaultRenderer(Long.class, renderer);
            jPivotTable.setDefaultRenderer(Double.class, renderer);

            // spezieller CellRenderer für letzte Total-Spalte
            TableColumn col = jPivotTable.getColumnModel().getColumn(vectorHeader.size() - 1);
            col.setCellRenderer(new MyTableTotalCellRenderer());

            /* *** Second Row --> Content Area *** */
            jPivotTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

            //Set up tool tips for column headers.
//            jPivotTable.getTableHeader().setToolTipText(
//                    "Click to specify sorting; Control-Click to specify secondary sorting");

            /* *** Second Row --> Content Area *** */
            jPivotTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            jScrollPane = new JScrollPane(jPivotTable);
            jScrollPane.setVerticalScrollBar(new JScrollBar());

            add(jScrollPane, BorderLayout.CENTER);

            validate();
            repaint();
        }
    }

    ActionListener al_createTable = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            boolean dataOnly = false;
            createPivotTable(dataOnly);
        }
    };

    /* deletes Textfields and most of all JTable */
    ActionListener al_reset = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            xAxisNodes = new Vector();
            yAxisNodes = new Vector();
            measureNodes = new Vector();
            dropAreaColX.setForeground(Color.LIGHT_GRAY);
            dropAreaColX.setToolTipText(textInXYBox);
            dropAreaColX.setText(textInXYBox);
            dropAreaRowY.setForeground(Color.LIGHT_GRAY);
            dropAreaRowY.setToolTipText(textInXYBox);
            dropAreaRowY.setText(textInXYBox);
            dropAreaMeasure.setForeground(Color.LIGHT_GRAY);
            dropAreaMeasure.setToolTipText(textInMeasureBox);
            dropAreaMeasure.setText(textInMeasureBox);
            try {
                remove(jScrollPane);
            }
            catch (Exception exc) {
            }
            validate();
            repaint();
            System.out.println("\n#### View and Data reset ! ####");
        }
    };

    ActionListener al_dataViewTable = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            boolean dataOnly = true;
            createPivotTable(dataOnly);
        }
    };

}
