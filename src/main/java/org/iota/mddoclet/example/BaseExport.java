package org.iota.mddoclet.example;

import java.util.Random;

import org.iota.mddoclet.DocumentMethodAnnotation;
import org.iota.mddoclet.data.ReturnParam;

import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.Type;

public abstract class BaseExport implements Export {

	protected static final String CMD = "%cmd";
	protected static final String TYPE = "%response";
	protected static final String COMMAND_NAME = "%command";
	protected static final String PARAMETERS = "%parameters";
	protected static final String EXAMPLE = "%example";
	
	protected String requestDelim, responseDelim;
	protected String requestFormat, responseFormat;
	
	@Override
	public String generateExample(MethodDoc command, DocumentMethodAnnotation api) {
		String post = getPost();
		
		StringBuilder generatedParams = new StringBuilder();
		boolean first = true;
		for (Parameter p : command.parameters()){
			String name = p.name();
			
			if (!first) generatedParams.append(getParamDelim());
            else first = false;
			
			generatedParams.append(generateExampleForCallAndType(api, name, p.type()));
		}
		
		post = post.replace(COMMAND_NAME, api.name());
		post = post.replace(PARAMETERS, generatedParams.toString());
		post = post.replace(TYPE, command.returnType().simpleTypeName());
		return post;
	}
	
	@Override
	public String generateResponse(MethodDoc command, DocumentMethodAnnotation api) {
		String start = getResponse();
		String responseObject = "\"duration\": " + exampleInt();
		if (!command.returnType().typeName().equals("void") && api.hasParam()) {
			
			//We assume its just returning an array of trytes, which the API calls without Response do
			responseObject += ", \"" + api.getParam() + "\": " + generateExampleForCallAndType(api, api.getParam(), command.returnType()) + "";
		}
		
		return start.replace(CMD, responseObject);
	}
	
	
	@Override
	public String generateResponse(MethodDoc command, DocumentMethodAnnotation api, ReturnParam[] fields) {
		String start = getResponse();
		StringBuilder generatedCommand = new StringBuilder("");
		
		for (int i = 0; i < fields.length; i++) {
		    ReturnParam field = fields[i];

			if (i != 0) generatedCommand.append(", ");
			generatedCommand.append(generateExampleForCallAndType(api, field.getName(), field.getReturnType()));
			
		}
		start = start.replace(CMD, generatedCommand.toString());
		return start;
	}
	
	@Override
	public String generateError() {
		return "{\"error\": \"'command' parameter has not been specified\"}";
	}
	
	/**
	 * 
	 * @param api
	 * @param argName
	 * @param argType
	 * @param prependName
	 * @return
	 */
	protected String generateExampleForCallAndType(DocumentMethodAnnotation api, 
                                        	       String argName, 
                                        	       Type argType) {
	    String example = exampleParamTemplate();
	    
		String type = argType.typeName();
		if (argType.asParameterizedType() != null) {
			type = argType.asParameterizedType().typeArguments()[0].typeName();
		}
		
		StringBuilder generatedCommand = new StringBuilder("");
		
		if (argType.dimension().equals("[]") || argType.asParameterizedType() != null) { //parameterized is a list of sorts, or T
			generatedCommand.append("[");
			generatedCommand.append("\"" + getExampleData(api.name(), argName, type) + "\"");
			generatedCommand.append(", ");
			generatedCommand.append("\"" + getExampleData(api.name(), argName, type) + "\"");
			generatedCommand.append("]");
		} else {
			generatedCommand.append("\"" + getExampleData(api.name(), argName, type) + "\"");
		}
		
		example = example.replace(EXAMPLE, generatedCommand.toString());
		example = example.replace(COMMAND_NAME, argName);
		example = example.replace(TYPE, type);
		return example;
	}
	
	private String getExampleData(String command, String name, String returnType) {
		//Blergh
		if (name.equals("minWeightMagnitude")) {
			return "18";
		} else if (name.equals("depth")) {
			return "15";
		} else if (name.equals("threshold")) {
			return "100";
		} else if (name.equals("uris")) {
			return "udp://8.8.8.8:14265";	
		} else if (command.equals("getNodeInfo")) {
			return name;
		} else if (returnType.equals("String")) {
			return randomHash();
		} else if (returnType.equals("Hash") || name.equals("trytes") || name.equals("trytes2")) {
			return randomHash() + randomHash();
		} else if (returnType.equals("Integer") || returnType.equals("int")) {
			return exampleInt() + "";
		} else if (returnType.equals("Boolean") || returnType.equals("boolean")) {
			return "true";
		} else if (returnType.equals("Neighbor") || returnType.equals("GetNeighborsResponse.Neighbor")) {
			//TODO auto generate this
		    //TODO Solution if we dont want JSON response
			return  "{ \n" +
				"\"address\": \"/8.8.8.8:14265\", \n" +
	            "\"numberOfAllTransactions\": " + exampleInt() + ", \n" +
	            "\"numberOfInvalidTransactions\": " + exampleInt() + ", \n" +
	            "\"numberOfNewTransactions\": " + exampleInt() + " \n" +
	        "}";
		}
		
		return name;
	}

	protected abstract String getPost();

	protected String getResponse() {
		//Normally empty, could be filled with just duration, 
		return "{" + CMD + "}";
	}
	
	protected String getParamDelim() {
	    return ", ";
	}
	
	protected String exampleParamTemplate() {
	    return "\"" + COMMAND_NAME + "\": " + EXAMPLE;
	}
	
	private String randomHash() {
		return "P9KFSJVGSPLXAEBJSHWFZLGP9GGJTIO9YITDEHATDTGAFLPLBZ9FOFWWTKMAZXZHFGQHUOXLXUALY9999";
	}
	
	protected int exampleInt() {
		return new Random().nextInt(999) + 1;
	}
	
    @Override
    public String getLanguage() {
        return getName();
    }
}
