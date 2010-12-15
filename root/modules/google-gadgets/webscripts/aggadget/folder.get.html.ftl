<#assign datetimeformat="EEE, dd MMM yyyy HH:mm">
<#assign weekms=1000*60*60*24*7>
<#assign user=person.properties.userName>
<#assign returl=url.service?url + "?f="?url + filter + "&up_sortby="?url + up_sortby + "&m="?url + mode>

<!-- macros and functions -->
<#function folderLink f>
<#if f.parent.parent?exists><#return folderLink(f.parent) + " &gt; <a class='breadcrumb' href=\"${url.serviceContext}/aggadget/folder" + encodepath(f) + "?f=${filter}&up_sortby=${up_sortby}&m=${mode}\">${f.name?html}</a>"><#else><#return "<a class='breadcrumb' href=\"${url.serviceContext}/aggadget/folder" + encodepath(f) + "?f=${filter}&up_sortby=${up_sortby}&m=${mode}\">${f.name?html}</a>"></#if>
</#function>

<#function encodepath node>
<#if node.parent?exists><#return encodepath(node.parent) + "/" + node.name?url><#else><#return ""></#if>
</#function>

<#macro urlargs>
f=${filter}&up_sortby=${up_sortby}&m=${mode}
</#macro>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
   <head>
      <title>${folder.name}</title>
      <link rel="stylesheet" href="${url.context}/css/gg.css" TYPE="text/css"/>
      <style type="text/css">
a.nodeLink
{
   font-size: 13px;
   font-weight: bold;
}

a.breadcrumb
{
   font-size: 12px;
   font-weight: bold;
   background-color: #DCE4EB;
}

a.filterLink:link, a.filterLink:visited
{
   color: #8EA1B3;
   font-size: 12px;
   font-weight: bold;
   padding-left: 4px;
   padding-right: 4px;
   white-space: nowrap;
}

a.filterLink:hover
{
   color: #168ECE;
   background-color: #EEF7FB;
}

a.filterLinkSelected:link, a.filterLinkSelected:visited
{
   color: #0085CA !important;
}

div.breadcrumb
{
   padding: 2px;
   background-color: #DCE4EB;
   border-bottom: 1px solid #CCD4DB;
}

div.rowEven
{
   padding: 4px 2px 4px 2px;
}

div.rowOdd
{
   padding: 4px 2px 4px 2px;
   background-color: #F1F7FD;
}

div.header
{
   background-image: url(${url.context}/images/parts/ggbrowser_headerbg.png);
   height: 26px;
}

