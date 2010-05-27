package org.springframework.flex.roo.addon.as.classpath.as3parser;

import java.util.ArrayList;
import java.util.List;

import org.springframework.flex.roo.addon.as.classpath.ASPhysicalTypeCategory;
import org.springframework.flex.roo.addon.as.classpath.ASPhysicalTypeMetadata;
import org.springframework.flex.roo.addon.as.classpath.ASPhysicalTypeMetadataProvider;
import org.springframework.flex.roo.addon.as.classpath.as3parser.details.As3ParserConstructorMetadata;
import org.springframework.flex.roo.addon.as.classpath.as3parser.details.As3ParserFieldMetadata;
import org.springframework.flex.roo.addon.as.classpath.as3parser.details.As3ParserMetaTagMetadata;
import org.springframework.flex.roo.addon.as.classpath.as3parser.details.As3ParserMethodMetadata;
import org.springframework.flex.roo.addon.as.classpath.details.ASClassOrInterfaceTypeDetails;
import org.springframework.flex.roo.addon.as.classpath.details.ASMemberHoldingTypeDetails;
import org.springframework.flex.roo.addon.as.classpath.details.ASMutableClassOrInterfaceTypeDetails;
import org.springframework.flex.roo.addon.as.classpath.details.ConstructorMetadata;
import org.springframework.flex.roo.addon.as.classpath.details.FieldMetadata;
import org.springframework.flex.roo.addon.as.classpath.details.MethodMetadata;
import org.springframework.flex.roo.addon.as.classpath.details.metatag.MetaTagMetadata;
import org.springframework.flex.roo.addon.as.model.ActionScriptPackage;
import org.springframework.flex.roo.addon.as.model.ActionScriptSymbolName;
import org.springframework.flex.roo.addon.as.model.ActionScriptType;

import org.springframework.roo.metadata.MetadataService;
import org.springframework.roo.process.manager.FileManager;
import org.springframework.roo.support.util.Assert;
import org.springframework.roo.support.util.CollectionUtils;
import org.springframework.roo.support.util.StringUtils;

import uk.co.badgersinfoil.metaas.dom.ASClassType;
import uk.co.badgersinfoil.metaas.dom.ASCompilationUnit;
import uk.co.badgersinfoil.metaas.dom.ASField;
import uk.co.badgersinfoil.metaas.dom.ASInterfaceType;
import uk.co.badgersinfoil.metaas.dom.ASMetaTag;
import uk.co.badgersinfoil.metaas.dom.ASMethod;
import uk.co.badgersinfoil.metaas.dom.ASType;

