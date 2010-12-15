<fb:mediaheader uid="${url.extension}">
  <fb:header-title><fb:name uid="${url.extension}" possessive="true" capitalize="true"/> Document Libraries</fb:header-title>
</fb:mediaheader>

<div class="summary_bar">
<#if userFolder.children?size == 1>1 Document Library<#else>${userFolder.children?size} Libraries</#if>
<#if userFolder.children?size &gt; 0>
<fb:if-is-user uid="${url.extension}">
  <span class="pipe"> | </span><a href="${facebook.canvasURL}/createlibdialog">Create Library</a>
</fb:if-is-user>
</#if>
</div>

<style>
<!--
#app${facebook.appId}_appbody { padding: 20px; }
.docrow { border-bottom: 1px solid #CCCCCC; }
-->
</style>

<div id="appbody">

<#if userFolder.children?size = 0>

<#if userFolder.properties.creator == facebook.user>
You haven't created any document libraries yet. <a href="${facebook.canvasURL}/createlibdialog">Create your first library</a>.
<#else>
<fb:name uid="${url.extension}" possessive="true" capitalize="true"/> hasn't created any document libraries yet.
</#if>

<#else>

<table>
  <#list userFolder.children as library>
  <#if library.isContainer>
  <tr>
    <td <#if library_has_next>class="docrow"</#if>>
      <table>
        <tr>
          <td><img align="center" src="${absurl("/alfresco" + library.icon64)}"/></td>
          <td><a href="${facebook.canvasURL}/library/${library.id}">${library.name}</a> (<#if library.children?size == 1>1 Document<#else>${library.children?size} Documents</#if>)</td>
        </tr>
        <#if library.properties.description?exists>
        <tr>
          <td></td><td>${library.properties.description}</td>
        </tr>
        </#if>
        <tr>
          <td></td><td>Updated ${library.properties.modified?date}</td></tr>
        <tr>
          <td></td><td>Created ${library.properties.created?date}</td>
        </tr>
      </table>
    </td>
  </tr>
  </#if>
  </#list>
</table>
    
</#if>

</div>
