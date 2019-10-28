package org.iota.mddoclet.example;

import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Type;

public class Java extends BaseExport {

	@Override
	protected String getPost(boolean isVoid, MethodDoc command) {
	    boolean acc = command.containingClass().simpleTypeName().equalsIgnoreCase("Account");
	    
		return  "IotaAPI iotaAPI = new IotaAPI.Builder().build();\n" + 
		        (acc ? "IotaAccount account = new IotaAccount.Builder(\"MY9SEED9..\").api(iotaAPI).build()" : "") + 
		        "\n" + 
		        "try { \n" + 
		        "    " + (isVoid ? "" : TYPE + " response = ") + (acc ? "account" : "iotaAPI") + "." + COMMAND_NAME + "(" + PARAMETERS + ");\n" +
		        "} catch (" + (acc ? "AccountError" : "ArgumentException") + " e) { \n" + 
    			"    // Handle error\n" + 
    			"    e.printStackTrace(); \n" + 
    			"}";
	}
	
	@Override
	protected String exampleParamTemplate() {
	    return EXAMPLE;
	}
	
	@Override
	protected String getParamDelim() {
	    return ", ";
	}
	
	@Override
	protected String getIndent(boolean request) {
	    return request ? "" : super.getIndent(request);
	}

	@Override
	public String getName() {
		return "Java";
	}

    @Override
    public String getLanguage() {
        return "Java";
    }
    
    @Override
    protected boolean addQuotes(String type) {
        return type.equalsIgnoreCase("string");
    }

    @Override
    protected String arrayStart(String type) {
        return "new " + type + "[]{";
    }
    
    @Override
    protected String arrayEnd(String type) {
        return "}";
    }
    
    @Override
    protected String parameterisedStart(String type, Type argType) {
        return "new " + argType.simpleTypeName() + "<" + type + ">(" + arrayStart(type);
    }
    
    @Override
    protected String parameterisedEnd(String type, Type argType) {
        return arrayEnd(type) + ")";
    }
    
    @Override
    protected String getExampleData(String command, String fullName, String returnType) {
        if (returnType.equalsIgnoreCase("AddressRequest")) {
            return "new AddressRequest.Builder().amount(5).checksum(true).build()";
        } else if (returnType.equalsIgnoreCase("Stopwatch")) {
            return "new Stopwatch()";
        } else if (returnType.equalsIgnoreCase("ExpireCondition")) {
            return "null";
        } else if (returnType.equalsIgnoreCase("Recipient")) {
            return "new Recipient(" + randomInt(500) + ", \"hi\", \"TAG\", " + randomHash() + ")";
        }
        
        return super.getExampleData(command, fullName, returnType);
    }
}
