package unikn.dbis.univis.meta;

import junit.framework.TestCase;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.PostgreSQLDialect;
import unikn.dbis.univis.hibernate.util.HibernateUtil;
import unikn.dbis.univis.meta.impl.CubeImpl;
import unikn.dbis.univis.meta.impl.DimensionImpl;

/**
 * TODO: document me!!!
 * <p/>
 * <code>MetaTest</code>.
 * <p/>
 * User: raedler, weiler
 * Date: 07.04.2006
 * Time: 17:33:04
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public class MetaTest extends TestCase {

    SessionFactory sessionFactory;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        sessionFactory = HibernateUtil.getSessionFactory();
    }

    private Long cubeId;
    private Long dimensionId;

    /*
    public void testInsertCube() {
        CubeImpl cube = new CubeImpl();
        cube.setTableName("STUDENTS");

        Session session = sessionFactory.openSession();
        Transaction trx = session.beginTransaction();
        session.saveOrUpdate(cube);
        trx.commit();
        session.close();

        session = sessionFactory.openSession();
        CubeImpl loadedCube = (CubeImpl) session.load(CubeImpl.class, cube.getId());

        assertEquals(cube.getId(), loadedCube.getId());
        assertEquals(cube.getDimensions(), loadedCube.getDimensions());

        cubeId = cube.getId();
    }

    public void testAddDimensionToCube() {
        testInsertCube();

        Session session = sessionFactory.openSession();
        CubeImpl cube = (CubeImpl) session.load(CubeImpl.class, cubeId);

        DimensionImpl dimension = new DimensionImpl();
        dimension.setAbstract(true);
        dimension.setTableName("BLUEP_ZEIT");

        cube.addDimension(dimension);

        Transaction trx = session.beginTransaction();
        session.saveOrUpdate(cube);
        trx.commit();
        session.close();

        session = sessionFactory.openSession();
        CubeImpl loadedCube = (CubeImpl) session.load(CubeImpl.class, cube.getId());

        assertEquals(cube.getDimensions(), loadedCube.getDimensions());

        session.close();

        dimensionId = dimension.getId();
    }

    public void testAddSubDimensionToDimension() {
        testAddDimensionToCube();

        Session session = sessionFactory.openSession();
        DimensionImpl dimension = (DimensionImpl) session.load(DimensionImpl.class, dimensionId);

        DimensionImpl subDimension = new DimensionImpl();
        subDimension.setAbstract(false);
        subDimension.setTableName("DIM_HALBJAHRE");

        dimension.addSubDimension(subDimension);

        Transaction trx = session.beginTransaction();
        session.saveOrUpdate(dimension);
        trx.commit();
        session.close();

        session = sessionFactory.openSession();
        DimensionImpl loadedDimension = (DimensionImpl) session.load(DimensionImpl.class, dimension.getId());

        for (int i = 0; i < dimension.getSubDimensions().size(); i++)
        assertEquals(dimension.getSubDimensions().get(i), loadedDimension.getSubDimensions().get(i));
    }
    */
}