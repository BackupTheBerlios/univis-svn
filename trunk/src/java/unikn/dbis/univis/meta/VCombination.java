package unikn.dbis.univis.meta;

/**
 * Created by IntelliJ IDEA.
 * User: Romsl
 * Date: 29.08.2006
 * Time: 01:20:12
 * To change this template use File | Settings | File Templates.
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
