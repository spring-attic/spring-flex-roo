package org.springframework.flex.roo.addon.as.classpath.details;

import java.util.List;

import org.springframework.flex.roo.addon.as.classpath.details.metatag.ASMetaTagMetadata;
import org.springframework.flex.roo.addon.as.model.ASTypeVisibility;
import org.springframework.flex.roo.addon.as.model.ActionScriptSymbolName;
import org.springframework.flex.roo.addon.as.model.ActionScriptType;
import org.springframework.roo.support.util.Assert;

public class DefaultASMethodMetadata extends AbstractInvocableMemberMetadata implements ASMethodMetadata {

	private ActionScriptSymbolName methodName;
	private ActionScriptType returnType;
	
	public DefaultASMethodMetadata(String declaredByMetadataId, ActionScriptSymbolName methodName, ActionScriptType returnType,
			ASTypeVisibility visibility, String methodBody, List<ASMetaTagMetadata> metaTags,
			List<ActionScriptType> paramTypes,
			List<ActionScriptSymbolName> paramNames) {
		super(declaredByMetadataId, methodBody, metaTags, paramTypes, paramNames,
				visibility);
		Assert.notNull(methodName, "Method name is required.");
		Assert.notNull(returnType, "Return type is require.");
		
		this.methodName = methodName;
		this.returnType = returnType;
	}

	public DefaultASMethodMetadata(String declaredByMetadataId, ActionScriptSymbolName methodName, ActionScriptType returnType, ASTypeVisibility visibility) {
		this(declaredByMetadataId, methodName, returnType, visibility, null, null, null, null);
	}

	public ActionScriptSymbolName getMethodName() {
		return methodName;
	}

	public ActionScriptType getReturnType() {
		return returnType;
	}
}
