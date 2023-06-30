package com.sochovsky;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.micronaut.test.annotation.MockBean;

//@Factory
public class TestFactory {

	@MockBean(CoreV1Api.class)
	CoreV1Api coreV1Api() throws ApiException {
		// CoreV1Api mock = Mockito.mock(CoreV1Api.class);
		// V1PodList v1PodList = new V1PodList();
		// v1PodList.addItemsItem(new
		// V1PodBuilder().editOrNewSpec().withNodeName("ubuntu").endSpec().build());
		// when(mock.listNamespacedPod(anyString(), null, null, null, null, anyString(),
		// null, null, null, null, null))
		// .thenReturn(null);
		// return mock;
		return null;
	}
}
