<#assign el=args.htmlid>
<script type="text/javascript">//<![CDATA[
   new Alfresco.RulesList("${el}").setOptions(
   {
      nodeRef: new Alfresco.util.NodeRef("${(page.url.args.nodeRef!"")?js_string}"),
      siteId: "${page.url.templateArgs.site!""}",      
      filter: "${args.filter!""}",
      selectDefault: ${args.selectDefault!"false"},
      editable: ${args.editable!"false"}
   }).setMessages(
      ${messages}
   );
//]]></script>
<div id="${el}-body" class="rules-list">
   <div id="${el}-rulesListText"></div>   
   <div class="rules-list-bar">
      <span class="rules-list-bar-icon">&nbsp;</span>
      <span id="${el}-rulesListBarText" class="rules-list-bar-text"></span>
   </div>
   <ul id="${el}-rulesListContainer" class="rules-list-container">
      <li class="message">${msg("message.loadingRules")}</li>
   </ul>
   <div id="${el}-buttonsContainer" class="rules-button-container hidden">
      <button id="${el}-save-button" tabindex="0">${msg("button.save")}</button>
      <button id="${el}-reset-button" tabindex="0">${msg("button.reset")}</button>
   </div>

   <!-- Rule Templates -->
   <div style="display:none">
      <ul id="${el}-ruleTemplate" >         
         <li class="rules-list-item">
            <input type="hidden" class="id" name="id" value=""/>
            <div class="rule-icons">
               <span class="no">&nbsp;</span>
               <span class="active-icon">&nbsp;</span>
               <span class="rule-icon">&nbsp;</span>
            </div>
            <div class="info">
               <a class="title" href="#">Name</a><span class="inherited">&nbsp;</span><br/>
               <span class="inherited-from">&nbsp;</span><a class="inherited-folder">&nbsp;</a>
               <span class="description">Description of the rules will go here</span>
            </div>
            <div class="clear"></div>
         </li>
      </ul>
   </div>

</div>
