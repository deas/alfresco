<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
   <head>
      <title>Web Component Status ${status.code} - ${status.codeName}</title>
      <link rel="stylesheet" href="${url.context}/css/base.css" type="text/css" />
   </head>
   <body>
      <div>
         <table>
            <tr>
               <td><img src="${url.context}/images/logo/AlfrescoLogo32.png" alt="Alfresco" /></td>
               <td><span class="title">Alfresco Surf<br/>Web Component Status ${status.code} - ${status.codeName}</span></td>
            </tr>
         </table>
         <br/>
         <table>
            <tr><td>The Web Component '${page.title}' has responded with a status of ${status.code} - ${status.codeName}.</td></tr>
         </table>
         <br/>
         <table>
            <tr><td><b>${status.code} Description:</b></td><td> ${status.codeDescription}</td></tr>
            <tr><td>&nbsp;</td></tr>
            <tr><td><b>Message:</b></td><td>${status.message!"<i>&lt;Not specified&gt;</i>"}</td></tr>
            <#if status.exception?exists>
            <tr><td></td><td>&nbsp;</td></tr>
            <@recursestack status.exception/>
            </#if>
            <tr><td><b>Server</b>:</td><td>Alfresco ${server.edition?html} v${server.version?html} schema ${server.schema?html}</td></tr>
            <tr><td><b>Time</b>:</td><td>${date?datetime}</td></tr>
         </table>
      </div>
   </body>
</html>

<#macro recursestack exception>
   <#if exception.cause?exists>
      <@recursestack exception=exception.cause/>
   </#if>
   <#if exception.message?? && exception.message?is_string>
   <tr><td><b>Exception:</b></td><td>${exception.class.name} - ${exception.message}</td></tr>
   <tr><td></td><td>&nbsp;</td></tr>
   <#if exception.cause?exists == false>
      <#list exception.stackTrace as element>
         <tr><td></td><td>${element}</td></tr>
      </#list>
   <#else>
      <tr><td></td><td>${exception.stackTrace[0]}</td></tr>
   </#if>
   <tr><td></td><td>&nbsp;</td></tr>
   </#if>
</#macro>