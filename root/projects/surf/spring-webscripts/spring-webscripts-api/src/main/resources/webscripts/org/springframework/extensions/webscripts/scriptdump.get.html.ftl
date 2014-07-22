<#import "/org/springframework/extensions/webscripts/webscripts.lib.html.ftl" as wsLib/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
   <@wsLib.head>Web Script: ${script.id}</@wsLib.head>
   <body>
      <div>
         <table>
            <tr><td><span class="title">Web Script: ${script.id}</span></td></tr>
            <tr><td>Generated from ${url.full?html} on ${date?datetime}</td></tr>
         </table>
         <p/>
         <table>
            <tr><td><span class="mainSubTitle">Script Properties</span></td></tr>
            <tr><td>Id:</td><td>${script.id}</td></tr>
            <tr><td>Short Name:</td><td>${script.shortName}</td></tr>
            <tr><td>Description:</td><td>${script.description!"[undefined]"}</td></tr>
            <tr><td>Authentication:</td><td>${script.requiredAuthentication}</td></tr>
            <tr><td>Transaction:</td><td>${script.requiredTransaction}</td></tr>
            <tr><td>Method:</td><td>${script.method}</td></tr>
            <#list script.URIs as URI>
            <tr><td>URL Template:</td><td>${URI}</td></tr>
            </#list>
            <tr><td>Format Style:</td><td>${script.formatStyle}</td></tr>
            <tr><td>Default Format:</td><td>${script.defaultFormat!"[undefined]"}</td></tr>
            <#if script.negotiatedFormats?exists && script.negotiatedFormats?size &gt; 0>
            <#list script.negotiatedFormats as negotiatedFormat>
            <tr><td>Negotiated Format:</td><td>${negotiatedFormat.mediatype} => ${negotiatedFormat.format}</td></tr>
            </#list>
            <#else>
            <tr><td>Negotiated Formats:</td><td>[undefined]</td></tr>
            </#if>
            <tr><td>Implementation:</td><td>${script_class}</td></tr>
            <#if script.extensions?exists && script.extensions?size &gt; 0>
            <#list script.extensions?keys as extensionName>
            <tr><td>[custom] ${extensionName}:</td><td>${script.extensions[extensionName]}</td></tr>
            </#list>
            <#else>
            <tr><td>Extensions:</td><td>[undefined]</td></tr>
            </#if>
         </table>
         <p/>
         <#list stores as store>
         <span class="mainSubTitle">Store: ${store.path}</span>
         <p/>
         <table>
            <#if store.files?size == 0>
            <tr><td>[No implementation files]</td></tr>
            <#else>
            <tr><td></td></tr>
            <#list store.files as file>
            <tr><td><span class="mainSubTitle">File: ${file.path}</span> <#if file.overridden>[overridden]</#if></td></tr>
            <tr><td><pre>${file.content?html}</pre></td></tr>
            </#list>
            </#if>
         </table>
         <p/>
         </#list>
      </div>
   </body>
</html>