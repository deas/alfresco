<#assign el=args.htmlid?js_string>
<script type="text/javascript">//<![CDATA[
   new Alfresco.ConsoleTagManagement("${el}").setOptions(
   {
   	  pageSize: ${args.pageSize!"15"}
   }).setMessages(${messages});
//]]></script>

<div class="search-panel">
    <div class="search-text"><input type="text" id="${el}-search-text" name="-" value="" />
               <!-- Search button -->
      <div class="search-button">
         <span class="yui-button yui-push-button" id="${el}-search-button">
         	<span class="first-child"><button>${msg("button.search")}</button></span>
         </span>
      </div>
    </div>
</div>

<div class="dashlet tags-List">
   <div class="title">${msg("item.tagList")}</div>
   <div id="${el}-tags-list-info" class="tags-list-info"></div>
   <div id="${el}-tags-list-bar-bottom" class="toolbar theme-bg-color-3 hidden">
   	   <div id="${el}-paginator" class="paginator hidden">&nbsp;</div>
   </div>
   <div id="${el}-tags" class="body scrollableList" <#--if args.height??-->style=" height: 439px; overflow: hidden;"<#--/#if-->>
   </div>
</div>