package org.iota.mddoclet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.iota.mddoclet.data.ReturnParam;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;

public final class Util {

    private String repoUrl = null;

    /**
     * 
     * Sets the url base used for links to other classes.
     * 
     * Example: "https://github.com/iotaledger/iri/blob/dev/src/main/java/"
     * 
     * @param repoUrl the url we link to
     */
    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }

    public String getRepoUrl() {
        return repoUrl;
    }
    
    /**
     * Checks if a type has dimensions
     * Returns <code>false</code> if the Dimension contains primitive fields.
     * 
     * @param type The type to check
     * @return
     */
    public boolean hasDimensions(Type type) {
        try {
            if (type.asParameterizedType() != null) {
                if (type.asParameterizedType().typeArguments().length > 1) {
                    return true;
                } else {
                    return type.asParameterizedType().typeArguments()[0].isPrimitive();
                }
            }
            return !type.dimension().equals("");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public String dimension(Type type) {
        if (type.asParameterizedType() != null) {
            return type.asParameterizedType().typeName();
        } else if (type.dimension().equals("[]")){
            return "Array";
        } else if (type.dimension().equals("[][]")){
            return "2 dimensional Array";
        } else {
            return "";
        }
    }
    
    public String dimensionType(Type type) {
        return type.simpleTypeName();
    }

    public boolean hasReturnClass(Tag[] tags) {
        return tags.length > 0 && tags[0].text().startsWith("{");
    }

    public ClassDoc getReturnClass(Tag[] tags) {
        if (tags.length > 0) {
            Tag tag = tags[0];
            for (Tag inlineTag : tag.inlineTags()) {
                if (inlineTag instanceof SeeTag) {
                    SeeTag seeTag = (SeeTag) inlineTag;
                    return seeTag.referencedClass();
                }
            }

            return null;
        }
        return null;
    }

    public ReturnParam[] parseReturnTag(Tag[] tags, ClassDoc doc, MethodDoc method, DocumentMethodAnnotation api) {
        if (tags.length > 0) {
            Tag tag = tags[0];
            for (Tag t : tag.inlineTags()) {
                if (t.name().equals("@link")) {
                    if (doc == null) {
                        return new ReturnParam[] {};
                    }

                    ReturnParam[] finalTags = parseGetters(doc);
                    return finalTags;
                }
            }
            if (!method.returnType().typeName().equals("void") && api.hasParam()) {
                return new ReturnParam[] { 
                    new ReturnParam(api.getParam(), processDescriptionAsMarkdown(tag.text()),
                    method.returnType()) 
                };
            } else {
                int ret = tag.text().indexOf(": ");
                String name = "";
                if (ret > -1)
                    name = tag.text().substring(0, ret);

                String text;
                if (ret > -1)
                    text = tag.text().substring(ret);
                else
                    text = tag.text();

                return new ReturnParam[] {
                    new ReturnParam(name, processDescriptionAsMarkdown(text), method.returnType()) 
                };
            }

        }

        return new ReturnParam[] {};
    }

    public ReturnParam[] parseParameters(MethodDoc doc) {
        List<ReturnParam> params = new ArrayList<>();

        for (int i = 0; i < doc.parameters().length; i++) {
            String description = "missing description";
            if (doc.paramTags().length > i) {
                description = processDescriptionAsMarkdown(parseTag(doc.paramTags()[i]));
            }

            Parameter param = doc.parameters()[i];

            ReturnParam rp = new ReturnParam(param.name(), description, param.type());
            params.add(rp);
        }

        return params.toArray(new ReturnParam[params.size()]);
    }

    public ReturnParam[] parseGetters(ClassDoc doc) {
        return parseGetters(doc, new ArrayList<>()).toArray(new ReturnParam[] {});
    }

    private List<ReturnParam> parseGetters(ClassDoc doc, List<ReturnParam> tags) {
        if (doc == null) {
            return tags;
        }

        if (doc.superclass() != null && !doc.superclass().name().equals(Object.class.getSimpleName())) {
            parseGetters(doc.superclass(), tags);
        }
        
        for (MethodDoc method : doc.methods()) {
            String fieldName;
            if (method.name().startsWith("get")) {
                //Getter
                fieldName = method.name().substring(3, 4).toLowerCase() + method.name().substring(4);
            } else if (method.name().startsWith("is")) {
                //Boolean
                fieldName = method.name().substring(2, 3).toLowerCase() + method.name().substring(3);
            } else {
                continue;
            }
            
            ReturnParam rp = new ReturnParam(fieldName, parseFieldText(method), method.returnType());
            tags.add(rp);
        }
        
        return tags;
    }

    public String parseCommentText(Doc field) {
        StringBuilder builder = new StringBuilder();

        // Parse inline tags, also called the regular comments
        for (Tag inlineTag : field.inlineTags()) {
            builder.append(parseTag(inlineTag));
        }
        return processDescriptionAsMarkdown(builder.toString());
    }

    /**
     * Parses the field or methods javadoc
     * @param field the Document reference to this field/method
     * @return All the formatted text
     */
    public String parseFieldText(Doc field) {
        StringBuilder builder = new StringBuilder();

        // Parse inline tags, also called the regular comments
        for (Tag inlineTag : field.inlineTags()) {
            builder.append(parseTag(inlineTag));
        }

        // Parse the special tags, @see, @return, etc
        // Reverse so @return (explanation) comes before @see (content)
        for (int i = field.tags().length - 1; i >= 0; i--) {
            Tag tag = field.tags()[i];
            builder.append(parseTag(tag));
        }
        return processDescriptionAsMarkdown(builder.toString());
    }

    public String parseTag(Tag tag) {
        StringBuilder builder = new StringBuilder();

        if (tag.inlineTags().length > 1) {
            for (Tag inlineTag : tag.inlineTags()) {
                builder.append(parseTag(inlineTag));
            }
        } else {
            if (tag instanceof SeeTag) {
                SeeTag seeTag = (SeeTag) tag;

                // We reference a class, parse its field like we do with a return type
                if (seeTag.name().equals("@see")) {
                    ReturnParam[] seeFields = parseGetters(seeTag.referencedClass());
                    for (ReturnParam param : seeFields) {
                        // Keep this html default, markdown parse happens later, optionally
                        builder.append("<BR/>");
                        builder.append("<b>");
                        builder.append(param.getName());
                        builder.append("</b>");
                        if (!param.getText().equals("")) {
                            builder.append(": ");
                            builder.append(param.getText());
                        }
                    }
                } else if (seeTag.name().equals("@link")) {
                    builder.append(parseLinkAsUrl(seeTag));
                }
            } else if (tag instanceof ParamTag) {
                ParamTag paramTag = (ParamTag) tag;
                builder.append(paramTag.parameterComment());
            } else if (tag.name().equals("Text")) {
                builder.append(tag.text());
            }
        }
        return builder.toString();
    }
    
    public String parseLinkAsUrl(SeeTag seeTag) {
        if (seeTag.referencedMember() != null) {
            if (seeTag.referencedMember().isPrivate()) {
                // We linked to the javadoc of a field
                return parseFieldText(seeTag.referencedMember());
                
            } else {
                if (repoUrl != null) {
                    return "[" + seeTag.referencedMemberName() + "](" + repoUrl
                            + seeTag.referencedMember().containingClass().qualifiedName().replace('.', '/')
                            + ".java#L" + seeTag.referencedMember().position().line() + ")";
                } else {
                    return seeTag.referencedMemberName();
                }
            }
        } else if (seeTag.referencedClass() != null) {
            if (repoUrl != null) {
                return "[" + seeTag.referencedClass().name() + "](" + repoUrl
                        + seeTag.referencedClass().qualifiedName().replace('.', '/') + ".java)";
            } else {
                return seeTag.referencedClass().qualifiedName();
            }
        } else {
            // ??
            return "";
        }
    }

    public String processDescriptionAsMarkdown(String text) {
        return processDescriptionAsMarkdown(text, true);
    }

    /**
     * CHanges HTML tags to Markdown tags.
     * Note: When you use upper case tags, we don't replace them. This can be useful for table layout.
     * 
     * @param text
     * @param removeLineEndings
     * @return
     */
    public String processDescriptionAsMarkdown(String text, boolean removeLineEndings) {
        try {
            // Filter away any unwanted newlines.
            if (removeLineEndings) {
                text = text.replaceAll("\\r\\n|\\r|\\n", "");
            }

            // Bold
            text = text.replaceAll("<b>", "**").replaceAll("</b>", "**");

            // Strike through
            text = text.replaceAll("<strike>", "~~").replaceAll("</strike>", "~~");
            text = text.replaceAll("<s>", "~~").replaceAll("</s>", "~~");

            // Underlined
            text = text.replaceAll("<u>", "__").replaceAll("</u>", "__");

            // Italic
            text = text.replaceAll("<i>", "_").replaceAll("</i>", "_");

            // Synonyms for code tags
            text = text.replaceAll("<code>", "`").replaceAll("</code>", "`");
            text = text.replaceAll("<tt>", "`").replaceAll("</tt>", "`");

            text = text.replaceAll("<li>", "* ").replaceAll("</li>", "<br/>");

            // Organised and unorganised are replaced with * (unorganised list)
            // Organised list requires managing amount, number count etc.
            text = text.replaceAll("<ol>", "").replaceAll("</ol>", "");
            text = text.replaceAll("<ul>", "").replaceAll("</ul>", "");

            // Paragraphs are taken literally, so if its formatted visually ok, it will be
            // ok
            text = text.replaceAll("<br/>", System.lineSeparator());
            text = text.replaceAll("<p>", "").replaceAll("</p>", System.lineSeparator());
            text = text.replaceAll("    ", "");
            
            text = parseJavadocTag(text, "code", "`");

            return text;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Parses a Javadoc tag with the following scheme: {@text text inside here}
     * 
     * @param string    The string we modify. Does not change this string.
     * @param name      The name of the tag
     * @param toReplace The value we replace "{@text" and "}" with.
     * @return The new, updated String.
     */
    private String parseJavadocTag(String string, String name, String toReplace) {
        return parseJavadocTag(string, name, toReplace, null);
    }

    /**
     * Parses a Javadoc tag with the following scheme: {@text text inside here}
     * 
     * @param string       The string we modify. Does not change this string.
     * @param name         The name of the tag
     * @param toReplace    The value we replace "{@text" with.
     * @param toReplaceEnd Optional, the ending tag we put after the "text inside
     *                     here". If null, uses toReplace.
     * @return The new, updated String.
     */
    private String parseJavadocTag(String string, String name, String toReplace, String toReplaceEnd) {
        int index = 0;
        String newText = string;
        while ((index = newText.indexOf("{@" + name, index)) > -1) {
            int closingBracket = newText.indexOf("}", index);

            StringBuilder sb = new StringBuilder(newText.substring(0, index));
            sb.append(toReplace);
            sb.append(newText.substring(index + name.length(), closingBracket));
            sb.append(toReplaceEnd != null ? toReplaceEnd : toReplace);
            sb.append(newText.substring(closingBracket + 1));

            newText = sb.toString();
        }
        return newText;
    }

    public String getTypeParamComment(TypeVariable variable) {
        final String name = variable.simpleTypeName();
        final ProgramElementDoc owner = variable.owner();

        if (owner.isClass()) {
            ClassDoc asClassDoc = (ClassDoc) owner;
            for (ParamTag tag : asClassDoc.typeParamTags()) {
                if (name.equals(tag.parameterName())) {
                    return tag.parameterComment();
                }
            }
        }
        return "";
    }

    public String getParamComment(TypeVariable variable) {
        final String name = variable.simpleTypeName();
        final ProgramElementDoc owner = variable.owner();

        if (owner.isClass()) {
            ClassDoc asClassDoc = (ClassDoc) owner;
            for (ParamTag tag : asClassDoc.typeParamTags()) {
                if (name.equals(tag.parameterName())) {
                    return tag.parameterComment();
                }
            }
        }
        return "";
    }

    public Tag findTag(TypeVariable typeVar) {
        final ProgramElementDoc owner = typeVar.owner();

        if (owner instanceof ExecutableMemberDoc) {
            for (ParamTag paramTag : ((ExecutableMemberDoc) owner).typeParamTags()) {
                if (paramTag.parameterName().equals(typeVar.toString())) {
                    return paramTag;
                }
            }
        }

        if (owner instanceof ClassDoc) {
            for (ParamTag paramTag : ((ExecutableMemberDoc) owner).typeParamTags()) {
                if (paramTag.parameterName().equals(typeVar.toString())) {
                    return paramTag;
                }
            }
        }

        return null;
    }
}