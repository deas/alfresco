<script type="text/javascript">//<![CDATA[
   new Alfresco.dashlet.Activities("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      mode: "site"
   }).setMessages(
      ${messages}
   );
   new Alfresco.widget.DashletResizer("${args.htmlid}", "${instance.object.id}");
//]]></script>

<div class="dashlet activities">
   <div class="title">${msg("header")}</div>
   <div class="feed"><a id="${args.htmlid}-feedLink" href="#" target="_blank">&nbsp;</a></div>
   <div class="toolbar flat-button">
      <input id="${args.htmlid}-range" type="button" name="range" value="${msg("filter.today")}" />
      <select id="${args.htmlid}-range-menu">
         <option value="today">${msg("filter.today")}</option>
         <option value="7">${msg("filter.7days")}</option>
         <option value="14">${msg("filter.14days")}</option>                
         <option value="28">${msg("filter.28days")}</option>
      </select>
      <input id="${args.htmlid}-user" type="button" name="user" value="${msg("filter.others")}" />
      <select id="${args.htmlid}-user-menu">
         <option value="mine">${msg("filter.mine")}</option>
         <option value="others">${msg("filter.others")}</option>                
         <option value="all">${msg("filter.all")}</option>
      </select>
   </div>
   <div id="${args.htmlid}-activityList" class="body scrollableList" <#if args.height??>style="height: ${args.height}px;"</#if>>
   </div>
</div>