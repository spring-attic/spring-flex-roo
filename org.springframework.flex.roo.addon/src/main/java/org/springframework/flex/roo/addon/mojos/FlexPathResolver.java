package org.springframework.flex.roo.addon.mojos;

import java.util.List;

import org.springframework.roo.project.Path;
import org.springframework.roo.project.PathResolver;

public interface FlexPathResolver extends PathResolver {

	List<Path> getFlexSourcePaths();
}
