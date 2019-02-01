package org.iota.mddoclet.example;

public class Python extends BaseExport {

	@Override
	protected String getPost() {
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
				"returnData = urllib2.urlopen(request).read()\n" + 
				"\n" + 
				"jsonData = json.loads(returnData)\n" + 
				"\n" + 
				"print jsonData";
	}

	@Override
	public String getName() {
		return "Python";
	}
}
