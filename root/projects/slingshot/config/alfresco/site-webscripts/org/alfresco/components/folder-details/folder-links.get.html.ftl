<#if folder??>
   <#assign el=args.htmlid?html>
   <script type="text/javascript">//<![CDATA[
      new Alfresco.FolderLinks("${el}").setOptions(
      {
         nodeRef: "${nodeRef?js_string}",
         siteId: <#if site??>"${site?js_string}"<#else>null</#if>,
      }).setMessages(${messages});
   //]]></script>

   <div id="${el}-body" class="folder-links folder-details-panel">

      <h2 id="${el}-heading" class="thin dark">${msg("header")}</h2>

      <div class="panel-body">

         <!-- Current page url - (javascript will prefix with the current browser location) -->
         <h3 class="thin dark">${msg("page.header")}</h3>
         <div class="link-info">
            <input id="${el}-page" value=""/>
            <a href="#" name=".onCopyLinkClick" class="${el} hidden">${msg("page.copy")}</a>
         </div>

   <#if webdavUrl??>
         <!-- webdav link -->
         <h3 class="thin dark">${msg("webdav.header")}</h3>
         <div class="link-info">
            <input id="${el}-page" value="${webdavUrl}" />
            <a href="#" name=".onCopyLinkClick" class="${el} hidden">${msg("webdav.copy")}</a>
         </div>
   </#if>

      </div>
   </div>

   <script type="text/javascript">//<![CDATA[
   Alfresco.util.createTwister("${el}-heading", "FolderLinks");
   //]]></script>
</#if>