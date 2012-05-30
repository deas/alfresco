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
      var rssFeedDashletEvent = new YAHOO.util.CustomEvent("onConfigFeedClick");
   </@>
</@>

<@markup id="widgets">
   <@createWidgets group="dashlets"/>
</@>

<@markup id="post">
   <@inlineScript group="dashlets">
      rssFeedDashletEvent.subscribe(rssFeed.onConfigFeedClick, rssFeed, true);
   </@>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign el=args.htmlid?js_string>
      <div class="dashlet rssfeed">
         <div class="title" id="${el}-title">${title!msg("label.header")}</div>
         <div class="body scrollableList" <#if args.height??>style="height: ${args.height}px;"</#if>>
            <div class="dashlet-padding" id="${el}-scrollableList">
               <h3>${msg("label.loading")}</h3>
            </div>
         </div>
      </div>
   </@>
</@>