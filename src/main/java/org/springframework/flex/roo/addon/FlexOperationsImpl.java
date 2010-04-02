package org.springframework.flex.roo.addon;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.springframework.roo.addon.web.mvc.controller.WebMvcOperations;
import org.springframework.roo.classpath.PhysicalTypeCategory;
import org.springframework.roo.classpath.PhysicalTypeIdentifier;
import org.springframework.roo.classpath.details.ClassOrInterfaceTypeDetails;
import org.springframework.roo.classpath.details.DefaultClassOrInterfaceTypeDetails;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

//import uk.co.badgersinfoil.metaas.ActionScriptFactory;
//import uk.co.badgersinfoil.metaas.dom.ASClassType;
//import uk.co.badgersinfoil.metaas.dom.ASCompilationUnit;
//import uk.co.badgersinfoil.metaas.dom.ASMetaTag;
//import uk.co.badgersinfoil.metaas.dom.Visibility;


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
	@Reference private PathResolver pathResolver;
	@Reference private ProjectOperations projectOperations;
	@Reference private WebMvcOperations webMvcOperations;
	@Reference private ClasspathOperations classpathOperations;
	
	public void createRemotingDestination(JavaType service, JavaType entity) {
		Assert.notNull(service, "Remoting Destination Java Type required");
		Assert.notNull(entity, "Entity Java Type required");
		
		//createActionScriptEntity(entity);
		
		String resourceIdentifier = classpathOperations.getPhysicalLocationCanonicalPath(service, Path.SRC_MAIN_JAVA);		
		
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
	
	//TODO - This is temporary until the Metadata System for ActionScript types is complete
	/*private void createActionScriptEntity(JavaType entity) {
		
		ASCompilationUnit cu = asFactory.newClass(entity.getFullyQualifiedTypeName());
		cu.setPackageName(entity.getPackage().getFullyQualifiedPackageName());
		ASMetaTag remoteClassTag = cu.getType().newMetaTag("RemoteClass");
		remoteClassTag.addParam("alias", entity.getFullyQualifiedTypeName());
			
		// We need to lookup the metadata we depend on
		BeanInfoMetadata beanInfoMetadata = getBeanInfoMetadata(entity);
		for (MethodMetadata accessor : beanInfoMetadata.getPublicAccessors()) {
			JavaSymbolName propertyName = beanInfoMetadata.getPropertyNameForJavaBeanMethod(accessor);
			FieldMetadata field = beanInfoMetadata.getFieldForPropertyName(propertyName);
			ActionScriptType flexFieldType = new ActionScriptType(mapJavaTypeToActionScriptType(field.getFieldType()));
			if (!flexFieldType.isDefaultPackage() && !cu.getPackage().findImports().contains(flexFieldType.getFullyQualifiedTypeName())) {
				cu.getPackage().addImport(flexFieldType.getFullyQualifiedTypeName());
			}
			((ASClassType)cu.getType()).newField(StringUtils.uncapitalize(propertyName.getSymbolName()), Visibility.PUBLIC, flexFieldType.getSimpleTypeName());
		}
		String flexPathId = pathResolver.getIdentifier(Path.ROOT, "/src/main/flex/"+cu.getType().getName()+".as");
		OutputStream out = fileManager.createFile(flexPathId).getOutputStream();
		Writer writer = new OutputStreamWriter(out);
		try {
			asFactory.newWriter().write(writer, cu);
		} catch (IOException e) {
			throw new IllegalStateException("Encountered an error during generation of ActionScript class file", e);
		} finally {
			try {
				writer.close();
			}
			catch (IOException e) {
				throw new IllegalStateException("Encountered an error during generation of ActionScript class file", e);
			}
		}
	}
	
	private String mapJavaTypeToActionScriptType(JavaType fieldType) {
		if (fieldType.isCommonCollectionType()) {
			if (fieldType.getSimpleTypeName().endsWith("Map")) {
				return "Object";
			} else {
				return "ArrayCollection";
			}
		} else if (fieldType.isArray()) {
			return "Array";
		} else {
			String primitiveType = amfTypeTable.get(fieldType);
			return primitiveType != null ? primitiveType : fieldType.getFullyQualifiedTypeName();
		}
	}

	private BeanInfoMetadata getBeanInfoMetadata(JavaType javaType) {
		// Lookup the form backing object's metadata
		Path path = Path.SRC_MAIN_JAVA;
		String beanInfoMetadataKey = BeanInfoMetadata.createIdentifier(javaType, path);
		
		// We need to lookup the metadata we depend on
		BeanInfoMetadata beanInfoMetadata = (BeanInfoMetadata) metadataService.get(beanInfoMetadataKey);
		return beanInfoMetadata;
	}*/

	public void installFlex() {
		createServicesConfig();
		createFlexConfig();
		updateDependencies();
	}

	public boolean isFlexAvailable() {
		return getPathResolver() != null;
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
				webMvcOperations.installMvcArtefacts();	
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

}