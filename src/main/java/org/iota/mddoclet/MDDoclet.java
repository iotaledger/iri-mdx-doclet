package org.iota.mddoclet;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.iota.mddoclet.example.CURL;
import org.iota.mddoclet.example.HTTP;
import org.iota.mddoclet.example.Java;
import org.iota.mddoclet.example.NodeJS;
import org.iota.mddoclet.example.Python;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationDesc.ElementValuePair;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.Doclet;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.RootDoc;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

public class MDDoclet extends Doclet {

    private static final Logger log = LoggerFactory.getLogger(MDDoclet.class);

    private static String version = "Unknown";

    /**
     * If empty/null, all classes are checked
     */
    private static List<String> classesList = null;

    private static String repoUrl = null;

    private static Template template;

    private static String extension = ".md";

    private Parser parser;

    public MDDoclet(RootDoc root) {

        Configuration configuration = new Configuration(new Version(2, 3, 26));
        // Where do we load the templates from:
        configuration.setClassForTemplateLoading(Parser.class, "/templates");

        // Some other recommended settings:
        configuration.setDefaultEncoding("UTF-8");
        configuration.setLocale(Locale.US);

        configuration.setBooleanFormat("yes,no");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        Util util = new Util();

        // https://github.com/iotaledger/iri/blob/dev/src/main/java/
        util.setRepoUrl(repoUrl);

        parser = new Parser(configuration, util);
        parser.addExport(new Python());
        parser.addExport(new NodeJS());
        parser.addExport(new CURL());
        parser.addExport(new Java());
        parser.addExport(new HTTP());
    }

    private void generate(ClassDoc apiDoc) {
        for (MethodDoc m : apiDoc.methods(false)) {
            DocumentMethodAnnotation call = getAnnotationData(m, Document.class.getSimpleName());
            if (call != null) {
                File classFile = new File(call.name() + extension);
                if (classFile.exists()) {
                    classFile = new File(call.name() + "(" + paramString(m.parameters()) + ")" + extension);
                }

                log.info("Generating Document(" + classFile.getName() + ")");
                
                try (FileOutputStream fileOutputStream = new FileOutputStream(classFile);
                        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream)) {

                    parser.renderMethod(bufferedOutputStream, m, call);

                } catch (TemplateException | IOException e) {
                    log.error("Processing method " + m.name() + " failed");
                    e.printStackTrace();
                } finally {

                }
            }
        }
    }

    private String paramString(Parameter[] parameters) {
        StringBuilder builder = new StringBuilder();
        for (Parameter p : parameters) {
            if (builder.length() != 0) {
                builder.append(", ");
            }
            builder.append(p.name());
        }
        
        return builder.toString();
    }

    /**
     * Reads annotations from a class. If no template name is specified, the default
     * template is used.
     * 
     * @param method
     * @param annotationName
     * @return DocumentMethodAnnotation
     */
    private DocumentMethodAnnotation getAnnotationData(MethodDoc method, String annotationName) {
        for (AnnotationDesc anon : method.annotations()) {
            if (anon.annotationType().name().equals(annotationName)) {
                ElementValuePair[] values = anon.elementValues();
                String templateName = valueFromPair(values, "template");
                String name = valueFromPair(values, "name");

                if (null == name) {
                    name = method.name();
                }

                Template template = templateName != null ? Template.getEnum(templateName) : MDDoclet.template;

                return new DocumentMethodAnnotation(method, name, valueFromPair(values, "returnParam"), template);
            }
        }
        return null;
    }

    private String valueFromPair(ElementValuePair[] values, String elemName) {
        for (ElementValuePair pair : values) {
            if (pair.element().name().equals(elemName)) {
                // First value is the entire object, second value is the data contained inside
                // Example:
                // value() = "name"
                // value().value() = name
                return pair.value().value().toString();
            }
        }
        return null;
    }

    /*
     * STATIC METHODS - USED BY DOCLET
     * 
     */

    public static boolean start(RootDoc root) {
        if (null == template) {
            log.info("Please provide a default template name using \"-template [name]\".");
            log.info("Options are: " + Arrays.toString(Template.values()));
            return false;
        }

        log.info("Generating " + extension.toUpperCase() + " docs for " + template.getTemplateName() + " V" + version);

        MDDoclet doclet = new MDDoclet(root);

        for (ClassDoc c : root.classes()) {
            if (classesList == null || classesList.isEmpty() || classesList.contains(c.name())) {
                doclet.generate(c);

                if (classesList != null && !classesList.isEmpty()) {
                    classesList.remove(c.name());
                    if (classesList.isEmpty())
                        break;
                }
            }
        }

        log.info("Documentation generated");
        return true;
    }

    /**
     * Doclet class method that returns how many arguments would be consumed if
     * <code>option</code> is a recognized option.
     * 
     * @param option the option to check
     */
    public static int optionLength(String option) {
        if (option.equals("-version")) {
            return 2;
        } else if (option.equals("-classeslist")) {
            return 2;
        } else if (option.equals("-repolink")) {
            return 2;
        } else if (option.equals("-template")) {
            return 2;
        } else if (option.equals("-extension")) {
            return 2;
        }
        return Doclet.optionLength(option);
    }

    /**
     * Doclet class method that checks the passed options and their arguments for
     * validity.
     * 
     * @param args the arguments to check
     * @param err  the interface to use for reporting errors
     */
    static public boolean validOptions(String[][] args, DocErrorReporter err) {
        for (int i = 0; i < args.length; ++i) {
            if (args[i][0].equals("-version")) {
                version = args[i][1];
            } else if (args[i][0].equals("-classeslist")) {
                classesList = getList(args[i][1]);
            } else if (args[i][0].equals("-repolink")) {
                repoUrl = args[i][1];
            } else if (args[i][0].equals("-template")) {
                try {
                    template = Template.getEnum(args[i][1]);
                } catch (IllegalArgumentException e) {
                    log.error(e.getLocalizedMessage());
                    log.info("Please provide a valid template name.");
                    log.info("Options are: ", Arrays.toString(Template.values()));
                }
            } else if (args[i][0].equals("-extension")) {
                String extension = args[i][1];
                if (extension.charAt(0) != '.') {
                    extension = "." + extension;
                }
                MDDoclet.extension = extension;
            }
        }

        return Doclet.validOptions(args, err);
    }

    /**
     * Transforms the given input into a list of strings
     * 
     * @param string can be a full path to an Enum class, or an array split by
     *               spaces
     * @return the list, or <code>null</code> if the input could not be parsed
     */
    private static List<String> getList(String string) {
        String[] values = string.split(" ");
        if (values.length > 1) {
            return Arrays.asList(values);
        } else {
            return null;
        }
    }

    /**
     * Without this method present and returning LanguageVersion.JAVA_1_5, Javadoc
     * will not process generics because it assumes LanguageVersion.JAVA_1_1
     */
    public static LanguageVersion languageVersion() {
        return LanguageVersion.JAVA_1_5;
    }
}