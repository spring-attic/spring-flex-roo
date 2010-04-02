package org.springframework.flex.roo.addon.as.classpath.as3parser;

import java.util.List;

import org.springframework.flex.roo.addon.as.classpath.details.metatag.BooleanAttributeValue;
import org.springframework.flex.roo.addon.as.classpath.details.metatag.MetaTagAttributeValue;
import org.springframework.flex.roo.addon.as.model.ASTypeVisibility;
import org.springframework.flex.roo.addon.as.model.ActionScriptPackage;
import org.springframework.flex.roo.addon.as.model.ActionScriptSymbolName;
import org.springframework.flex.roo.addon.as.model.ActionScriptType;
import org.springframework.roo.support.util.Assert;

import uk.co.badgersinfoil.metaas.ActionScriptFactory;
import uk.co.badgersinfoil.metaas.dom.ASType;
import uk.co.badgersinfoil.metaas.dom.Visibility;
import uk.co.badgersinfoil.metaas.dom.ASMetaTag.Param;

public class As3ParserUtils {
	
	//private static final ActionScriptFactory factory = new ActionScriptFactory();

	public static final ActionScriptType getActionScriptType(ActionScriptPackage compilationUnitPackage, List<String> imports, ASType type) {
		Assert.notNull(imports, "Compilation unit imports required");
		Assert.notNull(compilationUnitPackage, "Compilation unit package required");
		Assert.notNull(type, "ASType required");
		
		return getActionScriptType(compilationUnitPackage, imports, type.getName());
	}
	
	public static final ActionScriptType getActionScriptType(ActionScriptPackage compilationUnitPackage, List<String> imports, String nameToFind) {
		Assert.notNull(imports, "Compilation unit imports required");
		Assert.notNull(compilationUnitPackage, "Compilation unit package required");
		Assert.notNull(nameToFind, "Name to find is required");
		
		int offset = nameToFind.lastIndexOf('.');
		if (offset > -1) {
			return new ActionScriptType(nameToFind);
		}
		
		if (ActionScriptType.isImplicitType(nameToFind)) {
			return new ActionScriptType(nameToFind);
		}
		
		String importDeclaration = getImportDeclarationFor(imports, nameToFind);
		if (importDeclaration  == null) {
			String name = compilationUnitPackage.getFullyQualifiedPackageName() == "" ? nameToFind : compilationUnitPackage.getFullyQualifiedPackageName() + "." + nameToFind;
			return new ActionScriptType(name);
		}
		
		return new ActionScriptType(importDeclaration);
	}
	
	/*public static final ASType getASType(String typeName) {
		Assert.notNull(typeName, "ActionScript type required");
		return factory.newClass(typeName).getType();
	}*/
	
	private static final String getImportDeclarationFor(List<String> imports, String typeName) {
		Assert.notNull(imports, "Compilation unit imports required");
		Assert.notNull(typeName, "Type name required");
		for (String candidate : imports) {
			int offset = candidate.lastIndexOf('.');
			if (typeName.equals(candidate.substring(offset+1))) {
				return candidate;
			}
		}
		return null;
	}

	public static void importTypeIfRequired(ActionScriptPackage compilationUnitPackage, List<String> imports, ActionScriptType typeToImport) {
		Assert.notNull(compilationUnitPackage, "Compilation unit package is required");
		Assert.notNull(imports, "Compilation unit imports required");
		Assert.notNull(typeToImport, "ActionScript type to import is required");
		
		if(typeToImport.isDefaultPackage()) {
			return;
		}
		
		if (imports.contains(typeToImport.getFullyQualifiedTypeName())) {
			return;
		}
		
		imports.add(typeToImport.getFullyQualifiedTypeName());
	}
	
	public static ASTypeVisibility getASTypeVisibility(Visibility as3ParserVisibility) {
		return ASTypeVisibility.valueOf(as3ParserVisibility.toString().replaceAll("\\[|\\]", "").toUpperCase());
	}
	
	public static Visibility getAs3ParserVisiblity(ASTypeVisibility typeVisibility) {
		switch(typeVisibility) {
			case INTERNAL:
				return Visibility.INTERNAL;
			case PRIVATE:
				return Visibility.PRIVATE;
			case PROTECTED:
				return Visibility.PROTECTED;
			case PUBLIC:
				return Visibility.PUBLIC;
			case DEFAULT:
			default:
				return Visibility.DEFAULT;
			
		}
	}

	public static MetaTagAttributeValue<?> getMetaTagAttributeValue(Param param) {
		if (param.getValue() instanceof Boolean) {
			return new BooleanAttributeValue(new ActionScriptSymbolName(param.getName()), (Boolean) param.getValue());
		}
		return  null;
	}
}
