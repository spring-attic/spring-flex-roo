package org.springframework.flex.roo.addon.as.classpath.as3parser.details;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.flex.roo.addon.as.classpath.as3parser.As3ParserUtils;
import org.springframework.flex.roo.addon.as.classpath.as3parser.CompilationUnitServices;
import org.springframework.flex.roo.addon.as.classpath.details.ConstructorMetadata;
import org.springframework.flex.roo.addon.as.classpath.details.metatag.MetaTagMetadata;
import org.springframework.flex.roo.addon.as.model.ASTypeVisibility;
import org.springframework.flex.roo.addon.as.model.ActionScriptSymbolName;
import org.springframework.flex.roo.addon.as.model.ActionScriptType;
import org.springframework.roo.support.util.Assert;

import uk.co.badgersinfoil.metaas.dom.ASArg;
import uk.co.badgersinfoil.metaas.dom.ASMetaTag;
import uk.co.badgersinfoil.metaas.dom.ASMethod;
import uk.co.badgersinfoil.metaas.dom.ASType;
import uk.co.badgersinfoil.metaas.dom.Statement;

public class As3ParserConstructorMetadata implements ConstructorMetadata {

	private String declaredByMetadataId;
	
	private String methodBody;
	private List<MetaTagMetadata> metaTags = new ArrayList<MetaTagMetadata>();
	private Map<ActionScriptSymbolName, ActionScriptType> params = new LinkedHashMap<ActionScriptSymbolName, ActionScriptType>();
	private ASTypeVisibility visibility;

	public As3ParserConstructorMetadata(
			String declaredByMetadataId,
			ASMethod method,
			CompilationUnitServices compilationUnitServices) {
		Assert.notNull(declaredByMetadataId, "Declared by metadata ID required");
		Assert.notNull(method, "Method declaration required");
		Assert.notNull(compilationUnitServices, "Compilation unit services required");
		
		this.declaredByMetadataId = declaredByMetadataId;
				
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
		
		List<ASArg> args = method.getArgs();
		for(ASArg arg : args) {
			ActionScriptType paramType = As3ParserUtils.getActionScriptType(compilationUnitServices.getCompilationUnitPackage(), compilationUnitServices.getImports(), arg.getType());
			params.put(new ActionScriptSymbolName(arg.getName()), paramType);
		}
		
		this.visibility = As3ParserUtils.getASTypeVisibility(method.getVisibility());
	}
	
	public String getBody() {
		return this.methodBody;
	}

	public List<MetaTagMetadata> getMetaTags() {
		return this.metaTags;
	}

	public List<ActionScriptSymbolName> getParameterNames() {
		return new ArrayList<ActionScriptSymbolName>(params.keySet());
	}

	public List<ActionScriptType> getParameterTypes() {
		return new ArrayList<ActionScriptType>(this.params.values());
	}

	public String getDeclaredByMetadataId() {
		return this.declaredByMetadataId;
	}

	public ASTypeVisibility getVisibility() {
		return this.visibility;
	}
	
	public static void addConstructor(CompilationUnitServices compilationUnitServices, ASType type, ConstructorMetadata declaredConstructor, boolean permitFlush) {
		
		Assert.isNull(type.getMethod(type.getName()), "ActionScript classes may only have one constructor method.");
		
		ASMethod constructor = type.newMethod(type.getName(), As3ParserUtils.getAs3ParserVisiblity(declaredConstructor.getVisibility()), null);
		
		//TODO - The parser doesn't allow any control over re-ordering methods.  It would be good if we could ensure the constructor is the first method in the class.
		
		//Add MetaTags
		for (MetaTagMetadata metaTag : declaredConstructor.getMetaTags()) {
			As3ParserMetaTagMetadata.addMetaTagElement(compilationUnitServices, metaTag, constructor, false);			
		}
		
		//Add Arguments
		for (int x=0; x<declaredConstructor.getParameterNames().size(); x++) {
			ActionScriptSymbolName argName = declaredConstructor.getParameterNames().get(x);
			ActionScriptType argType = declaredConstructor.getParameterTypes().get(x);
			As3ParserUtils.importTypeIfRequired(compilationUnitServices, argType);
			constructor.addParam(argName.getSymbolName(), argType.getSimpleTypeName());
		}
		
		if (permitFlush) {
			compilationUnitServices.flush();
		}
	} 
}
