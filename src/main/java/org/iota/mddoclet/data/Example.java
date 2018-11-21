package org.iota.mddoclet.data;

public class Example {

	private String example;
	private String responseOk;
	private String responseError;
	private String generator;
	private String language;
	
	public Example(String example, String responseOk, String responseError, String generator, String language) {
		this.example = example;
		this.responseOk = responseOk;
		this.generator = generator;
		this.language = language;
		this.responseError = responseError;
	}

	public String getExample() {
		return example;
	}
	
	public String getGenerator() {
		return generator;
	}

	public String getResponseOk() {
		return responseOk;
	}
	
	public boolean hasResponseError(){
		return responseError != null;
	}

	public String getResponseError() {
		return responseError;
	}
	
	public String getLanguage() {
        return language;
    }
}
