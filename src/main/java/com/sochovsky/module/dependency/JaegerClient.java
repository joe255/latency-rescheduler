package com.sochovsky.module.dependency;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.client.annotation.Client;

@Client(value = "${dynamic-scheduler.jaeger-host}")
public interface JaegerClient {

	@Get(value = "/api/dependencies", processes = MediaType.APPLICATION_JSON)
	public JaegerResponse getDependencies(@QueryValue("endTs") Long endTs, @QueryValue("lookback") Long lookback);
}
