package org.springframework.flex.roo.addon.as.classpath.as3parser;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.core.io.ClassPathResource;
import org.springframework.flex.roo.addon.as.classpath.ASPhysicalTypeCategory;
import org.springframework.flex.roo.addon.as.classpath.ASPhysicalTypeDetails;
import org.springframework.flex.roo.addon.as.classpath.ASPhysicalTypeIdentifier;
import org.springframework.flex.roo.addon.as.classpath.ASPhysicalTypeMetadata;
import org.springframework.flex.roo.addon.as.classpath.details.DefaultASClassOrInterfaceTypeDetails;
import org.springframework.flex.roo.addon.as.classpath.details.DefaultASPhysicalTypeDetails;
import org.springframework.flex.roo.addon.as.classpath.details.DefaultASPhysicalTypeMetadata;
import org.springframework.flex.roo.addon.as.model.ActionScriptType;
import org.springframework.flex.roo.addon.mojos.FlexMojosPathResolver;
import org.springframework.flex.roo.addon.mojos.FlexPath;
import org.springframework.flex.roo.addon.mojos.FlexPathResolver;
import org.springframework.roo.file.monitor.event.FileDetails;
import org.springframework.roo.metadata.MetadataDependencyRegistry;
import org.springframework.roo.metadata.MetadataService;
import org.springframework.roo.process.manager.ActiveProcessManager;
import org.springframework.roo.process.manager.FileManager;
import org.springframework.roo.process.manager.ProcessManager;
import org.springframework.roo.process.manager.internal.DefaultFileManager;
import org.springframework.roo.project.PathInformation;
import org.springframework.test.util.ReflectionTestUtils;

public class As3ParserMetadataProviderTests {

	FileManager fileManager = new DefaultFileManager();
	
	@Mock
	MetadataService metadataService;
	
	@Mock
	MetadataDependencyRegistry registry;
	
	@Mock
	ProcessManager processManager;
	
	@Mock
	FileDetails fileDetails;
		
	FlexPathResolver pathResolver;

	private String metadataId;
	
	private As3ParserMetadataProvider provider;
	
	@Before
	public void setUp() throws Exception {
		initMocks(this);
		when(processManager.isDevelopmentMode()).thenReturn(true);
		ActiveProcessManager.setActiveProcessManager(processManager);
		metadataId = "MID:"+ASPhysicalTypeIdentifier.class.getName()+"#SRC_MAIN_FLEX?com.foo.stuff.FooImpl";
		
		pathResolver = new TestPathResolver();
		
		provider = new As3ParserMetadataProvider();
		ReflectionTestUtils.setField(provider, "fileManager", fileManager);
		ReflectionTestUtils.setField(provider, "metadataService", metadataService);
		ReflectionTestUtils.setField(provider, "metadataDependencyRegistry", registry);
		ReflectionTestUtils.setField(provider, "pathResolver", pathResolver);
	}
	
	@Test
	public void testGetWithValidIdentifier() {
		As3ParserClassMetadata metadata = (As3ParserClassMetadata) provider.get(metadataId);
		assertNotNull(metadata);
		assertNotNull(metadata.getPhysicalTypeDetails());
	}
	
	@Test
	public void testCreatePhysicalType_EmptyClass() throws IOException {
		String fileIdentifier = new ClassPathResource("com/foo/stuff/EmptyImpl.as").getFile().getCanonicalPath();
		ASPhysicalTypeDetails details = new DefaultASClassOrInterfaceTypeDetails(metadataId, new ActionScriptType("com.foo.stuff.EmptyImpl"), 
				ASPhysicalTypeCategory.CLASS, null);
		ASPhysicalTypeMetadata type = new DefaultASPhysicalTypeMetadata(metadataId, fileIdentifier, details);
		provider.createPhysicalType(type);
	}
	
	@Test
	public void testFindIdentifier() {
		fail("Not implemented.");
	}
	
	@Test
	public void testOnFileEvent() {
		fail("Not implemented.");
	}
	
	private static class TestPathResolver extends FlexMojosPathResolver {
		
		public TestPathResolver() throws IOException {
			File file = new ClassPathResource("").getFile();
			getPathInformation().add(new PathInformation(FlexPath.SRC_MAIN_FLEX, true, file));
			init();
		}
	}
}
