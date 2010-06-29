/*
 * Copyright 2002-2010 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.flex.roo.addon.as.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.roo.support.util.Assert;
import org.springframework.roo.support.util.StringUtils;

public final class ActionScriptSymbolName implements Comparable<ActionScriptSymbolName>{
	private String symbolName;
	
	public ActionScriptSymbolName(String symbolName) {
		Assert.hasText(symbolName, "Fully qualified type name required");
		assertActionScriptNameLegal(symbolName);
		this.symbolName = symbolName;
	}
	
	/**
	 * @return the symbol name (never null or empty)
	 */
	public String getSymbolName() {
		return symbolName;
	}
	
	/**
	 * @return the symbol name, capitalising the first letter (never null or empty)
	 */
	public String getSymbolNameCapitalisedFirstLetter() {
		return StringUtils.capitalize(symbolName);
	}
	
	/**
	 * 
	 * @return the symbol name in human readable form
	 */
	public String getReadableSymbolName() {
		Pattern p = Pattern.compile("[A-Z][^A-Z]*");
        Matcher m = p.matcher(StringUtils.capitalize(symbolName));
		StringBuilder string = new StringBuilder();
        while(m.find()){
            string.append(m.group()).append(" ");
        }
		return string.toString().trim();
	}
	
	public final int hashCode() {
		return this.symbolName.hashCode();
	}

	public final boolean equals(Object obj) {
		// NB: Not using the normal convention of delegating to compareTo (for efficiency reasons)
		return obj != null && obj instanceof ActionScriptSymbolName && this.symbolName.equals(((ActionScriptSymbolName)obj).symbolName);
	}

	public final int compareTo(ActionScriptSymbolName o) {
		// NB: If adding more fields to this class ensure the equals(Object) method is updated accordingly 
		if (o == null) return -1;
		return this.symbolName.compareTo(o.symbolName);
	}
	
	public final String toString() {
		return symbolName;
	}

	public static void assertActionScriptNameLegal(String fullyQualifiedTypeName) {
		// TODO Auto-generated method stub
		
	}

}
