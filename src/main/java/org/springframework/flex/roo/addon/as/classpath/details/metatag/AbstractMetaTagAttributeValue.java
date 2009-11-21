package org.springframework.flex.roo.addon.as.classpath.details.metatag;

import org.springframework.flex.roo.addon.as.model.ActionScriptSymbolName;
import org.springframework.roo.support.util.Assert;

public abstract class AbstractMetaTagAttributeValue<T extends Object> implements MetaTagAttributeValue<T> {

	private ActionScriptSymbolName name;
	
	public AbstractMetaTagAttributeValue(ActionScriptSymbolName name) {
		Assert.notNull(name, "Meta Tag attribute name required");
		this.name = name;
	}

	public ActionScriptSymbolName getName() {
		return name;
	}
}
