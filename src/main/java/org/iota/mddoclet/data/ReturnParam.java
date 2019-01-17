package org.iota.mddoclet.data;

import com.sun.javadoc.Type;

/**
 * 
 * This class represents a processed return parameter.
 * A text can be created in multiple ways based on the return type (void, primitive, Object) 
 *
 */
public class ReturnParam {

	private String text;
	private String name;
	private Type returnType;

	/**
	 * 
	 * @param name The name of this method return
	 * @param text The text of this method return
	 */
	public ReturnParam(String name, String text) {
		this(name, text, null);
	}
	
	/**
	 * 
	 * @param name The name of this method return
	 * @param text The text of this method return
	 * @param returnType The type of this method return
	 */
	public ReturnParam(String name, String text, Type returnType) {
        this.name = name;
        this.text = text;
        this.returnType = returnType;
    }
	
	/**
	 * 
	 * @return <code>false</code> if the method is void, otherwise <code>true</code>
	 */
	public boolean hasReturnType() {
        return returnType != null;
    }
	
	/**
	 * 
	 * @return the type this method returns, can be <code>null</code>
	 */
	public Type getReturnType() {
        return returnType;
    }
	
	/**
	 * 
	 * @return the name used to display this method return 
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * 
	 * @return the text used to explain this method return 
	 */
	public String getText() {
		return text;
	}
}
