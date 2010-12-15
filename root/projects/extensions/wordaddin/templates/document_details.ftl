<#list template.parent.children as child>
   <#if child.name = "my_alfresco.ftl"><#assign office_home = child.id>
   <#elseif child.name = "navigation.ftl"><#assign office_browse = child.id>
   <#elseif child.name = "search.ftl"><#assign office_search = child.id>
   <#elseif child.name = "document_details.ftl"><#assign office_details = child.id>
   <#elseif child.name = "version_history.ftl"><#assign office_history = child.id>
   <#elseif child.name = "doc_actions.js"><#assign doc_actions = child.id>
   <#elseif child.name = "doc_details.js"><#assign doc_script = child>
   </#if>
</#list>
<!DOCTYPE html
PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>

<head>
<title>Document Details</title>
<link rel="stylesheet" type="text/css" href="/alfresco/css/taskpane.css" />
<script type="text/javascript" src="/alfresco${doc_script.url}">
</script>
</head>

<body>

<div id="tabBar">
    <ul>
      <li><a href="/alfresco/template/workspace/SpacesStore/${document.id}/workspace/SpacesStore/${office_home}"><img src="/alfresco/images/taskpane/my_alfresco.gif" border="0" alt="My Alfresco" /></a></li>
      <li><a href="/alfresco/template/workspace/SpacesStore/${document.id}/workspace/SpacesStore/${office_browse}"><img src="/alfresco/images/taskpane/navigator.gif" border="0" alt="Browse Spaces and Documents" /></a></li>
      <li style="padding-right:6px;"><a href="/alfresco/template/workspace/SpacesStore/${document.id}/workspace/SpacesStore/${office_search}"><img src="/alfresco/images/taskpane/search.gif" border="0" alt="Search Alfresco" /></a></li>
      <li id="current"><a href="#"><img src="/alfresco/images/taskpane/document_details.gif" border="0" alt="View Details" /></a></li>
      <li><a href="/alfresco/template/workspace/SpacesStore/${document.id}/workspace/SpacesStore/${office_history}"><img src="/alfresco/images/taskpane/version_history.gif" border="0" alt="View Version History" /></a></li>
    </ul>
  </div>

<div id="detailsListHeader"><span style="font-weight:bold">Details</span></div>

<div id="detailsList">
   <table>
      <tbody>

         <tr>
            <td valign="top">
<#if document.isDocument && document != template>
               <img src="/alfresco${document.icon32}" border="0" alt="${document.name}" />
            </td>
            <td style="line-height:16px;" width="100%">
               <span style="font-weight:bold;">${document.name}
   <#if document.isLocked >
                  <img src="/alfresco/images/taskpane/lock.gif" border="0" style="padding:3px 6px 2px 0px;" alt="Locked">
   </#if>
               </span><br/>
               <table>
   <#if document.properties.title?exists>
                  <tr><td>Title:</td><td>${document.properties.title}</td></tr>
   <#else>
                  <tr><td>Title:</td><td></td></tr>
   </#if>
   <#if document.properties.description?exists>
		            <tr><td>Description:</td><td>${document.properties.description}</td></tr>
   <#else>
                  <tr><td valign="top">Description:</td><td></td></tr>
   </#if>
                  <tr><td>Creator:</td><td>${document.properties.creator}</td></tr>
                  <tr><td>Created:</td><td>${document.properties.created?datetime}</td></tr>
                  <tr><td>Modifier:</td><td>${document.properties.modifier}</td></tr>
                  <tr><td>Modified:</td><td>${document.properties.modified?datetime}</td></tr>
                  <tr><td>Size:</td><td>${document.size / 1024} Kb</td></tr>
                  <tr><td valign="top">Categories:</td>
                     <td>
   <#if document.hasAspect("cm:generalclassifiable")>
      <#list document.properties.categories as category>
                       ${companyhome.nodeByReference[category].name};
      </#list>
   <#else>
                       None.
   </#if>
                     </td>
                  </tr>
               </table>
<#else>
                       The current document is not managed by Alfresco.
</#if>
            </td>
         </tr>

      </tbody>
   </table>
</div>

<div id="documentActions">
<span style="font-weight:bold;">Document Actions</span><br/>
<#if document.isDocument && document != template>
<ul>
<#if document.isLocked >
<#elseif hasAspect(document, "cm:workingcopy") == 1>
    <li><a href="#" onClick="javascript:runAction('${doc_actions}','checkin','${document.id}', '');"><img src="/alfresco/images/taskpane/checkin.gif" border="0" style="padding-right:6px;" alt="Check In">Check In</a></li>
<#else>
    <li><a href="#" onClick="javascript:runAction('${doc_actions}','checkout','${document.id}', '');"><img src="/alfresco/images/taskpane/checkout.gif" border="0" style="padding-right:6px;" alt="Check Out">Check Out</a></li>
</#if>
    <li><a href="#" onClick="javascript:runAction('${doc_actions}','makepdf','${document.id}', '');"><img src="/alfresco/images/taskpane/makepdf.gif" border="0" style="padding-right:6px;" alt="Transform to PDF">Transform to PDF</a></li>
    <li><a href="/alfresco/navigate/showDocDetails/workspace/SpacesStore/${document.id}?ticket=${session.ticket}" target="_blank"><img src="/alfresco/images/taskpane/document_details.gif" border="0" style="padding-right:6px;" alt="Open Full Details">Open Full Details</a></li>
</ul>
</div>

<#else>
                       No actions available.
</#if>

<div id="bottomMargin"><span id="statusArea">&nbsp;</span>
</div>


</body>
</html>

