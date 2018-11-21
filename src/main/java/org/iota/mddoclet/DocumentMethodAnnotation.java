package org.iota.mddoclet;

import com.sun.javadoc.MethodDoc;

/**
 * 
 * Represents a {@link Document} annotation through the java Doclet API.
 * 
 */
public class DocumentMethodAnnotation {
    
    private MethodDoc method;

    private String name;
    private String returnValue;
    
    private Template template;
    
    public DocumentMethodAnnotation(MethodDoc method, String name, String returnValue, Template template) {
        this.method = method;
        this.name = name;
        this.returnValue = returnValue;
        
        this.template = template;
    }
    
    /**
     * The template this specific method should use.
     * If this is <tt>null</tt>, this will use the template defined by the \"-template\" argument.
     * @return The template name
     */
    public Template getTemplate() {
        return template;
    }
    
    /**
     * Should return the actual name of the function you wish to document. <br/>
     * For example: <code>commandGetNodeInfo</code> and the displayed name <code>getNodeInfo</code>
     */
    public String getProgrammedName() {
        return method.name();
    }
    
    /**
     * Used for returning raw data instead of wrapper objects
     * @return if this command returns a value
     */
    public boolean hasParam() {
        return returnValue != null;
    }
    
    /**
     * The name of this command as displayed in the response
     * Only if {@link #hasParam()} returns true
     * @return
     */
    public String getParam() {
        return returnValue;
    }
    
    /**
     * 
     * @return the display name of a method
     */
    public String name() {
        return name;
    }
    
    @Override
    public String toString() {
        return "Document(name=" + name + (hasParam() ? ", param=" + getParam() : "") + ")";
    }
}
