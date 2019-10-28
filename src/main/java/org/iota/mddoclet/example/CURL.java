package org.iota.mddoclet.example;

import com.sun.javadoc.MethodDoc;

public class CURL extends BaseExport {

	@Override
	protected String getPost(boolean isVoid, MethodDoc command) {
		return "curl http://localhost:14265 \n" +
			   "-X POST \n" +
			   "-H 'Content-Type: application/json' \n" +
			   "-H 'X-IOTA-API-Version: 1' \n" +
			   "-d '{ \n" +
			     getIndent(true) + "\"command\": \"" + BaseExport.COMMAND_NAME + "\", \n" +
			     BaseExport.PARAMETERS +
			   "}'";
	}

	@Override
	public String getName() {
		return "cURL";
	}

    @Override
    public String getLanguage() {
        return "bash";
    }
}
