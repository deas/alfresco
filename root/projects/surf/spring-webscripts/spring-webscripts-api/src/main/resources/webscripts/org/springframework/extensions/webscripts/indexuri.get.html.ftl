<#import "/org/springframework/extensions/webscripts/webscripts.lib.html.ftl" as wsLib/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <@wsLib.head>Index of Web Scripts URI '${uri.path}'</@wsLib.head>
  <body>
   <div>
   <@wsLib.indexheader size=uri.scripts?size>Index of Web Scripts URI '${uri.path}'</@wsLib.indexheader>
    <br/>
    <@wsLib.home/>
    <@wsLib.parent path=uri pathname="uri"/>
    <br/>
    <#if uri.children?size &gt; 0>
       <table>
          <@recurseuri uri=uri/>
       </table>
       <br>
    </#if>
    <#macro recurseuri uri>
       <#list uri.children as childpath>
          <#if childpath.scripts?size &gt; 0>
            <tr><td><a href="${url.serviceContext}/index/uri${childpath.path}">${childpath.path}</a>
          </#if>
          <@recurseuri uri=childpath/>
       </#list>  
    </#macro>
    <#list uri.scripts as webscript>
    <#assign desc = webscript.description>
    <span class="mainSubTitle">${desc.shortName}</span>
    <table>
      <#list desc.URIs as uri>
        <tr><td><a href="${url.serviceContext}${uri}">${desc.method} ${url.serviceContext}${uri}</a>
      </#list>
      <tr><td>
    </table>
    <table>
      <#if desc.description??><tr><td>Description:<td>${desc.description}<#else></#if>
      <tr><td>Authentication:<td>${desc.requiredAuthentication}
      <tr><td>Transaction:<td>${desc.requiredTransaction}
      <tr><td>Format Style:<td>${desc.formatStyle}
      <#if desc.lifecycle != 'none'>
      <tr><td>Lifecycle:</td><td>${desc.lifecycle}</td></tr>
      </#if>
      <tr><td>Default Format:<td>${desc.defaultFormat!"<i>Determined at run-time</i>"}
      <tr><td>
      <tr><td>Id:<td><a href="${url.serviceContext}/script/${desc.id}">${desc.id}</a>
      <tr><td>Description:<td><a href="${url.serviceContext}/description/${desc.id}">${desc.storePath}/${desc.descPath}</a>
    </table>
    <br>
    </#list>
    </div>
  </body>
</html>