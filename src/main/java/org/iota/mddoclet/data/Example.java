package org.iota.mddoclet.data;

public class Example {

	private String example;
	private String responseOk;
	private String responseError;
	private String generator;
	private String language;
	
	/**
	 * Create a new example used in pages/templates
	 * 
	 * @param example The request
	 * @param responseOk The successful return
	 * @param responseError The error return
	 * @param generator generator name
	 * @param language langauge name
	 */
	public Example(String example, String responseOk, String responseError, String generator, String language) {
		this.example = example;
		this.responseOk = responseOk;
		this.generator = generator;
		this.language = language;
		this.responseError = responseError;
	}

	/**
	 * The example for running this method
	 * 
	 * @return a filled example including parameters and example data
	 */
	public String getExample() {
		return example;
	}
	
	/**
	 * 
	 * @return the response you get when a request is successfully executed
	 */
	public String getResponseOk() {
		return responseOk;
	}
	
	/**
	 * 
	 * @return if we have a error response string made or not
	 */
	public boolean hasResponseError(){
		return responseError != null;
	}

	/**
	 * 
	 * @return if we have a error response, returns the response. Otherwise <code>null</code>
	 */
	public String getResponseError() {
		return responseError;
	}
	
	/**
     * 
     * @return The name of the generator used to make this example
     */
    public String getGenerator() {
        return generator;
    }

	
	/**
	 * 
	 * @return The name of the language used to make this example
	 */
	public String getLanguage() {
        return language;
    }
}
