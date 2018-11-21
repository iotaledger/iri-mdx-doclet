package org.iota.mddoclet.example;

import org.iota.mddoclet.DocumentMethodAnnotation;

import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;

public class Java extends BaseExport {
    
    public static final String RESPONSE_TYPE = "%response";
    public static final String COMMAND_NAME = "%command";
    public static final String PARAMETERS = "%parameters";
    
    @Override
    public String generateExample(MethodDoc command, DocumentMethodAnnotation api) {
        String start = getPost();
        
        StringBuilder args = new StringBuilder();
        boolean first = true;
        for (Parameter p : command.parameters()){
            String name = p.name();
            
            if (!first) args.append(", ");
            else first = false;
            
            args.append(generateExampleForCallAndType(api, name, p.type(), false));
        }
        
        start = start.replace(COMMAND_NAME, api.getProgrammedName());
        start = start.replace(PARAMETERS, args.toString());
        start = start.replace(RESPONSE_TYPE, command.returnType().simpleTypeName());
        return start;
    }

	@Override
	protected String getPost() {
		return  "IotaAPI iotaAPI = new IotaAPI.Builder().build();\n" + 
		        "\n" + 
		        "try { \n" + 
		        "    " + RESPONSE_TYPE + " response = iotaAPI." + COMMAND_NAME + "(" + PARAMETERS + ");\n" +
		        "} catch (ArgumentException e) { \n" + 
    			"    // Handle error\n" + 
    			"    e.printStackTrace(); \n" + 
    			"}";
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
