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

package org.springframework.flex.roo.addon;

import org.springframework.roo.model.JavaPackage;
import org.springframework.roo.model.JavaType;

/**
 * Operations for manipulating Flex projects.
 *
 * @author Jeremy Grelle
 */
public interface FlexOperations {

    void generateAll(JavaPackage javaPackage);

    void createRemotingDestination(JavaType service, JavaType entity);

    void installFlex();

    boolean isFlexAvailable();

    void createFlexCompilerConfig();

    void createScaffoldApp();

    boolean isFlexConfigured();

}