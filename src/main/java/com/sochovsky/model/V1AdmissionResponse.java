/*
 * k8s.io/api/admission/v1
 * No description provided (generated by Swagger Codegen https://github.com/swagger-api/swagger-codegen)
 *
 * OpenAPI spec version: 1.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package com.sochovsky.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.gson.annotations.SerializedName;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * AdmissionResponse describes an admission response.
 */
@Data
@Schema(description = "AdmissionResponse describes an admission response.")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2021-04-08T23:27:15.525561+02:00[Europe/Vienna]")
public class V1AdmissionResponse {
	@SerializedName("allowed")
	private Boolean allowed = null;

	@SerializedName("auditAnnotations")
	private Map<String, String> auditAnnotations = null;

	@SerializedName("patch")
	private String patch = null;

	@SerializedName("patchType")
	private String patchType = null;

	@SerializedName("status")
	private V1Status status = null;

	@SerializedName("uid")
	private String uid = null;

	@SerializedName("warnings")
	private List<String> warnings = null;

	public V1AdmissionResponse allowed(Boolean allowed) {
		this.allowed = allowed;
		return this;
	}

	/**
	 * Allowed indicates whether or not the admission request was permitted.
	 * 
	 * @return allowed
	 **/
	@Schema(required = true, description = "Allowed indicates whether or not the admission request was permitted.")
	public Boolean isAllowed() {
		return allowed;
	}

	public void setAllowed(Boolean allowed) {
		this.allowed = allowed;
	}

	public V1AdmissionResponse auditAnnotations(Map<String, String> auditAnnotations) {
		this.auditAnnotations = auditAnnotations;
		return this;
	}

	public V1AdmissionResponse putAuditAnnotationsItem(String key, String auditAnnotationsItem) {
		if (this.auditAnnotations == null) {
			this.auditAnnotations = new HashMap<String, String>();
		}
		this.auditAnnotations.put(key, auditAnnotationsItem);
		return this;
	}

	/**
	 * AuditAnnotations is an unstructured key value map set by remote admission
	 * controller (e.g. error&#x3D;image-blacklisted). MutatingAdmissionWebhook and
	 * ValidatingAdmissionWebhook admission controller will prefix the keys with
	 * admission webhook name (e.g.
	 * imagepolicy.example.com/error&#x3D;image-blacklisted). AuditAnnotations will
	 * be provided by the admission webhook to add additional context to the audit
	 * log for this request.
	 * 
	 * @return auditAnnotations
	 **/
	@Schema(description = "AuditAnnotations is an unstructured key value map set by remote admission controller (e.g. error=image-blacklisted). MutatingAdmissionWebhook and ValidatingAdmissionWebhook admission controller will prefix the keys with admission webhook name (e.g. imagepolicy.example.com/error=image-blacklisted). AuditAnnotations will be provided by the admission webhook to add additional context to the audit log for this request.")
	public Map<String, String> getAuditAnnotations() {
		return auditAnnotations;
	}

	public void setAuditAnnotations(Map<String, String> auditAnnotations) {
		this.auditAnnotations = auditAnnotations;
	}

	public V1AdmissionResponse patch(String patch) {
		this.patch = patch;
		return this;
	}

	/**
	 * The patch body. Currently we only support \&quot;JSONPatch\&quot; which
	 * implements RFC 6902.
	 * 
	 * @return patch
	 **/
	@Schema(description = "The patch body. Currently we only support \"JSONPatch\" which implements RFC 6902.")
	public String getPatch() {
		return patch;
	}

	public void setPatch(String patch) {
		this.patch = patch;
	}

	public V1AdmissionResponse patchType(String patchType) {
		this.patchType = patchType;
		return this;
	}

	/**
	 * The type of Patch. Currently we only allow \&quot;JSONPatch\&quot;.
	 * 
	 * @return patchType
	 **/
	@Schema(description = "The type of Patch. Currently we only allow \"JSONPatch\".")
	public String getPatchType() {
		return patchType;
	}

	public void setPatchType(String patchType) {
		this.patchType = patchType;
	}

	public V1AdmissionResponse status(V1Status status) {
		this.status = status;
		return this;
	}

	/**
	 * Get status
	 * 
	 * @return status
	 **/
	@Schema(description = "")
	public V1Status getStatus() {
		return status;
	}

	public void setStatus(V1Status status) {
		this.status = status;
	}

	public V1AdmissionResponse uid(String uid) {
		this.uid = uid;
		return this;
	}

	/**
	 * UID is an identifier for the individual request/response. This must be copied
	 * over from the corresponding AdmissionRequest.
	 * 
	 * @return uid
	 **/
	@Schema(required = true, description = "UID is an identifier for the individual request/response. This must be copied over from the corresponding AdmissionRequest.")
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public V1AdmissionResponse warnings(List<String> warnings) {
		this.warnings = warnings;
		return this;
	}

	public V1AdmissionResponse addWarningsItem(String warningsItem) {
		if (this.warnings == null) {
			this.warnings = new ArrayList<String>();
		}
		this.warnings.add(warningsItem);
		return this;
	}

	/**
	 * warnings is a list of warning messages to return to the requesting API
	 * client. Warning messages describe a problem the client making the API request
	 * should correct or be aware of. Limit warnings to 120 characters if possible.
	 * Warnings over 256 characters and large numbers of warnings may be truncated.
	 * 
	 * @return warnings
	 **/
	@Schema(description = "warnings is a list of warning messages to return to the requesting API client. Warning messages describe a problem the client making the API request should correct or be aware of. Limit warnings to 120 characters if possible. Warnings over 256 characters and large numbers of warnings may be truncated.")
	public List<String> getWarnings() {
		return warnings;
	}

	public void setWarnings(List<String> warnings) {
		this.warnings = warnings;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		V1AdmissionResponse v1AdmissionResponse = (V1AdmissionResponse) o;
		return Objects.equals(this.allowed, v1AdmissionResponse.allowed)
				&& Objects.equals(this.auditAnnotations, v1AdmissionResponse.auditAnnotations)
				&& Objects.equals(this.patch, v1AdmissionResponse.patch)
				&& Objects.equals(this.patchType, v1AdmissionResponse.patchType)
				&& Objects.equals(this.status, v1AdmissionResponse.status)
				&& Objects.equals(this.uid, v1AdmissionResponse.uid)
				&& Objects.equals(this.warnings, v1AdmissionResponse.warnings);
	}

	@Override
	public int hashCode() {
		return Objects.hash(allowed, auditAnnotations, patch, patchType, status, uid, warnings);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class V1AdmissionResponse {\n");

		sb.append("    allowed: ").append(toIndentedString(allowed)).append("\n");
		sb.append("    auditAnnotations: ").append(toIndentedString(auditAnnotations)).append("\n");
		sb.append("    patch: ").append(toIndentedString(patch)).append("\n");
		sb.append("    patchType: ").append(toIndentedString(patchType)).append("\n");
		sb.append("    status: ").append(toIndentedString(status)).append("\n");
		sb.append("    uid: ").append(toIndentedString(uid)).append("\n");
		sb.append("    warnings: ").append(toIndentedString(warnings)).append("\n");
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Convert the given object to string with each line indented by 4 spaces
	 * (except the first line).
	 */
	private String toIndentedString(java.lang.Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}

}
