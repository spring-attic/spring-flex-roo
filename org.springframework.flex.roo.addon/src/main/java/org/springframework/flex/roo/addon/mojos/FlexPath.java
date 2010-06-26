package org.springframework.flex.roo.addon.mojos;

import org.springframework.roo.project.Path;

public class FlexPath extends Path {

	public static final FlexPath SRC_MAIN_FLEX = new FlexPath("SRC_MAIN_FLEX");
	public static final FlexPath LIBS = new FlexPath("LIBS");
	
	public FlexPath(String name) {
		super(name);
	}

}
