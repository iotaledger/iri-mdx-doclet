package org.iota.mddoclet.example;

import com.sun.javadoc.MethodDoc;

public class HTTP extends BaseExport {

    //TODO: Separate request and response delim & template
    
	@Override
	protected String getPost(boolean isVoid, MethodDoc command) {
	    return "GET http://localhost:14265/" + BaseExport.COMMAND_NAME + "?" + BaseExport.PARAMETERS;
	}
	
	@Override
	protected String exampleParamTemplate() {
	    return COMMAND_NAME + "=" + EXAMPLE;
	}
	
	@Override
	protected String getParamDelim() {
	    return "&";
	}

	@Override
	public String getName() {
		return "HTTP";
	}

    @Override
    public String getLanguage() {
        return "http";
    }

}
