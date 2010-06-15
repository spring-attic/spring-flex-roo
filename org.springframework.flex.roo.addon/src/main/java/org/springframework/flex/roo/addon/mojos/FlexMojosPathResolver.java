package org.springframework.flex.roo.addon.mojos;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;
import org.springframework.roo.file.monitor.MonitoringRequest;
import org.springframework.roo.project.AbstractPathResolver;
import org.springframework.roo.project.Path;
import org.springframework.roo.project.PathInformation;

@Component(immediate=true)
@Service(FlexPathResolver.class)
public class FlexMojosPathResolver extends AbstractPathResolver implements FlexPathResolver {

	private List<PathInformation> pathInformation = new ArrayList<PathInformation>();
	
	protected void activate(ComponentContext context) {
		String workingDir = context.getBundleContext().getProperty("roo.working.directory");
		File root = MonitoringRequest.getInitialMonitoringRequest(workingDir).getFile();
		pathInformation.add(new PathInformation(FlexPath.SRC_MAIN_FLEX, true, new File(root, "src/main/flex")));
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

	@Override
	protected List<PathInformation> getPathInformation() {
		return pathInformation;
	}

}
