package org.springframework.flex.roo.addon.as.classpath.details;

import java.lang.reflect.Modifier;

import org.springframework.flex.roo.addon.as.model.ASTypeVisibility;

/**
 * Allows a member to be traced back to its declaring type.
 *
 */
public interface IdentifiableMember {

	/**
	 * @return the ID of the metadata that declared this member (never null)
	 */
	String getDeclaredByMetadataId();

	/**
	 * Indicates the visibility of the member. 
	 * 
	 * @return the visibility, if available (required) 
	 */
	ASTypeVisibility getVisibility();
	
}
