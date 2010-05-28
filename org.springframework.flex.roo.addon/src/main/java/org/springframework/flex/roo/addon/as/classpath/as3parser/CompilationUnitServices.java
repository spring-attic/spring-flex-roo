package org.springframework.flex.roo.addon.as.classpath.as3parser;

import java.util.List;

import org.springframework.flex.roo.addon.as.model.ActionScriptPackage;

public interface CompilationUnitServices {

	List<String> getImports();
	
	void addImport(String fullyQualifiedTypeName);

	ActionScriptPackage getCompilationUnitPackage();

	/**
	 * Forces the implementation to flush any changes.
	 */
	void flush();
}
