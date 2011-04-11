<#include "../../include/alfresco-macros.lib.ftl" />
<#assign el=args.htmlid?js_string>
<script type="text/javascript">//<![CDATA[
   new Alfresco.component.StartWorkflow("${el}").setOptions({
      failureMessage: "message.failure",
      submitButtonMessageKey: "button.startWorkflow",
      defaultUrl: "${siteURL("my-tasks")}",
      selectedItems: "${(page.url.args.selectedItems!"")?js_string}",
      destination: "${(page.url.args.destination!"")?js_string}",
      workflowDefinitions:
      [<#list workflowDefinitions as workflowDefinition>
         {
            name: "${workflowDefinition.name!""?js_string}",
            title: "${workflowDefinition.title!""?js_string}",
            description: "${workflowDefinition.description!""?js_string}"
         }<#if workflowDefinition_has_next>,</#if>
      </#list>],
      minSearchTermLength: ${(args.minSearchTermLength!config.scoped['Search']['search'].getChildValue('min-search-term-length'))?js_string},
      maxSearchResults: ${(args.maxSearchResults!config.scoped['Search']['search'].getChildValue('max-search-results'))?js_string}
   }).setMessages(
      ${messages}
   );
//]]></script>
<div id="${el}-body" class="form-manager start-workflow">
   <h1>${msg("header")}</h1>
   <div>
      <label for="${el}-workflowDefinitions" class="workflow-definition">${msg("label.workflow")}:</label>

      <#-- Workflow type menu button  -->
      <span class="selected-form-button">
         <span id="${el}-workflow-definition-button" class="yui-button yui-menu-button">
            <span class="first-child">
               <button type="button" tabindex="0"></button>
            </span>
         </span>
      </span>
      <#-- Workflow type menu -->
      <div id="${el}-workflow-definition-menu" class="yuimenu">
         <div class="bd">
            <ul>
               <#list workflowDefinitions as workflowDefinition>
               <li>
                  <span class="title" tabindex="0">${workflowDefinition.title!workflowDefinition.id?html}</span>
                  <span class="description">${(workflowDefinition.description!"")?html}</span>
               </li>
               </#list>
            </ul>
         </div>
      </div>
   </div>
</div>
<div id="${el}-workflowFormContainer"></div>
