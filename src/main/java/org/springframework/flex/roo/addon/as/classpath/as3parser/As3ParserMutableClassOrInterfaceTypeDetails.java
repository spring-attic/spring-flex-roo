package org.springframework.flex.roo.addon.as.classpath.as3parser;

import java.util.ArrayList;
import java.util.List;

import org.springframework.flex.roo.addon.as.classpath.ASPhysicalTypeCategory;
import org.springframework.flex.roo.addon.as.classpath.ASPhysicalTypeMetadataProvider;
import org.springframework.flex.roo.addon.as.classpath.details.ASMutableClassOrInterfaceTypeDetails;
import org.springframework.flex.roo.addon.as.classpath.details.ConstructorMetadata;
import org.springframework.flex.roo.addon.as.classpath.details.FieldMetadata;
import org.springframework.flex.roo.addon.as.classpath.details.MetaTagMetadata;
import org.springframework.flex.roo.addon.as.classpath.details.MethodMetadata;
import org.springframework.flex.roo.addon.as.model.ActionScriptPackage;
import org.springframework.flex.roo.addon.as.model.ActionScriptSymbolName;
import org.springframework.flex.roo.addon.as.model.ActionScriptType;

import org.springframework.roo.metadata.MetadataService;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.process.manager.FileManager;
import org.springframework.roo.support.util.Assert;

import uk.co.badgersinfoil.metaas.dom.ASClassType;
import uk.co.badgersinfoil.metaas.dom.ASCompilationUnit;
import uk.co.badgersinfoil.metaas.dom.ASField;
import uk.co.badgersinfoil.metaas.dom.ASInterfaceType;
import uk.co.badgersinfoil.metaas.dom.ASMetaTag;
import uk.co.badgersinfoil.metaas.dom.ASMethod;
import uk.co.badgersinfoil.metaas.dom.ASPackage;
import uk.co.badgersinfoil.metaas.dom.ASType;

