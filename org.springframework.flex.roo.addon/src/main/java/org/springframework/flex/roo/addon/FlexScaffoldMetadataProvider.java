package org.springframework.flex.roo.addon;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;
import org.springframework.roo.addon.beaninfo.BeanInfoMetadata;
import org.springframework.roo.addon.entity.EntityMetadata;
import org.springframework.roo.addon.finder.FinderMetadata;
import org.springframework.roo.classpath.PhysicalTypeIdentifier;
import org.springframework.roo.classpath.PhysicalTypeMetadata;
import org.springframework.roo.classpath.itd.AbstractItdMetadataProvider;
import org.springframework.roo.classpath.itd.ItdTypeDetailsProvidingMetadataItem;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.project.Path;

@Component(immediate=true)
@Service
public class FlexScaffoldMetadataProvider extends AbstractItdMetadataProvider {
	
	protected void activate(ComponentContext context) {
		metadataDependencyRegistry.registerDependency(PhysicalTypeIdentifier.getMetadataIdentiferType(), getProvidesType());
		addMetadataTrigger(new JavaType(RooFlexScaffold.class.getName()));
	}

	@Override
	protected String createLocalIdentifier(JavaType javaType, Path path) {
		return FlexScaffoldMetadata.createIdentifier(javaType, path);
	}

	@Override
	protected String getGovernorPhysicalTypeIdentifier(
			String metadataIdentificationString) {
		JavaType javaType = FlexScaffoldMetadata.getJavaType(metadataIdentificationString);
		Path path = FlexScaffoldMetadata.getPath(metadataIdentificationString);
		String physicalTypeIdentifier = PhysicalTypeIdentifier.createIdentifier(javaType, path);
		return physicalTypeIdentifier;
	}

	@Override
	protected ItdTypeDetailsProvidingMetadataItem getMetadata(
			String metadataIdentificationString, JavaType aspectName,
			PhysicalTypeMetadata governorPhysicalTypeMetadata,
			String itdFilename) {
		
		// We need to parse the annotation, which we expect to be present
		FlexScaffoldAnnotationValues annotationValues = new FlexScaffoldAnnotationValues(governorPhysicalTypeMetadata);
		if (!annotationValues.isAnnotationFound() || annotationValues.entity == null) {
			return null;
		}
		
		// Lookup the form backing object's metadata
		JavaType javaType = annotationValues.entity;
		Path path = Path.SRC_MAIN_JAVA;
		String beanInfoMetadataKey = BeanInfoMetadata.createIdentifier(javaType, path);
		String entityMetadataKey = EntityMetadata.createIdentifier(javaType, path);
		String finderMetdadataKey = FinderMetadata.createIdentifier(javaType, path);
		
		// We need to lookup the metadata we depend on
		BeanInfoMetadata beanInfoMetadata = (BeanInfoMetadata) metadataService.get(beanInfoMetadataKey);
		EntityMetadata entityMetadata = (EntityMetadata) metadataService.get(entityMetadataKey);
		FinderMetadata finderMetadata = (FinderMetadata) metadataService.get(finderMetdadataKey);
		
		// We need to abort if we couldn't find dependent metadata
		if (beanInfoMetadata == null || !beanInfoMetadata.isValid() || entityMetadata == null || !entityMetadata.isValid()) {
			return null;
		}
		
		// We need to be informed if our dependent metadata changes
		metadataDependencyRegistry.registerDependency(beanInfoMetadataKey, metadataIdentificationString);
		metadataDependencyRegistry.registerDependency(entityMetadataKey, metadataIdentificationString);
		
		return new FlexScaffoldMetadata(metadataIdentificationString, aspectName, governorPhysicalTypeMetadata, beanInfoMetadata, entityMetadata, finderMetadata);
	}

	public String getItdUniquenessFilenameSuffix() {
		return "Service";
	}

	public String getProvidesType() {
		return FlexScaffoldMetadata.getMetadataIdentiferType();
	}

}
