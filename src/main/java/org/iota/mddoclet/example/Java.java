package org.iota.mddoclet.example;

public class Java extends BaseExport {

	@Override
	protected String getPost() {
		return  "IotaAPI iotaAPI = new IotaAPI.Builder().build();\n" + 
		        "\n" + 
		        "try { \n" + 
		        "    " + TYPE + " response = iotaAPI." + COMMAND_NAME + "(" + PARAMETERS + ");\n" +
		        "} catch (ArgumentException e) { \n" + 
    			"    // Handle error\n" + 
    			"    e.printStackTrace(); \n" + 
    			"}";
	}
	
	@Override
	protected String exampleParamTemplate() {
	    return EXAMPLE;
	}

	@Override
	public String getName() {
		return "Java";
	}

    @Override
    public String getLanguage() {
        return "Java";
    }

}
