<#import "workflow.lib.ftl" as workflow/>
<#import "filter/filter.lib.ftl" as filter/>
<#assign el=args.htmlid?js_string>
<div id="${el}-body" class="workflow-list">
   <div class="yui-g workflow-list-bar flat-button">
      <div class="yui-u first">
         <h2 id="${el}-filterTitle" class="thin">
            &nbsp;
         </h2>
      </div>
      <div class="yui-u">
         <div id="${el}-paginator" class="paginator">&nbsp;</div>
      </div>
   </div>
   <div id="${el}-workflows" class="workflows"></div>
</div>
<script type="text/javascript">//<![CDATA[
   new Alfresco.component.WorkflowList("${el}").setOptions(
   {
      filterParameters: <@filter.jsonParameterFilter filterParameters />,
      hiddenWorkflowNames: <@workflow.jsonHiddenTaskTypes hiddenWorkflowNames/>,
      workflowDefinitions: <@workflow.jsonWorkflowDefinitions workflowDefinitions/>,
      maxItems: ${maxItems!"50"}
   }).setMessages(
      ${messages}
   );
//]]></script>
