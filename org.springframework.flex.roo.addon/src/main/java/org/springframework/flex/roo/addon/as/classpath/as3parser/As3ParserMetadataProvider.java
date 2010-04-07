package org.springframework.flex.roo.addon.as.classpath.as3parser;

import java.io.File;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;
import org.springframework.flex.roo.addon.as.classpath.ASMutablePhysicalTypeMetadataProvider;
import org.springframework.flex.roo.addon.as.classpath.ASPhysicalTypeDetails;
import org.springframework.flex.roo.addon.as.classpath.ASPhysicalTypeIdentifier;
import org.springframework.flex.roo.addon.as.classpath.ASPhysicalTypeMetadata;
import org.springframework.flex.roo.addon.as.classpath.details.ASMutableClassOrInterfaceTypeDetails;
import org.springframework.flex.roo.addon.as.model.ActionScriptType;
import org.springframework.flex.roo.addon.mojos.FlexPathResolver;
import org.springframework.roo.file.monitor.event.FileEvent;
import org.springframework.roo.file.monitor.event.FileEventListener;
import org.springframework.roo.metadata.MetadataDependencyRegistry;
import org.springframework.roo.metadata.MetadataItem;
import org.springframework.roo.metadata.MetadataService;
import org.springframework.roo.process.manager.FileManager;
import org.springframework.roo.project.Path;
import org.springframework.roo.project.PathResolver;
import org.springframework.roo.project.ProjectMetadata;
import org.springframework.roo.support.util.Assert;

@Component(immediate=true)
@Service
public class As3ParserMetadataProvider implements
		ASMutablePhysicalTypeMetadataProvider, FileEventListener {

	@Reference private FileManager fileManager;
	@Reference private MetadataService metadataService;
	@Reference private MetadataDependencyRegistry metadataDependencyRegistry;
	
	protected void activate(ComponentContext context) {
	}
	
	public void createPhysicalType(ASPhysicalTypeMetadata toCreate) {
		Assert.notNull(toCreate, "Metadata to create is required");
		ASPhysicalTypeDetails physicalTypeDetails = toCreate.getPhysicalTypeDetails();
		Assert.notNull(physicalTypeDetails, "Unable to parse '" + toCreate + "'");
		Assert.isInstanceOf(ASMutableClassOrInterfaceTypeDetails.class, physicalTypeDetails, "This implementation can only create class or interface types");
		ASMutableClassOrInterfaceTypeDetails cit = (ASMutableClassOrInterfaceTypeDetails) physicalTypeDetails;
		String fileIdentifier = toCreate.getPhysicalLocationCanonicalPath();
		As3ParserMutableClassOrInterfaceTypeDetails.createType(fileManager, cit, fileIdentifier);
	}

	public String findIdentifier(ActionScriptType actionScriptType) {
		Assert.notNull(actionScriptType, "ActionScript type to locate is required");
		FlexPathResolver pathResolver = getPathResolver();
		for (Path sourcePath : pathResolver.getFlexSourcePaths()) {
			String relativePath = actionScriptType.getFullyQualifiedTypeName().replace('.', File.separatorChar) + ".as";
			String fileIdentifier = pathResolver.getIdentifier(sourcePath, relativePath);
			if (fileManager.exists(fileIdentifier)) {
				// found the file, so use this one
				return ASPhysicalTypeIdentifier.createIdentifier(actionScriptType, sourcePath);
			}
		}
		return null;
	}

	public MetadataItem get(String metadataIdentificationString) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getProvidesType() {
		return ASPhysicalTypeIdentifier.getMetadataIdentiferType();
	}
	
	public void onFileEvent(FileEvent fileEvent) {
		// TODO Auto-generated method stub

	}
	
	private FlexPathResolver getPathResolver() {
		ProjectMetadata projectMetadata = (ProjectMetadata) metadataService.get(ProjectMetadata.getProjectIdentifier());
		Assert.notNull(projectMetadata, "Project metadata unavailable");
		PathResolver pathResolver = projectMetadata.getPathResolver();
		Assert.notNull(pathResolver, "Path resolver unavailable because valid project metadata not currently available");
		Assert.isInstanceOf(FlexPathResolver.class, "Path resolver is of an unexpected type, not appropriate for a Flex project.");
		return (FlexPathResolver) pathResolver;
	}

}
