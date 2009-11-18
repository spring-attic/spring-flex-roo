package org.springframework.flex.roo.addon.as.classpath.as3parser;

import java.util.List;

import org.springframework.flex.roo.addon.as.model.ActionScriptPackage;
import org.springframework.flex.roo.addon.as.model.ActionScriptType;
import org.springframework.roo.support.util.Assert;

import uk.co.badgersinfoil.metaas.ActionScriptFactory;
import uk.co.badgersinfoil.metaas.dom.ASType;

public class As3ParserUtils {
	
	//private static final ActionScriptFactory factory = new ActionScriptFactory();

	public static final ActionScriptType getActionScriptType(ActionScriptPackage compilationUnitPackage, List<String> imports, ASType type) {
		Assert.notNull(imports, "Compilation unit imports required");
		Assert.notNull(compilationUnitPackage, "Compilation unit package required");
		Assert.notNull(type, "ASType required");
		
		// Convert the ASType name into a ActionScriptType
		ActionScriptType effectiveType = getActionScriptType(compilationUnitPackage, imports, type.getName());
		
		return new ActionScriptType(effectiveType.getFullyQualifiedTypeName(), effectiveType.getArray(), effectiveType.getDataType());
	}
	
	public static final ActionScriptType getActionScriptType(ActionScriptPackage compilationUnitPackage, List<String> imports, String nameToFind) {
		Assert.notNull(imports, "Compilation unit imports required");
		Assert.notNull(compilationUnitPackage, "Compilation unit package required");
		Assert.notNull(nameToFind, "Name to find is required");
		
		int offset = nameToFind.lastIndexOf('.');
		if (offset > -1) {
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
}
