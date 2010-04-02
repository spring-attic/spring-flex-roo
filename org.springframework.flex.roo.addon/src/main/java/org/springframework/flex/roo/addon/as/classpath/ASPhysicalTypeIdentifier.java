package org.springframework.flex.roo.addon.as.classpath;

import org.springframework.roo.metadata.MetadataIdentificationUtils;

public final class ASPhysicalTypeIdentifier {

	private static final String PROVIDES_TYPE_STRING = ASPhysicalTypeIdentifier.class.getName();
	private static final String PROVIDES_TYPE = MetadataIdentificationUtils.create(PROVIDES_TYPE_STRING);
	
	public static final String getMetadataIdentiferType() {
		return PROVIDES_TYPE;
	}
}
