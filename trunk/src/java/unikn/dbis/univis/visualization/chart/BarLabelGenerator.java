package unikn.dbis.univis.visualization.chart;

import org.jfree.chart.labels.CategorySeriesLabelGenerator;
import org.jfree.data.category.CategoryDataset;

import java.text.DecimalFormat;

/**
 * TODO: document me!!!
 * <p/>
 * BarLabelGenerator.
 * <p/>
 * User: raedler, weiler
 * Date: 28.04.2006
 * Time: 23:09:51
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public class BarLabelGenerator implements CategorySeriesLabelGenerator {

    private int total;
    private double full = 100.0;
    private DecimalFormat df = new DecimalFormat("0.00");

    public BarLabelGenerator(int total) {

        this.total = total;

    }

    public String generateLabel(CategoryDataset dataset, int series) {

        Integer value = dataset.getValue(series, 0).intValue();
        Comparable name = dataset.getRowKey(series);
        Double procent = ((full / total) * value);

        String label = name.toString() + " = " + value.toString() + " -> " + df.format(procent) + "%";

        return label;
    }
}