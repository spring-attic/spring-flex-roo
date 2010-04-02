package org.springframework.flex.roo.addon.as.classpath.details.metatag;

import org.springframework.flex.roo.addon.as.model.ActionScriptSymbolName;

public interface MetaTagAttributeValue<T extends Object> {

	ActionScriptSymbolName getName();
	
	T getValue();

}
