package org.iota.mddoclet.example;

import java.security.SecureRandom;
import java.util.Random;

import org.iota.mddoclet.DocumentMethodAnnotation;
import org.iota.mddoclet.data.ReturnParam;

import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.Type;

public abstract class BaseExport implements Export {

    /**
     * Marker for the area of the entire command
     */
    protected static final String CMD = "%cmd";
    
    /**
     * Marker for the area of the response type of a method
     */
    protected static final String TYPE = "%response";
    
    /**
     * Marker for the command name
     */
    protected static final String COMMAND_NAME = "%command";
    
    /**
     * Marker for the area of method parameters
     */
    protected static final String PARAMETERS = "%parameters";
    
    /**
     * Marker for the area of an example data string
     */
    protected static final String EXAMPLE = "%example";

    protected String requestDelim, responseDelim;
    protected String requestFormat, responseFormat;

    private Random random = new Random();

    @Override
    public String generateExample(MethodDoc command, DocumentMethodAnnotation api) {
        String post = getPost();

        StringBuilder generatedParams = new StringBuilder();
        boolean first = true;
        for (Parameter p : command.parameters()) {
            String name = p.name();

            if (!first)
                generatedParams.append(getParamDelim());
            else
                first = false;

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
        String responseObject = "\"duration\": " + randomInt();
        if (!command.returnType().typeName().equals("void") && api.hasParam()) {

            // We assume its just returning an array of trytes, which the API calls without
            // Response do
            responseObject += ", \"" + api.getParam() + "\": "
                    + generateExampleForCallAndType(api, api.getParam(), command.returnType()) + "";
        }

        return start.replace(CMD, responseObject);
    }

    @Override
    public String generateResponse(MethodDoc command, DocumentMethodAnnotation api, ReturnParam[] fields) {
        String start = getResponse();
        StringBuilder generatedCommand = new StringBuilder("");

        for (int i = 0; i < fields.length; i++) {
            ReturnParam field = fields[i];

            if (i != 0)
                generatedCommand.append(", ");
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
     * Returns example data for the given arguments.
     * Makes an array with 2 examples when it is appropriate.
     * 
     * @param api The method we are documenting
     * @param argName The name of the argument/parameter in this method
     * @param argType The type of this argument
     * @return An example
     */
    protected String generateExampleForCallAndType(DocumentMethodAnnotation api, String argName, Type argType) {
        String example = exampleParamTemplate();

        String type = argType.typeName();
        if (argType.asParameterizedType() != null) {
            type = argType.asParameterizedType().typeArguments()[0].typeName();
        }

        StringBuilder generatedCommand = new StringBuilder("");

        if (argType.dimension().equals("[]") || argType.asParameterizedType() != null) { // parameterized is a list of
                                                                                         // sorts, or T
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

    /**
     * Finds the proper example based on command, name and type
     * 
     * @param command The name of the command
     * @param fullName The name of the parameter
     * @param returnType The return type of the parameter
     * @return Example data
     */
    private String getExampleData(String command, String fullName, String returnType) {
        String name = fullName.toLowerCase();

        if (name.equals("minweightmagnitude")) {
            return "18";
        } else if (name.equals("depth")) {
            return "15";
        } else if (name.equals("threshold")) {
            return "100";
        } else if (name.startsWith("uri")) {
            return "udp://8.8.8.8:14265";
        } else if (returnType.equals("Hash") || name.equals("trytes") || name.equals("trytes2")
                || name.contains("address") || (name.contains("milestone") && !name.contains("index"))) {
            return randomHash() + randomHash();
        } else if (name.contains("memory")) {
            return randomInt(6) + "G";
        } else if (returnType.equals("Integer") || returnType.equals("int")) {
            return randomInt() + "";
        } else if (returnType.equals("Boolean") || returnType.equals("boolean")) {
            return randomBoolean();
        } else if (returnType.equals("Neighbor") || returnType.equals("GetNeighborsResponse.Neighbor")) {
            // TODO auto generate this
            // TODO Solution if we dont want JSON response
            return "{ \n" 
                    + "\"address\": \"/8.8.8.8:14265\", \n" 
                    + "\"numberOfAllTransactions\": " + randomInt() + ", \n" 
                    + "\"numberOfRandomTransactionRequests\": " + randomInt() + " \n"
                    + "\"numberOfNewTransactions\": " + randomInt() + " \n" 
                    + "\"numberOfInvalidTransactions\": " + randomInt() + ", \n" 
                    + "\"numberOfStaleTransactions\": " + randomInt() + " \n"
                    + "\"numberOfSentTransactions\": " + randomInt() + " \n" 
                    + "\"connectiontype\": " + randomConnectionType() + " \n" 
                    + "}";
        } else if (command.equals("getNodeInfo")) {
            return name;
        } else if (returnType.equals("String")) {
            return randomHash();
        }

        return name;
    }

    protected abstract String getPost();

    /**
     * The template for a response call
     * 
     * @return A string filled with a {@value #CMD}
     */
    protected String getResponse() {
        // Normally empty, could be filled with just duration,
        return "{" + CMD + "}";
    }

    /**
     * The delimitation used in between parameters
     * 
     * @return a string to be placed when another parameter follows
     */
    protected String getParamDelim() {
        return ", ";
    }

    /**
     * The parameter has a format for displaying its example data.
     * 
     * @returnA string filled with {@value #COMMAND_NAME} and {@value #EXAMPLE}
     */
    protected String exampleParamTemplate() {
        return "\"" + COMMAND_NAME + "\": " + EXAMPLE;
    }

    @Override
    public String getLanguage() {
        return getName();
    }

    /**
     * 
     * @return TCP or UDB randomly
     */
    private String randomConnectionType() {
        int rand = random.nextInt(2);
        return rand == 0 ? "TCP" : "UDP";
    }

    /**
     * 
     * @return true or false randomly
     */
    private String randomBoolean() {
        int rand = random.nextInt(2);
        return rand == 0 ? "true" : "false";
    }

    /**
     * 
     * @return A random string of 81 trytes
     */
    private String randomHash() {
        return generateTrytes(81);
    }

    private static final char[] TRYTE_ALPHABET = "9ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    public static String generateTrytes(int size) {
        StringBuilder builder = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < size; i++) {
            char c = TRYTE_ALPHABET[random.nextInt(TRYTE_ALPHABET.length)];
            builder.append(c);
        }
        return builder.toString();
    }

    /**
     * 
     * @return an integer from 1 to 999
     */
    protected int randomInt() {
        return randomInt(999);
    }

    /**
     * 
     * @param max the maximum random integer to get
     * @return an integer from 1 to max (including)
     */
    protected int randomInt(int max) {
        return random.nextInt(max) + 1;
    }
}
