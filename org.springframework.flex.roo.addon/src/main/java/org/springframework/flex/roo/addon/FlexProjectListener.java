package org.springframework.flex.roo.addon;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.osgi.service.component.ComponentContext;
import org.springframework.flex.roo.addon.mojos.FlexPathResolver;
import org.springframework.roo.file.monitor.DirectoryMonitoringRequest;
import org.springframework.roo.file.monitor.MonitoringRequest;
import org.springframework.roo.file.monitor.NotifiableFileMonitorService;
import org.springframework.roo.file.monitor.event.FileDetails;
import org.springframework.roo.file.monitor.event.FileOperation;
import org.springframework.roo.file.undo.CreateDirectory;
import org.springframework.roo.file.undo.FilenameResolver;
import org.springframework.roo.file.undo.UndoManager;
import org.springframework.roo.metadata.MetadataDependencyRegistry;
import org.springframework.roo.metadata.MetadataIdentificationUtils;
import org.springframework.roo.metadata.MetadataNotificationListener;
import org.springframework.roo.metadata.MetadataService;
import org.springframework.roo.project.Path;
import org.springframework.roo.project.ProjectMetadata;
import org.springframework.roo.support.util.Assert;

@Component
//TODO - Is there a better way to achieve the monitoring of the necessary Flex directories?
public class FlexProjectListener implements MetadataNotificationListener {

	@Reference private MetadataService metadataService;
	@Reference private MetadataDependencyRegistry metadataDependencyRegistry;
	@Reference private UndoManager undoManager;
	@Reference private NotifiableFileMonitorService fileMonitorService;
	@Reference private FlexPathResolver pathResolver;
	
	private boolean pathsRegistered = false;
	
	protected void activate(ComponentContext context) {
		metadataDependencyRegistry.addNotificationListener(this);
	}
	
	protected void deactivate(ComponentContext context) {
		metadataDependencyRegistry.removeNotificationListener(this);
	}
	
	public void notify(String upstreamDependency, String downstreamDependency) {
		if (pathsRegistered) {
			return;
		}
		
		Assert.isTrue(MetadataIdentificationUtils.isValid(upstreamDependency), "Upstream dependency is an invalid metadata identification string ('" + upstreamDependency + "')");
		
		if (upstreamDependency.equals(ProjectMetadata.getProjectIdentifier())) {
			// Acquire the Project Metadata, if available
			ProjectMetadata md = (ProjectMetadata) metadataService.get(upstreamDependency);
			if (md == null) {
				return;
			}
			
			FilenameResolver filenameResolver = new PathResolvingAwareFilenameResolver();
			
			Set<FileOperation> notifyOn = new HashSet<FileOperation>();
			notifyOn.add(FileOperation.MONITORING_START);
			notifyOn.add(FileOperation.MONITORING_FINISH);
			notifyOn.add(FileOperation.CREATED);
			notifyOn.add(FileOperation.RENAMED);
			notifyOn.add(FileOperation.UPDATED);
			notifyOn.add(FileOperation.DELETED);
			
			for (Path p : pathResolver.getFlexSourcePaths()) {
				// Verify path exists and ensure it's monitored, except root (which we assume is already monitored via ProcessManager)
				if (!Path.ROOT.equals(p)) {
					String fileIdentifier = pathResolver.getRoot(p);
					File file = new File(fileIdentifier);
					Assert.isTrue(!file.exists() || (file.exists() && file.isDirectory()), "Path '" + fileIdentifier + "' must either not exist or be a directory");
					if (!file.exists()) {
						// Create directory, but no notifications as that will happen once we start monitoring it below
						new CreateDirectory(undoManager, filenameResolver, file);
					}
					MonitoringRequest request = new DirectoryMonitoringRequest(file, true, notifyOn);
					if (md.isValid()) {
						fileMonitorService.add(request);
					} else {
						fileMonitorService.remove(request);
					}
				}
			}
			
			// Explicitly perform a scan now that we've added all the directories we wish to monitor
			fileMonitorService.scanAll();
			
			// Avoid doing this operation again unless the validity changes
			pathsRegistered = md.isValid();
		}
	}
	
	private final class PathResolvingAwareFilenameResolver implements FilenameResolver {
		public String getMeaningfulName(File file) {
			Assert.notNull(file, "File required");
			return pathResolver.getFriendlyName(FileDetails.getCanonicalPath(file));
		}
	}

}