package com.sochovsky.module.dependency;

import java.util.List;

public interface DependencyModule {
	/**
	 * Retrieves a list of all cluster internal dependencies. Those are most
	 * probably all nodes inside the cluster. With there respective cluster owned
	 * services running on them.
	 * 
	 * @return a List of Dependencies
	 */
	public List<ApplicationEntry> getDependencies();

	/**
	 * Adds to an existing list of dependencies the executor lists.
	 */
	public List<ApplicationEntry> getExecutorForDependencies(List<ApplicationEntry> dependencies);

	/**
	 * Retrieves all dependencies for a given host. For the cluster internal running
	 * services the corresponding nodes will be attached as a list of executing
	 * nodes.
	 * 
	 * @param forHost the name of the host/service the dependencies are searched for
	 * @return a List of Dependencies
	 */
	public List<ApplicationEntry> getDependencies(String forNode);

	/**
	 * Retrieves all dependencies for a given host. For the cluster internal running
	 * services the corresponding nodes will be attached as a list of executing
	 * nodes.
	 * 
	 * @param forHost the name of the host/service the dependencies are searched for
	 * @return a List of Dependencies
	 */
	public List<ApplicationEntry> getSuccessors(String forNode);
}
