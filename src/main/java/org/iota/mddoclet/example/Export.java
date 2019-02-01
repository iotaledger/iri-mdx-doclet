package org.iota.mddoclet.example;

import org.iota.mddoclet.DocumentMethodAnnotation;
import org.iota.mddoclet.data.ReturnParam;

import com.sun.javadoc.MethodDoc;

/**
 * 
 * An export is a way of generating display-able data for a command in a language
 * 
 */
public interface Export {
	
	/**
	 * Generates an example based on parameters from the Statement in API
	 * 
	 * @param command The Statement Method
	 * @param api The Related API command
	 * @return the example code
	 */
	String generateExample(MethodDoc command, DocumentMethodAnnotation api);

	
	/**
	 * Generates basic response data based on MethodDoc return value
	 * 
	 * @param command The command the response is from (Statement in API)
	 * @param api The API command
	 * @return the response code
	 */
	String generateResponse(MethodDoc command, DocumentMethodAnnotation api);
	
	/**
	 * Generates response data based on a list of methods
	 * 
	 * @param command The command the response is from (Statement in API)
	 * @param api The API command
	 * @param fields The list of fields (only those with javadoc)
	 * @return the response code
	 */
	String generateResponse(MethodDoc command, DocumentMethodAnnotation api, ReturnParam[] fields);
	
	/**
	 * Generates an example error
	 * 
	 * @return the error code
	 */
	String generateError();
	
	/**
	 * The name of this generator (cURL, python, NodeJS)
	 * 
	 * @return the name
	 */
	String getName();


	/**
	 * The language is the actual coding language we format this example at (Nodejs->javascript)
	 * 
	 * @return the language
	 */
	String getLanguage();
}
