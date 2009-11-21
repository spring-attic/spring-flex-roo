package org.springframework.flex.roo.addon.as.classpath.as3parser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.flex.roo.addon.as.classpath.details.metatag.BooleanAttributeValue;
import org.springframework.flex.roo.addon.as.classpath.details.metatag.IntegerAttributeValue;
import org.springframework.flex.roo.addon.as.classpath.details.metatag.MetaTagAttributeValue;
import org.springframework.flex.roo.addon.as.classpath.details.metatag.MetaTagMetadata;
import org.springframework.flex.roo.addon.as.classpath.details.metatag.StringAttributeValue;
import org.springframework.flex.roo.addon.as.model.ActionScriptSymbolName;
import org.springframework.roo.support.util.Assert;

import uk.co.badgersinfoil.metaas.dom.ASMetaTag;
import uk.co.badgersinfoil.metaas.dom.MetaTagable;

public class As3ParserMetaTagMetadata implements MetaTagMetadata {
	
	//provided
	private String name;
	private Map<ActionScriptSymbolName, MetaTagAttributeValue<?>> attributes = new LinkedHashMap<ActionScriptSymbolName, MetaTagAttributeValue<?>>();
	
	public As3ParserMetaTagMetadata(ASMetaTag metaTag) {
		Assert.notNull(metaTag, "Meta Tag required");
		
		this.name = metaTag.getName();
		for (ASMetaTag.Param param : (List<ASMetaTag.Param>)metaTag.getParams()) {
			MetaTagAttributeValue<?> attr = As3ParserUtils.getMetaTagAttributeValue(param);
			attributes.put(attr.getName(), attr);
		}
	}

	public static void addMetaTagElement(CompilationUnitServices compilationUnitServices, MetaTagMetadata metaTag,
			MetaTagable element, boolean permitFlush) {
		
		for (ASMetaTag existingTag : (List<ASMetaTag>)element.getAllMetaTags()) {
			Assert.isTrue(!metaTag.getName().equals(existingTag.getName()), "Found an existing meta tag of type '" +metaTag.getName()+"'");
		}
		
		ASMetaTag newMetaTag = element.newMetaTag(metaTag.getName());
		for (ActionScriptSymbolName attrName : metaTag.getAttributeNames()) {
			MetaTagAttributeValue<?> value = metaTag.getAttribute(attrName);
			if (value instanceof BooleanAttributeValue) {
				newMetaTag.addParam(attrName.getSymbolName(), ((BooleanAttributeValue)value).getValue());
			} else if (value instanceof IntegerAttributeValue) {
				newMetaTag.addParam(attrName.getSymbolName(), ((IntegerAttributeValue)value).getValue());
			} else if (value instanceof StringAttributeValue) {
				newMetaTag.addParam(attrName.getSymbolName(), ((StringAttributeValue)value).getValue());
			} else {
				throw new IllegalArgumentException("Cannot add uknown meta tag attribute type.");
			}
		}
		
		if (permitFlush) {
			compilationUnitServices.flush();
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
