<#import "/org/springframework/extensions/webscripts/webscripts.lib.html.ftl" as wsLib/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<@wsLib.head>Web Scripts Package '${package.path}' Documentation</@wsLib.head>
<body>
    <div>
        <@wsLib.indexheader size=package.scripts?size>Web Scripts Package '${package.path}' Documentation</@wsLib.indexheader>
        <br/>
        <@wsLib.home/>
        <br/>

        <div class="titlebar">Package: ${packagedoc.shortName}</div> 

        <div>${packagedoc.description}</div>

        <#list schemas as schema>
            <a name="schema_${schema.id}"><div class="titlebar">Schema: ${schema.shortName!schema.id}</div></a> 
            <#if schema.description??><div>${schema.description}<div><br></#if>
            <#if schema.format??><span>format: ${schema.format}</span><br></#if>
            <#if schema.definition??><span>definition:</span>
<div><pre>
${schema.definition?html}
</pre></div></#if>
            <#if schema.url??><span>url: <a href="${schema.url}">${schema.url}</a><span><br></#if>
            <br>
        </#list>

        <#list packagedoc.resourceDescriptions as resource>
        
            <div class="titlebar">Resource: ${resource.shortName!"<i>unspecified</i>"}</div>
            
            <div>${resource.description!"<i>unspecified</i>"}</div>
            <br>
            
            <#list resource.scriptIds as scriptId>
                <#assign webscript = registry.getWebScript(scriptId)>
                <#assign desc = webscript.description>

                <span class="title"><#if desc.lifecycle != 'none'><i>[${desc.lifecycle}]</i> </#if>${desc.method} Method: ${desc.shortName}  <!#-- TODO: family --></span><br>
                <br>
                <#if desc.description??><div>${desc.description}</div></#if>
                <br>
                <#list desc.URIs as uri>
                   <span><a href="${url.serviceContext}${uri?html}">${url.serviceContext}${uri?html}</a></span><br>
                </#list>
            
                <#if desc.arguments??>
                    <#assign urlargs = desc.arguments>
                    <ul>
                    <#list urlargs as arg>
                        <li><span>${arg.shortName!"<i>unspecified</i>"}: <#if !arg.required>(optional) </#if><#if arg.defaultValue??>(default: ${arg.defaultValue}) </#if>${arg.description!"<i>unspecified</i>"}</span></li>
                    </#list>
                    </ul>
                <#else>
                    <br>
                </#if>

                <#if desc.requestTypes??>
                    <#list desc.requestTypes as requestType>
                        <@type type=requestType header="Accepted request format:"/>
                        <br>
                    </#list>
                </#if>
                
                <#if desc.responseTypes??>
                    <#list desc.responseTypes as responseType>
                        <@type type=responseType header="Available response format:"/>
                        <br>
                    </#list>
                </#if>

                <span>Options for selecting response format:</span><br> 
                <span class="indent">default: ${desc.defaultFormat!"<i>Determined at run-time</i>"}</span><br>
                <span class="indent">style: ${desc.formatStyle}</span><br>
                <#if desc.negotiatedFormats??>
                    <#list desc.negotiatedFormats as negotiatedFormat>
                       <span class="indent">negotiated: ${negotiatedFormat.mediaType} to ${negotiatedFormat.format}<br>
                    </#list>
                </#if>
                <br>
                
                <span>Advanced:</span><br>
                <span class="indent">authentication: ${desc.requiredAuthentication}</span><br>
                <#assign cache = desc.requiredCache>
                <span class="indent">cache: <#if cache.neverCache>neverCache </#if><#if cache.isPublic>isPublic </#if><#if cache.mustRevalidate>mustRevalidate</#if></span><br>
                <span class="indent">transaction: ${desc.requiredTransaction}</span><br>
                <br/>
                
            </#list>
        </#list>
        
        <#if unmapped?size &gt; 0>
            <div class="titlebar">Web Scripts Not Attached to a Resource</div> 
            <#list unmapped as unmappedwebscript>
                <a href="${url.serviceContext}/script/${unmappedwebscript.description.id}">${unmappedwebscript.description.id}</a><br>
            </#list>
        </#if>
   </body>
</html>

<#macro type type header="">
    <span>${header} ${type.format}</span><br>
    <#if type.id??>
        <span class="indent">schema: <a href="#schema_${type.id}">${type.shortName!type.id}</a></span><br>
    <#else>
        <#if type.shortName??><span class="indent">short name: ${type.shortName}</span><br></#if>
        <#if type.description??><div class="indent">description: ${type.description}</div></#if>
        <#if type.definition??><div class="indent">definition: ${type.definition}</div></#if>
        <#if type.url??><span class="indent">url: <a href="${type.url}">${type.url}</a></span><br></#if>
    </#if>
</#macro>

