<#import "/org/springframework/extensions/webscripts/webscripts.lib.html.ftl" as wsLib/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
   <@wsLib.head>${msg("alfresco.index.title")}</@wsLib.head>
   <body>
      <div>
      <@wsLib.indexheader>Web Scripts Home</@wsLib.indexheader>
      <#if failures?size &gt; 0>
      <br/>
      <table>
         <tr><td><a href="${url.serviceContext}/index/failures">(+${failures?size} failed)</td></tr>
      </table>
      </#if>      
      <br>
      <@wsLib.onlinedoc/>
      <br/>
      <span class="mainSubTitle">Index</span>
      <#if rootfamily.children?size &gt; 0>
      <table>
         <#list rootfamily.children as childpath>
         <tr><td><a href="${url.serviceContext}/index/family${childpath.path}">Browse '${childpath.name}' Web Scripts</a></td></tr>
         </#list>  
      </table>
      <br/>
      </#if> 
      <table>
         <tr><td><a href="${url.serviceContext}/index/all">Browse all Web Scripts</a></td></tr>
         <tr><td><a href="${url.serviceContext}/index/uri/">Browse by Web Script URI</a></td></tr>
         <tr><td><a href="${url.serviceContext}/index/package/">Browse by Web Script Package</a></td></tr>
         <tr><td><a href="${url.serviceContext}/index/lifecycle/">Browse by Web Script Lifecycle</a></td></tr>
      </table>
      <br/>
      <br/>
      <span class="mainSubTitle">Maintenance</span>
      <form action="${url.serviceContext}${url.match}" method="post">
          <input type="hidden" name="reset" value="on"/>
          <table>
             <#if failures?size &gt; 0>
             <tr><td><a href="${url.serviceContext}/index/failures">Browse failed Web Scripts</a></td></tr>
             </#if>
             <tr><td><a href="${url.serviceContext}/api/javascript/debugger">Alfresco Javascript Debugger</a></td></tr>
             <tr><td><a href="${url.serviceContext}/modules/deploy">Module Deployment</a></td></tr>
          </table>
          <br/>
          <table>
             <tr><td><input type="submit" name="submit" value="Refresh Web Scripts"/></td></tr>
          </table>
      </form>
      <br/>
      <form action="${url.serviceContext}/caches/dependency/clear" method="post">
          <table>
             <tr><td><input type="submit" name="submit" value="Clear Dependency Caches"/></td></tr>
          </table>
      </form>
      <#if surfbugEnabled??>
          <br/>
          <br/>
          <span class="mainSubTitle">SurfBug</span>
          <table>
            <tr align="left"><td>Current Status: <#if surfbugEnabled>Enabled<#else>Disabled</#if></td></tr>
          </table>
          <form action="${url.serviceContext}/surfBugStatus" method="post">
              <table><tr><td>
              <#if surfbugEnabled>
                  <input type="hidden" name="statusUpdate" value="disabled"/>
                  <input type="submit" name="submit" value="Disable SurfBug"/>
              <#else>
                  <input type="hidden" name="statusUpdate" value="enabled"/>
                  <input type="submit" name="submit" value="Enable SurfBug"/>
              </#if>
              </td></tr></table>
          </form>
       </#if>
      </div>
   </body>
</html>