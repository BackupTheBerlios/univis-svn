//$Id: InstrumentTask.java 9210 2006-02-03 22:15:19Z steveebersole $
package org.hibernate.tool.instrument.cglib;

import java.util.Arrays;

import net.sf.cglib.transform.AbstractTransformTask;
import net.sf.cglib.transform.ClassTransformer;
import net.sf.cglib.transform.impl.InterceptFieldEnabled;
import net.sf.cglib.transform.impl.InterceptFieldFilter;
import net.sf.cglib.transform.impl.InterceptFieldTransformer;

import org.objectweb.asm.Type;

/**
 * An Ant task for instrumenting persistent classes in order to enable
 * field-level interception using CGLIB.
 * <p/>
 * In order to use this task, typically you would define a a taskdef
 * similiar to:<pre>
 * <taskdef name="instrument" classname="org.hibernate.tool.instrument.cglib.InstrumentTask">
 *     <classpath refid="lib.class.path"/>
 * </taskdef>
 * </pre>
 * where <tt>lib.class.path</tt> is an ANT path reference containing all the
 * required Hibernate and CGLIB libraries.
 * <p/>
 * And then use it like:<pre>
 * <instrument verbose="true">
 *     <fileset dir="${testclasses.dir}/org/hibernate/test">
 *         <include name="yadda/yadda/**"/>
 *         ...
 *     </fileset>
 * </instrument>
 * </pre>
 * where the nested ANT fileset includes the class you would like to have
 * instrumented.
 *
 * @author Gavin King
 */
public class InstrumentTask extends AbstractTransformTask {

	/**
	 * Override the {@link AbstractTransformTask#getClassTransformer} method
	 * in order to define field access interception transformation should occur.
	 */
	protected ClassTransformer getClassTransformer(String[] classInfo) {
		if ( Arrays.asList( classInfo ).contains( InterceptFieldEnabled.class.getName() ) ) {
			// The class is already instrumented, so skip this step
			return null;
		}
		else {
			// Class was not yet enhanced, so apply the transformation
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

}
