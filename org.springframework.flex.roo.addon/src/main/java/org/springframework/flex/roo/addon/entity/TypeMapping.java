package org.springframework.flex.roo.addon.entity;

import org.springframework.flex.roo.addon.as.model.ActionScriptType;
import org.springframework.roo.model.JavaType;

public class TypeMapping {

	private String metadataId;
	private ActionScriptType asType;
	private JavaType javaType;
	
	public TypeMapping(String metadataId, ActionScriptType asType,
			JavaType javaType) {
		super();
		this.metadataId = metadataId;
		this.asType = asType;
		this.javaType = javaType;
	}
	
	public String getMetadataId() {
		return metadataId;
	}
	
	public ActionScriptType getAsType() {
		return asType;
	}
	
	public JavaType getJavaType() {
		return javaType;
	}
}
