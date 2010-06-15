package org.springframework.flex.roo.addon.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;
import org.springframework.flex.roo.addon.FlexScaffoldMetadata;
import org.springframework.flex.roo.addon.as.classpath.ASMutablePhysicalTypeMetadataProvider;
import org.springframework.flex.roo.addon.as.classpath.details.ASFieldMetadata;
import org.springframework.flex.roo.addon.as.model.ActionScriptMappingUtils;
import org.springframework.flex.roo.addon.as.model.ActionScriptType;
import org.springframework.flex.roo.addon.mojos.FlexPath;
import org.springframework.flex.roo.addon.mojos.FlexPathResolver;
import org.springframework.roo.addon.beaninfo.BeanInfoMetadata;
import org.springframework.roo.classpath.details.FieldMetadata;
import org.springframework.roo.classpath.details.MemberFindingUtils;
import org.springframework.roo.classpath.details.MethodMetadata;
import org.springframework.roo.classpath.details.annotations.AnnotationAttributeValue;
import org.springframework.roo.classpath.details.annotations.AnnotationMetadata;
import org.springframework.roo.metadata.MetadataDependencyRegistry;
import org.springframework.roo.metadata.MetadataIdentificationUtils;
import org.springframework.roo.metadata.MetadataItem;
import org.springframework.roo.metadata.MetadataNotificationListener;
import org.springframework.roo.metadata.MetadataProvider;
import org.springframework.roo.metadata.MetadataService;
import org.springframework.roo.model.JavaSymbolName;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.process.manager.FileManager;
import org.springframework.roo.process.manager.MutableFile;
import org.springframework.roo.project.Path;
import org.springframework.roo.project.ProjectMetadata;
import org.springframework.roo.support.util.Assert;
import org.springframework.roo.support.util.FileCopyUtils;
import org.springframework.roo.support.util.StringUtils;
import org.springframework.roo.support.util.XmlRoundTripUtils;
import org.springframework.roo.support.util.XmlUtils;
import org.w3c.dom.Document;

