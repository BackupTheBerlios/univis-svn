package org.hibernate.bytecode;

/**
 * Contract for providers of bytecode services to Hibernate.
 * <p/>
 * Bytecode requirements break down into basically 4 areas<ol>
 * <li>proxy generation (both for runtime-lazy-loading and basic proxy generation)
 * {@link #getProxyFactoryFactory()}
 * <li>bean relection optimization {@link #getReflectionOptimizer}
 * <li>build-time instumentation (not covered by this contract)
 * <li>class-load intrumentation {@link #generateDynamicFieldInterceptionClassLoader};
 * (currently only used in the test suite).
 * </ol>
 *
 * @author Steve Ebersole
 */
public interface BytecodeProvider {
	/**
	 * Retrieve the specific factory for this provider capable of
	 * generating run-time proxies for lazy-loading purposes.
	 *
	 * @return The provider specifc factory.
	 */
	public ProxyFactoryFactory getProxyFactoryFactory();

	/**
	 * Retrieve the ReflectionOptimizer delegate for this provider
	 * capable of generating reflection optimization components.
	 *
	 * @param clazz The class to be reflected upon.
	 * @param getterNames Names of all property getters to be accessed via reflection.
	 * @param setterNames Names of all property setters to be accessed via reflection.
	 * @param types The types of all properties to be accessed.
	 * @return The reflection optimization delegate.
	 */
	public ReflectionOptimizer getReflectionOptimizer(Class clazz, String[] getterNames, String[] setterNames, Class[] types);

	/**
	 * Generate a ClassLoader capable of performing dynamic bytecode manipulation
	 * on classes as they are loaded for the purpose of field-level interception.
	 * The returned ClassLoader is used for run-time bytecode manipulation as
	 * opposed to the more common build-time manipulation, since here we get
	 * into SecurityManager issues and such.
	 * <p/>
	 * Currently used only from the Hibernate test suite, although conceivably
	 * (SecurityManager concerns aside) could be used somehow in running systems.
	 *
	 * @param parent The parent classloader
	 * @param classpath The classpath to be searched
	 * @param packages can be null; use to limnit the packages to be loaded
	 * via this classloader (and transformed).
	 * @return The appropriate ClassLoader.
	 */
	public ClassLoader generateDynamicFieldInterceptionClassLoader(ClassLoader parent, String[] classpath, String[] packages);

	public void releaseDynamicFieldInterceptionClassLoader(ClassLoader classLoader);
}
