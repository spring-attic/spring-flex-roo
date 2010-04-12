package org.springframework.flex.roo.addon.as.classpath.as3parser.details;

import java.util.ArrayList;
import java.util.List;

import org.springframework.flex.roo.addon.as.classpath.as3parser.As3ParserUtils;
import org.springframework.flex.roo.addon.as.classpath.as3parser.CompilationUnitServices;
import org.springframework.flex.roo.addon.as.classpath.details.ConstructorMetadata;
import org.springframework.flex.roo.addon.as.classpath.details.metatag.MetaTagMetadata;
import org.springframework.flex.roo.addon.as.model.ASTypeVisibility;
import org.springframework.flex.roo.addon.as.model.ActionScriptSymbolName;
import org.springframework.flex.roo.addon.as.model.ActionScriptType;
import org.springframework.roo.support.util.Assert;


import uk.co.badgersinfoil.metaas.dom.ASMetaTag;
import uk.co.badgersinfoil.metaas.dom.ASMethod;
import uk.co.badgersinfoil.metaas.dom.Statement;

public class As3ParserConstructorMetadata implements ConstructorMetadata {

	private String declaredByMetadataId;
	private ActionScriptSymbolName methodName;
	private ActionScriptType returnType;
	
	private String methodBody;
	private List<MetaTagMetadata> metaTags = new ArrayList<MetaTagMetadata>();
	private List<ActionScriptType> paramTypes = new ArrayList<ActionScriptType>();
	private List<ActionScriptSymbolName> paramNames = new ArrayList<ActionScriptSymbolName>();
	private ASTypeVisibility visibility;

	public As3ParserConstructorMetadata(
			String declaredByMetadataId,
			ASMethod method,
			CompilationUnitServices compilationUnitServices) {
		Assert.notNull(declaredByMetadataId, "Declared by metadata ID required");
		Assert.notNull(method, "Method declaration required");
		Assert.notNull(compilationUnitServices, "Compilation unit services required");
		
		this.declaredByMetadataId = declaredByMetadataId;
		
		this.methodName = new ActionScriptSymbolName(method.getName());
		
		this.returnType = As3ParserUtils.getActionScriptType(compilationUnitServices.getCompilationUnitPackage(), compilationUnitServices.getImports(), method.getName());
		
		StringBuffer bodyBuf = new StringBuffer();
		for (Statement statement : (List<Statement>) method.getStatementList()){
			bodyBuf.append(statement.toString());
		}
		this.methodBody = bodyBuf.toString();
		
		List<ASMetaTag> metaTagList = method.getAllMetaTags();
		if (metaTagList != null) {
			for (ASMetaTag metaTag : metaTagList) {
				As3ParserMetaTagMetadata md = new As3ParserMetaTagMetadata(metaTag);
				metaTags.add(md);
			}
		}
		
		
	}
	
	public String getBody() {
		return this.methodBody;
	}

	public List<MetaTagMetadata> getMetaTags() {
		return this.metaTags;
	}

	public List<ActionScriptSymbolName> getParameterNames() {
		return this.paramNames;
	}

	public List<ActionScriptType> getParameterTypes() {
		return this.paramTypes;
	}

	public String getDeclaredByMetadataId() {
		return this.declaredByMetadataId;
	}

	public ASTypeVisibility getVisibility() {
		return this.visibility;
	}
}
