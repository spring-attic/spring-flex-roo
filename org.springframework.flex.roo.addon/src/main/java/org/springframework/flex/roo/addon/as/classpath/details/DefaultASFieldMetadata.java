package org.springframework.flex.roo.addon.as.classpath.details;

import java.util.ArrayList;
import java.util.List;

import org.springframework.flex.roo.addon.as.classpath.details.metatag.ASMetaTagMetadata;
import org.springframework.flex.roo.addon.as.model.ASTypeVisibility;
import org.springframework.flex.roo.addon.as.model.ActionScriptSymbolName;
import org.springframework.flex.roo.addon.as.model.ActionScriptType;
import org.springframework.roo.support.util.Assert;

public class DefaultASFieldMetadata extends AbstractASFieldMetadata {

	private String declaredByMetadataId;
	private ActionScriptType fieldType;
	private ActionScriptSymbolName fieldName;
	private ASTypeVisibility visibility;
	private String fieldInitializer;
	private List<ASMetaTagMetadata> metaTags = new ArrayList<ASMetaTagMetadata>();

	public DefaultASFieldMetadata(String declaredByMetadataId,
			ActionScriptType fieldType, ActionScriptSymbolName fieldName,
			ASTypeVisibility visibility, String fieldInitializer, List<ASMetaTagMetadata> metaTags) {
		Assert.hasText(declaredByMetadataId, "Declared by metadata ID required");
		Assert.notNull(fieldName, "Field name required");
		Assert.notNull(fieldType, "Field type required");
		
		this.declaredByMetadataId = declaredByMetadataId;
		this.fieldType = fieldType;
		this.fieldName = fieldName;
		this.visibility = visibility != null ? visibility : ASTypeVisibility.PUBLIC;
		this.fieldInitializer = fieldInitializer;
		
		if (metaTags != null) {
			this.metaTags = metaTags;
		}
	}
	
	@Override
	public String getDeclaredByMetadataId() {
		return declaredByMetadataId;
	}

	public ActionScriptType getFieldType() {
		return fieldType;
	}
	
	@Override
	public ActionScriptSymbolName getFieldName() {
		return fieldName;
	}
	
	public ASTypeVisibility getVisibility() {
		return visibility;
	}
	
	public List<ASMetaTagMetadata> getMetaTags() {
		return metaTags;
	}

	public String getFieldInitializer() {
		return fieldInitializer;
	}
}
