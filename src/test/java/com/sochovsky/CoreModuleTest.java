package com.sochovsky;

import javax.inject.Inject;

import com.sochovsky.module.core.CoreModule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;

@MicronautTest
public class CoreModuleTest {
	@Inject
	CoreModule coreModule;

	@Test
	@EnabledIfEnvironmentVariable(named = "MINIKUBE", matches = "enabled")
	void testScheduled() throws InterruptedException {
		coreModule.scheduledReevaluationExecutingNodes();
	}
}
