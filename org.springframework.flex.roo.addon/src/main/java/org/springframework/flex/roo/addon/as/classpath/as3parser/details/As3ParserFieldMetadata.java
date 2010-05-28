package org.springframework.flex.roo.addon.as.classpath.as3parser.details;

import java.util.ArrayList;
import java.util.List;

import org.springframework.flex.roo.addon.as.classpath.as3parser.As3ParserUtils;
import org.springframework.flex.roo.addon.as.classpath.as3parser.CompilationUnitServices;
import org.springframework.flex.roo.addon.as.classpath.details.FieldMetadata;
import org.springframework.flex.roo.addon.as.classpath.details.metatag.MetaTagMetadata;
import org.springframework.flex.roo.addon.as.model.ASTypeVisibility;
import org.springframework.flex.roo.addon.as.model.ActionScriptSymbolName;
import org.springframework.flex.roo.addon.as.model.ActionScriptType;
import org.springframework.roo.support.util.Assert;

import uk.co.badgersinfoil.metaas.dom.ASClassType;
import uk.co.badgersinfoil.metaas.dom.ASField;
import uk.co.badgersinfoil.metaas.dom.ASMetaTag;

public class As3ParserFieldMetadata implements FieldMetadata {

	private ActionScriptType fieldType;
	private ActionScriptSymbolName fieldName;
	private ASTypeVisibility visibility;
	private List<MetaTagMetadata> metaTags = new ArrayList<MetaTagMetadata>();
	private String declaredByMetadataId;
	
	@SuppressWarnings("unchecked")
	public As3ParserFieldMetadata(
			String declaredByMetadataId,
			ASField field,
			CompilationUnitServices compilationUnitServices) {
		Assert.notNull(declaredByMetadataId, "Declared by metadata ID required");
		Assert.notNull(field, "ActionScript field is required");
		Assert.notNull(compilationUnitServices, "Compilation unit services are required");
		
		this.declaredByMetadataId = declaredByMetadataId;
		
		this.fieldType = As3ParserUtils.getActionScriptType(compilationUnitServices.getCompilationUnitPackage(), compilationUnitServices.getImports(), field.getType());
		this.fieldName = new ActionScriptSymbolName(field.getName());
		this.visibility = As3ParserUtils.getASTypeVisibility(field.getVisibility());

		for(ASMetaTag metaTag : (List<ASMetaTag>)field.getAllMetaTags()) {
			metaTags.add(new As3ParserMetaTagMetadata(metaTag));
		}
	}

	public String getDeclaredByMetadataId() {
		return declaredByMetadataId;
	}

	public ActionScriptSymbolName getFieldName() {
		return fieldName;
	}

	public ActionScriptType getFieldType() {
		return fieldType;
	}

	public List<MetaTagMetadata> getMetaTags() {
		return metaTags;
	}

	public ASTypeVisibility getVisibility() {
		return visibility;
	}
	
	public static void addField(CompilationUnitServices compilationUnitServices, 
			ASClassType clazz, FieldMetadata field, boolean permitFlush) {
		
		Assert.notNull(compilationUnitServices, "Compilation unit services required");
		Assert.notNull(clazz, "Class required");
		Assert.notNull(field, "Field required");
		
		// Import the field type into the compilation unit
		As3ParserUtils.importTypeIfRequired(compilationUnitServices, field.getFieldType());
		
		// Add the field
		ASField newField = clazz.newField(field.getFieldName().getSymbolName(), As3ParserUtils.getAs3ParserVisiblity(field.getVisibility()), field.getFieldType().getSimpleTypeName());
		
		// Add meta tags to the field
		for(MetaTagMetadata metaTag : field.getMetaTags()) {
			As3ParserMetaTagMetadata.addMetaTagToElement(compilationUnitServices, metaTag, newField, false);
		}
		
		if (permitFlush) {
			compilationUnitServices.flush();
		}
	}
	
	public static void removeField(CompilationUnitServices compilationUnitServices, ASClassType clazz, ActionScriptSymbolName fieldName) {
		Assert.notNull(compilationUnitServices, "Compilation unit services required");
		Assert.notNull(clazz, "Class required");
		Assert.notNull(fieldName, "Field name required");
		
		Assert.notNull(clazz.getField(fieldName.getSymbolName()), "Could not locate field '" + fieldName + "' to delete");
		
		clazz.removeField(fieldName.getSymbolName());
		
		compilationUnitServices.flush();
	}
}
