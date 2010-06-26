package org.springframework.flex.roo.addon;

import org.springframework.roo.model.JavaType;

public interface FlexOperations {

	void createRemotingDestination(JavaType service, JavaType entity);

	void installFlex();

	boolean isFlexAvailable();

	void createFlexCompilerConfig();

	void createScaffoldApp();

}