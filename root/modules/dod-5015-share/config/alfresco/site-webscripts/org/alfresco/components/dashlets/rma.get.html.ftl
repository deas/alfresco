<script type="text/javascript">//<![CDATA[
   new Alfresco.dashlet.RMA("${args.htmlid}").setMessages(${messages});
//]]></script>
<div class="dashlet">
   <div class="title">${msg("label.title")}</div>
   <div class="body theme-color-1">
      <div class="detail-list-item-alt theme-bg-color-2 theme-color-2"
           style="padding: 0.5em;border-bottom: 1px solid #DDD6A0">
         <h4>${msg("label.summary")}</h4>
      </div>
      <div id="${args.htmlid}-display-site" class="detail-list-item" <#if !foundsite>style="display:none"</#if>>
         <a href="${url.context}/page/site/rm/dashboard">${msg("label.display-site")}</a>
      </div>
      <div id="${args.htmlid}-create-site" class="detail-list-item" <#if foundsite>style="display:none"</#if>>
         <a id="${args.htmlid}-create-site-link" href="#">${msg("label.create-site")}</a>
      </div>
      <#if user.isAdmin>
      <div id="${args.htmlid}-load-data" class="detail-list-item" <#if !foundsite>style="display:none"</#if>>
         <a id="${args.htmlid}-load-data-link" href="#">${msg("label.load-test-data")}</a>
      </div>
      </#if>
      <div class="detail-list-item last-item">
         <a id="${args.htmlid}-role-report-link" href="${url.context}/page/console/rm-console/">${msg("label.rm-console")}</a>
      </div>
   </div>
</div>