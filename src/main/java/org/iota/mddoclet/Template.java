package org.iota.mddoclet;
/**
 * 
 * Different templates supported by the MD doclet
 *
 */
public enum Template {
    IRI("iri", "iri-template.ftl"),
    IOTA_JAVA("iota-java", "library-template.ftl");
    
    private String templateName;
    private String fileName;

    Template(String templateName, String fileName){
        this.templateName = templateName;
        this.fileName = fileName;
    }
    
    public String getTemplateName() {
        return templateName;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    @Override
    public String toString() {
        return getTemplateName();
    }
    
    public static Template getEnum(String value) {
        for(Template v : values())
            if(v.toString().equals(value)) return v;
        throw new IllegalArgumentException();
    }
}
