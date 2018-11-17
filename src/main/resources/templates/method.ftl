<#import "lib/java.ftl" as java>

---
<#if util.getRepoUrl()??>
### [${name}](${util.getRepoUrl()}${subject.containingClass().qualifiedName()?replace('.','/')}.java#L${lineNumber})
<#else>
### ${name}
</#if>
${java.annotations_for(subject)} ${java.link(subject.returnType())} ${subject.name()}(<@java.parameterList subject.parameters() />)

${ util.parseFieldText(subject)}

<Tabs> 
<#list examples as example>

<Tab language="${ example.getGenerator() }">

<Section type="request">

```${ example.getLanguage() }
${ example.getExample() }
```
</Section>

<Section type="response">

```json
${ example.getResponseOk() }
```
</Section>

<Section type="error">
<#if example.hasResponseError()>

```json
${ example.getResponseError() }
```
<#else>
No response examples available
</#if>
</Section>
</#list>
</Tabs>



<#if subject.paramTags()?has_content>
***
	
<@java.parameterTags subject.paramTags() />
</#if>


<#if subject.tags("return")?has_content>
***

<#if returnclass??>
Returns ${java.link(returnclass)}
</#if>

<#if returnParams??>
|Return | Description |
|--|--|
  <#list returnParams as field>
    <@compress  single_line=true>
      | ${field.getName()} | ${field.getText()} |
    </@compress>
    <#sep>

    </#sep>
  </#list>
</#if>

</#if>

***
