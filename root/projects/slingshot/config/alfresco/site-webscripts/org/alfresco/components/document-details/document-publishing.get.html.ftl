<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/document-details/document-publishing.css" group="document-details"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/document-details/document-publishing.js" group="document-details"/>
</@>

<@markup id="widgets">
   <#if document??>
      <@createWidgets group="document-details"/>
      <@inlineScript group="document-details">
         YAHOO.util.Event.onContentReady("${args.htmlid}-heading", function() {
            Alfresco.util.createTwister("${args.htmlid}-heading", "DocumentPublishing");
         });
      </@>
   </#if>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#if document??>
         <#assign el=args.htmlid?html>
         <div id="${el}-body" class="document-publishing document-details-panel">
            <h2 id="${el}-heading" class="thin dark">
               ${msg("header.publishingHistory")}
            </h2>
            <div class="panel-body">
               <div id="${el}-publishing-events" class="publish-events"></div>
            </div>
         </div>
      </#if>
   </@>
</@>

