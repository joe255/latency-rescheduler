package com.sochovsky.module.dependency;

import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Node implements Comparable {
	String name;
	String ip;
	List<String> executingServices;
	Map<String, Integer> latencies;
	int absLatency;
	boolean clusterInternal;

	// used for comparison during rescheudling

	@Override
	public int compareTo(Object o) {
		if (o instanceof Node) {
			if (this.getAbsLatency() < ((Node) o).getAbsLatency())
				return -1;
			else if (this.getAbsLatency() > ((Node) o).getAbsLatency())
				return 1;
			else
				return -1;
		}
		return 0;
	}
}
