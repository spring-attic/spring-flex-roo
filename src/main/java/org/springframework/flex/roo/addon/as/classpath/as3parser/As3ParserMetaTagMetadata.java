package org.springframework.flex.roo.addon.as.classpath.as3parser;

import org.springframework.flex.roo.addon.as.classpath.details.MetaTagMetadata;
import org.springframework.roo.support.util.Assert;

import uk.co.badgersinfoil.metaas.dom.ASMetaTag;
import uk.co.badgersinfoil.metaas.dom.MetaTagable;

public class As3ParserMetaTagMetadata implements MetaTagMetadata {
	
	//provided
	private ASMetaTag metaTag;
	private CompilationUnitServices compilationUnitServices;

	//TODO - Support parameters in meta tags
	
	public As3ParserMetaTagMetadata(
			ASMetaTag metaTag,
			CompilationUnitServices compilationUnitServices) {
		Assert.notNull(metaTag, "Meta Tag required");
		Assert.notNull(compilationUnitServices, "Compilation unit services required");
		
		// Store required source information for subsequent mutability support
		this.metaTag = metaTag;
		this.compilationUnitServices = compilationUnitServices;
	}

	public static void addMetaTagElement(MetaTagMetadata metaTag,
			MetaTagable element, boolean permitFlush) {
		
		
	}

	public String getName() {
		return metaTag.getName();
	}

}
