<#-- Functions -->
<#function annotations_for executableMemberDoc delim="\n">
  <#assign  ret="" />
  <#if executableMemberDoc.annotations??>
    <#list executableMemberDoc.annotations() as annotationDesc>
      <#if annotationDesc.annotationType() != "com.iota.mdxdoclet.Document">
        <#assign ret += "@" + link(annotationDesc.annotationType()) + "\n" />
      </#if>
    </#list>
  </#if>
  <#return ret>
</#function>

<#function link type>
  <#if type.isPrimitive()>
    <#return type.typeName()>
  <#else>
    <#if type.qualifiedTypeName()?starts_with("com.iota.") >
      <#return "[" + type.typeName() + "](" + url(type) + ")">
    <#else>
      <#return type.qualifiedTypeName()>
    </#if> </#if>
</#function>

<#function annotations items>
  <#assign  ret="" />
  <#if items?has_content>
    <#list items as annotationDesc>
<#assign ret += "@" + link(annotationDesc.annotationType()) + "\n" />
    </#list>
  </#if>
<#return ret>
</#function>


<#--  Creating links:
TODO: configure root url
TODO: configure root package
-->
<#function url type>
  <#return "https://github.com/iotaledger/iri/blob/dev/src/main/java/" + type.qualifiedTypeName()?replace('.','/') + ".java"/>
</#function>


<#-- Macros -->

<#macro parameter param isisvarag>
  <@compress>
    <@annotations param.annotations() />  ${link(param.type())}<#if isisvarag>...</#if> ${param.name()}
  </@compress>
</#macro>


<#macro parameterList params isisvarag=false>
  <@compress  single_line=true>
    <#if params??><#list params as param><@parameter param=param isisvarag=(isisvarag && !param?has_next)/><#sep>, </#sep></#list></#if>
  </@compress>
</#macro>

<#macro returnTags returnTags>
  <#if returnTags??>
|Return | Description |
|--|--|
    <#list returnTags as tag>
      <@compress  single_line=true>
        | ${tag.getName()} | ${tag.getText()} |
      </@compress>
      <#sep>

      </#sep>
    </#list>
  </#if>
</#macro>

<#macro parameterTag tag>
  <@compress  single_line=true>
  | ${tag.parameterName()} | ${tag.parameterComment()} |
  </@compress>
</#macro>
  
<#macro parameterTags parameterTags>
  <#if parameterTags??>
|Parameters | Description |
|--|--|
    <#list parameterTags as tag>
      <@compress  single_line=true>
         <@parameterTag tag />
    </@compress>
      <#sep>

      </#sep>
    </#list>
  </#if>
</#macro>

<#-- Method parameters -->
<#macro annotations annotations>
  <@compress>
    <#list annotations as annotation>@link(annotation) </#list>
  </@compress>
</#macro>

<#macro parameter param isisvarag>
  <@compress>
    <@annotations param.annotations() />  ${link(param.type())}<#if isisvarag>...</#if> ${param.name()}
  </@compress>
</#macro>