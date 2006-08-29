package unikn.dbis.univis.meta;

/**
 * TODO: document me!!!
 * <p/>
 * <code>VCombination</code>.
 * <p/>
 * User: raedler, weiler
 * Date: 29.08.2006
 * Time: 01:20:24
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Revision$
 * @since UniVis Explorer 0.2
 */
public interface VCombination {
    public VCube getCube();
    public void setCube(VCube cube);
    public VDimension getDimension();
    public void setDimension(VDimension dimension);
    public VMeasure getMeasure();
    public void setMeasure(VMeasure measure);
    public VFunction getFunction();
    public void setFunction(VFunction function);
}
