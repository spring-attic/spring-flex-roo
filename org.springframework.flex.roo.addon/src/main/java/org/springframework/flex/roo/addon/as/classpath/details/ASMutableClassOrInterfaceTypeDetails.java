package org.springframework.flex.roo.addon.as.classpath.details;

import java.util.List;

import org.springframework.flex.roo.addon.as.classpath.ASPhysicalTypeDetails;
import org.springframework.flex.roo.addon.as.classpath.details.metatag.MetaTagMetadata;
import org.springframework.flex.roo.addon.as.model.ActionScriptSymbolName;
import org.springframework.flex.roo.addon.as.model.ActionScriptType;

/**
 * Provides information about the different members in a class, interface, enum or aspect.
 * 
 * @author Jeremy Grelle
 * @since 1.0
 *
 */
public interface ASMutableClassOrInterfaceTypeDetails extends ASPhysicalTypeDetails {
	
	List<? extends MethodMetadata> getDeclaredMethods();
	
	List<? extends ConstructorMetadata> getDeclaredConstructors();
	
	List<? extends FieldMetadata> getDeclaredFields();
	
	/**
	 * Lists the type-level meta tags.
	 * 
	 * <p>
	 * This includes those meta tags declared on the type.
	 * 
	 * @return an unmodifiable representation of meta tags declared on this type (may be empty, but never null)
	 */
	List<? extends MetaTagMetadata> getTypeMetaTags();
	
	ActionScriptType getSuperType();
	
	/**
	 * Lists the classes this type implements. Always empty in the case of an interface.
	 * 
	 * <p>
	 * A {@link List} is used to support interfaces.
	 * 
	 * @return an unmodifiable representation of classes this type implements (may be empty, but never null)
	 */
	List<ActionScriptType> getImplementsTypes();
	
	/**
	 * Obtains the physical type identifier that included this {@link ASMutableClassOrInterfaceTypeDetails}.
	 * 
	 * @return the physical type identifier (never null)
	 */
	String getDeclaredByMetadataId();
	
	//TODO - Do we need an equivalent to getModifier() in the Java version?
	
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
	void removeTypeAnnotation(String name);
	
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
