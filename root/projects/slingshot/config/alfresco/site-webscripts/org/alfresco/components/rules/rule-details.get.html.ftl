<#assign el=args.htmlid>
<script type="text/javascript">//<![CDATA[
   new Alfresco.RuleDetails("${el}").setOptions(
   {
      nodeRef: new Alfresco.util.NodeRef("${(page.url.args.nodeRef!"")?js_string}"),
      siteId: "${page.url.templateArgs.site!""}"            
   }).setMessages(
      ${messages}
   );
//]]></script>
<div id="${el}-body" class="rule-details">

   <div id="${el}-display" class="display theme-bg-color-6 theme-border-3" style="display: none;">
      <div id="${el}-actions" class="actions">
         <input type="button" id="${el}-edit-button" value="${msg("button.edit")}" tabindex="0"/>
         <input type="button" id="${el}-delete-button" value="${msg("button.delete")}" tabindex="0"/>
      </div>

      <h2 id="${el}-title">&nbsp;</h2>
      <div>
         <em>${msg("label.description")}: </em><span id="${el}-description">&nbsp;</span>
      </div>

      <hr/>

      <div id="${el}-disabled" class="behaviour">${msg("label.disabled")}</div>
      <div id="${el}-executeAsynchronously" class="behaviour">${msg("label.executeAsynchronously")}</div>
      <div id="${el}-applyToChildren" class="behaviour">${msg("label.applyToChildren")}</div>

      <hr/>

      <div id="${el}-configsMessage">${msg("message.loading")}</div>
      <div id="${el}-configsContainer" class="hidden">
         <div id="${el}-ruleConfigType"></div>
         <div id="${el}-conditionSeparator" class="configuration-separator">&nbsp;</div>
         <div id="${el}-ruleConfigIfCondition" class="if"></div>
         <div id="${el}-ruleConfigUnlessCondition" class="unless"></div>
         <div class="configuration-separator">&nbsp;</div>
         <div id="${el}-ruleConfigAction"></div>
      </div>
      
   </div>
</div>
