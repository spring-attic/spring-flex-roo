package org.springframework.flex.roo.addon.as.classpath.as3parser;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.File;

import org.junit.Before;
import org.mockito.Mock;
import org.springframework.core.io.ClassPathResource;
import org.springframework.flex.roo.addon.as.classpath.ASPhysicalTypeIdentifier;
import org.springframework.flex.roo.addon.as.classpath.ASPhysicalTypeMetadataProvider;
import org.springframework.roo.file.monitor.event.FileDetails;
import org.springframework.roo.metadata.MetadataService;
import org.springframework.roo.process.manager.ActiveProcessManager;
import org.springframework.roo.process.manager.FileManager;
import org.springframework.roo.process.manager.ProcessManager;


public class As3ParserClassMetadataMutableTypeDetailsTests {

	@Mock
	FileManager fileManager;
	
	@Mock
	MetadataService metadataService;
	
	@Mock
	ASPhysicalTypeMetadataProvider provider;
	
	@Mock
	ProcessManager processManager;
	
	@Mock
	FileDetails fileDetails;

	private String fileIdentifier;

	private String metadataId;

	private File testFile;

	private As3ParserClassMetadata metadata;

	private As3ParserMutableClassOrInterfaceTypeDetails details;
	
	@Before
	public void setUp() throws Exception {
		initMocks(this);
		when(processManager.isDevelopmentMode()).thenReturn(true);
		ActiveProcessManager.setActiveProcessManager(processManager);
		fileIdentifier = "org/springframework/flex/roo/addon/as/classpath/as3parser/FooImpl.as";
		metadataId = "MID:"+ASPhysicalTypeIdentifier.class.getName()+"#SRC_MAIN_FLEX?com.foo.stuff.FooImpl";
		when(fileManager.exists(fileIdentifier)).thenReturn(true);
		when(fileManager.readFile(fileIdentifier)).thenReturn(fileDetails);
		testFile = new ClassPathResource(fileIdentifier).getFile();
		when(fileDetails.getFile()).thenReturn(testFile);
		
		metadata = new As3ParserClassMetadata(fileManager, fileIdentifier, metadataId, metadataService, provider);
		assertNotNull(metadata);
		assertNotNull(metadata.getPhysicalTypeDetails());
		details = (As3ParserMutableClassOrInterfaceTypeDetails) metadata.getPhysicalTypeDetails();
	}
}
