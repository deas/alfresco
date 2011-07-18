<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <@script type="text/javascript" src="${url.context}/res/modules/documentlibrary/doclib-actions.js"></@script>
   <@script type="text/javascript" src="${page.url.context}/res/templates/manage-permissions/template.manage-permissions.js"></@script>   
</@>

<@templateBody>
   <div id="alf-hd">
      <@region id="header" scope="global" />
      <@region id="title" scope="template" />
      <@region id="navigation" scope="template" />
   </div>
   <div id="bd">
      <@region id="path" scope="template" />
      <@region id="manage-permissions" scope="template" />
   </div>

   <script type="text/javascript">//<![CDATA[
   new Alfresco.template.ManagePermissions().setOptions(
   {
      nodeRef: new Alfresco.util.NodeRef("${url.args.nodeRef?js_string}"),
      siteId: "${page.url.templateArgs.site!""}",
      rootNode: "${(config.scoped["RepositoryLibrary"]["root-node"].getValue())!"alfresco://company/home"}"
   });
   //]]></script>
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" />
   </div>
</@>
