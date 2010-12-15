<#assign returl=url.full?url>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
   <head>
      <title>Details</title>
      <link rel="stylesheet" href="${url.context}/css/gg.css" TYPE="text/css"/>
      <style type="text/css">
div.details
{
   padding: 2px 2px 6px 2px;
}

div.detailAction
{
   text-align: center;
}

div.actionPanel
{
   margin-left:8px;
   border: 1px solid #cccccc;
}

tr
{
   vertical-align: top;
}
      </style>
      <script type="text/javascript">
function goback()
{
   window.location.href = "${args.returl}";
}
      </script>
   </head>
   
   <body>
   
   <div class="main">
      <div class="titlebar">Details for '${node.name}'</div>
      <div class="dialog">
         <div class="details">
            <table cellspacing="0" cellpadding="0" border="0" width="100%">
               <tr><td style="vertical-align:middle" rowspan="99" width="44" align="left"><#if node.isDocument><a href="${url.serviceContext}${node.serviceUrl}" target="alfnew"></#if><img src="${url.context}${node.icon32}" width="32" height="32" border="0" alt="${node.name?html}" title="${node.name?html}"/><#if node.isDocument></a></#if></td>
                   <td><span class="metaTitle">Name:</span></td><td rowspan="99" width="8"></td><td><span class="metaData">${node.name?html}</span></td>
                   <td rowspan="99">
                     <div class="actionPanel">
                        <#assign act=false>
                        <div class="titlebar" style="padding: 0px 4px 0px 4px; margin-bottom: 4px; text-align: center;">Actions</div>
                        
                        <#if node.hasPermission("Write")>
                        <#assign act=true>
                        <div class="detailAction">
                           <a href="${url.serviceContext}/aggadget/editdetails?id=${node.id}&returl=${returl}" title="Edit Details"><img src="${url.context}/images/icons/edit_properties.gif" border="0" alt="Edit Details" title="Edit Details"/></a>
                        </div>
                        </#if>
                        
                        <#if node.hasPermission("Write") && node.isDocument>
                        <#assign act=true>
                        <div class="detailAction">
                           <a href="${url.serviceContext}/aggadget/update?name=${node.name?url}&id=${node.id}&returl=${returl}" title="Update"><img src="${url.context}/images/icons/update.gif" border="0" alt="Update" title="Update"/></a>
                        </div>
                        </#if>
                        
                        <#if node.hasPermission("Delete")>
                        <#assign act=true>
                        <div class="detailAction">
                           <a href="${url.serviceContext}/aggadget/delete?name=${node.name?url}&id=${node.id}&returl=${args.returl}" title="Delete"><img src="${url.context}/images/icons/delete.gif" border="0" alt="Delete" title="Delete"/></a>
                        </div>
                        </#if>
                        
                        <#if !act><div class="detailAction"><i>None</i></div></#if>
                     </div>
                   </td>
               </tr>
               <tr><td><span class="metaTitle">Title:</span></td><td><span class="metaData"><#if node.properties.title?exists>${node.properties.title?html}<#else>&nbsp;</#if></span></td></tr>
               <tr><td><span class="metaTitle">Description:</span></td><td><span class="metaData"><#if node.properties.description?exists>${node.properties.description?html}<#else>&nbsp;</#if></span></td></tr>
               <#if node.isDocument>
               <tr><td><span class="metaTitle">Author:</span></td><td><span class="metaData"><#if node.properties.author?exists>${node.properties.author?html}<#else>&nbsp;</#if></span></td></tr>
               </#if>
               <tr><td><span class="metaTitle">Modified:</span></td><td><span class="metaData">${node.properties.modified?datetime}</span></td></tr>
               <tr><td><span class="metaTitle">Modified By:</span></td><td><span class="metaData">${node.properties.modifier?html}</span></td></tr>
               <tr><td><span class="metaTitle">Created:</span></td><td><span class="metaData">${node.properties.created?datetime}</span></td></tr>
               <tr><td><span class="metaTitle">Created By:</span></td><td><span class="metaData">${node.properties.creator?html}</span></td></tr>
               <#if node.isDocument>
               <tr><td><span class="metaTitle">Content Type:</span></td><td><span class="metaData">${node.displayMimetype!"<i>Unknown</i>"}</span></td></tr>
               <tr><td><span class="metaTitle">Encoding:</span></td><td><span class="metaData">${node.encoding!"<i>Unknown</i>"}</span></td></tr>
               <tr><td><span class="metaTitle">Size:</span></td><td><span class="metaData">${(node.size/1000)?string("0.##")}&nbsp;KB</span></td></tr>
               </#if>
            </table>
         </div>
         <div><input style="width:auto" type="button" onclick="goback();" value="Continue"/></div>
      </div>
   </div>
   
   </body>
</html>