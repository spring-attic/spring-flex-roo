package org.springframework.flex.roo.addon;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.springframework.flex.roo.addon.as.classpath.ASMutablePhysicalTypeMetadataProvider;
import org.springframework.flex.roo.addon.as.classpath.ASPhysicalTypeCategory;
import org.springframework.flex.roo.addon.as.classpath.ASPhysicalTypeIdentifier;
import org.springframework.flex.roo.addon.as.classpath.ASPhysicalTypeMetadata;
import org.springframework.flex.roo.addon.as.classpath.details.ASClassOrInterfaceTypeDetails;
import org.springframework.flex.roo.addon.as.classpath.details.ASFieldMetadata;
import org.springframework.flex.roo.addon.as.classpath.details.DefaultASClassOrInterfaceTypeDetails;
import org.springframework.flex.roo.addon.as.classpath.details.DefaultASPhysicalTypeMetadata;
import org.springframework.flex.roo.addon.as.classpath.details.metatag.ASMetaTagMetadata;
import org.springframework.flex.roo.addon.as.classpath.details.metatag.DefaultASMetaTagMetadata;
import org.springframework.flex.roo.addon.as.classpath.details.metatag.MetaTagAttributeValue;
import org.springframework.flex.roo.addon.as.classpath.details.metatag.StringAttributeValue;
import org.springframework.flex.roo.addon.as.model.ActionScriptMappingUtils;
import org.springframework.flex.roo.addon.as.model.ActionScriptSymbolName;
import org.springframework.flex.roo.addon.as.model.ActionScriptType;
import org.springframework.flex.roo.addon.mojos.FlexPath;
import org.springframework.flex.roo.addon.mojos.FlexPathResolver;
import org.springframework.roo.addon.beaninfo.BeanInfoMetadata;
import org.springframework.roo.addon.web.mvc.controller.WebMvcOperations;
import org.springframework.roo.classpath.PhysicalTypeCategory;
import org.springframework.roo.classpath.PhysicalTypeIdentifier;
import org.springframework.roo.classpath.PhysicalTypeMetadata;
import org.springframework.roo.classpath.details.ClassOrInterfaceTypeDetails;
import org.springframework.roo.classpath.details.DefaultClassOrInterfaceTypeDetails;
import org.springframework.roo.classpath.details.FieldMetadata;
import org.springframework.roo.classpath.details.MethodMetadata;
import org.springframework.roo.classpath.details.annotations.AnnotationAttributeValue;
import org.springframework.roo.classpath.details.annotations.AnnotationMetadata;
import org.springframework.roo.classpath.details.annotations.ClassAttributeValue;
import org.springframework.roo.classpath.details.annotations.DefaultAnnotationMetadata;
import org.springframework.roo.classpath.operations.ClasspathOperations;
import org.springframework.roo.metadata.MetadataService;
import org.springframework.roo.model.JavaSymbolName;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.process.manager.FileManager;
import org.springframework.roo.process.manager.MutableFile;
import org.springframework.roo.project.Dependency;
import org.springframework.roo.project.Path;
import org.springframework.roo.project.PathResolver;
import org.springframework.roo.project.ProjectMetadata;
import org.springframework.roo.project.ProjectOperations;
import org.springframework.roo.project.ProjectType;
import org.springframework.roo.support.util.Assert;
import org.springframework.roo.support.util.FileCopyUtils;
import org.springframework.roo.support.util.TemplateUtils;
import org.springframework.roo.support.util.XmlUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Implementation of flex commands that are available via the Roo shell.
 *
 * @author Jeremy Grelle
 * @since 1.0
 */
@Component
@Service
public class FlexOperationsImpl implements FlexOperations {
	
	//private static Logger logger = Logger.getLogger(FlexOperationsImpl.class.getName());
	
	//private static final ActionScriptFactory asFactory = new ActionScriptFactory();
	
	@Reference private FileManager fileManager;
	@Reference private MetadataService metadataService;
	@Reference private FlexPathResolver pathResolver;
	@Reference private ProjectOperations projectOperations;
	@Reference private WebMvcOperations webMvcOperations;
	@Reference private ClasspathOperations classpathOperations;
	@Reference private ASMutablePhysicalTypeMetadataProvider physicalTypeProvider;
	
