package org.springframework.flex.roo.addon.as.classpath;


public interface ASMutablePhysicalTypeMetadataProvider extends
		ASPhysicalTypeMetadataProvider {

	void createPhysicalType(ASPhysicalTypeMetadata toCreate);
}
