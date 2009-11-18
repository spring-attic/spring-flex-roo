package org.springframework.flex.roo.addon.as.classpath.as3parser;

import java.util.ArrayList;
import java.util.List;

import org.springframework.flex.roo.addon.as.classpath.details.FieldMetadata;
import org.springframework.flex.roo.addon.as.classpath.details.MetaTagMetadata;
import org.springframework.flex.roo.addon.as.model.ActionScriptSymbolName;
import org.springframework.flex.roo.addon.as.model.ActionScriptType;
import org.springframework.roo.support.util.Assert;

import uk.co.badgersinfoil.metaas.dom.ASClassType;
import uk.co.badgersinfoil.metaas.dom.ASField;
import uk.co.badgersinfoil.metaas.dom.Visibility;

public class As3ParserFieldMetadata implements FieldMetadata {

	private ActionScriptType fieldType;
	private ActionScriptSymbolName fieldName;
	private List<MetaTagMetadata> metaTags = new ArrayList<MetaTagMetadata>();
	private String declaredByMetadataId;
	//TODO - store visibility
	
	public As3ParserFieldMetadata(
			String declaredByMetadataId,
			ASField field,
			CompilationUnitServices compilationUnitServices) {
		Assert.notNull(declaredByMetadataId, "Declared by metadata ID required");
		Assert.notNull(field, "ActionScript field is required");
		Assert.notNull(compilationUnitServices, "Compilation unit services are required");
		
		this.declaredByMetadataId = declaredByMetadataId;
		
		this.fieldType = As3ParserUtils.getActionScriptType(compilationUnitServices.getCompilationUnitPackage(), compilationUnitServices.getImports(), field.getType());
	}

	public static void addField(
			CompilationUnitServices compilationUnitServices, 
			ASClassType clazz, FieldMetadata field, boolean permitFlush) {
		
		Assert.notNull(compilationUnitServices, "Compilation unit services required");
		Assert.notNull(clazz, "Class required");
		Assert.notNull(field, "Field required");
		
		// Import the field type into the compilation unit
		As3ParserUtils.importTypeIfRequired(compilationUnitServices.getCompilationUnitPackage(), compilationUnitServices.getImports(), field.getFieldType());
		
		//TODO - Implement proper visibility semantics
		ASField newField = clazz.newField(field.getFieldName().getSymbolName(), Visibility.PUBLIC, field.getFieldType().getSimpleTypeName());
		
		for(MetaTagMetadata metaTag : field.getMetaTags()) {
			As3ParserMetaTagMetadata.addMetaTagElement(metaTag, newField, false);
		}
	}

	public ActionScriptSymbolName getFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	public ActionScriptType getFieldType() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<MetaTagMetadata> getMetaTags() {
		// TODO Auto-generated method stub
		return null;
	}

}
