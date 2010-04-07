package org.springframework.flex.roo.addon.as.classpath.as3parser.details;

import java.util.List;

import org.springframework.flex.roo.addon.as.classpath.as3parser.As3ParserUtils;
import org.springframework.flex.roo.addon.as.classpath.as3parser.CompilationUnitServices;
import org.springframework.flex.roo.addon.as.classpath.details.MethodMetadata;
import org.springframework.flex.roo.addon.as.classpath.details.metatag.MetaTagMetadata;
import org.springframework.flex.roo.addon.as.model.ASTypeVisibility;
import org.springframework.flex.roo.addon.as.model.ActionScriptSymbolName;
import org.springframework.flex.roo.addon.as.model.ActionScriptType;
import org.springframework.roo.support.util.Assert;

import uk.co.badgersinfoil.metaas.dom.ASMethod;

public class As3ParserMethodMetadata implements MethodMetadata {
	
	private ActionScriptSymbolName methodName;
	private ActionScriptType returnType;
	private String declaredByMetadataId;
	
	public As3ParserMethodMetadata(
			String declaredByMetadataId,
			ASMethod method,
			CompilationUnitServices compilationUnitServices) {
		Assert.notNull(declaredByMetadataId, "Declared by metadata ID required");
		Assert.notNull(method, "Method declaration required");
		Assert.notNull(compilationUnitServices, "Compilation unit services required");
		
		this.declaredByMetadataId = declaredByMetadataId;
		
		this.methodName = new ActionScriptSymbolName(method.getName());
		
		this.returnType = As3ParserUtils.getActionScriptType(compilationUnitServices.getCompilationUnitPackage(), compilationUnitServices.getImports(), method.getType());
	}

	public ActionScriptSymbolName getMethodName() {
		// TODO Auto-generated method stub
		return null;
	}

	public ActionScriptType getReturnType() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<MetaTagMetadata> getMetaTags() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getBody() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ActionScriptSymbolName> getParameterNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ActionScriptType> getParameterTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDeclaredByMetadataId() {
		return declaredByMetadataId;
	}

	public ASTypeVisibility getVisibility() {
		// TODO Auto-generated method stub
		return null;
	}

}
