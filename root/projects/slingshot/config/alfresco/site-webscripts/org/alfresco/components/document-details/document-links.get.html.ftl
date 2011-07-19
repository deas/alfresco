<#if document??>
   <#if document.workingCopy??>
      <!-- Don't display links since this nodeRef points to one of a working copy pair -->
   <#else>
      <#assign el=args.htmlid?html>
      <script type="text/javascript">//<![CDATA[
      new Alfresco.DocumentLinks("${el}").setOptions(
      {
         nodeRef: "${nodeRef?js_string}",
         siteId: <#if site??>"${site?js_string}"<#else>null</#if>
      }).setMessages(${messages});
      //]]></script>
      <div id="${el}-body" class="document-links document-details-panel">

         <h2 id="${el}-heading" class="thin dark">${msg("header")}</h2>

         <div class="panel-body">

            <!-- Current page url - (javascript will prefix with the current browser location) -->
            <h3 class="thin dark">${msg("page.header")}</h3>
            <div class="link-info">
               <input id="${el}-page" value=""/>
               <a href="#" name=".onCopyLinkClick" class="${el} hidden">${msg("page.copy")}</a>
            </div>

         </div>

         <script type="text/javascript">//<![CDATA[
         Alfresco.util.createTwister("${el}-heading", "DocumentLinks");
         //]]></script>

      </div>
   </#if>
</#if>