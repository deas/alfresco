<#import "/org/springframework/extensions/webscripts/webscripts.lib.html.ftl" as wsLib/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <@wsLib.head>Index of Web Scripts Family '${family.path}'</@wsLib.head>
  <body>
    <div>
    <@wsLib.indexheader size=family.scripts?size>Index of Web Scripts Family '${family.path}'</@wsLib.indexheader>
    <br>
    <@wsLib.home/>
    <@wsLib.parent path=family pathname="family"/>
    <br>
    <#if family.children?size &gt; 0>
       <table>
          <@recurseuri family=family/>
       </table>
       <br>
    </#if>
    <#macro recurseuri family>
       <#list family.children as childpath>
          <#if childpath.scripts?size &gt; 0>
            <tr><td><a href="${url.serviceContext}/index/family${childpath.path}">${childpath.name}</a>
          </#if>
          <@recurseuri family=childpath/>
       </#list>  
    </#macro>
    <#list family.scripts as webscript>
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
      <tr><td>Authentication:<td>${desc.requiredAuthentication}
      <tr><td>Transaction:<td>${desc.requiredTransaction}
      <tr><td>Format Style:<td>${desc.formatStyle}
      <tr><td>Default Format:<td>${desc.defaultFormat!"<i>Determined at run-time</i>"}
      <#if desc.lifecycle != 'none'>
      	    <tr><td>Lifecycle:</td><td>${desc.lifecycle}</td></tr>
      </#if>
      <tr><td>
      <tr><td>Id:<td><a href="${url.serviceContext}/script/${desc.id}">${desc.id}</a>
      <tr><td>Descriptor:<td><a href="${url.serviceContext}/description/${desc.id}">${desc.storePath}/${desc.descPath}</a>
    </table>
    <br>
    </#list>
    </div>
  </body>
</html>