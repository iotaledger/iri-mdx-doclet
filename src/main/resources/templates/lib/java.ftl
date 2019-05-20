<#-- Functions -->
<#function annotations_for executableMemberDoc delim="\n">
  <#assign  ret="" />
  <#if executableMemberDoc.annotations??>
    <#list executableMemberDoc.annotations() as annotationDesc>
      <#if annotationDesc.annotationType() != "org.iota.mddoclet.Document"
      && annotationDesc.annotationType() != "java.lang.Override">
        <#assign ret += "@" + link(annotationDesc.annotationType()) + "\n" />
      </#if>
    </#list>
  </#if>
  <#return ret>
</#function>

<#function paramRequired paramType>
<#if paramType.typeName() == "Optional">
    <#return "Optional">
<#else>
    <#return "Required">
</#if>
</#function>

<#function typeName type>
    <#return type.typeName() + type.dimension()>
</#function>

<#function link type>
  <#if type.isPrimitive()>
    <#return typeName(type)>
  <#else>
    <#if type.qualifiedTypeName()?starts_with("com.iota.") 
    || type.qualifiedTypeName()?starts_with("org.iota.")
    || type.qualifiedTypeName()?starts_with("jota.")
    || util.getRepoUrl()?contains(type.qualifiedTypeName()?replace('.','/'))>
    
      <#return "[" + typeName(type) + "](" + url(type) + ")">
    <#else>
      <#if type.asParameterizedType()??>
      
        <#assign inner = "">
        <#assign first = true>
        <#list type.asParameterizedType().typeArguments() as innerTypes>
          <#if first>
            <#assign inner += link(innerTypes)>
            <#assign first = false>
          <#else>
            <#assign inner += ", " + link(innerTypes)>
          </#if>
        </#list>
      
        <#return typeName(type) + "<" + inner + ">">
      <#else>
        <#return typeName(type)>
      </#if>
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


<#--  Creating links: -->
<#function url type>
  <#return util.getRepoUrl() + type.qualifiedTypeName()?replace('.','/') + ".java"/>
</#function>


<#-- Macros -->

<#macro parameter param isisvarag>
  <@compress>
    <@annotations param.annotations() />  ${link(param.type())}<#if isisvarag>...</#if> ${param.name()}
  </@compress>
</#macro>


<#macro parameterList params isisvarag=false>
  <@compress  single_line=true>
    <#if params??>
        <#list params as param>
            <@parameter param=param isisvarag=(isisvarag && !param?has_next)/>
            <#sep>, </#sep>
        </#list>
    </#if>
  </@compress>
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