package org.springframework.flex.roo.addon.as.model;

import org.springframework.roo.support.util.Assert;

public class ActionScriptPackage implements Comparable<ActionScriptPackage> {

	private String fullyQualifiedPackageName;
	
	public ActionScriptPackage(String fullyQualifiedPackageName) {
		Assert.notNull(fullyQualifiedPackageName, "Fully qualified package name required");
		ActionScriptSymbolName.assertActionScriptNameLegal(fullyQualifiedPackageName);
		Assert.isTrue(fullyQualifiedPackageName.toLowerCase().equals(fullyQualifiedPackageName), "The entire package name must be lowercase");
		this.fullyQualifiedPackageName = fullyQualifiedPackageName;
	}

	public String getFullyQualifiedPackageName() {
		return fullyQualifiedPackageName;
	}

	public final int hashCode() {
		return this.fullyQualifiedPackageName.hashCode();
	}

	public final boolean equals(Object obj) {
		return obj != null && obj instanceof ActionScriptPackage && this.compareTo((ActionScriptPackage)obj) == 0;
	}

	public final int compareTo(ActionScriptPackage o) {
		if (o == null) return -1;
		return this.fullyQualifiedPackageName.compareTo(o.fullyQualifiedPackageName);
	}
	
	public final String toString() {
		return fullyQualifiedPackageName;
	}

}