	public void installFlex() {
		createServicesConfig();
		createFlexConfig();
		updateDependencies();
	}

	public boolean isFlexAvailable() {
		return getPathResolver() != null;
	}
	
	public void createRemotingDestination(JavaType service, JavaType entity) {
		Assert.notNull(service, "Remoting Destination Java Type required");
		Assert.notNull(entity, "Entity Java Type required");
		
		String resourceIdentifier = classpathOperations.getPhysicalLocationCanonicalPath(service, Path.SRC_MAIN_JAVA);
		
		createOrUpdateActionScriptEntity(entity);
		
		//create annotation @RooFlexScaffold
		List<AnnotationAttributeValue<?>> rooFlexScaffoldAttributes = new ArrayList<AnnotationAttributeValue<?>>();
		rooFlexScaffoldAttributes.add(new ClassAttributeValue(new JavaSymbolName("entity"), entity));
		AnnotationMetadata atRooFlexScaffold = new DefaultAnnotationMetadata(new JavaType(RooFlexScaffold.class.getName()), rooFlexScaffoldAttributes);
		
		//create annotation @RemotingDestination
		List<AnnotationAttributeValue<?>> remotingDestinationAttributes = new ArrayList<AnnotationAttributeValue<?>>();
		AnnotationMetadata atRemotingDestination = new DefaultAnnotationMetadata(new JavaType("org.springframework.flex.remoting.RemotingDestination"), remotingDestinationAttributes);
	
		//create annotation @Service
		List<AnnotationAttributeValue<?>> serviceAttributes = new ArrayList<AnnotationAttributeValue<?>>();
		AnnotationMetadata atService = new DefaultAnnotationMetadata(new JavaType("org.springframework.stereotype.Service"), serviceAttributes);

		String declaredByMetadataId = PhysicalTypeIdentifier.createIdentifier(service, pathResolver.getPath(resourceIdentifier));
		List<AnnotationMetadata> annotations = new ArrayList<AnnotationMetadata>();
		annotations.add(atRooFlexScaffold);
		annotations.add(atRemotingDestination);
		annotations.add(atService);
		ClassOrInterfaceTypeDetails details = new DefaultClassOrInterfaceTypeDetails(declaredByMetadataId, service, Modifier.PUBLIC, PhysicalTypeCategory.CLASS, annotations);
		
		classpathOperations.generateClassFile(details);
	}
	
	public String getPhysicalLocationCanonicalPath(String physicalTypeIdentifier) {
		Assert.isTrue(ASPhysicalTypeIdentifier.isValid(physicalTypeIdentifier), "Physical type identifier is invalid");
		Assert.notNull(pathResolver, "Cannot computed metadata ID of a type because the path resolver is presently unavailable");
		ActionScriptType asType = ASPhysicalTypeIdentifier.getActionScriptType(physicalTypeIdentifier);
		Path path = PhysicalTypeIdentifier.getPath(physicalTypeIdentifier);
		String relativePath = asType.getFullyQualifiedTypeName().replace('.', File.separatorChar) + ".as";
		String physicalLocationCanonicalPath = pathResolver.getIdentifier(path, relativePath);
		return physicalLocationCanonicalPath;
	}
	
	private void createOrUpdateActionScriptEntity(JavaType entity) {
		ActionScriptType asEntity = ActionScriptMappingUtils.toActionScriptType(entity);
		String asEntityId = physicalTypeProvider.findIdentifier(asEntity);
		if (!StringUtils.hasText(asEntityId)) {
			//TODO - for now we will only handle classes...interfaces could come later but would add complexity (i.e., need to find all implementations and mirror those as well)
			asEntityId = ASPhysicalTypeIdentifier.createIdentifier(asEntity, FlexPath.SRC_MAIN_FLEX);
			createActionScriptMirrorClass(asEntityId, asEntity, entity);
		} else {
			//exists already - update to match Java type if necessary
		}		
	}
	
