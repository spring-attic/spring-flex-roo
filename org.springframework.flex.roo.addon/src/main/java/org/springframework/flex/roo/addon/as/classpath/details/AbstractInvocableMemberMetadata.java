package org.springframework.flex.roo.addon.as.classpath.details;

import java.util.ArrayList;
import java.util.List;

import org.springframework.flex.roo.addon.as.classpath.details.metatag.MetaTagMetadata;
import org.springframework.flex.roo.addon.as.model.ASTypeVisibility;
import org.springframework.flex.roo.addon.as.model.ActionScriptSymbolName;
import org.springframework.flex.roo.addon.as.model.ActionScriptType;
import org.springframework.roo.support.util.Assert;

public abstract class AbstractInvocableMemberMetadata implements
		InvocableMemberMetadata {

	private String declaredByMetadataId;

	private String methodBody;
	private List<MetaTagMetadata> metaTags = new ArrayList<MetaTagMetadata>();
	private List<ActionScriptType> paramTypes = new ArrayList<ActionScriptType>();
	private List<ActionScriptSymbolName> paramNames = new ArrayList<ActionScriptSymbolName>();
	private ASTypeVisibility visibility;
	
	public AbstractInvocableMemberMetadata(String declaredByMetadataId) {
		this(declaredByMetadataId, null, null, null, null, null);
	}

	public AbstractInvocableMemberMetadata(String declaredByMetadataId,
			String methodBody, List<MetaTagMetadata> metaTags,
			List<ActionScriptType> paramTypes,
			List<ActionScriptSymbolName> paramNames, ASTypeVisibility visibility) {
		Assert.notNull(declaredByMetadataId, "Metadata ID of owning type is required.");
		
		this.declaredByMetadataId = declaredByMetadataId;
		this.visibility = visibility != null ? visibility : ASTypeVisibility.PUBLIC;
		this.methodBody = methodBody != null ? methodBody : "";
		
		if(metaTags != null) { 
			this.metaTags = metaTags;
		}
		
		if (paramTypes != null) {
			this.paramTypes = paramTypes;
		}
		
		if (paramNames != null) {
			this.paramNames = paramNames;
		}
	}

	public String getDeclaredByMetadataId() {
		return declaredByMetadataId;
	}

	public String getBody() {
		return methodBody;
	}

	public List<MetaTagMetadata> getMetaTags() {
		return metaTags;
	}

	public List<ActionScriptType> getParameterTypes() {
		return paramTypes;
	}

	public List<ActionScriptSymbolName> getParameterNames() {
		return paramNames;
	}

	public ASTypeVisibility getVisibility() {
		return visibility;
	}
}
