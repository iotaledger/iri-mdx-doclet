<#import "lib/java.ftl" as java>

<#if util.getRepoUrl()??>
# [${name}](${util.getRepoUrl()}${subject.containingClass().qualifiedName()?replace('.','/')}.java#L${lineNumber})
<#else>
# ${name}
</#if>
${java.annotations_for(subject)} ${java.link(subject.returnType())} ${subject.name()}(<@java.parameterList subject.parameters() />)

${ util.parseCommentText(subject)}
> **Important note:** This API is currently in Beta and is subject to change. Use of these APIs in production applications is not supported.

<#if (parameters?size > 0)>
## Input
| Parameter       | Type | Required or Optional | Description |
|:---------------|:--------|:--------| :--------|
<#list parameters as param>
| ${param.getName()} | ${java.link(param.getReturnType())} | ${java.paramRequired(param.getReturnType())} | ${param.getText()} |
</#list>
</#if>
    
<#if returnParams??>
## Output
<#if util.hasDimensions(subject.returnType())> 
${java.link(subject.returnType())}, which contains the following fields:
</#if>
| Return type | Description |
|--|--|
<#list returnParams as field>
| ${java.link(field.getReturnType())} ${field.getName()} | ${field.getText()} |
</#list>
</#if>

<#if (subject.throwsTags()?size > 0) >
## Exceptions
| Exceptions     | Description |
|:---------------|:--------|
<#list subject.throwsTags() as exception>
| ${java.link(exception.exceptionType())} | ${exception.exceptionComment()} |
</#list>
</#if>

<#if (subject.seeTags()?size > 0) >
## Related APIs (link to other product documentation)
| API     | Description |
|:---------------|:--------|
<#list subject.seeTags() as see>
<#if (see.label()?? && see.label() != "") >
| ${util.parseLinkAsUrl(see)} | ${util.processDescriptionAsMarkdown(see.label())} |
<#else>
| ${util.parseLinkAsUrl(see)} | ${util.parseCommentText(see.referencedMember())} |
</#if>
</#list>
</#if>

<#list examples as example>
<#if example.getLanguage()?lower_case == "java">
 ## Example
 
 ```${ example.getLanguage() }
 ${ example.getExample() }
 ```
</#if>
</#list>