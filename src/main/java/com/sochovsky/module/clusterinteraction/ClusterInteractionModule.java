package com.sochovsky.module.clusterinteraction;

import java.util.List;

import com.sochovsky.module.dependency.Node;

public interface ClusterInteractionModule {
	// Think how to make this a transaction
	public boolean addHostToScan(String host);

	public List<String> getClusterInternalNodeIps();

	public List<Node> getNodeLatencies();

	public List<String> getNodesForDeployment(String deploymentName, String namespace);

	public boolean setAllHostsToScan(List<String> list);

	public Node getClusterInternalIp(String forNode);

	public void patchDeploymentForAffinity(String deploymentName, String deploymentNamespace, List<Node> nodes);
	public String getPatchDeploymentForAffinity(String deploymentName, String deploymentNamespace, List<Node> nodes);
}
