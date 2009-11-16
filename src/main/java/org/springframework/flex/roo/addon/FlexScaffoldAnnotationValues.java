package org.springframework.flex.roo.addon;

import org.springframework.roo.classpath.PhysicalTypeMetadata;
import org.springframework.roo.classpath.details.annotations.populator.AbstractAnnotationValues;
import org.springframework.roo.classpath.details.annotations.populator.AutoPopulate;
import org.springframework.roo.classpath.details.annotations.populator.AutoPopulationUtils;
import org.springframework.roo.model.JavaType;

public class FlexScaffoldAnnotationValues extends AbstractAnnotationValues {

	@AutoPopulate JavaType entity = null;
	
	public FlexScaffoldAnnotationValues(PhysicalTypeMetadata governorPhysicalTypeMetadata) {
		super(governorPhysicalTypeMetadata, new JavaType(RooFlexScaffold.class.getName()));
		AutoPopulationUtils.populate(this, annotationMetadata);
	}

	public JavaType getEntity() {
		return entity;
	}
}
