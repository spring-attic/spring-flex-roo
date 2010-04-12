package org.springframework.flex.roo.addon.as.classpath.details;

public interface ASClassOrInterfaceTypeDetails extends ASMemberHoldingTypeDetails{

	ASClassOrInterfaceTypeDetails getSuperClass();

	String getDeclaredByMetadataId();

}