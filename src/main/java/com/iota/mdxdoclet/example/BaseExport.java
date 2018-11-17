package com.iota.mdxdoclet.example;

import java.util.Random;

import com.iota.mdxdoclet.DocumentMethodAnnotation;
import com.iota.mdxdoclet.data.ReturnParam;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.Type;

public abstract class BaseExport implements Export {

	protected static final String CMD = "%cmd";

	@Override
	public String generateExample(MethodDoc command, DocumentMethodAnnotation api) {
		
		String start = getPost();
		StringBuilder generatedCommand = new StringBuilder("{\"command\": \"" + api.name() + "\"");
		
		for (Parameter p : command.parameters()){
			String name = p.name();
			
			generatedCommand.append(", ");
			generatedCommand.append(generateExampleForCallAndType(api, name, p.type()));
		}
		generatedCommand.append("}");
		
		return start.replace(CMD, generatedCommand.toString());
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
	
	private String generateExampleForCallAndType(DocumentMethodAnnotation api, String argname, Type t) {
		String type = t.typeName();
		if (t.asParameterizedType() != null) {
			type = t.asParameterizedType().typeArguments()[0].typeName();
		}
		
		StringBuilder generatedCommand = new StringBuilder("");
		generatedCommand.append("\"" + argname + "\": ");
		if (t.dimension().equals("[]") || t.asParameterizedType() != null) { //parameterized is a list of sorts, or T
			generatedCommand.append("[");
			generatedCommand.append("\"" + getExampleData(api.name(), argname, type) + "\"");
			generatedCommand.append(", ");
			generatedCommand.append("\"" + getExampleData(api.name(), argname, type) + "\"");
			generatedCommand.append("]");
		} else {
			generatedCommand.append("\"" + getExampleData(api.name(), argname, type) + "\"");
		}
		return generatedCommand.toString();
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
			return  "{ \n" +
				"\"address\": \"/8.8.8.8:14265\", \n" +
	            "\"numberOfAllTransactions\": " + exampleInt() + ", \n" +
	            "\"numberOfInvalidTransactions\": " + exampleInt() + ", \n" +
	            "\"numberOfNewTransactions\": " + exampleInt() + " \n" +
	        "}";
		}
		
		return "missing_data";
	}

	protected abstract String getPost();

	protected String getResponse() {
		//Normally empty, could be filled with just duration, 
		return "{" + CMD + "}";
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
