<#if document??>
   <#assign el=args.htmlid?html>
   <script type="text/javascript">//<![CDATA[
   	new Alfresco.DocumentPublishing("${args.htmlid?js_string}").setOptions(
   	{
   	  nodeRef: "${nodeRef?js_string}"
   	}).setMessages(
   	   ${messages}
   	);
   //]]></script>

   <div id="${el}-body" class="document-publishing document-details-panel">

      <h2 id="${el}-heading" class="thin dark">
         ${msg("header.publishingHistory")}
      </h2>

      <div class="panel-body">
      	<div id="${el}-publishing-events" class="publish-events"></div>
      </div>

      <script type="text/javascript">//<![CDATA[
         Alfresco.util.createTwister("${el}-heading", "DocumentPublishing");
      //]]></script>

   </div>
</#if>