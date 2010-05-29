package org.springframework.flex.roo.addon.as.classpath.details;

import java.util.List;

import org.springframework.flex.roo.addon.as.classpath.details.metatag.ASMetaTagMetadata;
import org.springframework.flex.roo.addon.as.model.ActionScriptSymbolName;
import org.springframework.flex.roo.addon.as.model.ActionScriptType;

public interface ASFieldMetadata extends ASIdentifiableMember{

	ActionScriptType getFieldType();
	
	ActionScriptSymbolName getFieldName();

	List<ASMetaTagMetadata> getMetaTags();
	
}
