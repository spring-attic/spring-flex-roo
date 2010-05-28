package org.springframework.flex.roo.addon.as.classpath.as3parser;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.core.io.ClassPathResource;
import org.springframework.flex.roo.addon.as.classpath.ASPhysicalTypeIdentifier;
import org.springframework.flex.roo.addon.as.classpath.details.DefaultFieldMetadata;
import org.springframework.flex.roo.addon.as.classpath.details.FieldMetadata;
import org.springframework.flex.roo.addon.as.classpath.details.metatag.DefaultMetaTagMetadata;
import org.springframework.flex.roo.addon.as.classpath.details.metatag.MetaTagMetadata;
import org.springframework.flex.roo.addon.as.model.ASTypeVisibility;
import org.springframework.flex.roo.addon.as.model.ActionScriptSymbolName;
import org.springframework.flex.roo.addon.as.model.ActionScriptType;
import org.springframework.flex.roo.addon.mojos.FlexMojosPathResolver;
import org.springframework.flex.roo.addon.mojos.FlexPath;
import org.springframework.flex.roo.addon.mojos.FlexPathResolver;
import org.springframework.roo.metadata.MetadataDependencyRegistry;
import org.springframework.roo.metadata.MetadataService;
import org.springframework.roo.process.manager.ActiveProcessManager;
import org.springframework.roo.process.manager.MutableFile;
import org.springframework.roo.process.manager.ProcessManager;
import org.springframework.roo.process.manager.internal.DefaultFileManager;
import org.springframework.roo.project.PathInformation;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.StringUtils;

import uk.co.badgersinfoil.metaas.ActionScriptFactory;
import uk.co.badgersinfoil.metaas.dom.ASClassType;
import uk.co.badgersinfoil.metaas.dom.ASCompilationUnit;


public class As3ParserClassMetadataMutableTypeDetailsTests {

	private static final Log log = LogFactory.getLog(As3ParserClassMetadataMutableTypeDetailsTests.class);

	private TestFileManager fileManager;
	
	@Mock
	private MetadataService metadataService;
	
	@Mock
	private MetadataDependencyRegistry registry;
	
	@Mock
	private ProcessManager processManager;
	
	@Mock
	private MutableFile updateFile;
		
	private FlexPathResolver pathResolver;

	private String metadataId;
	
	private As3ParserMetadataProvider provider;
	
	private ActionScriptFactory factory = new ActionScriptFactory();

	private As3ParserClassMetadata metadata;

	private As3ParserMutableClassOrInterfaceTypeDetails details;
	
	private ByteArrayOutputStream outputStream;
	
	private String lastFile;
	
	@Before
	public void setUp() throws Exception {
		initMocks(this);
		when(processManager.isDevelopmentMode()).thenReturn(true);
		ActiveProcessManager.setActiveProcessManager(processManager);
		String fileIdentifier = new ClassPathResource("com/foo/stuff/FooImpl.as").getFile().getCanonicalPath();
		metadataId = "MID:"+ASPhysicalTypeIdentifier.class.getName()+"#SRC_MAIN_FLEX?com.foo.stuff.FooImpl";
		
		fileManager = new TestFileManager();
		pathResolver = new TestPathResolver();
		
		provider = new As3ParserMetadataProvider();
		ReflectionTestUtils.setField(provider, "fileManager", fileManager);
		ReflectionTestUtils.setField(provider, "metadataService", metadataService);
		ReflectionTestUtils.setField(provider, "metadataDependencyRegistry", registry);
		ReflectionTestUtils.setField(provider, "pathResolver", pathResolver);
		
		metadata = new As3ParserClassMetadata(fileManager, fileIdentifier, metadataId, metadataService, provider);
		assertNotNull(metadata);
		assertNotNull(metadata.getPhysicalTypeDetails());
		details = (As3ParserMutableClassOrInterfaceTypeDetails) metadata.getPhysicalTypeDetails();
		
		lastFile = "";
		outputStream = new ByteArrayOutputStream();
		
		when(updateFile.getOutputStream()).thenReturn(outputStream);
	}
	
	@After
	public void logFileContents() {
		if (StringUtils.hasText(lastFile)){
			if (log.isDebugEnabled()) {
				log.debug("\n"+lastFile);
			}
		}
	}
	
	@Test
	public void testAddSimpleField() throws UnsupportedEncodingException {
		
		FieldMetadata fieldMetadata = new DefaultFieldMetadata(metadataId, new ActionScriptType("String"), 
				new ActionScriptSymbolName("name"), ASTypeVisibility.PRIVATE, null);
		
		details.addField(fieldMetadata);
		
		readLastFile();
		ASCompilationUnit compUnit = factory.newParser().parse(new StringReader(lastFile));
		assertTrue(compUnit.getType() instanceof ASClassType);
		ASClassType clazz = (ASClassType) compUnit.getType();
		assertNotNull(clazz.getField("name"));
	}
	
	@Test
	public void testAddComplexField() throws UnsupportedEncodingException {
		
		List<MetaTagMetadata> metaTags = new ArrayList<MetaTagMetadata>();
		MetaTagMetadata metaTag = new DefaultMetaTagMetadata("Bindable", null);
		metaTags.add(metaTag);
		
		FieldMetadata fieldMetadata = new DefaultFieldMetadata(metadataId, new ActionScriptType("com.foo.other.Baz"), 
				new ActionScriptSymbolName("baz"), ASTypeVisibility.PRIVATE, metaTags);
		
		details.addField(fieldMetadata);
		
		readLastFile();
		ASCompilationUnit compUnit = factory.newParser().parse(new StringReader(lastFile));
		assertTrue(compUnit.getType() instanceof ASClassType);
		ASClassType clazz = (ASClassType) compUnit.getType();
		assertNotNull(clazz.getField("baz"));
		assertEquals("Baz",clazz.getField("baz").getType());
		assertTrue(compUnit.getPackage().findImports().contains("com.foo.other.Baz"));
	}
	
	private void readLastFile() throws UnsupportedEncodingException {
		this.lastFile = this.outputStream.toString(Charset.defaultCharset().toString());
	}
	
	private static class TestPathResolver extends FlexMojosPathResolver {
		
		public TestPathResolver() throws IOException {
			File file = new ClassPathResource("").getFile();
			getPathInformation().add(new PathInformation(FlexPath.SRC_MAIN_FLEX, true, file));
			init();
		}
	}
	
	private class TestFileManager extends DefaultFileManager {
		@Override
		public MutableFile updateFile(String fileIdentifier) {
			return updateFile;
		}
	}
}
