<fb:mediaheader uid="${library.properties.creator}">
  <fb:header-title><fb:name uid="${library.properties.creator}" possessive="true" capitalize="true"/> ${library.name} Library</fb:header-title>
</fb:mediaheader>

<div class="summary_bar">
<#if library.children?size &gt; 0>Documents 1 - ${library.children?size}<span class="pipe"> | </span></#if>
<fb:if-is-user uid="${library.properties.creator}">
  <a href="${facebook.canvasURL}/adddocdialog/${library.id}">Add Document</a><span class="pipe"> | </span>
</fb:if-is-user>
<a href="${facebook.canvasURL}/libraries/${library.properties.creator}">Back to <fb:name uid="${library.properties.creator}" possessive="true"/> Libraries</a>
</div>

<style>
<!--
#app${facebook.appId}_appbody { padding: 20px; }
.docrow { border-bottom: 1px solid #CCCCCC; }
-->
</style>

<div id="appbody">

<#if library.children?size = 0>

This library is empty.

<#else>

<table>
  <#list library.children as document>
  <#if document.isDocument>
  <tr>
    <td <#if document_has_next>class="docrow"</#if>>
      <table>
        <tr>
          <td><img align="center" src="${absurl("/alfresco" + document.icon64)}"/></td>
          <td><a href="${absurl(url.context)}/fbservice/api/node/content/${document.nodeRef.storeRef.protocol}/${document.nodeRef.storeRef.identifier}/${document.nodeRef.id}/${document.name?url}?fb_sig_user=${facebook.user}&fb_sig_session_key=${facebook.sessionKey}">${document.name}</a> (${document.properties.content.size} bytes)</td>
        </tr>
        <#if document.properties.description?exists>
        <tr>
          <td></td><td>${document.properties.description}</td>
        </tr>
        </#if>
        <tr>
          <td></td><td>Updated ${document.properties.modified?date}</td>
        </tr>
        <tr>
          <td></td><td>Created ${document.properties.created?date}</td>
        </tr>
      </table>
    </td>
  </tr>
  </#if>
  </#list>
</table>
    
</#if>

</div>