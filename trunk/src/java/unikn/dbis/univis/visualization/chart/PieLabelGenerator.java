package unikn.dbis.univis.visualization.chart;

import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.data.general.PieDataset;

import java.text.AttributedString;
import java.text.DecimalFormat;

/**
 * TODO: document me!!!
 * <p/>
 * PieLabelGenerator.
 * <p/>
 * User: raedler, weiler
 * Date: 29.04.2006
 * Time: 12:30:31
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public class PieLabelGenerator implements PieSectionLabelGenerator {

    private int total;
    private double full = 100.0;
    private DecimalFormat df = new DecimalFormat("0.00");

    public PieLabelGenerator(int total) {

        this.total = total;

    }

    public String generateSectionLabel(PieDataset dataset, Comparable key) {

        Integer value = dataset.getValue(key).intValue();
        Comparable name = dataset.getKey(dataset.getIndex(key));
        Double procent = ((full / total) * value);

        String label = name.toString() + " = " + value.toString() + " -> " + df.format(procent) + "%";

        return label;
    }

    public AttributedString generateAttributedSectionLabel(PieDataset dataset, Comparable key) {
        return null;
    }
}