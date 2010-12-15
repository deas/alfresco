<#assign id = args.htmlid>
<script type="text/javascript">//<![CDATA[
   new Alfresco.component.DataLists("${id}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      containerId: "${template.properties.container!"dataLists"}",
      listId: "${(page.url.args.list!"")?js_string}",
      listTypes: [<#list listTypes as type>
      {
         name: "${type.name?js_string}",
         title: "${type.title?js_string}",
         description: "${type.description?js_string}"
      }<#if type_has_next>,</#if></#list>]
   }).setMessages(${messages});
//]]></script>
<div id="${id}-body" class="datalists">
   <div id="${id}-headerBar" class="header-bar toolbar flat-button theme-bg-2">
      <div class="left">
         <span id="${id}-newListButton" class="yui-button yui-push-button new-list">
             <span class="first-child">
                 <button type="button">${msg('button.new-list')}</button>
             </span>
         </span>
      </div>
   </div>
   
   <h2>${msg("header.lists")}</h2>
   <div id="${id}-lists" class="filter"></div>

   <div class="horiz-rule">&nbsp;</div>
</div>
