package unikn.dbis.univis.hibernate.util;

import org.hibernate.SessionFactory;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO: document me!!!
 * <p/>
 * <code>HibernateUtil</code>.
 * <p/>
 * User: raedler, weiler
 * Date: 07.04.2006
 * Time: 23:20:10
 *
 * @author Roman R&auml;dle
 * @author Andreas Weiler
 * @version $Id$
 * @since UniVis Explorer 0.1
 */
public class HibernateUtil {

    // The logger to log info, error and other occuring messages
    // or exceptions.
    public static final Log LOG = LogFactory.getLog(HibernateUtil.class);

    public static final SessionFactory sessionFactory;

    static {
        // Create the SessionFactory from the hibernate.cfg.xml
        try {

            /*
            Properties props = new Properties();

            try {
                props.load(new FileInputStream("d:/projects/univis explorer/build/classes/hibernate.properties"));
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            Configuration cfg = new Configuration().addProperties(props);
            cfg.addDirectory(new File("d:/projects/univis explorer/build/classes/unikn/dbis/univis/meta"));

            sessionFactory = cfg.buildSessionFactory();
            */
            sessionFactory = new Configuration().configure().buildSessionFactory();
        }
        catch (HibernateException he) {

            // Make sure you log the exception, as it might be swallowed
            LOG.error("Initial SessionFactory creation failed.", he);

            throw new ExceptionInInitializerError(he);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}