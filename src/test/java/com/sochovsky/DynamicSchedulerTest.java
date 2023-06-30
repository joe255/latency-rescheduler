package com.sochovsky;

import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;

@MicronautTest
class DynamicSchedulerTest {

	@Inject
	EmbeddedApplication<?> application;

	@Test
	void testItWorks() {
		Assertions.assertTrue(application.isRunning());
	}

}
