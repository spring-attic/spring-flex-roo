package org.springframework.flex.roo.addon.as.classpath.details.metatag;

import java.util.List;

import org.springframework.flex.roo.addon.as.model.ActionScriptSymbolName;

public interface MetaTagMetadata {

	String getName();
	
	/**
	 * @return the attribute names, preferably in the order they are declared in the annotation (never null, but may be empty)
	 */
	List<ActionScriptSymbolName> getAttributeNames();
	
	/**
	 * Acquires an attribute value for the requested name.
	 * 
	 * @return the requested attribute (or null if not found)
	 */
	MetaTagAttributeValue<?> getAttribute(ActionScriptSymbolName attributeName);}
