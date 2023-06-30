package com.sochovsky;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import com.sochovsky.module.clusterinteraction.ClusterInteractionModule;
import com.sochovsky.module.dependency.ApplicationEntry;
import com.sochovsky.module.dependency.DependencyModule;
import com.sochovsky.module.dependency.JaegerClient;
import com.sochovsky.module.dependency.JaegerResponse;
import com.sochovsky.module.dependency.ServiceEntry;

import org.apache.commons.compress.utils.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;

@MicronautTest
public class DependencyModuleTest {
	@Inject
	private DependencyModule dependencyModule;

	@Test
	public void testInjection() {
		assertNotNull(dependencyModule);
	}

	@Test
	public void testGetDependencies() {
		List<ApplicationEntry> dependencies = dependencyModule.getDependencies();
		assertNotNull(dependencies);
	}

	@MockBean(JaegerClient.class)
	JaegerClient jaegerClient() {
		JaegerClient mock = Mockito.mock(JaegerClient.class);
		JaegerResponse jaegerResponse = new JaegerResponse();
		jaegerResponse.setErrors(null);
		jaegerResponse.setLimit(0);
		jaegerResponse.setOffset(0);
		jaegerResponse.setTotal(0);
		List<ServiceEntry> services = new LinkedList<>();
		services.add(ServiceEntry.builder().parent("a.default").child("b.default").callCount(1).build());
		services.add(ServiceEntry.builder().parent("c.default").child("b.default").callCount(1).build());
		services.add(ServiceEntry.builder().parent("c.default").child("a.default").callCount(1).build());
		services.add(ServiceEntry.builder().parent("d.default").child("e.default").callCount(1).build());
		services.add(ServiceEntry.builder().parent("c.default").child("d.default").callCount(1).build());
		jaegerResponse.setData(services);
		when(mock.getDependencies(anyLong(), anyLong())).thenReturn(jaegerResponse);
		return mock;
	}

	@MockBean(ClusterInteractionModule.class)
	ClusterInteractionModule clusterInteractionModule() {
		ClusterInteractionModule mock = Mockito.mock(ClusterInteractionModule.class);
		when(mock.getNodesForDeployment(any(), any())).thenReturn(Arrays.asList("ubuntu"));
		when(mock.getNodeLatencies()).thenReturn(Lists.newArrayList());
		return mock;
	}
}
