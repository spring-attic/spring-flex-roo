package org.springframework.flex.roo.addon.as.classpath.details;

import org.springframework.flex.roo.addon.as.classpath.ASPhysicalTypeCategory;
import org.springframework.flex.roo.addon.as.classpath.ASPhysicalTypeDetails;
import org.springframework.flex.roo.addon.as.model.ActionScriptType;
import org.springframework.roo.support.util.Assert;

public class DefaultASPhysicalTypeDetails implements ASPhysicalTypeDetails {

	private ASPhysicalTypeCategory physicalTypeCategory;
	private ActionScriptType name;
	
	public DefaultASPhysicalTypeDetails(ASPhysicalTypeCategory physicalTypeCategory, ActionScriptType name) {
		Assert.notNull(physicalTypeCategory, "Physical type category required");
		Assert.notNull(name, "Name required");
		this.physicalTypeCategory = physicalTypeCategory;
		this.name = name;
	}

	public ActionScriptType getName() {
		return name;
	}

	public ASPhysicalTypeCategory getPhysicalTypeCategory() {
		return physicalTypeCategory;
	}

}
