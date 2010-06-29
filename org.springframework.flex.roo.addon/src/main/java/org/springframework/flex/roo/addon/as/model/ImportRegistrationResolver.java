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

import java.util.Set;

/**
 * Represents the known imports for a particular compilation unit, and resolves whether a particular type
 * name can be expressed as a simple type name or requires a fully-qualified type name.
 * 
 * @author Jeremy Grelle
 * @since 1.0
 *
 */
public interface ImportRegistrationResolver {
	
	/**
	 * @return the package this compilation unit belongs to (never null)
	 */
	ActionScriptPackage getCompilationUnitPackage();
	
	/**
	 * Determines whether the presented {@link ActionScriptType} must be used in a fully-qualified form or not.
	 * It may only be used in simple form if:
	 * 
	 * <ul>
	 * <li>it is of {@link DataType#VARIABLE}; or</li>
	 * <li>it is already registered as an import; or</li>
	 * <li>it is in the same package as the compilation unit; or</li>
	 * <li>it is in the top-level package</li>
	 * </ul>
	 * 
	 * <p>
	 * Note that advanced implementations may be able to determine all types available in a particular package, 
	 * but this is not required.
	 * 
	 * @param aactionScriptType to lookup (required)
	 * @return true if a fully-qualified form must be used, or false if a simple form can be used 
	 */
	boolean isFullyQualifiedFormRequired(ActionScriptType actionScriptType);
	
	/**
	 * Automatically invokes {@link #isAdditionLegal(ActionScriptType)}, then {@link #addImport(ActionScriptType)}, and
	 * finally {@link #isFullyQualifiedFormRequired(ActionScriptType)}, returning the result of the final method.
	 * This method is the main method that should be used by callers, as it will automatically attempt to
	 * cause a {@link ActionScriptType} to be used in its simple form if at all possible.
	 * 
	 * @param ActionScriptType to automatically register (if possible) and lookup whether simplified used is available (required)
	 * @return true if a fully-qualified form must be used, or false if a simple form can be used 
	 */
	boolean isFullyQualifiedFormRequiredAfterAutoImport(ActionScriptType ActionScriptType);
	
	/**
	 * Indicates whether the presented {@link ActionScriptType} can be legally presented to {@link #addImport(ActionScriptType)}.
	 * It is considered legal only if the presented {@link ActionScriptType} is of type {@link ASDataType#TYPE},
	 * there is not an existing conflicting registered import, and the proposed type is not within the default
	 * package. Note it is legal to add types from the same package as the compilation unit, and indeed may be 
	 * required by implementations that are otherwise unaware of all the types available in a particular package.
	 * 
	 * @param ActionScriptType
	 * @return
	 */
	boolean isAdditionLegal(ActionScriptType ActionScriptType);

	/**
	 * Explicitly registers an import. Note that no verification will be performed to ensure an import is legal or
	 * does not conflict with an existing import (use {@link #isAdditionLegal(ActionScriptType)} for verification).
	 * 
	 * @param ActionScriptType to register (required)
	 */
	void addImport(ActionScriptType ActionScriptType);
	
	/**
	 * Provides access to the registered imports.
	 * 
	 * @return an unmodifiable representation of all registered imports (never null, but may be empty)
	 */
	Set<ActionScriptType> getRegisteredImports();

}
