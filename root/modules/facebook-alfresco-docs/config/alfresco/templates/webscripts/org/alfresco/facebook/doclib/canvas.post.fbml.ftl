<fb:dashboard>
  <fb:action href="${facebook.canvasURL}/libraries/${facebook.user}">My Documents</fb:action>
  <fb:create-button href="${facebook.canvasURL}/createlibdialog">Create a Document Library</fb:create-button> 
</fb:dashboard>

<fb:tabs>
  <fb:tab-item href='${facebook.canvasURL}' title='Recent Documents' selected='true'/>  
</fb:tabs>

<style>
<!--
#app${facebook.appId}_appbody { padding: 20px; }
.docrow { border-bottom: 1px solid #CCCCCC; }
-->
</style>

<div id="appbody">

<#if recentDocs?size == 0>

None of your friends have recently uploaded documents.

<#else>

<table>
  <#list recentDocs as document>
  <#if document.isDocument>
  <tr>
    <td <#if document_has_next>class="docrow"</#if>>
      <table>
        <tr>
          <td><img align="center" src="${absurl("/alfresco" + document.icon32)}"/></td>
          <td><a href="${absurl(url.context)}/fbservice/api/node/content/${document.nodeRef.storeRef.protocol}/${document.nodeRef.storeRef.identifier}/${document.id}/${document.name?url}?fb_sig_user=${facebook.user}&fb_sig_session_key=${facebook.sessionKey}">${document.name}</a> (<a href="${facebook.canvasURL}/library/${document.parent.id}">${document.parent.name} Library</a>)</td>
        </tr>
        <#if document.properties.description?exists>
        <tr>
          <td></td>
          <td>${document.properties.description}</td>
        </tr>
        </#if>
        <tr>
          <td></td>
          <td>Updated ${document.properties.modified?date} by <fb:name uid="${document.properties.creator}" capitalize="true"/></td>
        </tr>
      </table>
    </td>
  </tr>
  </#if>
  </#list>
</table>
    
</#if>

</div>