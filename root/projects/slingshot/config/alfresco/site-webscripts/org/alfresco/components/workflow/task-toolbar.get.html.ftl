<#include "../../include/alfresco-macros.lib.ftl" />
<div class="share-toolbar theme-bg-2">
   <div class="navigation-bar">
      <div>
         <span class="<#if (page.url.args.myTasksLinkBack! == "true")>backLink<#else>forwardLink</#if>">
            <a href="${siteURL("my-tasks")}">${msg("link.myTasks")}</a>
         </span>
      </div>
   </div>
</div>
