package org.springframework.flex.roo.addon.as.model;

import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.flex.roo.addon.as.classpath.details.ASFieldMetadata;
import org.springframework.flex.roo.addon.as.classpath.details.DefaultASFieldMetadata;
import org.springframework.flex.roo.addon.as.classpath.details.metatag.ASMetaTagMetadata;
import org.springframework.roo.classpath.details.FieldMetadata;
import org.springframework.roo.model.DataType;
import org.springframework.roo.model.JavaPackage;
import org.springframework.roo.model.JavaSymbolName;
import org.springframework.roo.model.JavaType;

public abstract class ActionScriptMappingUtils {

	private static final Map<JavaType, ActionScriptType> javaToAmfTypeMap = new HashMap<JavaType, ActionScriptType>();
	
	static {
		javaToAmfTypeMap.put(new JavaType(Enum.class.getName()), ActionScriptType.STRING_TYPE);
		javaToAmfTypeMap.put(JavaType.STRING_OBJECT, ActionScriptType.STRING_TYPE);
		javaToAmfTypeMap.put(JavaType.BOOLEAN_OBJECT, ActionScriptType.BOOLEAN_TYPE);
		javaToAmfTypeMap.put(JavaType.BOOLEAN_PRIMITIVE, ActionScriptType.BOOLEAN_TYPE);
		javaToAmfTypeMap.put(JavaType.INT_OBJECT, ActionScriptType.INT_TYPE);
		javaToAmfTypeMap.put(JavaType.INT_PRIMITIVE, ActionScriptType.INT_TYPE);
		javaToAmfTypeMap.put(JavaType.SHORT_OBJECT, ActionScriptType.INT_TYPE);
		javaToAmfTypeMap.put(JavaType.SHORT_PRIMITIVE, ActionScriptType.INT_TYPE);
		javaToAmfTypeMap.put(JavaType.BYTE_OBJECT, ActionScriptType.INT_TYPE);
		javaToAmfTypeMap.put(new JavaType(Byte.class.getName(), 1, DataType.PRIMITIVE, null, null), ActionScriptType.INT_TYPE);
		javaToAmfTypeMap.put(new JavaType(Byte.class.getName(), 1, DataType.TYPE, null, null), new ActionScriptType("flash.utils.ByteArray"));
		javaToAmfTypeMap.put(JavaType.DOUBLE_OBJECT, ActionScriptType.NUMBER_TYPE);
		javaToAmfTypeMap.put(JavaType.DOUBLE_PRIMITIVE, ActionScriptType.NUMBER_TYPE);
		javaToAmfTypeMap.put(JavaType.LONG_OBJECT, ActionScriptType.NUMBER_TYPE);
		javaToAmfTypeMap.put(JavaType.LONG_PRIMITIVE, ActionScriptType.NUMBER_TYPE);
		javaToAmfTypeMap.put(JavaType.FLOAT_OBJECT, ActionScriptType.NUMBER_TYPE);
		javaToAmfTypeMap.put(JavaType.FLOAT_PRIMITIVE, ActionScriptType.NUMBER_TYPE);
		javaToAmfTypeMap.put(JavaType.CHAR_OBJECT, ActionScriptType.STRING_TYPE);
		javaToAmfTypeMap.put(JavaType.CHAR_PRIMITIVE, ActionScriptType.STRING_TYPE);
		javaToAmfTypeMap.put(new JavaType(Character.class.getName(), 1, DataType.TYPE, null, null), ActionScriptType.STRING_TYPE);
		javaToAmfTypeMap.put(new JavaType(Character.class.getName(), 1, DataType.PRIMITIVE, null, null), ActionScriptType.STRING_TYPE);
		javaToAmfTypeMap.put(new JavaType(BigInteger.class.getName(), 0, DataType.TYPE, null, null), ActionScriptType.STRING_TYPE);
		javaToAmfTypeMap.put(new JavaType(BigDecimal.class.getName(), 0, DataType.TYPE, null, null), ActionScriptType.STRING_TYPE);
		javaToAmfTypeMap.put(new JavaType(Calendar.class.getName(), 0, DataType.TYPE, null, null), ActionScriptType.DATE_TYPE);
		javaToAmfTypeMap.put(new JavaType(Date.class.getName(), 0, DataType.TYPE, null, null), ActionScriptType.DATE_TYPE);
	}
	
	public static ActionScriptType toActionScriptType(JavaType javaType) {
		if (javaToAmfTypeMap.containsKey(javaType)) {
			return javaToAmfTypeMap.get(javaType);
		}
		
		if (javaType.isCommonCollectionType()) {
			if(javaType.getSimpleTypeName().endsWith("Map")) {
				if (javaType.isArray()) {
					return ActionScriptType.ARRAY_TYPE;
				} else {
					return ActionScriptType.OBJECT_TYPE;
				}
			} else {
				return new ActionScriptType("mx.collections.ArrayCollection");
			}
		}
		
		return new ActionScriptType(javaType.getFullyQualifiedTypeName(), (javaType.isArray() ? 1 : 0), ASDataType.TYPE);
	}
	
	public static ActionScriptSymbolName toActionScriptSymbolName(JavaSymbolName name) {
		return new ActionScriptSymbolName(name.getSymbolName());
	}
	
	public static ActionScriptPackage toActionScriptPackage(JavaPackage javaPackage) {
		return new ActionScriptPackage(javaPackage.getFullyQualifiedPackageName());
	}
	
	public static ASTypeVisibility toASTypeVisibility(int mod) {
		if (Modifier.isPublic(mod)) {
			return ASTypeVisibility.PUBLIC;
		} else if (Modifier.isProtected(mod)) {
			return ASTypeVisibility.PROTECTED;
		} else if (Modifier.isPrivate(mod)) {
			return ASTypeVisibility.PRIVATE;
		} else {
			return ASTypeVisibility.DEFAULT;
		}
	}

	public static ASFieldMetadata toASFieldMetadata(String asEntityId, FieldMetadata javaField, List<ASMetaTagMetadata> metaTags, boolean makePublic) {
		return new DefaultASFieldMetadata(asEntityId, toActionScriptType(javaField.getFieldType()), toActionScriptSymbolName(javaField.getFieldName()), 
				(makePublic ? ASTypeVisibility.PUBLIC : toASTypeVisibility(javaField.getModifier())), metaTags);
	}
	
}
