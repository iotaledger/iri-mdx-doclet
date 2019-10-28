package org.iota.mddoclet.example;

import com.sun.javadoc.MethodDoc;

public class Python extends BaseExport {

	@Override
	protected String getPost(boolean isVoid, MethodDoc command) {
		return "import urllib2\n" + 
				"import json\n" + 
				"\n" + 
				"command = " + BaseExport.CMD + "\n" + 
				"\n" + 
				"stringified = json.dumps(command)\n" + 
				"\n" + 
				"headers = {\n" + 
				"    'content-type': 'application/json',\n" + 
				"    'X-IOTA-API-Version': '1'\n" + 
				"}\n" + 
				"\n" + 
				"request = urllib2.Request(url=\"http://localhost:14265\", data=stringified, headers=headers)\n" + 
				(isVoid ? 
    				"returnData = urllib2.urlopen(request).read()\n" + 
    				"\n" + 
    				"jsonData = json.loads(returnData)\n" + 
    				"\n" + 
    				"print jsonData"
    			: "urllib2.urlopen(request).read()\\n");
	}

	@Override
	public String getName() {
		return "Python";
	}
}
