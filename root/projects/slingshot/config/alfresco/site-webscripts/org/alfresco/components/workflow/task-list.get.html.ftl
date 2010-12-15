<#import "workflow.lib.ftl" as workflow/>
<#import "filter/filter.lib.ftl" as filter/>
<#assign el=args.htmlid?js_string>
<div id="${el}-body" class="task-list">
   <div class="yui-g task-list-bar flat-button">
      <div class="yui-u first">
         <h2 id="${el}-filterTitle" class="thin">
            &nbsp;
         </h2>
      </div>
      <div class="yui-u">
         <div id="${el}-paginator" class="paginator">&nbsp;</div>
      </div>
   </div>
   <div id="${el}-tasks" class="tasks"></div>
</div>
<script type="text/javascript">//<![CDATA[
   new Alfresco.component.TaskList("${el}").setOptions(
   {
      filterParameters: <@filter.jsonParameterFilter filterParameters />,
      hiddenTaskTypes: <@workflow.jsonHiddenTaskTypes hiddenTaskTypes/>,
      maxItems: ${maxItems!"50"}
   }).setMessages(
      ${messages}
   );
//]]></script>
