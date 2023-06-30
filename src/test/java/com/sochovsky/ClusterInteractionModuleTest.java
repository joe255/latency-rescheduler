package com.sochovsky;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import com.sochovsky.module.clusterinteraction.ClusterInteractionModuleImpl;
import com.sochovsky.module.dependency.Node;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1ConfigMapBuilder;
import io.kubernetes.client.openapi.models.V1NodeList;
import io.kubernetes.client.openapi.models.V1NodeListBuilder;

public class ClusterInteractionModuleTest {
	@Test
	public void testGetNodes() throws ApiException {
		ClusterInteractionModuleImpl clusterInteractionModuleImpl = new ClusterInteractionModuleImpl();
		CoreV1Api mock = mock(CoreV1Api.class);
		V1NodeList build = new V1NodeListBuilder().addNewItem().editOrNewMetadata()
				.addToAnnotations("projectcalico.org/IPv4Address", "127.0.0.1").endMetadata().editOrNewStatus()
				.addNewAddress().withAddress("127.0.0.2").endAddress().endStatus().endItem().addNewItem()
				.editOrNewMetadata().addToAnnotations("inCorrectAnnotation", "127.0.0.1").endMetadata()
				.editOrNewStatus().addNewAddress().withAddress("127.0.0.3").endAddress().endStatus().endItem().build();
		when(mock.listNode(any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(build);
		clusterInteractionModuleImpl.setCoreApi(mock);
		List<String> clusterInternalNodeIps = clusterInteractionModuleImpl.getClusterInternalNodeIps();
		assertTrue(clusterInternalNodeIps.contains("127.0.0.1"));
		assertFalse(clusterInternalNodeIps.contains("127.0.0.2"));
		assertTrue(clusterInternalNodeIps.contains("127.0.0.3"));
	}

	@Test
	@EnabledIfEnvironmentVariable(named = "MINIKUBE", matches = "enabled")
	public void testGetDependencies() throws ApiException {
		ClusterInteractionModuleImpl clusterInteractionModuleImpl = new ClusterInteractionModuleImpl();
		CoreV1Api mock = mock(CoreV1Api.class);
		V1ConfigMap build = new V1ConfigMapBuilder().addToData("ubuntu", "1.1.1.1;24\ngoogle.at;65\norf.at;27").build();
		when(mock.readNamespacedConfigMap(any(), any(), any(), any(), any())).thenReturn(build);
		clusterInteractionModuleImpl.setCoreApi(mock);
		List<Node> nodeLatencies = clusterInteractionModuleImpl.getNodeLatencies();
		assertTrue(nodeLatencies.size() == 1);
		assertTrue(nodeLatencies.get(0).getLatencies().keySet().size() == 3);
	}
}
