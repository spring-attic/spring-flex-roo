package org.springframework.flex.roo.addon.as.classpath.details;

import java.util.List;

import org.springframework.flex.roo.addon.as.classpath.details.metatag.MetaTagMetadata;
import org.springframework.flex.roo.addon.as.model.ASTypeVisibility;
import org.springframework.flex.roo.addon.as.model.ActionScriptSymbolName;
import org.springframework.flex.roo.addon.as.model.ActionScriptType;

public class DefaultConstructorMetadata extends AbstractInvocableMemberMetadata implements ConstructorMetadata {
	
	public DefaultConstructorMetadata(String declaredByMetadataId,
			String methodBody, List<MetaTagMetadata> metaTags,
			List<ActionScriptType> paramTypes,
			List<ActionScriptSymbolName> paramNames, ASTypeVisibility visibility) {
		super(declaredByMetadataId, methodBody, metaTags, paramTypes, paramNames,
				visibility);
	}

	public DefaultConstructorMetadata(String declaredByMetadataId) {
		super(declaredByMetadataId);
	}
}