	/**
	 * TODO - If the entity implements an interface, should we generate the interface as well? Currently they are ignored.
	 * TODO - Consider adding support for getters and setters
	 * TODO - Currently ignoring non-accessor methods - is there any reason to do otherwise?
	 * TODO - If the entity has a single constructor specified, should we mimic it? Would probably prove overly complicated.
	 */
	private void createActionScriptMirrorClass(String asEntityId, ActionScriptType asType, JavaType javaType) {
		Queue<TypeMapping> relatedTypes = new LinkedList<TypeMapping>();
		
		//TODO - Get JavaType's superclass and recursively generate a corresponding ActionScript class if necessary
		ClassOrInterfaceTypeDetails javaDetails = getClassDetails(javaType);
		javaDetails.getSuperclass();//etc
		
		List <MetaTagAttributeValue<?>> attributes = new ArrayList<MetaTagAttributeValue<?>>();
		attributes.add(new StringAttributeValue(new ActionScriptSymbolName("alias"), javaType.getFullyQualifiedTypeName()));
		ASMetaTagMetadata remoteClassTag = new DefaultASMetaTagMetadata("RemoteClass", attributes);
		List<ASMetaTagMetadata> typeMetaTags = new ArrayList<ASMetaTagMetadata>();
		typeMetaTags.add(remoteClassTag);
		
		List<ASFieldMetadata> declaredFields = new ArrayList<ASFieldMetadata>();
		BeanInfoMetadata beanInfoMetadata = getBeanInfoMetadata(javaType);
		for (MethodMetadata accessor : beanInfoMetadata.getPublicAccessors()) {
			JavaSymbolName propertyName = BeanInfoMetadata.getPropertyNameForJavaBeanMethod(accessor);
			FieldMetadata javaField = beanInfoMetadata.getFieldForPropertyName(propertyName);
			
			//TODO - We don't add any meta-tags and we set the field to public - any other choice?
			ASFieldMetadata asField = ActionScriptMappingUtils.toASFieldMetadata(asEntityId, javaField, null, true);
			String relatedEntityId = physicalTypeProvider.findIdentifier(asField.getFieldType());
			if (!StringUtils.hasText(relatedEntityId)) {
				relatedEntityId = ASPhysicalTypeIdentifier.createIdentifier(asField.getFieldType(), FlexPath.SRC_MAIN_FLEX);
				relatedTypes.add(new TypeMapping(relatedEntityId, asField.getFieldType(), javaField.getFieldType()));
			}
			declaredFields.add(asField);
		}  
		
		ASClassOrInterfaceTypeDetails asDetails = new DefaultASClassOrInterfaceTypeDetails(asEntityId, asType, ASPhysicalTypeCategory.CLASS, declaredFields, null, null, null, null, null, typeMetaTags);
		//new DefaultASClassOrInterfaceTypeDetails(declaredByMetadataId, name, physicalTypeCategory, declaredFields, 
		//		declaredConstructor, declaredMethods, superClass, extendsTypes, implementsTypes, typeMetaTags);
		ASPhysicalTypeMetadata asMetadata = new DefaultASPhysicalTypeMetadata(asEntityId, getPhysicalLocationCanonicalPath(asEntityId), asDetails);
		physicalTypeProvider.createPhysicalType(asMetadata);
		
		//TODO - Register the proper meta-data dependency relationship so that we can update the ActionScript class when the Java class changes.
		
		//Now trigger the creation of any related types
		while (!relatedTypes.isEmpty()) {
			TypeMapping mapping = relatedTypes.poll();
			createActionScriptMirrorClass(mapping.getMetadataId(), mapping.getAsType(), mapping.getJavaType());
		}
	}
	
	private ClassOrInterfaceTypeDetails getClassDetails(JavaType javaType){
		String id = PhysicalTypeIdentifier.createIdentifier(javaType, Path.SRC_MAIN_JAVA);
		PhysicalTypeMetadata metadata = (PhysicalTypeMetadata) metadataService.get(id);
		Assert.isInstanceOf(ClassOrInterfaceTypeDetails.class, metadata.getPhysicalTypeDetails(), "Java entity must be a class or interface.");
		return (ClassOrInterfaceTypeDetails) metadata.getPhysicalTypeDetails();
	}
	
