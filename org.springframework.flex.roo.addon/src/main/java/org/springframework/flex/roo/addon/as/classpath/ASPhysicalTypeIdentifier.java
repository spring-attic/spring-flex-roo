package org.springframework.flex.roo.addon.as.classpath;

import org.springframework.flex.roo.addon.as.model.ActionScriptType;
import org.springframework.roo.metadata.MetadataIdentificationUtils;
import org.springframework.roo.project.Path;
import org.springframework.roo.support.util.Assert;

public final class ASPhysicalTypeIdentifier {

	private static final String PROVIDES_TYPE_STRING = ASPhysicalTypeIdentifier.class.getName();
	private static final String PROVIDES_TYPE = MetadataIdentificationUtils.create(PROVIDES_TYPE_STRING);
	
	public static final String getMetadataIdentiferType() {
		return PROVIDES_TYPE;
	}

	public static final String createIdentifier(ActionScriptType actionScriptType, Path path) {
		return ASPhysicalTypeIdentifierNamingUtils.createIdentifier(PROVIDES_TYPE_STRING, actionScriptType, path);
	}

	public static final ActionScriptType getActionScriptType(String metadataIdentificationString) {
		return ASPhysicalTypeIdentifierNamingUtils.getActionScriptType(PROVIDES_TYPE_STRING, metadataIdentificationString);
	}

	public static final Path getPath(String metadataIdentificationString) {
		return ASPhysicalTypeIdentifierNamingUtils.getPath(PROVIDES_TYPE_STRING, metadataIdentificationString);
	}

	public static boolean isValid(String metadataIdentificationString) {
		return ASPhysicalTypeIdentifierNamingUtils.isValid(PROVIDES_TYPE_STRING, metadataIdentificationString);
	}
	
	public static String getFriendlyName(String metadataIdentificationString) {
		Assert.isTrue(isValid(metadataIdentificationString), "Invalid metadata identification string '" + metadataIdentificationString + "' provided");
		return getPath(metadataIdentificationString) + "/" + getActionScriptType(metadataIdentificationString);
	}
}
