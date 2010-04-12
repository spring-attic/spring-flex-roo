package org.springframework.flex.roo.addon.as.classpath.details;

import java.util.List;

import org.springframework.flex.roo.addon.as.classpath.ASPhysicalTypeDetails;
import org.springframework.flex.roo.addon.as.classpath.details.metatag.MetaTagMetadata;
import org.springframework.flex.roo.addon.as.model.ActionScriptType;

public interface ASMemberHoldingTypeDetails extends ASPhysicalTypeDetails{

	List<? extends MethodMetadata> getDeclaredMethods();

	List<? extends ConstructorMetadata> getDeclaredConstructors();

	List<? extends FieldMetadata> getDeclaredFields();

	List<? extends MetaTagMetadata> getTypeMetaTags();

	List<ActionScriptType> getImplementsTypes();

}