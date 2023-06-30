package com.sochovsky.module.dependency;

import java.util.List;

import lombok.Data;

@Data
public class JaegerResponse {
	int total;
	int limit;
	int offset;
	List<ServiceEntry> data;
	Object errors;
}
