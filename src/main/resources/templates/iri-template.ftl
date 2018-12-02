<#import "lib/java.ftl" as java>

<#if util.getRepoUrl()??>
# [${name}](${util.getRepoUrl()}${subject.containingClass().qualifiedName()?replace('.','/')}.java#L${lineNumber})
<#else>
# ${name}
</#if>
${java.annotations_for(subject)} ${java.link(subject.returnType())} ${subject.name()}(<@java.parameterList subject.parameters() />)

${ util.parseCommentText(subject)}

> **Important note:** This API is currently in Beta and is subject to change. Use of these APIs in production applications is not supported.
<#if false>

## Permissions
|Permission type      | Permissions (from least to most privileged)              |
|:--------------------|:---------------------------------------------------------|
| permission type | List of permissions    |
| permission type | List of permissions    |
| permission type | List of permissions    |
</#if>

## Request
<#if false>

Example request - the simplest request that is done by the API. Or the [expected] most common one. 

## Supported query parameters
List out the supported query parameters. For example `$filter`.
http://docs.oasis-open.org/odata/odata/v4.01/cs01/part2-url-conventions/odata-v4.01-cs01-part2-url-conventions.html#_Toc505773216

If no parameters are supported, we should explicitly state:

*No query parameters are supported.*
</#if>

## Request headers

| Header       | Value | Required or Optional |
|:---------------|:--------|:--------|
| X-IOTA-API-Version | 1 | Required |
| Content-Type | application/json | Optional |
| Authorization  | Bearer {token} | Optional  |
<#if (parameters?size > 0)>

## Request parameters
| Parameter       | Type | Required or Optional | Description |
|:---------------|:--------|:--------| :--------|
<#list parameters as param>
| ${param.getName()} | ${java.link(param.getReturnType())} | ${java.paramRequired(param.getReturnType())} | ${param.getText()} |
</#list>
</#if>
<#if false>

## (Optional) Request body

If no request body is required for the call, state it explicitly:

*Do not supply a request body for this method.*

If request body is required: 1) link the object that needs to be supplied in the body or 2) provide an example:

```json
{
    "param 1": value,
    "param 2": value
}
```
</#if>

## Responses

If successful, this method returns a `200 OK` response code and ${java.link(returnclass)} in the body.

<#if returnParams??>
| Return type | Description |
|--|--|
<#list returnParams as field>
| ${java.link(field.getReturnType())} ${field.getName()} | ${field.getText()} |
</#list>
</#if>

## Example  

### Request

The following is an example of the request.

<#list examples as example>
<#if example.getLanguage()?lower_case == "bash">
 ## Example
 
 ```${ example.getLanguage() }
 ${ example.getExample() }
 ```

### Response - 200

The following is an example of the response. Note: The response object shown here may be truncated for brevity. All of the properties will be returned from an actual call.

```json
${ example.getResponseOk() }
```

### Response - 400

A node returns this for various reasons. These are the most common ones:
* Invalid API Version
* The maximal number of characters the body of an API call is exceeded
* The command contains invalid parameters

```json
{
  "duration": 15,
  "error": "Error specific information"
}
```

### Response - 401

```json
{
  "duration": 15,
  "error": "COMMAND ${ name } is not available on this node"
}
```

### Response - 500

```json
{
  "duration": 15,
  "exception": "Internal server error message"
}
```
</#if>
</#list>