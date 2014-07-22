<#import "/org/springframework/extensions/webscripts/webscripts.lib.html.ftl" as wsLib/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
   <@wsLib.head>Index of All Web Scripts</@wsLib.head>
   <body>
      <div>
         <@wsLib.indexheader>Index of All Web Scripts</@wsLib.indexheader>
         <br/>
         <@wsLib.home/>
         <br/>
         <#macro recursepackage package>
            <#if package.scripts?size &gt; 0>
            <span class="mainSubTitle">Package: <a href="${url.serviceContext}/index/package${package.path}">${package.path}</a></span>
            <br/><br/>
            <#list package.scripts as webscript>
            <#assign desc = webscript.description>
            <span class="mainSubTitle">${desc.shortName}</span>
            <table>
               <#list desc.URIs as uri>
               <tr><td><a href="${url.serviceContext}${uri?html}">${desc.method?html} ${url.serviceContext}${uri?html}</a></td></tr>
               </#list>
            </table>
            <#if desc.description??>
            <table>
               <tr><td>---</td></tr>
               <tr><td>${desc.description}</td></tr>
               <tr><td>---</td></tr>
            </table>
            </#if>
            <table>
               <tr><td>Authentication:</td><td>${desc.requiredAuthentication}</td></tr>
               <tr><td>Transaction:</td><td>${desc.requiredTransaction}</td></tr>
               <tr><td>Format Style:</td><td>${desc.formatStyle}</td></tr>
               <tr><td>Default Format:</td><td>${desc.defaultFormat!"<i>Determined at run-time</i>"}</td></tr>
      	<#if desc.lifecycle != 'none'>
      	    <tr><td>Lifecycle:</td><td>${desc.lifecycle}</td></tr>
        </#if>
               <tr><td></td></tr>
               <tr><td>Id:</td><td><a href="${url.serviceContext}/script/${desc.id}">${desc.id}</a></td></tr>
               <tr><td>Descriptor:</td><td><a href="${url.serviceContext}/description/${desc.id}">${desc.storePath}/${desc.descPath}</a></td></tr>
            </table>
            <br/>
            </#list>
            </#if>
            <#list package.children as childpath>
            <@recursepackage package=childpath/>
            </#list>  
         </#macro>
         
         <@recursepackage package=rootpackage/>
      </div>
   </body>
</html>