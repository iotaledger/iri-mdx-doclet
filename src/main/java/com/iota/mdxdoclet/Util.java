package com.iota.mdxdoclet;

import java.util.ArrayList;
import java.util.Arrays;

import com.iota.mdxdoclet.data.ReturnParam;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;

public final class Util {
	
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

	public ReturnParam[] parseReturnTag(Tag[] tags, ClassDoc doc, MethodDoc method, MethodCall api) {
		if (tags.length > 0) {
			Tag tag = tags[0];
			for (Tag t : tag.inlineTags()) {
			    if (t.name().equals("@link")) {
			        if (doc == null) {
	                    return new ReturnParam[] {};
	                }

	                ReturnParam[] finalTags =  parseFields(doc).toArray(new ReturnParam[] {});
	                return finalTags;
			    }
			}
			if (!method.returnType().typeName().equals("void") && api.hasParam()) {
			    return new ReturnParam[] {
                        new ReturnParam(
                            api.getParam(), 
                            processDescription(tag.text()),
                            method.returnType())
                        };
			} else {
			    int ret = tag.text().indexOf(" ");
	            return new ReturnParam[] {
	                    new ReturnParam(
	                        tag.text().substring(0,  ret), 
	                        processDescription(tag.text().substring(ret))) 
	                    };
			}
			
		}

		return new ReturnParam[] {};
	}

	public ArrayList<ReturnParam> parseFields(ClassDoc doc) {
		return parseFields(doc, new ArrayList<ReturnParam>());
	}

	private ArrayList<ReturnParam> parseFields(ClassDoc doc, ArrayList<ReturnParam> tags) {
	    if (doc.superclass() != null && !doc.superclass().name().equals(Object.class.getSimpleName())) {
			parseFields(doc.superclass(), tags);
		}

		for (FieldDoc field : doc.fields(false)) {
			if (field.inlineTags().length > 0) {
				Tag t = field.inlineTags()[0];
				ReturnParam rp = new ReturnParam(field.name(), 
				                                 processDescription(field.commentText()), 
				                                 field.type());
				tags.add(rp);
			}
		}
		return tags;
	}

	public String dimension(Type type) {
		try {
			StringBuilder ret = new StringBuilder(type.qualifiedTypeName());
			int dimension = Integer.parseInt(type.dimension());
			for (int dim = 0; dim < dimension; dim++) {
				ret.append("[]");
			}
			return ret.toString();
		} catch (Exception e) {
			return "";
		}
	}

	public String processDescription(String text) {
		try {
		    text = text.replaceAll("<b>", "**").replaceAll("</b>", "**");
		    
		    text = text.replaceAll("<code>", "`").replaceAll("</code>", "`");
			text = text.replaceAll("<tt>", "`").replaceAll("</tt>", "`");
			
			text = parseJavadocTag(text, "code", "`");
			
			return text;
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
     * Parses a Javadoc tag with the following scheme: {@text text inside here}
     * @param string The string we modify. Does not change this string.
     * @param name The name of the tag
     * @param toReplace The value we replace "{@text" and "}" with.
     * @return The new, updated String.
     */
	private String parseJavadocTag(String string, String name, String toReplace) {
	    return parseJavadocTag(string, name, toReplace, null);
	}

	/**
	 * Parses a Javadoc tag with the following scheme: {@text text inside here}
	 * @param string The string we modify. Does not change this string.
	 * @param name The name of the tag
	 * @param toReplace The value we replace "{@text" with.
	 * @param toReplaceEnd Optional, the ending tag we put after the "text inside here". 
	 *                     If null, uses toReplace.
	 * @return The new, updated String.
	 */
	private String parseJavadocTag(String string, String name, String toReplace, String toReplaceEnd) {
	    int index = 0;
	    String newText = string;
        while ((index = newText.indexOf("{@" + name, index)) > -1) {
            int closingBracket = newText.indexOf("}", index);
            
            StringBuilder sb = new StringBuilder(newText.substring(0, index));
            sb.append(toReplace);
            sb.append(newText.substring(index + 6, closingBracket));
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