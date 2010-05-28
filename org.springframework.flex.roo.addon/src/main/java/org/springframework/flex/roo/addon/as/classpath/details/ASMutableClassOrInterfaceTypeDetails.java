package org.springframework.flex.roo.addon.as.classpath.details;


import org.springframework.flex.roo.addon.as.classpath.details.metatag.MetaTagMetadata;
import org.springframework.flex.roo.addon.as.model.ActionScriptSymbolName;

/**
 * Provides information about the different members in a class or interface.
 * 
 * @author Jeremy Grelle
 * @since 1.0
 *
 */
public interface ASMutableClassOrInterfaceTypeDetails extends ASClassOrInterfaceTypeDetails {
	
	/**
	 * Adds a new type-level meta tag. There must not already be an equivalent meta tag of this
	 * defined on the type.
	 * 
	 * @param metaTag to add (required)
	 */
	void addTypeMetaTag(MetaTagMetadata metaTag);
	
	/**
	 * Removes the type-level meta tag of the name indicated. This meta tag must
	 * already exist.
	 * 
	 * @param name of the meta tag to remove (required)
	 */
	void removeTypeMetaTag(String name);
	
	/**
	 * Adds a new field. There must not be a field of this name already existing.
	 * 
	 * @param fieldMetadata to add (required)
	 */
	void addField(FieldMetadata fieldMetadata);
	
	/**
	 * Removes an existing field. A field with the specified name must already exist.
	 * 
	 * @param fieldName to remove (required)
	 */
	void removeField(ActionScriptSymbolName fieldName);
	
	/**
	 * Adds a new method. A method with the same name and parameter types must not already exist.
	 * 
	 * @param methodMetadata to add (required)
	 */
	void addMethod(MethodMetadata methodMetadata);
	

}
