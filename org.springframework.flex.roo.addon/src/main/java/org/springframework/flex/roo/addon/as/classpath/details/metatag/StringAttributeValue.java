package org.springframework.flex.roo.addon.as.classpath.details.metatag;

import org.springframework.flex.roo.addon.as.model.ActionScriptSymbolName;

public class StringAttributeValue extends AbstractMetaTagAttributeValue<String> {

	private String value;

	public StringAttributeValue(ActionScriptSymbolName name, String value) {
		super(name);
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	
	public String toString() {
		return getName() + " -> " + value;
	}

}
