<script type="text/javascript">//<![CDATA[
   new Alfresco.dashlet.MySites("${args.htmlid}").setOptions(
   {
      imapEnabled: ${imapServerEnabled?string}
   }).setMessages(${messages});
   new Alfresco.widget.DashletResizer("${args.htmlid}", "${instance.object.id}");
//]]></script>

<div class="dashlet my-sites">
   <div class="title">${msg("header.mySites")}</div>
   <div class="toolbar flat-button">
      <div class="hidden">
         <span class="yui-button-align">
            <span class="first-child">
               <a href="#" id="${args.htmlid}-createSite-button" class="theme-color-1">${msg("link.createSite")}</a>
            </span>
         </span>
         <input id="${args.htmlid}-type" type="button" name="type" value="${msg("filter.all")}" />
         <select id="${args.htmlid}-type-menu">
            <option value="all">${msg("filter.all")}</option>
            <option value="sites">${msg("filter.sites")}</option>
            <option value="favSites">${msg("filter.favSites")}</option>
            <option value="docWorkspaces">${msg("filter.docWorkspaces")}</option>
            <option value="meetWorkspaces">${msg("filter.meetWorkspaces")}</option>
         </select>
      </div>         
   </div>
   <div id="${args.htmlid}-sites" class="body scrollableList" <#if args.height??>style="height: ${args.height}px;"</#if>></div>
</div>