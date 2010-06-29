package org.springframework.flex.roo.addon;

import org.springframework.roo.model.JavaPackage;
import org.springframework.roo.model.JavaType;

public interface FlexOperations {

	void generateAll(JavaPackage javaPackage);
	
	void createRemotingDestination(JavaType service, JavaType entity);

	void installFlex();

	boolean isFlexAvailable();

	void createFlexCompilerConfig();

	void createScaffoldApp();

}