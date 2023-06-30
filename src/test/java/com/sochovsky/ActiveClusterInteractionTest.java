package com.sochovsky;

import java.util.List;

import javax.inject.Inject;

import com.sochovsky.module.clusterinteraction.ClusterInteractionModule;
import com.sochovsky.module.dependency.ApplicationEntry;
import com.sochovsky.module.dependency.DependencyModule;
import com.sochovsky.module.dependency.Node;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;

@MicronautTest
public class ActiveClusterInteractionTest {
	@Inject
	private ClusterInteractionModule clusterInteractionModule;
	@Inject
	private DependencyModule dependencyModule;

	@Test
	@EnabledIfEnvironmentVariable(named = "MINIKUBE", matches = "enabled")
	public void testSingleRun() {
		List<ApplicationEntry> dependencies = dependencyModule.getDependencies();
		System.out.println("asd");
	}

	@Test
	@EnabledIfEnvironmentVariable(named = "MINIKUBE", matches = "enabled")
	public void testGetNodeIPS() {
		List<ApplicationEntry> dependencies = dependencyModule.getDependencies();
		List<Node> nodeLatencies = clusterInteractionModule.getNodeLatencies();
		// .forEach(node ->
		// clusterInteractionModule.getClusterInternalIp(node.getName()));
		System.out.println("asd");
	}
}
