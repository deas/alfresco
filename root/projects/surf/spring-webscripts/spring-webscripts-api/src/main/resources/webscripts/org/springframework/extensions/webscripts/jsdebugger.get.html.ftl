<#import "/org/springframework/extensions/webscripts/webscripts.lib.html.ftl" as wsLib/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
   <@wsLib.head>Alfresco Javascript Debugger</@wsLib.head>
   <body>
     <div>
      <form action="${url.serviceContext}${url.match}" method="post">
         <div>
            <input type="hidden" name="visible" value="<#if visible>false<#else>true</#if>" />
            <table>
               <tr>
                  <td><img src="${resourceurl('/images/logo/AlfrescoLogo32.png', true)}" alt="Alfresco" /></td>
                  <td><span class="title">Alfresco Javascript Debugger</span></td>
               </tr>
               <tr><td colspan="2">Alfresco ${server.edition?html} v${server.version?html}</td></tr>
               <tr><td colspan="2">Currently <#if visible>enabled<#else>disabled</#if>.
                                   <input type="submit" name="submit" value="<#if visible>Disable<#else>Enable</#if>" /></td></tr>
            </table>
         </div>
      </form>
      </div>
   </body>
</html>