package org.springframework.flex.roo.addon.as.classpath.details;

import org.springframework.flex.roo.addon.as.classpath.ASPhysicalTypeDetails;
import org.springframework.flex.roo.addon.as.classpath.ASPhysicalTypeIdentifier;
import org.springframework.flex.roo.addon.as.classpath.ASPhysicalTypeMetadata;
import org.springframework.roo.metadata.AbstractMetadataItem;
import org.springframework.roo.support.util.Assert;

public class DefaultASPhysicalTypeMetadata extends AbstractMetadataItem implements ASPhysicalTypeMetadata {

	private ASPhysicalTypeDetails physicalTypeDetails;
	private String physicalLocationCanonicalPath;

	public DefaultASPhysicalTypeMetadata(String metadataIdentificationString, String physicalLocationCanonicalPath,
			ASPhysicalTypeDetails physicalTypeDetails) {
		super(metadataIdentificationString);
		Assert.isTrue(ASPhysicalTypeIdentifier.isValid(metadataIdentificationString), "Metadata identification string '" + metadataIdentificationString + "' does not appear to be a valid physical type identifier");
		Assert.hasText(physicalLocationCanonicalPath, "Physical location canonical path required");
		Assert.notNull(physicalTypeDetails, "Physical type details required");
		this.physicalTypeDetails = physicalTypeDetails;
		this.physicalLocationCanonicalPath = physicalLocationCanonicalPath;
	}

	public ASPhysicalTypeDetails getPhysicalTypeDetails() {
		return physicalTypeDetails;
	}

	public String getPhysicalLocationCanonicalPath() {
		return physicalLocationCanonicalPath;
	}

}
