<#assign el=args.htmlid?js_string>
<div id="${el}-body" class="task-list-toolbar toolbar">
   <div id="${el}-headerBar" class="header-bar flat-button theme-bg-2">
      <div class="left">
         <div class="hideable hidden">
            <div class="start-workflow"><button id="${el}-startWorkflow-button" name="startWorkflow">${msg("button.startWorkflow")}</button></div>
         </div>
      </div>
      <div class="right">
      </div>
   </div>
</div>
<script type="text/javascript">//<![CDATA[
   new Alfresco.component.TaskListToolbar("${el}").setMessages(
      ${messages}
   );
//]]></script>