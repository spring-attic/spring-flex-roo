package org.springframework.flex.roo.addon.as.classpath.details;

import java.util.List;

import org.springframework.flex.roo.addon.as.classpath.ASPhysicalTypeDetails;
import org.springframework.flex.roo.addon.as.classpath.details.metatag.ASMetaTagMetadata;
import org.springframework.flex.roo.addon.as.model.ActionScriptType;

public interface ASMemberHoldingTypeDetails extends ASPhysicalTypeDetails {

	List<ASMethodMetadata> getDeclaredMethods();

	ASConstructorMetadata getDeclaredConstructor();

	List<ASFieldMetadata> getDeclaredFields();

	List<ASMetaTagMetadata> getTypeMetaTags();
	
	List<ActionScriptType> getExtendsTypes();

	List<ActionScriptType> getImplementsTypes();

}