package com.sochovsky;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.inject.Inject;

import com.sochovsky.module.dependency.JaegerClient;
import com.sochovsky.module.dependency.JaegerResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;

@MicronautTest
@Requires(missingProperty = "dynamic-scheduler.skip-jaeger-test")
public class TestJaegerClient {
	@Inject
	JaegerClient jaegerClient;
	@Property(name = "dynamic-scheduler.traceHistory")
	String traceHistory;

	@Test
	@EnabledIfEnvironmentVariable(named = "MINIKUBE", matches = "enabled")
	public void testJaegerClientResponse() {
		assertNotNull(jaegerClient);

		LocalDateTime minus = LocalDateTime.now();// .minus(Duration.ofMillis(Long.parseLong(traceHistory)));
		JaegerResponse dependencies = jaegerClient.getDependencies(convertToDateViaInstant(minus).getTime(),
				604800000L);

		assertNotNull(dependencies);
	}

	Date convertToDateViaInstant(LocalDateTime dateToConvert) {
		return java.util.Date.from(dateToConvert.atZone(ZoneId.systemDefault()).toInstant());
	}
}
