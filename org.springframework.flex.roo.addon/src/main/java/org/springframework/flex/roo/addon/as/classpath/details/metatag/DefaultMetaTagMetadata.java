package org.springframework.flex.roo.addon.as.classpath.details.metatag;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.flex.roo.addon.as.model.ActionScriptSymbolName;
import org.springframework.roo.support.util.Assert;

public class DefaultMetaTagMetadata implements MetaTagMetadata {

	private String name;
	private Map<ActionScriptSymbolName, MetaTagAttributeValue<?>> attributes = new LinkedHashMap<ActionScriptSymbolName, MetaTagAttributeValue<?>>();
	
	public DefaultMetaTagMetadata(String name,
			List<MetaTagAttributeValue<?>> attributes) {
		this.name = name;
		if (attributes != null) {
			for (MetaTagAttributeValue<?> attr : attributes) {
				this.attributes.put(attr.getName(), attr);
			}
		}
	}

	public String getName() {
		return name;
	}

	public MetaTagAttributeValue<?> getAttribute(ActionScriptSymbolName attributeName) {
		Assert.notNull(attributeName, "Attribute name required");
		return attributes.get(attributeName);		
	}

	public List<ActionScriptSymbolName> getAttributeNames() {
		return new ArrayList<ActionScriptSymbolName>(attributes.keySet());
	}

}