@Component(immediate=true)
@Service
public class FlexUIMetadataProvider implements MetadataProvider,
		MetadataNotificationListener {

	@Reference private FlexPathResolver flexPathResolver;
	@Reference private MetadataDependencyRegistry metadataDependencyRegistry;
	@Reference private FileManager fileManager;
	@Reference private MetadataService metadataService;
	@Reference private ASMutablePhysicalTypeMetadataProvider asPhysicalTypeProvider;
	
	private Map<JavaType, String> pluralCache;
	
	private StringTemplateGroup templateGroup;
	
	protected void activate(ComponentContext context) {
		metadataDependencyRegistry.registerDependency(FlexScaffoldMetadata.getMetadataIdentiferType(), getProvidesType());
		this.templateGroup = new StringTemplateGroup("flexUIMetadataTemplateGroup");
	}
	
	protected void deactivate(ComponentContext context) {
		metadataDependencyRegistry.deregisterDependency(FlexScaffoldMetadata.getMetadataIdentiferType(), getProvidesType());
	}
	
	//TODO - For now this is mainly being driven by the JavaType referenced by FlexScaffoldMetadata...should see if we can make
	//things happen in the right order to be able to consistently rely on the ActionScriptType instead
	public MetadataItem get(String metadataId) {
		pluralCache = new HashMap<JavaType, String>();
		
		JavaType javaType = FlexUIMetadata.getJavaType(metadataId);
		Path path = FlexUIMetadata.getPath(metadataId);
		String flexScaffoldMetadataKey = FlexScaffoldMetadata.createIdentifier(javaType, path);
		FlexScaffoldMetadata flexScaffoldMetadata = (FlexScaffoldMetadata) metadataService.get(flexScaffoldMetadataKey);
		ProjectMetadata projectMetadata = (ProjectMetadata) metadataService.get(ProjectMetadata.getProjectIdentifier());
		
		if (flexScaffoldMetadata == null || !flexScaffoldMetadata.isValid() || 
				projectMetadata == null || !projectMetadata.isValid()) {
			return null;
		}

		String presentationPackage = projectMetadata.getTopLevelPackage()+".presentation."+flexScaffoldMetadata.getEntityReference();
		
		// Install the root application MXML document if it doesn't already exist
		String scaffoldAppFileId = flexPathResolver.getIdentifier(FlexPath.SRC_MAIN_FLEX, projectMetadata.getProjectName()+"_scaffold.mxml");
		if (!fileManager.exists(scaffoldAppFileId)) {
			createScaffoldApp(scaffoldAppFileId, presentationPackage, flexScaffoldMetadata, projectMetadata);
		} else {
			//TODO - update the entity list if necessary
		}
		
		// Install the entity event class if it doesn't already exist
		ActionScriptType entityEventType = new ActionScriptType(presentationPackage + "." + flexScaffoldMetadata.getBeanInfoMetadata().getJavaBean().getSimpleTypeName() + "Event");
		if (!StringUtils.hasText(asPhysicalTypeProvider.findIdentifier(entityEventType))) {
			createEntityEventType(entityEventType, flexScaffoldMetadata);
		}
		
		List<FieldMetadata> elegibleFields = getElegibleFields(projectMetadata, flexScaffoldMetadata);
		
		// Create or update the list view
		String listViewRelativePath = (presentationPackage + "." + flexScaffoldMetadata.getBeanInfoMetadata().getJavaBean().getSimpleTypeName() + "View").replace('.', File.separatorChar)+".mxml";
		String listViewPath = flexPathResolver.getIdentifier(FlexPath.SRC_MAIN_FLEX, listViewRelativePath);
		writeToDiskIfNecessary(listViewPath, buildListViewDocument(flexScaffoldMetadata, elegibleFields));
		
		// Create or update the form view
		String formRelativePath = (presentationPackage + "." + flexScaffoldMetadata.getBeanInfoMetadata().getJavaBean().getSimpleTypeName() + "Form").replace('.', File.separatorChar)+".mxml";
		String formPath = flexPathResolver.getIdentifier(FlexPath.SRC_MAIN_FLEX, formRelativePath);
		writeToDiskIfNecessary(formPath, buildFormDocument(flexScaffoldMetadata, elegibleFields));
		
		// Add an entry to flex-config
		
		return new FlexUIMetadata(metadataId);
	}

	public String getProvidesType() {
		return FlexUIMetadata.getMetadataIdentifierType();
	}

	public void notify(String upstreamDependency, String downstreamDependency) {
		if (MetadataIdentificationUtils.isIdentifyingClass(downstreamDependency)) {
			Assert.isTrue(MetadataIdentificationUtils.getMetadataClass(upstreamDependency).equals(MetadataIdentificationUtils.getMetadataClass(FlexScaffoldMetadata.getMetadataIdentiferType())), "Expected class-level notifications only for web scaffold metadata (not '" + upstreamDependency + "')");
			
			// A physical Java type has changed, and determine what the corresponding local metadata identification string would have been
			JavaType javaType = FlexScaffoldMetadata.getJavaType(upstreamDependency);
			Path path = FlexScaffoldMetadata.getPath(upstreamDependency);
			downstreamDependency = FlexUIMetadata.createIdentifier(javaType, path);
			
			// We only need to proceed if the downstream dependency relationship is not already registered
			// (if it's already registered, the event will be delivered directly later on)
			if (metadataDependencyRegistry.getDownstream(upstreamDependency).contains(downstreamDependency)) {
				return;
			}
		}

		// We should now have an instance-specific "downstream dependency" that can be processed by this class
		Assert.isTrue(MetadataIdentificationUtils.getMetadataClass(downstreamDependency).equals(MetadataIdentificationUtils.getMetadataClass(getProvidesType())), "Unexpected downstream notification for '" + downstreamDependency + "' to this provider (which uses '" + getProvidesType() + "'");
		
		metadataService.evict(downstreamDependency);
		if (get(downstreamDependency) != null) {
			metadataDependencyRegistry.notifyDownstream(downstreamDependency);
		}

	}
	
	private void createScaffoldApp(String scaffoldAppFileId, String presentationPackage, FlexScaffoldMetadata flexScaffoldMetadata, ProjectMetadata projectMetadata) {
		StringTemplate scaffoldTemplate = templateGroup.getInstanceOf("org/springframework/flex/roo/addon/ui/appname_scaffold");
		scaffoldTemplate.setAttribute("flexScaffoldMetadata", flexScaffoldMetadata);
		scaffoldTemplate.setAttribute("presentationPackage", presentationPackage);
		//TODO - Extract this value from services-config.xml?
		scaffoldTemplate.setAttribute("amfRemotingUrl", "http://{server.name}:{server.port}/"+projectMetadata.getProjectName()+"/messagebroker/amf");
		fileManager.createOrUpdateTextFileIfRequired(scaffoldAppFileId, scaffoldTemplate.toString());
	}
	
	private void createEntityEventType(ActionScriptType entityEventType, FlexScaffoldMetadata flexScaffoldMetadata) {
		ActionScriptType entityType = ActionScriptMappingUtils.toActionScriptType(flexScaffoldMetadata.getBeanInfoMetadata().getJavaBean());
		StringTemplate entityEventTemplate = templateGroup.getInstanceOf("org/springframework/flex/roo/addon/ui/entity_event");
		entityEventTemplate.setAttribute("entityEventType", entityEventType);
		entityEventTemplate.setAttribute("entityType", entityType);
		entityEventTemplate.setAttribute("flexScaffoldMetadata", flexScaffoldMetadata);
		
		String relativePath = entityEventType.getFullyQualifiedTypeName().replace('.', File.separatorChar) + ".as";
		String fileIdentifier = flexPathResolver.getIdentifier(FlexPath.SRC_MAIN_FLEX, relativePath);
		fileManager.createOrUpdateTextFileIfRequired(fileIdentifier, entityEventTemplate.toString());
	}
	
	private Document buildListViewDocument(FlexScaffoldMetadata flexScaffoldMetadata, List<FieldMetadata> elegibleFields) {
		ActionScriptType entityType = ActionScriptMappingUtils.toActionScriptType(flexScaffoldMetadata.getBeanInfoMetadata().getJavaBean());
		StringTemplate listViewTemplate = templateGroup.getInstanceOf("org/springframework/flex/roo/addon/ui/entity_list_view");
		listViewTemplate.setAttribute("entityType", entityType);
		listViewTemplate.setAttribute("flexScaffoldMetadata", flexScaffoldMetadata);
		listViewTemplate.setAttribute("fields", elegibleFields);
		
		ByteArrayInputStream stream = new ByteArrayInputStream(listViewTemplate.toString().getBytes(Charset.forName("UTF-8")));
		try {
			return XmlUtils.getDocumentBuilder().parse(stream);
		} catch (Exception e) {
			throw new IllegalStateException("Failed to build list view document", e);
		}
	}
	
	private Document buildFormDocument(FlexScaffoldMetadata flexScaffoldMetadata, List<FieldMetadata> elegibleFields) {
		ActionScriptType entityType = ActionScriptMappingUtils.toActionScriptType(flexScaffoldMetadata.getBeanInfoMetadata().getJavaBean());
		StringTemplate listViewTemplate = templateGroup.getInstanceOf("org/springframework/flex/roo/addon/ui/entity_form");
		listViewTemplate.setAttribute("entityType", entityType);
		listViewTemplate.setAttribute("flexScaffoldMetadata", flexScaffoldMetadata);
		listViewTemplate.setAttribute("fields", wrapFields(elegibleFields));
		String test = listViewTemplate.toString();
		ByteArrayInputStream stream = new ByteArrayInputStream(listViewTemplate.toString().getBytes(Charset.forName("UTF-8")));
		try {
			return XmlUtils.getDocumentBuilder().parse(stream);
		} catch (Exception e) {
			throw new IllegalStateException("Failed to build list view document", e);
		}
	}
	
	private List<FormFieldWrapper> wrapFields(List<FieldMetadata> elegibleFields) {
		List<FormFieldWrapper> wrappedFields = new ArrayList<FormFieldWrapper>();
		for(FieldMetadata field : elegibleFields) {
			wrappedFields.add(new FormFieldWrapper(field));
		}
		return wrappedFields;
	}

	private List<FieldMetadata> getElegibleFields(ProjectMetadata projectMetadata, FlexScaffoldMetadata flexScaffoldMetadata) {
		List<FieldMetadata> eligibleFields = new ArrayList<FieldMetadata>();
		BeanInfoMetadata beanInfoMetadata = flexScaffoldMetadata.getBeanInfoMetadata();
		for (MethodMetadata accessor : beanInfoMetadata.getPublicAccessors(false)) {
			JavaSymbolName propertyName = BeanInfoMetadata.getPropertyNameForJavaBeanMethod(accessor);
			FieldMetadata javaField = beanInfoMetadata.getFieldForPropertyName(propertyName);
			//TODO - For now we ignore relationships in the list view
			if (!javaField.getFieldType().isCommonCollectionType() && 
					!javaField.getFieldType().isArray() &&
					!javaField.getFieldType().getPackage(). getFullyQualifiedPackageName().startsWith((projectMetadata.getTopLevelPackage().getFullyQualifiedPackageName()))) {
				// Never include id field 
				if (MemberFindingUtils.getAnnotationOfType(javaField.getAnnotations(), new JavaType("javax.persistence.Id")) != null) {
					continue;
				}
				// Never include version field 
				if (MemberFindingUtils.getAnnotationOfType(javaField.getAnnotations(), new JavaType("javax.persistence.Version")) != null) {
					continue;
				}
				eligibleFields.add(javaField);
			}
		}
		return eligibleFields;
	}
	
	/** return indicates if disk was changed (ie updated or created) */
	private boolean writeToDiskIfNecessary(String mxmlFilename, Document proposed) {
		
		Document original = null;
		
		// If mutableFile becomes non-null, it means we need to use it to write out the contents of jspContent to the file
		MutableFile mutableFile = null;
		if (fileManager.exists(mxmlFilename)) {	
			try {
				original = XmlUtils.getDocumentBuilder().parse(fileManager.getInputStream(mxmlFilename));
			} catch (Exception e) {
				new IllegalStateException("Could not parse file: " + mxmlFilename);
			} 
			Assert.notNull(original, "Unable to parse " + mxmlFilename);
			if (XmlRoundTripUtils.compareDocuments(original, proposed)) {
				mutableFile = fileManager.updateFile(mxmlFilename);
			}
		} else {
			original = proposed;
			mutableFile = fileManager.createFile(mxmlFilename);
			Assert.notNull(mutableFile, "Could not create MXML file '" + mxmlFilename + "'");
		}
		
		try {
			if (mutableFile != null) {
				// Build a string representation of the MXML
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				XmlUtils.writeXml(XmlUtils.createIndentingTransformer(), byteArrayOutputStream, original);
				String mxmlContent = byteArrayOutputStream.toString();

				// We need to write the file out (it's a new file, or the existing file has different contents)
				FileCopyUtils.copy(mxmlContent, new OutputStreamWriter(mutableFile.getOutputStream()));
				// Return and indicate we wrote out the file
				return true;
			}
		} catch (IOException ioe) {
			throw new IllegalStateException("Could not output '" + mutableFile.getCanonicalPath() + "'", ioe);
		}
		
		// A file existed, but it contained the same content, so we return false
		return false;
	}
	
	private Map<String, String> buildValidationsForField(FieldMetadata field) {
		Map<String,String> validations = new HashMap<String, String>();
		AnnotationMetadata annotationMetadata;
		if (null != (annotationMetadata = MemberFindingUtils.getAnnotationOfType(field.getAnnotations(), new JavaType("javax.validation.constraints.Min")))) {
			AnnotationAttributeValue<?> min = annotationMetadata.getAttribute(new JavaSymbolName("value"));
			if(min != null) {
				validations.put("minValue", min.getValue().toString());
			}
		}
		if (null != (annotationMetadata = MemberFindingUtils.getAnnotationOfType(field.getAnnotations(), new JavaType("javax.validation.constraints.Max")))) {
			AnnotationAttributeValue<?> max = annotationMetadata.getAttribute(new JavaSymbolName("value"));
			if(max != null) {
				validations.put("maxValue", max.getValue().toString());
			}
		}
		if (null != (annotationMetadata = MemberFindingUtils.getAnnotationOfType(field.getAnnotations(), new JavaType("javax.validation.constraints.Pattern")))) {
			AnnotationAttributeValue<?> regexp = annotationMetadata.getAttribute(new JavaSymbolName("regexp"));
			if(regexp != null) {
				validations.put("expression", regexp.getValue().toString());
			}
		}
		if (null != (annotationMetadata = MemberFindingUtils.getAnnotationOfType(field.getAnnotations(), new JavaType("javax.validation.constraints.Size")))) {
			AnnotationAttributeValue<?> max = annotationMetadata.getAttribute(new JavaSymbolName("max"));
			if(max != null) {
				validations.put("maxLength", max.getValue().toString());
			}
			AnnotationAttributeValue<?> min = annotationMetadata.getAttribute(new JavaSymbolName("min"));
			if(min != null) {
				validations.put("minLength", min.getValue().toString());
			}
		}
		if (null != (annotationMetadata = MemberFindingUtils.getAnnotationOfType(field.getAnnotations(), new JavaType("javax.validation.constraints.NotNull")))) {
			validations.put("required", "true");
		}
		return validations.size() > 0 ? validations : null;
	}
	
	public final class FormFieldWrapper {
		
		private FieldMetadata metadata;
		
		private Map<String,String> validations;

		public FormFieldWrapper(FieldMetadata metadata) {
			this.metadata = metadata;
			validations = buildValidationsForField(metadata);
		}

		public FieldMetadata getMetadata() {
			return metadata;
		}
		
		public Map<String, String> getValidations() {
			return validations;
		}

		public Boolean isDate() {
			return ActionScriptMappingUtils.toActionScriptType(metadata.getFieldType()).equals(ActionScriptType.DATE_TYPE);
		}
		
		public Boolean isBoolean() {
			return ActionScriptMappingUtils.toActionScriptType(metadata.getFieldType()).equals(ActionScriptType.BOOLEAN_TYPE);
		}
		
		public Boolean isNumber() {
			return ActionScriptMappingUtils.toActionScriptType(metadata.getFieldType()).isNumeric();
		}
	}

}
