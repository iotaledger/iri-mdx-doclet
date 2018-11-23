package org.iota.mddoclet.example;

public class HTTP extends BaseExport {

    //TODO: Separate request and response delim & template
    
	@Override
	protected String getPost() {
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
