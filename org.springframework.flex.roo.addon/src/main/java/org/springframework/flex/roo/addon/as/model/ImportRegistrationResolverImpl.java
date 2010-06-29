/*
 * Copyright 2002-2010 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.flex.roo.addon.as.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.roo.support.util.Assert;
//TODO - Not actually sure we need this...only here as a result of the Java porting process...not currently used.
public class ImportRegistrationResolverImpl implements ImportRegistrationResolver {

	private Set<ActionScriptType> registeredImports = new HashSet<ActionScriptType>();
	private ActionScriptPackage compilationUnitPackage;
	
	public ImportRegistrationResolverImpl(ActionScriptPackage compilationUnitPackage) {
		Assert.notNull(compilationUnitPackage, "Compilation unit package required");
		this.compilationUnitPackage = compilationUnitPackage;
	}

	public void addImport(ActionScriptType actionScriptType) {
		Assert.notNull(actionScriptType, "ActionScript type required");
		registeredImports.add(actionScriptType);
	}

	public ActionScriptPackage getCompilationUnitPackage() {
		return compilationUnitPackage;
	}

	public Set<ActionScriptType> getRegisteredImports() {
		return Collections.unmodifiableSet(registeredImports);
	}

	public boolean isAdditionLegal(ActionScriptType actionScriptType) {
		Assert.notNull(actionScriptType, "ActionScript type required");
		
		if (actionScriptType.getDataType() != ASDataType.TYPE) {
			// It's a type variable or primitive
			return false;
		}
		
		if (actionScriptType.isDefaultPackage()) {
			// Cannot import types from the default package
			return false;
		}
		
		// Must be a class, so it's legal if there isn't an existing registration that conflicts
		for (ActionScriptType candidate : registeredImports) {
			if (candidate.getSimpleTypeName().equals(actionScriptType.getSimpleTypeName())) {
				// conflict detected
				return false;
			}
		}

		return true;
	}
	
	public boolean isFullyQualifiedFormRequired(ActionScriptType actionScriptType) {
		Assert.notNull(actionScriptType, "ActionScript type required");
		
		if (actionScriptType.getDataType() == ASDataType.VARIABLE || actionScriptType.isDefaultPackage()) {
			return false;
		}

		if (registeredImports.contains(actionScriptType)) {
			// Already know about this one
			return false;
		}
		
		if (compilationUnitPackage.equals(actionScriptType.getPackage())) {
			// No need for an explicit registration, given it's in the same package
			return false;
		}
		
		// To get this far, it must need a fully-qualified name
		return true;
	}
	
	public boolean isFullyQualifiedFormRequiredAfterAutoImport(ActionScriptType actionScriptType) {
		Assert.notNull(actionScriptType, "ActionScript type required");
		
		// Try to add import if possible
		if (isAdditionLegal(actionScriptType)) {
			addImport(actionScriptType);
		}
		
		// Indicate whether we can use in a simple or need a fully-qualified form
		return isFullyQualifiedFormRequired(actionScriptType);
	}

}