public class As3ParserMutableClassOrInterfaceTypeDetails implements
		ASMutableClassOrInterfaceTypeDetails, CompilationUnitServices {

	// passed into constructor
	private FileManager fileManager;
	private String fileIdentifier;
	private String declaredByMetadataId;
	
	// to satisfy interface
	private ActionScriptType name;
	private ASPhysicalTypeCategory physicalTypeCategory;
	private List<ConstructorMetadata> declaredConstructors = new ArrayList<ConstructorMetadata>();
	private List<FieldMetadata> declaredFields = new ArrayList<FieldMetadata>();
	private List<MethodMetadata> declaredMethods = new ArrayList<MethodMetadata>();
	private ASClassOrInterfaceTypeDetails superclass = null;
	private List<ActionScriptType> extendsTypes = new ArrayList<ActionScriptType>();
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
		//ActionScriptType newName = As3ParserUtils.getActionScriptType(compilationUnitPackage, imports, this.clazz);
		
		// Revert back to the original type name (thus avoiding unnecessary inferences about java.lang types; see ROO-244)
		//TODO - is this necessary for us?
		//this.name = new ActionScriptType(this.name.getFullyQualifiedTypeName(), newName.getArray(), newName.getDataType());
		
		if (this.clazz instanceof ASInterfaceType) {
			physicalTypeCategory = ASPhysicalTypeCategory.INTERFACE;
		} else {
			physicalTypeCategory = ASPhysicalTypeCategory.CLASS;
		}
		
		// Verify the package declaration appears to be correct
		Assert.isTrue(compilationUnitPackage.equals(name.getPackage()), "Compilation unit package '" + compilationUnitPackage + "' unexpected for type '" + name.getPackage() + "'");
		
		if (this.clazz instanceof ASClassType) {
			ASClassType classDef = (ASClassType) this.clazz;
			if (StringUtils.hasLength(classDef.getSuperclass())) {
				ActionScriptType superType = As3ParserUtils.getActionScriptType(compilationUnitPackage, imports, classDef.getSuperclass());
				this.extendsTypes.add(superType);
				String superclassId = physicalTypeMetadataProvider.findIdentifier(superType);
				ASPhysicalTypeMetadata superPtm = null;
				if (superclassId != null) {
					superPtm = (ASPhysicalTypeMetadata) metadataService.get(superclassId);
				}
				if (superPtm != null && superPtm.getPhysicalTypeDetails() != null && superPtm.getPhysicalTypeDetails() instanceof ASClassOrInterfaceTypeDetails) {
					this.superclass = (ASClassOrInterfaceTypeDetails) superPtm.getPhysicalTypeDetails();
				}
			}
			if (!CollectionUtils.isEmpty(classDef.getImplementedInterfaces())) {
				List<String> interfaces = classDef.getImplementedInterfaces();
				for(String interfaceName : interfaces) {
					this.implementsTypes.add(As3ParserUtils.getActionScriptType(compilationUnitPackage, imports, interfaceName));
				}
			}
		} else if (this.clazz instanceof ASInterfaceType) {
			ASInterfaceType interfaceDef = (ASInterfaceType) this.clazz;
			if (!CollectionUtils.isEmpty(interfaceDef.getSuperInterfaces())) {
				List<String> superInterfaces = interfaceDef.getSuperInterfaces();
				for(String superInterface : superInterfaces) {
					this.extendsTypes.add(As3ParserUtils.getActionScriptType(compilationUnitPackage, imports, superInterface));
				}
			}
		}
		
		List<ASMetaTag> metaTagList = this.clazz.getAllMetaTags();
		if (metaTagList != null) {
			for (ASMetaTag metaTag : metaTagList) {
				As3ParserMetaTagMetadata md = new As3ParserMetaTagMetadata(metaTag);
				typeMetaTags.add(md);
			}
		}
		
		for (ASMethod method : ((List<ASMethod>)this.clazz.getMethods())) {
			if (method.getName().equals(name.getSimpleTypeName())) {
				declaredConstructors.add(new As3ParserConstructorMetadata(declaredByMetadataId, method, this));
			} else {
				declaredMethods.add(new As3ParserMethodMetadata(declaredByMetadataId, method, this));
			}
		}
		
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
		return this.declaredByMetadataId;
	}

	public List<? extends ConstructorMetadata> getDeclaredConstructors() {
		return this.declaredConstructors;
	}

	public List<? extends FieldMetadata> getDeclaredFields() {
		return this.declaredFields;
	}

	public List<? extends MethodMetadata> getDeclaredMethods() {
		return this.declaredMethods;
	}
	
	public List<ActionScriptType> getExtendsTypes() {
		return this.extendsTypes;
	}

	public List<ActionScriptType> getImplementsTypes() {
		return this.implementsTypes;
	}

	public List<? extends MetaTagMetadata> getTypeMetaTags() {
		return this.typeMetaTags;
	}

	public void removeField(ActionScriptSymbolName fieldName) {
		// TODO Auto-generated method stub

	}

	public void removeTypeAnnotation(String name) {
		// TODO Auto-generated method stub

	}

	public ActionScriptType getName() {
		return this.name;
	}

	public void flush() {
		// TODO Auto-generated method stub

	}

	public ActionScriptPackage getCompilationUnitPackage() {
		return this.compilationUnitPackage;
	}

	public List<String> getImports() {
		return this.imports;
	}

	public ASPhysicalTypeCategory getPhysicalTypeCategory() {
		return this.physicalTypeCategory;
	}

	public static final void createType(FileManager fileManager, final ASClassOrInterfaceTypeDetails cit, String fileIdentifier) {
		Assert.notNull(fileManager, "File manager required");
		Assert.notNull(cit, "Class or interface type details required");
		Assert.hasText(fileIdentifier, "File identifier required");
		
		final String newContents = getOutput(cit);

		fileManager.createOrUpdateTextFileIfRequired(fileIdentifier, newContents);
	}
	
	public static final String getOutput(final ASClassOrInterfaceTypeDetails cit) {
		return "";
	}
	
	public ASClassOrInterfaceTypeDetails getSuperClass() {
		return this.superclass;
	}
}
