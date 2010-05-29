package org.springframework.flex.roo.addon.as.classpath.details;

import java.util.List;

import org.springframework.flex.roo.addon.as.classpath.details.metatag.ASMetaTagMetadata;
import org.springframework.flex.roo.addon.as.model.ASTypeVisibility;
import org.springframework.flex.roo.addon.as.model.ActionScriptSymbolName;
import org.springframework.flex.roo.addon.as.model.ActionScriptType;

public class DefaultASConstructorMetadata extends AbstractInvocableMemberMetadata implements ASConstructorMetadata {
	
	public DefaultASConstructorMetadata(String declaredByMetadataId,
			String methodBody, List<ASMetaTagMetadata> metaTags,
			List<ActionScriptType> paramTypes,
			List<ActionScriptSymbolName> paramNames, ASTypeVisibility visibility) {
		super(declaredByMetadataId, methodBody, metaTags, paramTypes, paramNames,
				visibility);
	}

	public DefaultASConstructorMetadata(String declaredByMetadataId) {
		super(declaredByMetadataId);
	}
}
