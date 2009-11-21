package org.springframework.flex.roo.addon.as.classpath.as3parser;

import org.springframework.flex.roo.addon.as.model.ASTypeVisibility;

import uk.co.badgersinfoil.metaas.dom.Visibility;
import junit.framework.TestCase;

public class As3ParserUtilsTest extends TestCase {

	public void testGetASTypeVisibility() {
		ASTypeVisibility t = As3ParserUtils.getASTypeVisibility(Visibility.DEFAULT);
		System.out.println(t);
	}
}
