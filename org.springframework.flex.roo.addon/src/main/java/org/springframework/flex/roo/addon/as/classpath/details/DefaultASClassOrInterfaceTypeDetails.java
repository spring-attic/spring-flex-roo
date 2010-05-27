package org.springframework.flex.roo.addon.as.classpath.details;

import java.util.ArrayList;
import java.util.List;

import org.springframework.flex.roo.addon.as.classpath.ASPhysicalTypeCategory;
import org.springframework.flex.roo.addon.as.classpath.details.metatag.MetaTagMetadata;
import org.springframework.flex.roo.addon.as.model.ActionScriptType;
import org.springframework.roo.support.util.Assert;

public class DefaultASClassOrInterfaceTypeDetails implements
		ASClassOrInterfaceTypeDetails {

	private ActionScriptType name;
	private ASPhysicalTypeCategory physicalTypeCategory;
	private List<ConstructorMetadata> declaredConstructors = new ArrayList<ConstructorMetadata>();
	private List<FieldMetadata> declaredFields = new ArrayList<FieldMetadata>();
	private List<MethodMetadata> declaredMethods = new ArrayList<MethodMetadata>();
	private ASClassOrInterfaceTypeDetails superClass;
	private List<ActionScriptType> extendsTypes = new ArrayList<ActionScriptType>();
	private List<ActionScriptType> implementsTypes = new ArrayList<ActionScriptType>();
	private List<MetaTagMetadata> typeMetaTags = new ArrayList<MetaTagMetadata>();
	private String declaredByMetadataId;

	public DefaultASClassOrInterfaceTypeDetails(String declaredByMetadataId,
			ActionScriptType name, ASPhysicalTypeCategory physicalTypeCategory,
			List<MetaTagMetadata> typeMetaTags) {
		Assert
				.hasText(declaredByMetadataId,
						"Declared by metadata ID required");
		Assert.notNull(name, "Name required");
		Assert.notNull(physicalTypeCategory, "Physical type category required");

		this.declaredByMetadataId = declaredByMetadataId;
		this.name = name;
		this.physicalTypeCategory = physicalTypeCategory;

		if (typeMetaTags != null) {
			this.typeMetaTags = typeMetaTags;
		}
	}

	public DefaultASClassOrInterfaceTypeDetails(String declaredByMetadataId,
			ActionScriptType name, ASPhysicalTypeCategory physicalTypeCategory,
			List<ConstructorMetadata> declaredConstructors,
			List<FieldMetadata> declaredFields,
			List<MethodMetadata> declaredMethods,
			ASClassOrInterfaceTypeDetails superClass,
			List<ActionScriptType> extendsTypes,
			List<ActionScriptType> implementsTypes,
			List<MetaTagMetadata> typeMetaTags) {
		Assert
				.hasText(declaredByMetadataId,
						"Declared by metadata ID required");
		Assert.notNull(name, "Name required");
		Assert.notNull(physicalTypeCategory, "Physical type category required");

		this.declaredByMetadataId = declaredByMetadataId;
		this.name = name;
		this.physicalTypeCategory = physicalTypeCategory;
		this.superClass = superClass;

		if (declaredConstructors != null) {
			this.declaredConstructors = declaredConstructors;
		}

		if (declaredFields != null) {
			this.declaredFields = declaredFields;
		}

		if (declaredMethods != null) {
			this.declaredMethods = declaredMethods;
		}

		if (extendsTypes != null) {
			this.extendsTypes = extendsTypes;
		}

		if (implementsTypes != null) {
			this.implementsTypes = implementsTypes;
		}

		if (typeMetaTags != null) {
			this.typeMetaTags = typeMetaTags;
		}
	}

	public ActionScriptType getName() {
		return name;
	}

	public ASPhysicalTypeCategory getPhysicalTypeCategory() {
		return physicalTypeCategory;
	}

	public List<? extends ConstructorMetadata> getDeclaredConstructors() {
		return declaredConstructors;
	}

	public List<? extends FieldMetadata> getDeclaredFields() {
		return declaredFields;
	}

	public List<? extends MethodMetadata> getDeclaredMethods() {
		return declaredMethods;
	}

	public ASClassOrInterfaceTypeDetails getSuperClass() {
		return superClass;
	}

	public List<ActionScriptType> getExtendsTypes() {
		return extendsTypes;
	}

	public List<ActionScriptType> getImplementsTypes() {
		return implementsTypes;
	}

	public List<? extends MetaTagMetadata> getTypeMetaTags() {
		return typeMetaTags;
	}

	public String getDeclaredByMetadataId() {
		return declaredByMetadataId;
	}

}
