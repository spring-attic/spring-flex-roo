package org.springframework.flex.roo.addon.as.classpath.details.metatag;

import org.springframework.flex.roo.addon.as.model.ActionScriptSymbolName;

public class BooleanAttributeValue extends
		AbstractMetaTagAttributeValue<Boolean> {

	boolean value;
	
	public BooleanAttributeValue(ActionScriptSymbolName name, boolean value) {
		super(name);
		this.value = value;
	}

	public Boolean getValue() {
		return value;
	}
	
	public String toString() {
		return getName() + " -> " + new Boolean(value).toString();
	}
}
