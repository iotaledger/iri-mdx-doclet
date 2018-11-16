package com.iota.mdxdoclet;

import com.iota.mdxdoclet.data.Example;
import com.iota.mdxdoclet.example.Export;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Tag;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The main parser class. It scans the given Doclet document root and creates
 * the MDX files.
 *
 * The work is done in Freemarker templates.
 *
 * Based on work from https://github.com/neuhalje/markdown-doclet
 */
public class Parser {

	private final Configuration configuration;
	private final Util util;

	private List<Export> exports;

	Parser(Configuration configuration, Util util) {
		this.configuration = configuration;
		this.util = util;
		this.exports = new ArrayList<>();
	}

	public void renderMethod(OutputStream out, MethodDoc methodDoc, MethodCall call) throws IOException, TemplateException {
		Writer w = new OutputStreamWriter(out);
		render(w, methodDoc, "method.ftl", call);
	}

	private void render(Writer w, MethodDoc doc, String templateName, MethodCall api)
			throws IOException, TemplateException {
		Template template = configuration.getTemplate(templateName);
		Map<String, Object> input = new HashMap<String, Object>();

		// Check for return class
		Tag[] returnTags = doc.tags("return");
		ClassDoc c = util.getReturnClass(returnTags);
		if (c != null) {
			input.put("returnclass", c);
		}
		
		//Only generate methods once, if c == null, returns empty list
		List<MethodDoc> methods = publicGetMethods(c);

		// Make the examples
		List<Example> examples = new java.util.ArrayList<>();
		for (Export x : exports) {
			//Response based on return class or default + return var, or default in case of void
			String response;
			if (c != null) {
				response = x.generateResponse(doc, api, methods);
			} else {
				response = x.generateResponse(doc, api);
			}
			
			examples.add(new Example(
				x.generateExample(doc, api), 
				response,
				x.generateError(), 
				x.getName(),
				x.getLanguage())
			);
		}

		input.put("examples", examples.toArray(new Example[] {}));
		input.put("lineNumber", doc.position().line() + "");
		input.put("subject", doc);
		input.put("util", util);
		input.put("name", api.toString());
		template.process(input, w);
	}
	
	public ArrayList<MethodDoc> publicGetMethods(ClassDoc doc) {
		return publicGetMethods(doc, new ArrayList<MethodDoc>());
	}

	private ArrayList<MethodDoc> publicGetMethods(ClassDoc c, ArrayList<MethodDoc> docs) {
		if (c == null)
			return docs;
		
		if (c.superclass() != null) {
			publicGetMethods(c.superclass(), docs);
		}

		for (MethodDoc m : c.methods()) {
			if (m.isPublic() && m.inlineTags().length > 0 && m.name().startsWith("get")) {
				docs.add(m);
			}
		}
		return docs;
	}

	public void addExport(Export export) {
		exports.add(export);
	}
}