<#include "../../include/alfresco-macros.lib.ftl" />
<div id="${args.htmlid}-body" class="document-workflows">

   <div class="info-section">

      <div class="heading">${msg("header.workflows")}</div>

      <div class="info">
      <#if workflows?size == 0>
         ${msg("label.partOfNoWorkflows")}
      <#else>
         ${msg("label.partOfWorkflows")}
      </#if>
      </div>
      
      <#if workflows?size &gt; 0>
         <div class="workflows">
         <#list workflows as workflow>
            <div class="workflow <#if !workflow_has_next>workflow-last</#if>">
               <div class="icon"><img src="${url.context}/res/components/documentlibrary/images/workflow-indicator-16.png" /></div>
               <div class="details">
                  <div class="message">
                     <a href="${siteURL("workflow-details?workflowId=" + workflow.id?js_string + "&nodeRef=" + (args.nodeRef!"")?js_string)}"><#if workflow.message?? && workflow.message?length &gt; 0>${workflow.message?html}<#else>${msg("workflow.no_message")?html}</#if></a>
                  </div>
                  <div class="title">${workflow.title?html}</div>
               </div>
            </div>
         </#list>
         </div>
      </#if>

   </div>

</div>
