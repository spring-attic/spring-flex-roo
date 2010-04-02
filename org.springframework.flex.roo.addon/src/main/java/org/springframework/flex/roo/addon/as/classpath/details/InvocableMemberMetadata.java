package org.springframework.flex.roo.addon.as.classpath.details;

import java.util.List;

import org.springframework.flex.roo.addon.as.classpath.details.metatag.MetaTagMetadata;
import org.springframework.flex.roo.addon.as.model.ActionScriptSymbolName;
import org.springframework.flex.roo.addon.as.model.ActionScriptType;

/**
 * Metadata concerning an invocable member, namely a method or constructor.
 *
 */
public interface InvocableMemberMetadata extends IdentifiableMember {
	
	/**
	 * @return the parameter types (never null, but may be empty)
	 */
	List<ActionScriptType> getParameterTypes();

	/**
	 * @return the parameter names, if available (never null, but may be empty)
	 */
	List<ActionScriptSymbolName> getParameterNames();

	/**
	 * @return meta tags on this invocable member (never null, but may be empty)
	 */
	 List<MetaTagMetadata> getMetaTags();
	 
	 /**
	  * @return the body of the method, if available (can be null if unavailable)
	  */
	 String getBody();

}