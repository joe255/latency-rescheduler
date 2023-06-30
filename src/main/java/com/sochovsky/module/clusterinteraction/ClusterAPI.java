package com.sochovsky.module.clusterinteraction;

import java.io.IOException;
import java.util.Arrays;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.Config;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import okhttp3.Protocol;

@Factory
public class ClusterAPI {
	@Bean
	ApiClient getApiClient() throws IOException {
		ApiClient client = Config.defaultClient();
		client.setHttpClient(client.getHttpClient().newBuilder().protocols(Arrays.asList(Protocol.HTTP_1_1)).build());
		return client;
	}

	@Bean
	CoreV1Api getCoreV1Api(ApiClient apiClient) {
		return new CoreV1Api(apiClient);
	}

	@Bean
	AppsV1Api getAppsV1Api(ApiClient apiClient) {
		return new AppsV1Api(apiClient);
	}
}
