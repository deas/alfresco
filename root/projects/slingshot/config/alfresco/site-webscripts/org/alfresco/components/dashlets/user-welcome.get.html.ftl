<#assign el=args.htmlid?html>
<#assign helpPages = config.scoped["HelpPages"]["help-pages"]>
<#assign tutorialLink = helpPages.getChildValue("share-tutorial")!"">
<script type="text/javascript">//<![CDATA[
new Alfresco.dashlet.UserWelcome("${el}");
//]]></script>
<div class="dashlet user-welcome">
   <div class="title">${msg("header.userWelcome")}</div>
   <div class="body">
      <div class="detail-list-item-alt theme-bg-color-2 theme-border-4">
         <h4 class="theme-color-2">${msg("header.userDashboard")}</h4>
         <div>${msg("text.userDashboard")}</div>
      </div>
      <div class="detail-list-item">
         <h4 class="theme-color-2">${msg("header.featureTour")}</h4>
         <div>${msg("text.featureTour")}</div>
         <div><a href="${tutorialLink}" class="theme-color-2" target="_blank">${msg("link.featureTour")}</a></div>
      </div>
      <div class="detail-list-item">
         <h4 class="theme-color-2">${msg("header.userProfile")}</h4>
         <div>${msg("text.userProfile")}</div>
         <div><a href="${url.context}/page/user/profile" class="theme-color-2">${msg("link.userProfile")}</a></div>
      </div>
      <div class="detail-list-item">
         <h4 class="theme-color-2">${msg("header.customiseDashboard")}</h4>
         <div>${msg("text.customiseDashboard")}</div>
         <div><a href="${url.context}/page/customise-user-dashboard" class="theme-color-2">${msg("link.customiseDashboard")}</a></div>
      </div>
      <div class="detail-list-item last-item">
         <h4 class="theme-color-2">${msg("header.createSite")}</h4>
         <div>${msg("text.createSite")}</div>
         <div><a id="${el}-createSite-button" href="#" class="theme-color-2">${msg("link.createSite")}</a></div>
      </div>
      <div class="clear"></div>
   </div>
</div>