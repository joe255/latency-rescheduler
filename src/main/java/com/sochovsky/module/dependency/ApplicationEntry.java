package com.sochovsky.module.dependency;

import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApplicationEntry {
	private String namespace;
	private String deploymentName;
	private List<Node> executers;
	private Map<String, Integer> dependencies;
	private Map<String, Integer> successors;
	private boolean external;

	public String getFullname() {
		if (namespace == null)
			return deploymentName;
		return deploymentName + "." + namespace;
	}
}
