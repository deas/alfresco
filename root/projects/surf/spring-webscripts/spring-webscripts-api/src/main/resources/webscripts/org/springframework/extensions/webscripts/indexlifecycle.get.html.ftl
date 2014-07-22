<#import "/org/springframework/extensions/webscripts/webscripts.lib.html.ftl" as wsLib/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <@wsLib.head>Index of Web Scripts Lifecycle '${lifecycle.path}'</@wsLib.head>
  <body>
    <div>
    <@wsLib.indexheader size=lifecycle.scripts?size>Index of Web Scripts Lifecycle '${lifecycle.path}'</@wsLib.indexheader>
    <br>
    <@wsLib.home/>
    <@wsLib.parent path=lifecycle pathname="lifecycle"/>
    <br>
    <#if lifecycle.children?size &gt; 0>
       <table>
          <@recurseuri lifecycle=lifecycle/>
       </table>
       <br>
    </#if>
    <#macro recurseuri lifecycle>
       <#list lifecycle.children as childpath>
          <#if childpath.scripts?size &gt; 0>
            <tr><td><a href="${url.serviceContext}/index/lifecycle${childpath.path}">${childpath.name}</a>
          </#if>
          <@recurseuri lifecycle=childpath/>
       </#list>  
    </#macro>
    <#list lifecycle.scripts as webscript>
    <#assign desc = webscript.description>
    <span class="mainSubTitle">${desc.shortName}</span>
    <table>
      <#list desc.URIs as uri>
        <tr><td><a href="${url.serviceContext}${uri}">${desc.method} ${url.serviceContext}${uri}</a>
      </#list>
      <tr><td>
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
      <tr><td>Format Style:</td><td>${desc.formatStyle}</td><tr>
      <tr><td>Default Format:</td><td>${desc.defaultFormat!"<i>Determined at run-time</i>"}</td></tr>
      <tr><td>Lifecycle:</td><td>${desc.lifecycle}</td></tr>
      <tr><td></td><td></td></tr>
      <tr><td>Id:</td><td><a href="${url.serviceContext}/script/${desc.id}">${desc.id}</a></td></tr>
      <tr><td>Descriptor:</td><td><a href="${url.serviceContext}/description/${desc.id}">${desc.storePath}/${desc.descPath}</a></td></tr>
    </table>
    <br>
    </#list>
    </div>
  </body>
</html>