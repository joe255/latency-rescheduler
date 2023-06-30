package com.sochovsky.module.clusterinteraction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.sochovsky.module.dependency.Node;

import io.kubernetes.client.custom.V1Patch;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1ConfigMapBuilder;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1NodeList;
import io.kubernetes.client.openapi.models.V1ObjectMetaBuilder;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.PatchUtils;
import io.micronaut.context.annotation.Value;
import lombok.Setter;

@Singleton
public class ClusterInteractionModuleImpl implements ClusterInteractionModule {

	@Inject
	@Setter
	CoreV1Api coreApi;
	@Inject
	ApiClient client;
	@Inject
	AppsV1Api appsApi;
	@Value("${dynamic-scheduler.latencyInformer.configmap}")
	private String configmap;
	@Value("${dynamic-scheduler.latencyInformer.namespace}")
	private String namespace;
	@Value("${dynamic-scheduler.latencyInformer.hosts}")
	private String hosts;

	@Override
	public boolean addHostToScan(String host) {
		V1ConfigMap configMap = null;
		String data = host;
		try {
			configMap = coreApi.readNamespacedConfigMap(configmap, namespace, null, null, null);
		} catch (ApiException e) {
			e.printStackTrace();
			return false;
		}
		if (configMap != null) {
			System.out.println("Exists allready");
			if (configMap.getData().containsKey(hosts)) {
				data += ";" + configMap.getData().get(hosts);
			}
			data = data.replaceAll("\n", "\\\\n");
			String patch = "{\"data\":{\"" + hosts + "\":\"" + host + "\"}}";
			System.out.println(patch);
			V1Patch v1Patch = new V1Patch(patch);
			try {
				PatchUtils.patch(V1ConfigMap.class, () -> coreApi.patchNamespacedConfigMapCall(configmap, namespace,
						v1Patch, null, null, null, null, null), V1Patch.PATCH_FORMAT_STRATEGIC_MERGE_PATCH, client);
			} catch (ApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}

		} else {
			System.out.println("Will create a new one");
			V1ConfigMap build = new V1ConfigMapBuilder().addToData(hosts, data)
					.withMetadata(new V1ObjectMetaBuilder().withName(configmap).withNamespace(namespace).build())
					.build();
			try {
				coreApi.createNamespacedConfigMap(namespace, build, null, null, null);
			} catch (ApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	@Override
	public List<Node> getNodeLatencies() {
		List<Node> result = new LinkedList<>();
		Map<String, String> hostname2ip = getNodeIps();
		Set<String> existingNodes = new HashSet<>();
		try {
			V1ConfigMap configMap = coreApi.readNamespacedConfigMap(configmap, namespace, null, null, null);
			configMap.getData().forEach((k, v) -> {
				Map<String, Integer> latencies = new HashMap<>();
				if (!k.equals(hosts)) {
					Arrays.asList(((String) v).split("\n")).forEach((entry) -> {
						String[] record = entry.split(";");
						latencies.put(record[0], Integer.parseInt(record[1]));
						latencies.put(hostname2ip.get(record[0]), Integer.parseInt(record[1]));
					});
					result.add(Node.builder().clusterInternal(true).name(k).ip(hostname2ip.get(k)).latencies(latencies)
							.executingServices(new LinkedList<>()).build());
				} else {
					existingNodes.addAll(Arrays.asList(v.split(";")));
				}
			});
			for (String n : existingNodes) {
				if (!hostname2ip.values().contains(n)) {
					Map<String, Integer> latencies = new HashMap<>();
					for (Node n1 : result) {
						latencies.put(n1.getIp(), n1.getLatencies().get(n));
					}
					result.add(Node.builder().clusterInternal(false).name(n).ip(n).executingServices(new LinkedList<>())
							.latencies(latencies).build());
				}
			}
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	private Map<String, String> getNodeIps() {
		Map<String, String> result = new HashMap<>();
		try {
			V1NodeList listNode = coreApi.listNode(null, null, null, null, null, null, null, null, null, null);
			listNode.getItems().forEach(
					n -> result.put(n.getMetadata().getName(), n.getStatus().getAddresses().get(0).getAddress()));
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public List<String> getNodesForDeployment(String deploymentName, String namespace) {
		try {
			// V1Deployment deployment = appsApi.listNamespacedDeployment(namespace, null,
			// null, null,
			// "metadata.name=" + deploymentName, null, null, null, null, null,
			// null).getItems().get(0);
			V1PodList listNamespacedPod = coreApi.listNamespacedPod(namespace, null, null, null, null,
					"app=" + deploymentName, null, null, null, null, null);
			return listNamespacedPod.getItems().stream().map(pod -> pod.getSpec().getNodeName())
					.collect(Collectors.toList());
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new LinkedList<String>();
	}

	@Override
	public List<String> getClusterInternalNodeIps() {
		List<String> result = new LinkedList<>();
		try {
			V1NodeList listNode = coreApi.listNode(null, null, null, null, null, null, null, null, null, null);
			result.addAll(listNode.getItems().stream().map((node) -> {
				return node.getMetadata().getAnnotations().getOrDefault("projectcalico.org/IPv4Address",
						node.getStatus().getAddresses().get(0).getAddress());
			}).collect(Collectors.toList()));
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public Node getClusterInternalIp(String forNode) {
		Node build = Node.builder().executingServices(new LinkedList<>()).name(forNode).build();
		try {
			V1NodeList listNode = coreApi.listNode(null, null, null, "metadata.name=" + forNode, null, null, null, null,
					null, null);
			V1Node node = listNode.getItems().get(0);
			System.out.println("x");
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return build;
	}

	@Override
	public boolean setAllHostsToScan(List<String> list) {
		return addHostToScan(list.stream().reduce((s1, s2) -> s1 + ";" + s2).get());
	}

	@Override
	public void patchDeploymentForAffinity(String deploymentName, String deploymentNamespace, List<Node> nodes) {
		int maxSize = 2;
		List<Node> setNotList = new LinkedList<>();
		setNotList.addAll(nodes);
		String a = "{\"spec\": {\"template\": inhere}}";
		String jsonPatch = a.replaceAll("inhere", "{\"spec\": {" + "\"affinity\": {"
				+ "\"nodeAffinity\": {\"preferredDuringSchedulingIgnoredDuringExecution\": [toBeReplaced]" + "}}}}");
		String replacer = setNotList.stream().limit(maxSize).map(n -> {
			int prio = (int) (100d * (maxSize - (setNotList.size() - (setNotList.size() - setNotList.indexOf(n))))
					/ maxSize);
			return "{\"weight\": " + prio
					+ ", \"preference\": {\"matchExpressions\": [{\"key\": \"kubernetes.io/hostname\", \"operator\": \"In\", \"values\": [\""
					+ n.getName() + "\"]}]}}";
		}).reduce((n1, n2) -> n1 + "," + n2).get();
		final String patch = (jsonPatch.replaceAll("toBeReplaced", replacer));
		// System.out.println(patch);
		try {
			V1Patch body = new V1Patch(patch);
			V1Deployment result = PatchUtils.patch(V1Deployment.class, () -> {
				return appsApi.patchNamespacedDeploymentCall(deploymentName, deploymentNamespace, body, null, null,
						null, null, null);
			}, V1Patch.PATCH_FORMAT_STRATEGIC_MERGE_PATCH, client);
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String getPatchDeploymentForAffinity(String deploymentName, String deploymentNamespace, List<Node> nodes) {
		int maxSize = 2;
		List<Node> setNotList = new LinkedList<>();
		setNotList.addAll(nodes);
		String a = "{\"spec\": {\"template\": inhere}}";
		String jsonPatch = a.replaceAll("inhere", "{\"spec\": {" + "\"affinity\": {"
				+ "\"nodeAffinity\": {\"preferredDuringSchedulingIgnoredDuringExecution\": [toBeReplaced]" + "}}}}");
		String replacer = setNotList.stream().limit(maxSize).map(n -> {
			int prio = (int) (100d * (maxSize - (setNotList.size() - (setNotList.size() - setNotList.indexOf(n))))
					/ maxSize);
			return "{\"weight\": " + prio
					+ ", \"preference\": {\"matchExpressions\": [{\"key\": \"kubernetes.io/hostname\", \"operator\": \"In\", \"values\": [\""
					+ n.getName() + "\"]}]}}";
		}).reduce((n1, n2) -> n1 + "," + n2).get();
		final String patch = (jsonPatch.replaceAll("toBeReplaced", replacer));
		return patch;
	}
}
