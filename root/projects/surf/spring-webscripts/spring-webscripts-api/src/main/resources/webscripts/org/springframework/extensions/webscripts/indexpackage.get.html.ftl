<#import "/org/springframework/extensions/webscripts/webscripts.lib.html.ftl" as wsLib/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
   <@wsLib.head>Index of Web Scripts Package '${package.path}'</@wsLib.head>
   <body>
      <div>
         <@wsLib.indexheader size=package.scripts?size>Index of Web Scripts Package '${package.path}'</@wsLib.indexheader>
         <br/>
         <@wsLib.home/>
         <@wsLib.parent path=package pathname="package"/>
         <br/>
         <#if package.children?size &gt; 0>
         <table>
         <@recurseuri package=package/>
         </table>
         <br/>
         </#if>
         <#macro recurseuri package>
         <#list package.children as childpath>
         <#if childpath.scripts?size &gt; 0>
         <tr><td><a href="${url.serviceContext}/index/package${childpath.path}">${childpath.path}</a></td></tr>
         </#if>
         <@recurseuri package=childpath/>
         </#list>  
         </#macro>
         <#list package.scripts as webscript>
         <#assign desc = webscript.description>
         <span class="mainSubTitle">${desc.shortName}</span>
         <table>
            <#list desc.URIs as uri>
            <tr><td><a href="${url.serviceContext}${uri?html}">${desc.method?html} ${url.serviceContext}${uri?html}</a></td></tr>
            </#list>
            <tr><td></td></tr>
         </table>
         <table>
            <#if desc.description??><tr><td>Description:</td><td>${desc.description}</td></tr><#else></#if>
            <tr><td>Authentication:</td><td>${desc.requiredAuthentication}</td></tr>
            <tr><td>Transaction:</td><td>${desc.requiredTransaction}</td></tr>
            <tr><td>Format Style:</td><td>${desc.formatStyle}</td></tr>
            <tr><td>Default Format:</td><td>${desc.defaultFormat!"<i>Determined at run-time</i>"}</td></tr>
      	<#if desc.lifecycle != 'none'>
      	    <tr><td>Lifecycle:</td><td>${desc.lifecycle}</td></tr>
        </#if>
            <tr><td></td></tr>
            <tr><td>Id:</td><td><a href="${url.serviceContext}/script/${desc.id}">${desc.id}</a></td></tr>
            <tr><td>Description:</td><td><a href="${url.serviceContext}/description/${desc.id}">${desc.storePath}/${desc.descPath}</a></td></tr>
         </table>
         <br/>
         </#list>
      </div>
   </body>
</html>