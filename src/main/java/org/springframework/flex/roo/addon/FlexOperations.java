package org.springframework.flex.roo.addon;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

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
import org.springframework.roo.support.lifecycle.ScopeDevelopment;
import org.springframework.roo.support.util.Assert;
import org.springframework.roo.support.util.FileCopyUtils;
import org.springframework.roo.support.util.TemplateUtils;
import org.springframework.roo.support.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Implementation of flex commands that are available via the Roo shell.
 *
 * @author Jeremy Grelle
 * @since 1.0
 */
@ScopeDevelopment
public class FlexOperations {
	
	//private static Logger logger = Logger.getLogger(FlexOperations.class.getName());
	
	private FileManager fileManager;
	private MetadataService metadataService;
	private PathResolver pathResolver;
	private ProjectOperations projectOperations;
	private WebMvcOperations webMvcOperations;
	private ClasspathOperations classpathOperations;
	
	public FlexOperations(FileManager fileManager, MetadataService metadataService, PathResolver pathResolver, ProjectOperations projectOperations, WebMvcOperations webMvcOperations, ClasspathOperations classpathOperations) {
		Assert.notNull(fileManager, "File manager required");
		Assert.notNull(metadataService, "Metadata service required");
		Assert.notNull(pathResolver, "Path resolver required");
		Assert.notNull(projectOperations, "Project operations required");
		Assert.notNull(webMvcOperations, "Web MVC operations required");
		this.fileManager = fileManager;
		this.metadataService = metadataService;
		this.pathResolver = pathResolver;
		this.projectOperations = projectOperations;
		this.webMvcOperations = webMvcOperations;
		this.classpathOperations = classpathOperations;
	}
	
	public void createRemotingDestination(JavaType service, JavaType entity) {
		Assert.notNull(service, "Remoting Destination Java Type required");
		Assert.notNull(entity, "Entity Java Type required");
		
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
	
	public void installFlex() {
		createServicesConfig();
		createFlexConfig();
		updateDependencies();
	}

	public boolean isProjectAvailable() {
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
		
		String flexConfigFilename = "/WEB-INF/config/flex-config.xml";
		
		try {	
			if (!fileManager.exists(pathResolver.getIdentifier(Path.SRC_MAIN_WEBAPP, flexConfigFilename))) {
				FileCopyUtils.copy(TemplateUtils.getTemplate(getClass(), "flex-config.xml"), fileManager.createFile(pathResolver.getIdentifier(Path.SRC_MAIN_WEBAPP, flexConfigFilename)).getOutputStream());
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		
		//adjust MVC config to accommodate Spring Flex
		String mvcContextPath = pathResolver.getIdentifier(Path.SRC_MAIN_WEBAPP, "/WEB-INF/config/webmvc-config.xml");
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