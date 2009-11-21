package org.springframework.flex.roo.addon.as.classpath.details;

import java.util.List;

import org.springframework.flex.roo.addon.as.classpath.details.metatag.MetaTagMetadata;
import org.springframework.flex.roo.addon.as.model.ASTypeVisibility;
import org.springframework.flex.roo.addon.as.model.ActionScriptSymbolName;
import org.springframework.flex.roo.addon.as.model.ActionScriptType;

public interface FieldMetadata {

	ActionScriptType getFieldType();
	
	ActionScriptSymbolName getFieldName();

	List<MetaTagMetadata> getMetaTags();
	
	ASTypeVisibility getVisibility();
	
}
