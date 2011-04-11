<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <@script type="text/javascript" src="${url.context}/res/modules/documentlibrary/doclib-actions.js"></@script>
   <@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/document-details/document-details-panel.css" />
   <@templateHtmlEditorAssets />
</@>

<@templateBody>
   <div id="alf-hd">
      <@region id=appType + "header" scope="global" protected=true />
      <@region id=appType + doclibType + "title" scope="template" protected=true />
      <@region id=appType + doclibType + "navigation" scope="template" protected=true />
   </div>
   <div id="bd">
      <@region id=doclibType + "actions-common" scope="template" protected=true />
      <@region id=doclibType + "actions" scope="template" protected=true />
      <@region id=doclibType + "document-header" scope="template" protected=true />

      <div class="yui-gc">
         <div class="yui-u first">
            <#if (config.scoped['DocumentDetails']['document-details'].getChildValue('display-web-preview') == "true")>
               <@region id=doclibType + "web-preview" scope="template" protected=true />
            </#if>
            <#if doclibType?starts_with("dod5015")>
               <@region id=doclibType + "events" scope="template" protected=true />
            <#else>
               <@region id=doclibType + "comments" scope="template" protected=true />
            </#if>
         </div>
         <div class="yui-u">
            <@region id=doclibType + "document-actions" scope="template" protected=true />
            <@region id=doclibType + "document-tags" scope="template" protected=true />
            <@region id=appType + doclibType + "document-links" scope="template" protected=true />
            <div id="document-details-meta-data">
               <@region id=doclibType + "document-metadata-header" scope="template" protected=true />
               <@region id=doclibType + "document-metadata" scope="template" protected=true />
               <script type="text/javascript">//<![CDATA[
                  var metaDataHeader = YUISelector.query("#document-details-meta-data h2", null, true);
                     metaData = YUISelector.query("#document-details-meta-data > div", null)[1];
                  if (metaDataHeader && metaData)
                  {
                     metaDataHeader.parentNode.insertBefore(metaData, metaDataHeader.nextSibling);
                     Alfresco.util.createTwister(metaDataHeader, "DocumentProperties");
                  }
               //]]></script>
            </div>
            <@region id=doclibType + "document-permissions" scope="template" protected=true />
            <@region id=doclibType + "document-workflows" scope="template" protected=true />
            <@region id=doclibType + "document-versions" scope="template" protected=true />
            <#if doclibType?starts_with("dod5015")>
               <@region id=doclibType + "document-references" scope="template" protected=true />
            </#if>            
         </div>
      </div>

      <@region id="html-upload" scope="template" protected=true />
      <@region id="flash-upload" scope="template" protected=true />
      <@region id="file-upload" scope="template" protected=true />
   </div>

</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>
