<#macro initialFilter>
   <#assign filterId = page.url.args["filter"]!"path">
      initialFilter:
      {
         filterId: "${filterId?js_string}"
      },
</#macro>
<!--[if IE]>
   <iframe id="yui-history-iframe" src="${url.context}/res/yui/history/assets/blank.html"></iframe> 
<![endif]-->
<input id="yui-history-field" type="hidden" />
<script type="text/javascript">//<![CDATA[
   new Alfresco.RecordsDocumentList("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      containerId: "${args.container!"documentLibrary"}",
      initialPath: "${(page.url.args["path"]!"")?js_string}",
      <@initialFilter />
      usePagination: false,
      showFolders: true,
      simpleView: ${(preferences.simpleView!false)?string},
      highlightFile: "${(page.url.args["file"]!"")?js_string}"
   }).setMessages(
      ${messages}
   );
//]]></script>
<div id="${args.htmlid}-body" class="doclist">
   <div id="${args.htmlid}-doclistBar" class="yui-gc doclist-bar flat-button">
      <div class="yui-u first">
         <div class="file-select">
            <button id="${args.htmlid}-fileSelect-button" name="doclist-fileSelect-button">${msg("menu.select")}</button>
            <div id="${args.htmlid}-fileSelect-menu" class="yuimenu">
               <div class="bd">
                  <ul>
                     <li><a href="#"><span class="selectRecords">${msg("menu.select.records")}</span></a></li>
                     <li><a href="#"><span class="selectUndeclaredRecords">${msg("menu.select.undeclared-records")}</span></a></li>
                     <li><a href="#"><span class="selectFolders">${msg("menu.select.folders")}</span></a></li>
                     <li><a href="#"><span class="selectAll">${msg("menu.select.all")}</span></a></li>
                     <li><a href="#"><span class="selectInvert">${msg("menu.select.invert")}</span></a></li>
                     <li><a href="#"><span class="selectNone">${msg("menu.select.none")}</span></a></li>
                  </ul>
               </div>
            </div>
         </div>
         <div id="${args.htmlid}-paginator" class="paginator"></div>
      </div>
      <div class="yui-u align-right">
         <div id="${args.htmlid}-simpleDetailed" class="simple-detailed yui-buttongroup inline">
            <#-- Don't insert linefeeds between these <input> tags -->
            <input id="${args.htmlid}-simpleView" type="radio" name="simpleDetailed" title="${msg("button.view.simple")}" value="" /><input id="${args.htmlid}-detailedView" type="radio" name="simpleDetailed" title="${msg("button.view.detailed")}" value="" />
         </div>
      </div>
   </div>

   <div id="${args.htmlid}-documents" class="documents"></div>

   <div id="${args.htmlid}-doclistBarBottom" class="yui-gc doclist-bar doclist-bar-bottom flat-button">
      <div class="yui-u first">
         <div class="file-select">&nbsp;</div>
         <div id="${args.htmlid}-paginatorBottom" class="paginator"></div>
      </div>
   </div>

   <!-- Action Sets -->
   <div style="display:none">
      <!-- Action Set "More..." container -->
      <div id="${args.htmlid}-moreActions">
         <div class="onActionShowMore"><a href="#" class="show-more" title="${msg("actions.more")}"><span>${msg("actions.more")}</span></a></div>
         <div class="more-actions hidden"></div>
      </div>

      <!-- Action Set Templates -->
<#list actionSets?keys as key>
   <#assign actionSet = actionSets[key]>
      <div id="${args.htmlid}-actionSet-${key}" class="action-set">
   <#list actionSet as action>
         <div class="${action.id}"><a rel="${action.permission!""}" href="${action.href}" class="${action.type}" title="${msg(action.label)}"><span>${msg(action.label)}</span></a></div>
   </#list>
      </div>
</#list>
   </div>

</div>