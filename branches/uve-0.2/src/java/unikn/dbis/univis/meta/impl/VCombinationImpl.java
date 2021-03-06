package unikn.dbis.univis.meta.impl;

import unikn.dbis.univis.meta.*;

/**
 * TODO: document me!!!
 * <p/>
 * <code>VCombinationImpl</code>.
 * <p/>
 * User: raedler, weiler
 * Date: 29.08.2006
 * Time: 01:23:24
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Revision$
 * @since UniVis Explorer 0.2
 */
public class VCombinationImpl implements VCombination {

    private VCube cube;
    private VDimension dimension;
    private VMeasure measure;
    private VFunction function;

    public VCube getCube() {
        return cube;
    }

    public void setCube(VCube cube) {
        this.cube = cube;
    }

    public VDimension getDimension() {
        return dimension;
    }

    public void setDimension(VDimension dimension) {
        this.dimension = dimension;
    }

    public VMeasure getMeasure() {
        return measure;
    }

    public void setMeasure(VMeasure measure) {
        this.measure = measure;
    }

    public VFunction getFunction() {
        return function;
    }

    public void setFunction(VFunction function) {
        this.function = function;
    }
}
