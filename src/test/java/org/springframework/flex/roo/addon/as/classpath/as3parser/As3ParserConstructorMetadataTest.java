package org.springframework.flex.roo.addon.as.classpath.as3parser;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import uk.co.badgersinfoil.metaas.ActionScriptFactory;
import uk.co.badgersinfoil.metaas.ActionScriptParser;
import uk.co.badgersinfoil.metaas.dom.ASCompilationUnit;


public class As3ParserConstructorMetadataTest {

    @Mock
	private CompilationUnitServices cus;
	
	@Before
	public void setup() throws FileNotFoundException{
		InputStreamReader reader = new InputStreamReader(this.getClass().getResourceAsStream("Foo.as"));
		ActionScriptFactory fact = new ActionScriptFactory();
		ActionScriptParser parser = fact.newParser();
		ASCompilationUnit unit = parser.parse(reader);
		MockitoAnnotations.initMocks(this);
		new As3ParserConstructorMetadata("foo_as",unit.getType().getMethod("Foo"), cus);
	}
	
	@Test
	public void body(){
		
	}
}
