<#assign helpPages = config.scoped["HelpPages"]["help-pages"]>
<#assign helpLink = helpPages.getChildValue("share-help")!"">
<#assign tutorialLink = helpPages.getChildValue("share-tutorial")!"">
<div class="dashlet site-welcome">
   <div class="title">${msg("header.userWelcome")}</div>
   <div class="body">
      <div class="detail-list-item-alt theme-bg-color-2 theme-border-4">
         <h4 class="theme-color-2">${msg("header.siteDashboard")}</h4>
         <div>${msg("text.siteDashboard")}</div>
      </div>
<#if userIsSiteManager>
      <div class="detail-list-item">
         <h4 class="theme-color-2">${msg("header.joinSite")}</h4>
         <div>${msg("text.joinSite")}</div>
         <div><a href="${url.context}/page/site/${page.url.templateArgs.site!}/invite" class="theme-color-2">${msg("link.joinSite")}</a></div>
      </div>
      <div class="detail-list-item">
         <h4 class="theme-color-2">${msg("header.customiseDashboard")}</h4>
         <div>${msg("text.customiseDashboard")}</div>
         <div><a href="${url.context}/page/site/${page.url.templateArgs.site!}/customise-site-dashboard" class="theme-color-2">${msg("link.customiseDashboard")}</a></div>
      </div>
      <div class="detail-list-item last-item">
         <h4 class="theme-color-2">${msg("header.customiseSite")}</h4>
         <div>${msg("text.customiseSite")}</div>
         <div><a href="${url.context}/page/site/${page.url.templateArgs.site!}/customise-site" class="theme-color-2">${msg("link.customiseSite")}</a></div>
      </div>         
<#else>
      <div class="detail-list-item">
         <h4 class="theme-color-2">${msg("header.siteMembers")}</h4>
         <div>${msg("text.siteMembers")}</div>
         <div><a href="${url.context}/page/site/${page.url.templateArgs.site!}/site-members" class="theme-color-2">${msg("link.siteMembers")}</a></div>
      </div>
      <div class="detail-list-item">
         <h4 class="theme-color-2">${msg("header.onlineHelp")}</h4>
         <div>${msg("text.onlineHelp")}</div>
         <div><a href="${helpLink}" class="theme-color-2" target="_blank">${msg("link.onlineHelp")}</a></div>
      </div>
      <div class="detail-list-item last-item">
         <h4 class="theme-color-2">${msg("header.featureTour")}</h4>
         <div>${msg("text.featureTour")}</div>
         <div><a href="${tutorialLink}" class="theme-color-2" target="_blank">${msg("link.featureTour")}</a></div>
      </div>
</#if>
      <div class="clear"></div>
   </div>                                                    
</div>