div.nodeActions
{
   float: right;
   padding: 0px 4px 0px 4px;
}
      </style>
      
      <script type="text/javascript" src="${url.context}/scripts/ajax/mootools.v1.11.js"></script>
      <script type="text/javascript" src="${url.context}/scripts/ajax/common.js"></script>
      <script type="text/javascript">setContextPath('${url.context}');</script> 

   </head>
   
   <body>

   <div class="main">
   
   <div class="breadcrumb">${folderLink(folder)}</div>
   
   <!-- filters -->
   <div class="header">
      <table border="0" cellspacing="4" cellpadding="0" width="100%">
         <tr>
            <th><a class="filterLink <#if filter=0>filterLinkSelected</#if>" href="${url.service}?f=0&up_sortby=${up_sortby}&m=${mode}">All</a></th>
            <th><a class="filterLink <#if filter=1>filterLinkSelected</#if>" href="${url.service}?f=1&up_sortby=${up_sortby}&m=${mode}">Folders</a></th>
            <th><a class="filterLink <#if filter=2>filterLinkSelected</#if>" href="${url.service}?f=2&up_sortby=${up_sortby}&m=${mode}">Documents</a></th>
            <th><a class="filterLink <#if filter=3>filterLinkSelected</#if>" href="${url.service}?f=3&up_sortby=${up_sortby}&m=${mode}">My Items</a></th>
            <th><a class="filterLink <#if filter=4>filterLinkSelected</#if>" href="${url.service}?f=4&up_sortby=${up_sortby}&m=${mode}">Recent</a></th>
         </tr>
      </table>
   </div>
   
   <!-- toolbar -->
   <div class="toolbar">
      <#if folder.parent.parent?exists>
         <div style="float:left">
            <a href="${url.serviceContext}/aggadget/folder${encodepath(folder.parent)}?<@urlargs/>"><img src="${url.context}/images/office/arrow_up.gif" border="0" alt=""/>..</a>
         </div>
      </#if>
      <div style="float:right">
         <!-- My Home action -->
         <a href="${url.serviceContext}/aggadget/folder${encodepath(userhome)}?<@urlargs/>">My&nbsp;Home</a>
         &nbsp;
         <!-- View Mode toggle -->
         <#if mode=0><a href="${url.service}?f=${filter}&up_sortby=${up_sortby}&m=1">Mini&nbsp;View</a></#if>
         <#if mode=1><a href="${url.service}?f=${filter}&up_sortby=${up_sortby}&m=0">Full&nbsp;View</a></#if>
         &nbsp;
         <#if folder.hasPermission("Write")>
            <!-- Create Folder action -->
            <a href="${url.serviceContext}/aggadget/createfolder?fdrnodeid=${folder.id}&returl=${returl}">Create&nbsp;Folder</a>
            &nbsp;
            <!-- Upload action -->
            <a href="${url.serviceContext}/aggadget/upload?fdrnodeid=${folder.id}&returl=${returl}">Upload</a>
         </#if>
      </div>
   </div>
   
   <!-- main file/folder list -->
   <#if sortby?starts_with("cm:")>
      <#assign folderlist=folder.children?sort_by(['properties', sortby])>
   <#else>
      <#assign folderlist=folder.children?sort_by(sortby)>
   </#if>
   <#assign count=0>
   <#list folderlist as c>
      <#if (c.isContainer || c.isDocument) &&
         ((filter=0) ||
          (filter=1 && c.isContainer) ||
          (filter=2 && c.isDocument) ||
          (filter=3 && (c.properties.creator == user || c.properties.modifier == user)) ||
          (filter=4 && (dateCompare(c.properties["cm:modified"],date,weekms) == 1 || dateCompare(c.properties["cm:created"],date,weekms) == 1)))>
         <#assign count=count+1>
         <#if c.isContainer>
            <#assign curl=url.serviceContext + "/aggadget/folder" + encodepath(c) + "?f=" + filter + "&up_sortby=" + up_sortby + "&m=" + mode>
         <#elseif c.isDocument>
            <#assign curl=url.serviceContext + c.serviceUrl>
         </#if>
         <#if c.isContainer || c.isDocument>
            <div class="${(count%2=0)?string("rowEven", "rowOdd")}">
               <div style="float:left">
                  <a href="${curl}" <#if c.isDocument>target="alfnew"</#if>><img src="${url.context}<#if mode=0>${c.icon32}" width="32" height="32"<#else>${c.icon16}" width="16" height="16"</#if> border="0" alt="${c.name?html}" title="${c.name?html}"/></a>
               </div>
               <div style="margin-left:<#if mode=0>36px<#else>20px</#if>">
                  <div>
                     <div class="nodeActions">
                        <a href="${url.serviceContext}/aggadget/details?id=${c.id}&returl=${returl}" title="Details"><img src="${url.context}/images/icons/View_details.gif" border="0" alt="Details" title="Details"/></a>
                        <#if c.hasPermission("Write") && c.isDocument>
                        <a href="${url.serviceContext}/aggadget/update?name=${c.name?url}&id=${c.id}&returl=${returl}" title="Update"><img src="${url.context}/images/icons/update.gif" border="0" alt="Update" title="Update"/></a>
                        </#if>
                        <#if c.hasPermission("Delete")>
                        <a href="${url.serviceContext}/aggadget/delete?name=${c.name?url}&id=${c.id}&returl=${returl}" title="Delete"><img src="${url.context}/images/icons/delete.gif" border="0" alt="Delete" title="Delete"/></a>
                        </#if>
                     </div>
                     <a class="nodeLink" href="${curl}" <#if c.isDocument>target="alfnew"</#if> <#if mode=1>title="${c.properties.modified?string(datetimeformat)} <#if c.properties.description?exists>'${c.properties.description?html}'</#if>"</#if>>${c.name?html}</a>
                  </div>
                  <#if mode=0>
                  <div>
                     <#if c.properties.description?exists>${c.properties.description?html}</#if>
                  </div>
                  <div>
                     <span class="metaTitle">Modified:</span>&nbsp;<span class="metaData">${c.properties.modified?string(datetimeformat)}</span>&nbsp;
                     <span class="metaTitle">Modified&nbsp;By:</span>&nbsp;<span class="metaData">${c.properties.modifier}</span>
                     <#if c.isDocument>
                     <span class="metaTitle">Size:</span>&nbsp;<span class="metaData">${(c.size/1000)?string("0.##")}&nbsp;KB</span>
                     </#if>
                  </div>
                  </#if>
               </div>
            </div>
         </#if>
      </#if>
   </#list>
   
   </div>
   
   </body>
</html>