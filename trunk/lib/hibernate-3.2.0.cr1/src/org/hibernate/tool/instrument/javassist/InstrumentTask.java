package org.hibernate.tool.instrument.javassist;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javassist.bytecode.ClassFile;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

/**
 * An Ant task for instrumenting persistent classes in order to enable
 * field-level interception using Javassist.
 * <p/>
 * In order to use this task, typically you would define a a taskdef
 * similiar to:<pre>
 * <taskdef name="instrument" classname="org.hibernate.tool.instrument.javassist.InstrumentTask">
 *     <classpath refid="lib.class.path"/>
 * </taskdef>
 * </pre>
 * where <tt>lib.class.path</tt> is an ANT path reference containing all the
 * required Hibernate and Javassist libraries.
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
 * @author Muga Nishizawa
 * @author Steve Ebersole
 */
public class InstrumentTask extends Task {
	private List filesets = new ArrayList();
	private boolean verbose;

	public void addFileset(FileSet set) {
		this.filesets.add( set );
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public void execute() throws BuildException {
		Project project = getProject();
		Iterator filesets = this.filesets.iterator();
		while ( filesets.hasNext() ) {
			FileSet fs = ( FileSet ) filesets.next();
			DirectoryScanner ds = fs.getDirectoryScanner( project );
			String[] includedFiles = ds.getIncludedFiles();
			File d = fs.getDir( project );
			for ( int i = 0; i < includedFiles.length; ++i ) {
				File file = new File( d, includedFiles[i] );
				try {
					transformFile( file );
				}
				catch ( Exception e ) {
					throw new BuildException( e );
				}
			}
		}
	}

	private void transformFile(File file) throws Exception {
		DataInputStream in = new DataInputStream( new FileInputStream( file ) );
		ClassFile classfile = null;
		try {
			// WARNING: classfile only
			classfile = new ClassFile( in );
		}
		catch ( IOException e ) {
			System.err.println( "ignoring " + file.toURL() + " : " + e );
			return;
		}
		FieldTransformer transformer = getFieldTransformer( classfile );
		if ( transformer == null ) {
			return;
		}
		if ( verbose ) {
			System.out.println( "processing " + file.toURL() );
		}
		transformer.transform( classfile );
		DataOutputStream out = new DataOutputStream( new FileOutputStream( file ) );
		try {
			classfile.write( out );
		}
		finally {
			out.close();
		}
	}

	protected FieldTransformer getFieldTransformer(ClassFile classfile) {
		if ( alreadyInstrumented( classfile ) ) {
			return null;
		}
		else {
			return new FieldTransformer(
					new FieldFilter() {
						public boolean handleRead(String desc, String name) {
							return true;
						}

						public boolean handleWrite(String desc, String name) {
							return true;
						}
					}
			);
		}
	}

	private boolean alreadyInstrumented(ClassFile classfile) {
		String[] intfs = classfile.getInterfaces();
		for ( int i = 0; i < intfs.length; i++ ) {
			if ( FieldHandled.class.getName().equals( intfs[i] ) ) {
				return true;
			}
		}
		return false;
	}

}
