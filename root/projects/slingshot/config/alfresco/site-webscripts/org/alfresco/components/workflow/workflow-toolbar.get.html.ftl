<#include "../../include/alfresco-macros.lib.ftl" />
<div class="share-toolbar theme-bg-2">
   <div class="navigation-bar">
      <div>
         <span class="<#if (page.url.args.myWorkflowsLinkBack! == "true")>backLink<#else>forwardLink</#if>">
            <a href="${siteURL("my-workflows")}">${msg("link.myWorkflows")}</a>
         </span>
      </div>
   </div>
</div>
