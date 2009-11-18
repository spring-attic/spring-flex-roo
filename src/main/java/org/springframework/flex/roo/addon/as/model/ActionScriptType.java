package org.springframework.flex.roo.addon.as.model;


public final class ActionScriptType implements Comparable<ActionScriptType> {

	private int array = 0;
	private boolean defaultPackage;
	private ASDataType dataType;
	private String fullyQualifiedTypeName;
	private String simpleTypeName;
	
	public static final ActionScriptType BOOLEAN_TYPE = new ActionScriptType("Boolean");
	public static final ActionScriptType INT_TYPE = new ActionScriptType("int");
	public static final ActionScriptType NULL_TYPE = new ActionScriptType("Null");
	public static final ActionScriptType NUMBER_TYPE = new ActionScriptType("Number");
	public static final ActionScriptType STRING_TYPE = new ActionScriptType("String");
	public static final ActionScriptType UINT_TYPE = new ActionScriptType("uint");
	public static final ActionScriptType VOID_TYPE = new ActionScriptType("void");
	public static final ActionScriptType OBJECT_TYPE = new ActionScriptType("Object");
	public static final ActionScriptType ARRAY_TYPE = new ActionScriptType("Array");
	public static final ActionScriptType DATE_TYPE = new ActionScriptType("Date");
	public static final ActionScriptType ERROR_TYPE = new ActionScriptType("Error");
	public static final ActionScriptType FUNCTION_TYPE = new ActionScriptType("Function");
	public static final ActionScriptType REGEXP_TYPE = new ActionScriptType("RegExp");
	public static final ActionScriptType XML_TYPE = new ActionScriptType("XML");
	public static final ActionScriptType XML_LIST_TYPE = new ActionScriptType("XMLList");
	
	public ActionScriptType(String fullyQualifiedTypeName) {
		this(fullyQualifiedTypeName, 0, ASDataType.TYPE);
	}
	
	public ActionScriptType(String fullyQualifiedTypeName, int array, ASDataType dataType) {
		if (fullyQualifiedTypeName == null || fullyQualifiedTypeName.length() == 0) {
			throw new IllegalArgumentException("Fully qualified type name required");
		}
		ActionScriptSymbolName.assertActionScriptNameLegal(fullyQualifiedTypeName);
		this.fullyQualifiedTypeName = fullyQualifiedTypeName;
		this.defaultPackage = !fullyQualifiedTypeName.contains(".");
		if (defaultPackage) {
			simpleTypeName = fullyQualifiedTypeName;
		} else {
			int offset = fullyQualifiedTypeName.lastIndexOf(".");
			simpleTypeName = fullyQualifiedTypeName.substring(offset+1);
		}
		if (!defaultPackage && !Character.isUpperCase(simpleTypeName.charAt(0))) {
			throw new IllegalArgumentException("The first letter of the type name portion must be uppercase (attempted '" + fullyQualifiedTypeName + "')");
		}
		
		this.array = array;
		this.dataType = dataType;
	}
	
	public ActionScriptPackage getPackage() {
		if (isDefaultPackage()) {
			return new ActionScriptPackage("");
		}
		int offset = fullyQualifiedTypeName.lastIndexOf(".");
		return new ActionScriptPackage(fullyQualifiedTypeName.substring(0, offset));
	}
	
	public boolean isDefaultPackage() {
		return defaultPackage;
	}
	
	public final int hashCode() {
		return this.fullyQualifiedTypeName.hashCode();
	}
	
	public boolean isArray() {
		return array > 0;
	}
	
	public int getArray() {
		return array;
	}
	
	/**
	 * @return the name (does not contain any periods; never null or empty)
	 */
	public String getSimpleTypeName() {
		return simpleTypeName;
	}

	/**
	 * @return the fully qualified name (complies with the rules specified in the constructor)
	 */
	public String getFullyQualifiedTypeName() {
		return fullyQualifiedTypeName;
	}
	
	public ASDataType getDataType() {
		return dataType;
	}
	
	public final boolean equals(Object obj) {
		// NB: Not using the normal convention of delegating to compareTo (for efficiency reasons)
		return obj != null && obj instanceof ActionScriptType && this.fullyQualifiedTypeName.equals(((ActionScriptType)obj).fullyQualifiedTypeName) && this.dataType == ((ActionScriptType)obj).dataType;
	}

	public final int compareTo(ActionScriptType o) {
		// NB: If adding more fields to this class ensure the equals(Object) method is updated accordingly 
		if (o == null) return -1;
		return this.fullyQualifiedTypeName.compareTo(o.fullyQualifiedTypeName);
	}
}
