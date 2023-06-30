package com.sochovsky.module.core;

import java.util.List;

public interface CoreModule {
	public void scheduledReevaluationExecutingNodes() throws InterruptedException;
	public List<String> preferedForPatch(String name, String namespace);
}
