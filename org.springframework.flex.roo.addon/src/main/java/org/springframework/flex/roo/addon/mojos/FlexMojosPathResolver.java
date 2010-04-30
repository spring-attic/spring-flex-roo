package org.springframework.flex.roo.addon.mojos;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;
import org.springframework.roo.addon.maven.MavenPathResolver;
import org.springframework.roo.file.monitor.MonitoringRequest;
import org.springframework.roo.project.Path;
import org.springframework.roo.project.PathInformation;

//@Component
//@Service
public class FlexMojosPathResolver extends MavenPathResolver implements FlexPathResolver {

	@Override
	protected void activate(ComponentContext context) {
		//File root = MonitoringRequest.getInitialMonitoringRequest().getFile();
		//getPathInformation().add(new PathInformation(FlexPath.SRC_MAIN_FLEX, true, new File(root, "src/main/flex")));
		init();
	}

	public List<Path> getFlexSourcePaths() {
		List<Path> flexSourcePaths = new ArrayList<Path>();
		for (Path path : getPaths()) {
			if (path instanceof FlexPath) {
				flexSourcePaths.add(path);
			}
		}
		return flexSourcePaths;
	}

}