public class As3ParserMutableClassOrInterfaceTypeDetails implements
		ASMutableClassOrInterfaceTypeDetails, CompilationUnitServices {

	// passed into constructor
	private FileManager fileManager;
	
	// computed from constructor
	private String fileIdentifier;
	
	private String declaredByMetadataId;
	
	// to satisfy interface
	private ActionScriptType name;
	private ASPhysicalTypeCategory physicalTypeCategory;
	private List<ConstructorMetadata> declaredConstructors = new ArrayList<ConstructorMetadata>();
	private List<FieldMetadata> declaredFields = new ArrayList<FieldMetadata>();
	private List<MethodMetadata> declaredMethods = new ArrayList<MethodMetadata>();
	private ASMutableClassOrInterfaceTypeDetails superclass = null;
	private ActionScriptType superType = null;
	private List<ActionScriptType> implementsTypes = new ArrayList<ActionScriptType>();
	private List<MetaTagMetadata> typeMetaTags = new ArrayList<MetaTagMetadata>();
	
	// internal use
	private ASCompilationUnit compilationUnit;
	private List<String> imports;
	private ActionScriptPackage compilationUnitPackage;
	private ASType clazz;
	
	public As3ParserMutableClassOrInterfaceTypeDetails(ASCompilationUnit compilationUnit, FileManager fileManager, String declaredByMetadataId, String fileIdentifier, ActionScriptType typeName, MetadataService metadataService, ASPhysicalTypeMetadataProvider physicalTypeMetadataProvider) {
		Assert.notNull(compilationUnit, "Compilation unit required");
		Assert.notNull(fileManager, "File manager requried");
		Assert.notNull(declaredByMetadataId, "Declared by metadata ID required");
		Assert.notNull(fileIdentifier, "File identifier (canonical path) required");
		Assert.notNull(typeName, "Name required");
		Assert.notNull(metadataService, "Metadata service required");
		Assert.notNull(physicalTypeMetadataProvider, "Physical type metadata provider required");
		
		this.name = typeName;
		
		this.declaredByMetadataId = declaredByMetadataId;
		this.fileManager = fileManager;
		
		this.fileIdentifier = fileIdentifier;
		
		this.compilationUnit = compilationUnit;
		
		imports = compilationUnit.getPackage().findImports();
		if (imports == null) {
			imports = new ArrayList<String>();
		}
		
		compilationUnitPackage = typeName.getPackage();
		
		Assert.notNull(compilationUnit.getType(), "No types in compilation unit, so unable to continue parsing");
		
		this.clazz = compilationUnit.getType();

		// Determine the type name
		ActionScriptType newName = As3ParserUtils.getActionScriptType(compilationUnitPackage, imports, this.clazz);
		
		// Revert back to the original type name (thus avoiding unnecessary inferences about java.lang types; see ROO-244)
		//TODO - is this necessary for us?
		this.name = new ActionScriptType(this.name.getFullyQualifiedTypeName(), newName.getArray(), newName.getDataType());
		
		if (this.clazz instanceof ASInterfaceType) {
			physicalTypeCategory = ASPhysicalTypeCategory.INTERFACE;
		} else {
			physicalTypeCategory = ASPhysicalTypeCategory.CLASS;
		}
		
		// Verify the package declaration appears to be correct
		Assert.isTrue(compilationUnitPackage.equals(name.getPackage()), "Compilation unit package '" + compilationUnitPackage + "' unexpected for type '" + name.getPackage() + "'");
		
		//TODO - populate superclass, supertype, and implementsTypes
		
		List<ASMetaTag> metaTagList = this.clazz.getAllMetaTags();
		if (metaTagList != null) {
			for (ASMetaTag metaTag : metaTagList) {
				As3ParserMetaTagMetadata md = new As3ParserMetaTagMetadata(metaTag, this);
				typeMetaTags.add(md);
			}
		}
		
		for (ASMethod method : ((List<ASMethod>)this.clazz.getMethods())) {
			declaredMethods.add(new As3ParserMethodMetadata(declaredByMetadataId, method, this));
		}
		
		//TODO - is it possible to find constructors???
		if (physicalTypeCategory == ASPhysicalTypeCategory.CLASS) {
			ASClassType clazzType = (ASClassType) this.clazz;
			
			for (ASField field : ((List<ASField>)clazzType.getFields()) ) {
				declaredFields.add(new As3ParserFieldMetadata(declaredByMetadataId, field, this));
			}
		}
		
	}
	
	public void addField(FieldMetadata fieldMetadata) {
		Assert.isInstanceOf(ASClassType.class, this.clazz, "Cannot add a field to an interface");
		As3ParserFieldMetadata.addField(this, ((ASClassType)this.clazz), fieldMetadata, true);
	}

	public void addMethod(MethodMetadata methodMetadata) {
		// TODO Auto-generated method stub

	}

	public void addTypeMetaTag(MetaTagMetadata metaTag) {
		// TODO Auto-generated method stub

	}

	public String getDeclaredByMetadataId() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<? extends ConstructorMetadata> getDeclaredConstructors() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<? extends FieldMetadata> getDeclaredFields() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<? extends MethodMetadata> getDeclaredMethods() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ActionScriptType> getImplementsTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	public ActionScriptType getSuperType() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<? extends MetaTagMetadata> getTypeMetaTags() {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeField(ActionScriptSymbolName fieldName) {
		// TODO Auto-generated method stub

	}

	public void removeTypeAnnotation(String name) {
		// TODO Auto-generated method stub

	}

	public ActionScriptType getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public void flush() {
		// TODO Auto-generated method stub

	}

	public ActionScriptPackage getCompilationUnitPackage() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getImports() {
		// TODO Auto-generated method stub
		return null;
	}

	public ASPhysicalTypeCategory getPhysicalTypeCategory() {
		// TODO Auto-generated method stub
		return null;
	}

}
