package com.sochovsky.module.clusterinteraction;

import java.util.Base64;
import java.util.List;

import javax.inject.Inject;

import com.sochovsky.model.V1AdmissionResponse;
import com.sochovsky.model.V1AdmissionReview;
import com.sochovsky.module.core.CoreModule;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;

@Controller
public class AmissionController {
	@Inject
	private CoreModule coreModule;

	@Get(uri = "/string", produces = MediaType.APPLICATION_JSON)
	public String getString() {
		return "String";
	}

	/**
	 * https://kubernetes.io/docs/reference/access-authn-authz/extensible-admission-controllers/#response
	 * 
	 * @param admission
	 * @return
	 */
	@Post(uri = "/admission", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public V1AdmissionReview admission(@Body V1AdmissionReview admission) {
		//System.out.println(admission.toString());
		V1AdmissionResponse v1AdmissionResponse = new V1AdmissionResponse();
		v1AdmissionResponse.setAllowed(true);
		v1AdmissionResponse.setUid(admission.getRequest().getUid());
		admission.setResponse(v1AdmissionResponse);
		return admission;
	}

	@Post(uri = "/admission2", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public V1AdmissionReview admission2(@Body V1AdmissionReview admission) {
		V1AdmissionResponse v1AdmissionResponse = new V1AdmissionResponse();
		v1AdmissionResponse.setAllowed(true);
		v1AdmissionResponse.setUid(admission.getRequest().getUid());
		List<String> nodes = coreModule.preferedForPatch(admission.getRequest().getName(), admission.getRequest().getNamespace());
		if(!nodes.isEmpty()){
			String patch = "[{ 'op': 'add', 'path': '/spec/template/spec/affinity', 'value': {'nodeAffinity': {'preferredDuringSchedulingIgnoredDuringExecution': [replace]} } }]";
			int maxSize = 2;
			String replacer = nodes.stream().limit(maxSize).map(n -> {
				int prio = (int) (100d * (maxSize - (nodes.size() - (nodes.size() - nodes.indexOf(n))))
						/ maxSize);
				return "{\"weight\": " + prio
						+ ", \"preference\": {\"matchExpressions\": [{\"key\": \"kubernetes.io/hostname\", \"operator\": \"In\", \"values\": [\""
						+ n + "\"]}]}}";
			}).reduce((n1, n2) -> n1 + "," + n2).get();
			patch = patch.replace("replace", replacer);
			patch = patch.replace("'", "\"");
			System.out.println(patch);
			v1AdmissionResponse.setPatch(Base64.getEncoder().encodeToString(patch.getBytes()));
			v1AdmissionResponse.setPatchType("JSONPatch");
		}
		admission.setResponse(v1AdmissionResponse);
		return admission;
	}
}
