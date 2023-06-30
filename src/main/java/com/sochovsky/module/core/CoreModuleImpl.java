package com.sochovsky.module.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.sochovsky.module.clusterinteraction.ClusterInteractionModule;
import com.sochovsky.module.dependency.ApplicationEntry;
import com.sochovsky.module.dependency.DependencyModule;
import com.sochovsky.module.dependency.Node;

import io.micronaut.scheduling.annotation.Scheduled;

@Singleton
public class CoreModuleImpl implements CoreModule {

	@Inject
	ClusterInteractionModule clusterInteractionModule;
	@Inject
	DependencyModule dependencyModule;

	@Override
	@Scheduled(fixedRate = "${dynamic-scheduler.interval}", initialDelay = "${dynamic-scheduler.startwait}")
	public void scheduledReevaluationExecutingNodes() throws InterruptedException {
		Map<String, ApplicationEntry> aes = new HashMap<>();
		dependencyModule.getDependencies().forEach(ae -> aes.put(ae.getFullname(), ae));
		for (String key : aes.keySet()) {
			// for (ApplicationEntry ae : aes.values()) {
			dependencyModule.getDependencies().forEach(ae -> aes.put(ae.getFullname(), ae));
			ApplicationEntry ae = aes.get(key);
			List<Node> nodes = clusterInteractionModule.getNodeLatencies();
			// Map<Integer, Node> candidates = new TreeMap<>();
			TreeSet<Node> candidates = new TreeSet<>();
			for (Node n : nodes) {
				int totalLatency = 1;
				for (String dependency : ae.getDependencies().keySet()) {
					List<Node> dependencyExecutors = aes.get(dependency).getExecuters();
					totalLatency += ae.getDependencies().get(dependency) * dependencyExecutors.stream()
							.map(n1 -> n1.getLatencies().getOrDefault(n.getIp(), Integer.MAX_VALUE))
							.reduce((l1, l2) -> (l1 + l2) / 2).get();
				}
				for (String successor : ae.getSuccessors().keySet()) {
					List<Node> successoryExecutors = aes.get(successor).getExecuters();
					totalLatency += ae.getSuccessors().get(successor) * successoryExecutors.stream()
							.map(n1 -> n1.getLatencies().get(n.getIp())).reduce((l1, l2) -> (l1 + l2) / 2).get();
				}
				n.setAbsLatency(totalLatency);
				candidates.add(n);
			}
			if (!ae.isExternal() && !candidates.stream().filter(n -> n.isClusterInternal()).map(s -> s.getName())
					.limit(ae.getExecuters().size()).collect(Collectors.toList())
					.containsAll(ae.getExecuters().stream().map(s -> s.getName()).collect(Collectors.toList()))) {
				System.out.println("For app: " + ae.getDeploymentName() + " the prefered executor list would be: "
						+ candidates.stream().filter(n -> n.isClusterInternal()).map(n1 -> n1.getName())
								.reduce((n1, n2) -> n1 + "," + n2).get());
				clusterInteractionModule.patchDeploymentForAffinity(ae.getDeploymentName(), ae.getNamespace(),
						candidates.stream().filter(n -> n.isClusterInternal()).collect(Collectors.toList()));
				Thread.sleep(10000); // sleep in order to let the scheduler pick a node
			} else {
				System.out.println("For app " + ae.getDeploymentName()
						+ " all executors are already executed in the list of prefered nodes");
			}
		}
		// TODO Auto-generated method stub
	}

	@Override
	public List<String> preferedForPatch(String name, String namespace) {
		Map<String, ApplicationEntry> aes = new HashMap<>();
		dependencyModule.getDependencies().forEach(ae -> aes.put(ae.getFullname(), ae));
		ApplicationEntry ae = aes.get(name + "." + namespace);
		if(ae == null || ae.getDependencies() == null || ae.getDependencies().isEmpty())
			return Collections.emptyList();
		List<Node> nodes = clusterInteractionModule.getNodeLatencies();
		TreeSet<Node> candidates = new TreeSet<>();
		for (Node n : nodes) {
			int totalLatency = 1;
			for (String dependency : ae.getDependencies().keySet()) {
				List<Node> dependencyExecutors = aes.get(dependency).getExecuters();
				if(!dependencyExecutors.isEmpty())
					totalLatency += ae.getDependencies().get(dependency) * dependencyExecutors.stream()
						.map(n1 -> n1.getLatencies().getOrDefault(n.getIp(), Integer.MAX_VALUE))
						.reduce((l1, l2) -> (l1 + l2) / 2).get();
			}
			for (String successor : ae.getSuccessors().keySet()) {
				List<Node> successoryExecutors = aes.get(successor).getExecuters();
				if(!successoryExecutors.isEmpty())
					totalLatency += ae.getSuccessors().get(successor) * successoryExecutors.stream()
						.map(n1 -> n1.getLatencies().get(n.getIp())).reduce((l1, l2) -> (l1 + l2) / 2).get();
			}
			n.setAbsLatency(totalLatency);
			candidates.add(n);
		}
		return candidates.stream().filter(n -> n.isClusterInternal()).map(n -> n.getName()).collect(Collectors.toList());
	}	
}
