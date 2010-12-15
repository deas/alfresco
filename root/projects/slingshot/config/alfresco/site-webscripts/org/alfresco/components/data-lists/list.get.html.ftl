<script type="text/javascript">//<![CDATA[
   new Alfresco.DataListList("${args.htmlid}").setOptions(
      {
         siteId: "${page.url.templateArgs["site"]!""}"
      }).setMessages(${messages});
//]]></script>
<div id="${args.htmlid}-body" class="lists">
   <span id="datalist-newListBtn" class="yui-button yui-push-button">
       <em class="first-child">
           <button type="button" id="newListBtn-button">${msg('label.new-list')}</button>
       </em>
   </span>
   <h2>${msg("header.lists")}</h2>
   <ul class="filterLink">
      <#list lists as list>
      <li><span><a href="${url.context}/page/site/${page.url.templateArgs.site!""}/data-lists?list=${list}" class="filter-link">${list}</a></span></li>
      </#list>
   </ul>
</div>