package org.hibernate.bytecode.cglib;

import org.hibernate.bytecode.BytecodeProvider;
import org.hibernate.bytecode.ProxyFactoryFactory;
import org.hibernate.bytecode.ReflectionOptimizer;
import org.hibernate.util.StringHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.objectweb.asm.Type;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.beans.BulkBean;
import net.sf.cglib.beans.BulkBeanException;
import net.sf.cglib.transform.TransformingClassLoader;
import net.sf.cglib.transform.ClassFilter;
import net.sf.cglib.transform.ClassTransformerFactory;
import net.sf.cglib.transform.ClassTransformer;
import net.sf.cglib.transform.impl.InterceptFieldTransformer;
import net.sf.cglib.transform.impl.InterceptFieldFilter;

import java.lang.reflect.Modifier;

/**
 * Bytecode provider implementation for CGLIB.
 *
 * @author Steve Ebersole
 */
public class BytecodeProviderImpl implements BytecodeProvider {

	private static final Log log = LogFactory.getLog( BytecodeProviderImpl.class );

	public ProxyFactoryFactory getProxyFactoryFactory() {
		return new ProxyFactoryFactoryImpl();
	}

	public ReflectionOptimizer getReflectionOptimizer(
			Class clazz,
	        String[] getterNames,
	        String[] setterNames,
	        Class[] types) {
		FastClass fastClass;
		BulkBean bulkBean;
		try {
			fastClass = FastClass.create( clazz );
			bulkBean = BulkBean.create( clazz, getterNames, setterNames, types );
			if ( !clazz.isInterface() && !Modifier.isAbstract( clazz.getModifiers() ) ) {
				if ( fastClass == null ) {
					bulkBean = null;
				}
				else {
					//test out the optimizer:
					Object instance = fastClass.newInstance();
					bulkBean.setPropertyValues( instance, bulkBean.getPropertyValues( instance ) );
				}
			}
		}
		catch( Throwable t ) {
			fastClass = null;
			bulkBean = null;
			String message = "reflection optimizer disabled for: " +
			                 clazz.getName() +
			                 " [" +
			                 StringHelper.unqualify( t.getClass().getName() ) +
			                 ": " +
			                 t.getMessage();

			if (t instanceof BulkBeanException ) {
				int index = ( (BulkBeanException) t ).getIndex();
				if (index >= 0) {
					message += " (property " + setterNames[index] + ")";
				}
			}

			log.debug( message );
		}

		if ( fastClass != null && bulkBean != null ) {
			return new ReflectionOptimizerImpl(
					new InstantiationOptimizerAdapter( fastClass ),
			        new AccessOptimizerAdapter( bulkBean, clazz )
			);
		}
		else {
			return null;
		}
	}

	public ClassLoader generateDynamicFieldInterceptionClassLoader(
			ClassLoader parent,
	        String[] classpath,
	        String[] packages) {
		return new TransformingClassLoader(
				parent,
		        new ClassLoaderClassFilter( packages ),
		        new ClassTransformerFactory() {
			        public ClassTransformer newInstance() {
						return new InterceptFieldTransformer(
								new InterceptFieldFilter() {
									public boolean acceptRead(Type owner, String name) {
										return true;
									}
									public boolean acceptWrite(Type owner, String name) {
										return true;
									}
								}
						);
			        }
		        }
		);
	}

	public void releaseDynamicFieldInterceptionClassLoader(ClassLoader classLoader) {
	}

	private static class ClassLoaderClassFilter implements ClassFilter {
		private final String[] packages;

		public ClassLoaderClassFilter(String[] packages) {
			this.packages = packages;
		}

		public boolean accept(String className) {
			if ( packages == null ) {
				return true;
			}
			else {
				for ( int i = 0; i < packages.length; i++ ) {
					if ( className.startsWith( packages[i] ) ) {
						return true;
					}
				}
				return false;
			}
		}
	}
}
