<script type="text/javascript">//<![CDATA[
   new Alfresco.dashlet.MyDocuments("${args.htmlid}").setMessages(
      ${messages}
   );
   new Alfresco.widget.DashletResizer("${args.htmlid}", "${instance.object.id}");
//]]></script>

<div class="dashlet my-documents">
   <div class="title">${msg("header")}</div>
   <div class="toolbar flat-button">
      <div id="${args.htmlid}-filters" class="yui-buttongroup">
         <span id="${args.htmlid}-favourites" class="yui-button yui-radio-button yui-button-checked">
            <span class="first-child">
               <button type="button" name="${args.htmlid}" value="favourites">${msg("filter.favourites")}</button>
            </span>
         </span>
         <span id="${args.htmlid}-editing" class="yui-button yui-radio-button">
            <span class="first-child">
               <button type="button" name="${args.htmlid}" value="editingMe">${msg("filter.editing")}</button>
            </span>
         </span>
         <span id="${args.htmlid}-modified" class="yui-button yui-radio-button">
            <span class="first-child">
               <button type="button" name="${args.htmlid}" value="recentlyModifiedByMe">${msg("filter.modified")}</button>
            </span>
         </span>
      </div>
   </div>
   <div id="${args.htmlid}-documents" class="body scrollableList" <#if args.height??>style="height: ${args.height}px;"</#if>>
   </div>
</div>