	private BeanInfoMetadata getBeanInfoMetadata(JavaType javaType) {
		String beanInfoMetadataKey = BeanInfoMetadata.createIdentifier(javaType, Path.SRC_MAIN_JAVA);
		return (BeanInfoMetadata) metadataService.get(beanInfoMetadataKey);
	}

	private void createServicesConfig() {
		String servicesConfigFilename = "WEB-INF/flex/services-config.xml";
		
		if (fileManager.exists(pathResolver.getIdentifier(Path.SRC_MAIN_WEBAPP, servicesConfigFilename))) {
			//file exists, so nothing to do
			return;
		}		
		
		try {
			FileCopyUtils.copy(TemplateUtils.getTemplate(getClass(), "services-config-template.xml"), fileManager.createFile(pathResolver.getIdentifier(Path.SRC_MAIN_WEBAPP, servicesConfigFilename)).getOutputStream());
		} catch (IOException e) {
			new IllegalStateException("Encountered an error during copying of resources for maven addon.", e);
		}
	}
	
	private void createFlexConfig() {
		
		String flexConfigFilename = "/WEB-INF/spring/flex-config.xml";
		
		try {	
			if (!fileManager.exists(pathResolver.getIdentifier(Path.SRC_MAIN_WEBAPP, flexConfigFilename))) {
				FileCopyUtils.copy(TemplateUtils.getTemplate(getClass(), "flex-config.xml"), fileManager.createFile(pathResolver.getIdentifier(Path.SRC_MAIN_WEBAPP, flexConfigFilename)).getOutputStream());
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		
		//adjust MVC config to accommodate Spring Flex
		String mvcContextPath = pathResolver.getIdentifier(Path.SRC_MAIN_WEBAPP, "/WEB-INF/spring/webmvc-config.xml");
		MutableFile mvcContextMutableFile = null;
		
		Document mvcAppCtx;
		try {
			if (!fileManager.exists(mvcContextPath)) {
				webMvcOperations.installAllWebMvcArtifacts();	
			} 
			mvcContextMutableFile = fileManager.updateFile(mvcContextPath);
			mvcAppCtx = XmlUtils.getDocumentBuilder().parse(mvcContextMutableFile.getInputStream());
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		
		Element root = mvcAppCtx.getDocumentElement();

		if (null == XmlUtils.findFirstElement("/beans/import[@resource='flex-config.xml']", root)) {
			Element importFlex = mvcAppCtx.createElement("import");
			importFlex.setAttribute("resource", "flex-config.xml");
			root.appendChild(importFlex);
			XmlUtils.writeXml(mvcContextMutableFile.getOutputStream(), mvcAppCtx);	
		}
	}
	
	private void updateDependencies() {	
		InputStream templateInputStream = TemplateUtils.getTemplate(getClass(), "dependencies.xml");
		Assert.notNull(templateInputStream, "Could not acquire dependencies.xml file");
		Document dependencyDoc;
		try {
			dependencyDoc = XmlUtils.getDocumentBuilder().parse(templateInputStream);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

		Element dependenciesElement = (Element) dependencyDoc.getFirstChild();
		
		List<Element> flexDependencies = XmlUtils.findElements("/dependencies/springFlex/dependency", dependenciesElement);
		for(Element dependency : flexDependencies) {
			projectOperations.dependencyUpdate(new Dependency(dependency));
		}
		
		projectOperations.updateProjectType(ProjectType.WAR);
	}
	
	/**
	 * @return the path resolver or null if there is no user project
	 */
	private PathResolver getPathResolver() {
		ProjectMetadata projectMetadata = (ProjectMetadata) metadataService.get(ProjectMetadata.getProjectIdentifier());
		if (projectMetadata == null) {
			return null;
		}
		return projectMetadata.getPathResolver();
	}
	
	private static final class TypeMapping {
		private String metadataId;
		private ActionScriptType asType;
		private JavaType javaType;
		
		public TypeMapping(String metadataId, ActionScriptType asType,
				JavaType javaType) {
			super();
			this.metadataId = metadataId;
			this.asType = asType;
			this.javaType = javaType;
		}
		
		public String getMetadataId() {
			return metadataId;
		}
		
		public ActionScriptType getAsType() {
			return asType;
		}
		
		public JavaType getJavaType() {
			return javaType;
		}
	}

}