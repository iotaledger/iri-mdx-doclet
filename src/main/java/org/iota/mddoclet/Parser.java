package org.iota.mddoclet;

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

import org.iota.mddoclet.data.Example;
import org.iota.mddoclet.data.ReturnParam;
import org.iota.mddoclet.example.Export;

/**
 * The main parser class. It scans the given Doclet document root and creates
 * the MD files.
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

	public void renderMethod(OutputStream out, MethodDoc methodDoc, DocumentMethodAnnotation call) throws IOException, TemplateException {
		Writer w = new OutputStreamWriter(out);
		render(w, methodDoc, call);
	}

	private void render(Writer w, MethodDoc doc, DocumentMethodAnnotation api) throws IOException, TemplateException {
		Template template = configuration.getTemplate(api.getTemplate().getFileName());
		Map<String, Object> input = new HashMap<String, Object>();

		// Check for return class
		Tag[] returnTags = doc.tags("return");
		ClassDoc c = util.getReturnClass(returnTags);
		
		if (c != null) {
			input.put("returnclass", c);
		} else {
		    if (!doc.returnType().isPrimitive()) {
		        input.put("returnclass", doc.returnType().asClassDoc());
		    }
		}
		
		//Only generate fields once, if c == null, returns empty list
		ReturnParam[] returnFields = util.parseReturnTag(returnTags, c, doc, api);
		ReturnParam[] parameters = util.parseParameters(doc);

		// Make the examples
		Example[] examples = new Example[exports.size()];
		for (int i = 0; i < exports.size(); i++) {
            Export x = exports.get(i);
            
			//Response based on return class or default + return var, or default in case of void
			String response = x.generateResponse(doc, api, returnFields);
			
			examples[i] = new Example(
				x.generateExample(doc, api), 
				response,
				x.generateError(), 
				x.getName(),
				x.getLanguage()
			);
			
		}
		
		input.put("parameters", parameters);
		input.put("returnParams", returnFields);
		input.put("examples", examples);
		input.put("lineNumber", doc.position().line() + "");
		input.put("subject", doc);
		input.put("util", util);
		input.put("name", api.name());
		template.process(input, w);
	}

	public void addExport(Export export) {
		exports.add(export);
	}
}