<#import "/org/alfresco/utils/feed.utils.ftl" as feedLib/>

<@markup id="css" >
   <#-- No CSS Dependencies -->
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
  <@script type="text/javascript" src="${url.context}/res/components/dashlets/rssfeed.js" group="dashlets"/>
  <@script type="text/javascript" src="${url.context}/res/modules/simple-dialog.js" group="dashlets"/>
</@>

<@markup id="pre">
   <@inlineScript group="dashlets">
      var addOnsRssFeedDashletEvent = new YAHOO.util.CustomEvent("openFeedClick");
   </@>
</@>

<@markup id="widgets">
   <@createWidgets group="dashlets"/>
</@>

<@markup id="post">
   <@inlineScript group="dashlets">
      addOnsRssFeedDashletEvent.subscribe(addOnsRssFeed.onConfigFeedClick, addOnsRssFeed, true);
   </@>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign el=args.htmlid?js_string>
      <div class="dashlet rssfeed">
         <div class="title" id="${el}-title">${title!msg("label.header")}</div>
         <div class="toolbar">
            <div>${msg("label.body")} <a href="${msg("label.addonsLink")}" target="${target}">${msg("label.addonsText")}</a></div>
         </div>
         <div class="body scrollableList" <#if args.height??>style="height: ${args.height}px;"</#if> id="${el}-scrollableList">
            <h3>${msg("label.loading")}</h3>
         </div>
      </div>
   </@>
</@>