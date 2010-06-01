package org.springframework.flex.roo.addon.as.classpath.details;

import org.springframework.flex.roo.addon.as.model.ActionScriptSymbolName;

public abstract class AbstractASFieldMetadata implements ASFieldMetadata{

	public abstract ActionScriptSymbolName getFieldName();

	public abstract String getDeclaredByMetadataId();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((getDeclaredByMetadataId() == null) ? 0 : getDeclaredByMetadataId()
						.hashCode());
		result = prime * result
				+ ((getFieldName() == null) ? 0 : getFieldName().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!ASFieldMetadata.class.isAssignableFrom(obj.getClass()))
			return false;
		ASFieldMetadata other = (ASFieldMetadata) obj;
		if (getDeclaredByMetadataId() == null) {
			if (other.getDeclaredByMetadataId() != null)
				return false;
		} else if (!getDeclaredByMetadataId().equals(other.getDeclaredByMetadataId()))
			return false;
		if (getFieldName() == null) {
			if (other.getFieldName() != null)
				return false;
		} else if (!getFieldName().equals(other.getFieldName()))
			return false;
		return true;
	}

}