package org.springframework.flex.roo.addon.as.classpath.details.metatag;

import org.springframework.flex.roo.addon.as.model.ActionScriptSymbolName;

public class IntegerAttributeValue extends AbstractMetaTagAttributeValue<Integer> {

	private int value;
	
	public IntegerAttributeValue(ActionScriptSymbolName name, Integer value) {
		super(name);
		this.value = value;
	}
	
	public Integer getValue() {
		return value;
	}
	
	public String toString() {
		return getName() + " -> " + new Integer(value).toString();
	}
}
