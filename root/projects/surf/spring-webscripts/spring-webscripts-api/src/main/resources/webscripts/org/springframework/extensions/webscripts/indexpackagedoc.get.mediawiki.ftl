<i>Note: This document was generated on ${date?datetime} from Alfresco ${server.edition} v${server.version}.</i>


= ${packagedoc.shortName} =

${packagedoc.description}

<#if schemas?size &gt; 0>
= Schemas =
<#list schemas as schema>
== ${schema.shortName!schema.id} ==

<#if schema.description??><nowiki>${schema.description}</nowiki></#if>

<#if schema.format??>format: ${schema.format}<br></#if>
<#if schema.definition??>definition: ${schema.definition}</#if>
<#if schema.url??>[${schema.url} ${schema.url}]</#if>
</#list>
</#if>

<#if packagedoc.resourceDescriptions?size &gt; 0>
= Resources =
<#list packagedoc.resourceDescriptions as resource>
== ${resource.shortName!"<i>unspecified</i>"} ==
            
<nowiki>${resource.description!"unspecified"}</nowiki>
            
<#list resource.scriptIds as scriptId>
<#assign webscript = registry.getWebScript(scriptId)>
<#assign desc = webscript.description>
=== ${desc.shortName} ===
<#if desc.lifecycle != 'none'><i>${desc.lifecycle}</i><br><br></#if>
<#if desc.description??><nowiki>${desc.description}</nowiki></#if>

<#list desc.URIs as uri>
${desc.method} [${absurl(url.serviceContext)}${uri} ${url.serviceContext}${uri}]<br>
</#list>
<#if desc.arguments??>
    <#assign urlargs = desc.arguments>
    <#list urlargs as arg>
* ${arg.shortName!"<i>unspecified</i>"}: <#if !arg.required>(optional) </#if><#if arg.defaultValue??>(default: ${arg.defaultValue}) </#if>${arg.description!"<i>unspecified</i>"}
    </#list>
</#if>
<br>
<#if desc.requestTypes??>
    <#list desc.requestTypes as requestType>
<@type type=requestType header="Accepted request format:"/>
    </#list>
</#if>
<#if desc.responseTypes??>
    <#list desc.responseTypes as responseType>
<@type type=responseType header="Available response format:"/>
    </#list>
</#if>
Options for selecting response format: 
* default: ${desc.defaultFormat!"<i>Determined at run-time</i>"}
* style: ${desc.formatStyle}
<#if desc.negotiatedFormats??>
    <#list desc.negotiatedFormats as negotiatedFormat>
* negotiated: ${negotiatedFormat.mediaType} to ${negotiatedFormat.format}
    </#list>
</#if>

Advanced:
* authentication: ${desc.requiredAuthentication}
<#assign cache = desc.requiredCache>
* cache: <#if cache.neverCache>neverCache </#if><#if cache.isPublic>isPublic </#if><#if cache.mustRevalidate>mustRevalidate</#if>
* transaction: ${desc.requiredTransaction}
                
</#list>
</#list>
</#if>
        
<#if unmapped?size &gt; 0>
= Web Scripts Not Attached to a Resource = 
<#list unmapped as unmappedwebscript>
[${url.serviceContext}/script/${unmappedwebscript.description.id} ${unmappedwebscript.description.id}]<br>
</#list>
</#if>

<#macro type type header="">
${header} ${type.format}<br>
<#if type.id??>
* schema: <a href="#schema_${type.id}">${type.shortName!type.id}</a>
<#else>
<#if type.shortName??>* short name: ${type.shortName}</#if>
<#if type.description??>* description: ${type.description}</#if>
<#if type.definition??>* definition: ${type.definition}</#if>
<#if type.url??>* url: [${type.url}]</#if>
</#if>
</#macro>
