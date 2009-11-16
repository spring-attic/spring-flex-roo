package org.springframework.flex.roo.addon;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.roo.classpath.details.annotations.populator.AutoPopulate;
import org.springframework.roo.model.JavaType;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface RooFlexScaffold {

	@AutoPopulate JavaType entity = null;
}
