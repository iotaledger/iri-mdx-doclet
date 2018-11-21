package org.iota.mddoclet.data;

import com.sun.javadoc.Type;

public class ReturnParam {

	private String text;
	private String name;
	private Type returnType;

	public ReturnParam(String name, String text) {
		this(name, text, null);
	}
	
	public ReturnParam(String name, String text, Type returnType) {
        this.name = name;
        this.text = text;
        this.returnType = returnType;
    }
	
	public boolean hasReturnType() {
        return returnType != null;
    }
	
	public Type getReturnType() {
        return returnType;
    }
	
	public String getName() {
		return name;
	}
	
	public String getText() {
		return text;
	}
}
