package com.sochovsky.module.dependency;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.sochovsky.module.clusterinteraction.ClusterInteractionModule;

@Singleton
public class DependencyModuleImpl implements DependencyModule {
	@Inject
	private ClusterInteractionModule clusterInteractionModule;
	@Inject
	private JaegerClient jaegerClient;

	@Override
	public List<ApplicationEntry> getDependencies() {
		JaegerResponse dependencies = jaegerClient.getDependencies(new Date().getTime(), (1000L * 60L * 10L));
		List<ApplicationEntry> result = new LinkedList<>();
		for (ServiceEntry i : dependencies.data) {
			// TODO: think about external ones
			String[] parentArgs = i.parent.split("\\.");
			if (parentArgs.length == 2) {
				final String name = parentArgs[0];
				final String namespace = parentArgs[1];
				Optional<ApplicationEntry> existing;
				if (!(existing = result.stream()
						.filter((p) -> p.getDeploymentName().equals(name) && p.getNamespace().equals(namespace))
						.findAny()).isPresent()) {
					Map<String, Integer> m = new HashMap<>();
					m.put(i.getChild(), i.callCount);
					result.add(ApplicationEntry.builder().deploymentName(name).namespace(namespace).dependencies(m)
							.successors(new HashMap<>()).executers(new LinkedList<>()).build());
				} else {
					existing.get().getDependencies().put(i.getChild(), i.callCount);
				}
			} else {
				final String name = i.parent;
				Optional<ApplicationEntry> existing;
				if (!(existing = result.stream().filter((p) -> p.getDeploymentName().equals(name)).findAny())
						.isPresent()) {
					Map<String, Integer> m = new HashMap<>();
					m.put(i.getChild(), i.callCount);
					result.add(ApplicationEntry.builder().deploymentName(name).dependencies(m)
							.successors(new HashMap<>()).executers(new LinkedList<>()).build());
				} else {
					existing.get().getDependencies().put(i.getChild(), i.callCount);
				}
			}
			String[] childArgs = i.child.split("\\.");
			if (childArgs.length == 2) {
				final String name = childArgs[0];
				final String namespace = childArgs[1];
				Optional<ApplicationEntry> existing;
				if (!(existing = result.stream()
						.filter((p) -> p.getDeploymentName().equals(name) && p.getNamespace().equals(namespace))
						.findAny()).isPresent()) {
					Map<String, Integer> m = new HashMap<>();
					m.put(i.getParent(), i.getCallCount());
					result.add(ApplicationEntry.builder().deploymentName(name).namespace(namespace).successors(m)
							.dependencies(new HashMap<String, Integer>()).executers(new LinkedList<>()).build());// .dependencies(dependencies)
				} else {
					existing.get().getSuccessors().put(i.getChild(), i.getCallCount());
				}
			} else {
				final String name = i.child;
				Optional<ApplicationEntry> existing;
				if (!(existing = result.stream().filter((p) -> p.getDeploymentName().equals(name)).findAny())
						.isPresent()) {
					Map<String, Integer> m = new HashMap<>();
					m.put(i.getParent(), i.getCallCount());
					result.add(ApplicationEntry.builder().deploymentName(name).successors(m)
							.dependencies(new HashMap<String, Integer>()).executers(new LinkedList<>()).build());// .dependencies(dependencies)
				} else {
					existing.get().getSuccessors().put(i.getChild(), i.getCallCount());
				}
			}
		}
		result = getExecutorForDependencies(result);
		return result;
	}

	@Override
	public List<ApplicationEntry> getExecutorForDependencies(List<ApplicationEntry> dependencies) {
		Map<String, Node> nodeBuilder = new HashMap<>();
		clusterInteractionModule.getNodeLatencies().forEach(node -> nodeBuilder.put(node.getName(), node));
		for (ApplicationEntry applicationEntry : dependencies) {
			if (applicationEntry.getNamespace() != null) {
				List<String> nodesForDeployment = clusterInteractionModule
						.getNodesForDeployment(applicationEntry.getDeploymentName(), applicationEntry.getNamespace());
				nodesForDeployment.forEach((pod) -> {
					String nodeName = pod;
					Node n = nodeBuilder.getOrDefault(nodeName, Node.builder().clusterInternal(true).name(nodeName)
							.executingServices(new LinkedList<>()).build());
					n.getExecutingServices()
							.add(applicationEntry.getDeploymentName() + "." + applicationEntry.getNamespace());
					nodeBuilder.put(nodeName, n);
					applicationEntry.setExternal(false);
					applicationEntry.getExecuters().add(n);
				});
			} else {
				Node n = nodeBuilder.getOrDefault(applicationEntry.getDeploymentName(),
						Node.builder().name(applicationEntry.getDeploymentName()).executingServices(new LinkedList<>())
								.ip(applicationEntry.getDeploymentName()).clusterInternal(false).build());
				nodeBuilder.put(applicationEntry.getDeploymentName(), n);
				applicationEntry.setExternal(true);
				applicationEntry.getExecuters().add(n);
			}
		}
		// check the ones without executors
		return dependencies;
	}

	@Override
	public List<ApplicationEntry> getDependencies(String forHost) {
		List<ApplicationEntry> apps = getDependencies();
		List<String> dependencies = apps.stream()
				.map(app -> app.getDependencies().keySet().stream().collect(Collectors.toList())).reduce((d1, d2) -> {
					d1.addAll(d2);
					return d1;
				}).get();
		return apps.stream().filter(app -> dependencies.contains(app.getDeploymentName() + "." + app.getNamespace()))
				.collect(Collectors.toList());
	}

	@Override
	public List<ApplicationEntry> getSuccessors(String forNode) {
		// TODO Auto-generated method stub
		return null;
	}

}
