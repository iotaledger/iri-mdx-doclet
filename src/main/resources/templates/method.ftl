<#import "lib/java.ftl" as java>

---
### [${name}](https://github.com/iotaledger/iri/blob/dev/src/main/java/com/iota/iri/service/API.java#L${lineNumber})
${java.annotations_for(subject)} ${java.link(subject.returnType())} ${subject.name()}(<@java.parameterList subject.parameters() />)

${ util.processDescription(subject.commentText())}

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

<@java.returnTags util.parseReturnTag(subject.tags("return"), returnclass) />
</#if>

***
