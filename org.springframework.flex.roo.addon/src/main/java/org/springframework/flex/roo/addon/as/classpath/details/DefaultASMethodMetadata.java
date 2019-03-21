/*
 * Copyright 2002-2010 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   https://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.flex.roo.addon.as.classpath.details;

import java.util.List;

import org.springframework.flex.roo.addon.as.classpath.details.metatag.ASMetaTagMetadata;
import org.springframework.flex.roo.addon.as.model.ASTypeVisibility;
import org.springframework.flex.roo.addon.as.model.ActionScriptSymbolName;
import org.springframework.flex.roo.addon.as.model.ActionScriptType;
import org.springframework.roo.support.util.Assert;

/**
 * Default metadata representation of an ActionScript method.
 *
 * @author Jeremy Grelle
 */
public class DefaultASMethodMetadata extends AbstractInvocableMemberMetadata implements ASMethodMetadata {

    private final ActionScriptSymbolName methodName;

    private final ActionScriptType returnType;

    public DefaultASMethodMetadata(String declaredByMetadataId, ActionScriptSymbolName methodName, ActionScriptType returnType,
        ASTypeVisibility visibility, String methodBody, List<ASMetaTagMetadata> metaTags, List<ActionScriptType> paramTypes,
        List<ActionScriptSymbolName> paramNames) {
        super(declaredByMetadataId, methodBody, metaTags, paramTypes, paramNames, visibility);
        Assert.notNull(methodName, "Method name is required.");
        Assert.notNull(returnType, "Return type is require.");

        this.methodName = methodName;
        this.returnType = returnType;
    }

    public DefaultASMethodMetadata(String declaredByMetadataId, ActionScriptSymbolName methodName, ActionScriptType returnType,
        ASTypeVisibility visibility) {
        this(declaredByMetadataId, methodName, returnType, visibility, null, null, null, null);
    }

    public ActionScriptSymbolName getMethodName() {
        return this.methodName;
    }

    public ActionScriptType getReturnType() {
        return this.returnType;
    }
}
