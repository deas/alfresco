<#assign el=args.htmlid?js_string>
<script type="text/javascript">//<![CDATA[
   new Alfresco.dashlet.MyTasks("${el}").setOptions(
   {
      hiddenTaskTypes: [<#list hiddenTaskTypes as type>"${type}"<#if type_has_next>, </#if></#list>],
      maxItems: ${maxItems!"50"},
      filters:
      [<#list filters as filter>
         {
            "text": "${msg(filter.label)?js_string}",
            "value": "${filter.parameters?js_string}"            
         }<#if filter_has_next>,</#if>
      </#list>]
   }).setMessages(
      ${messages}
   );
   new Alfresco.widget.DashletResizer("${el}", "${instance.object.id}");
//]]></script>

<div class="dashlet my-tasks">
   <div class="title">${msg("header")}</div>
   <div class="toolbar yui-toolbar">
      <div class="actions">
         <a href="${page.url.context}/page/start-workflow?referrer=tasks" class="theme-color-1">${msg("link.startWorkflow")}</a>
      </div>
      <div>
         <button id="${el}-filters" class="hide"></button>&nbsp;
      </div>
   </div>
   <div class="toolbar last">
      <div class="links">
         <a href="${page.url.context}/page/my-tasks" class="theme-color-1">${msg("link.allTasks")}</a>
      </div>
      <div id="${el}-paginator">&nbsp;</div>
   </div>
   <div id="${el}-tasks" class="body scrollableList" <#if args.height??>style="height: ${args.height}px;"</#if>>
   </div>
</div>
