<script type="text/javascript">//<![CDATA[
   new Alfresco.RecordsPermissions("${args.htmlid}").setOptions({
    siteId: "${page.url.templateArgs.site!""}",
    nodeRef: "${(page.url.args.nodeRef!"")?js_string}",
    nodeType: "${(page.url.args.nodeType!"")?js_string}",
    itemName: "${(page.url.args.itemName!"")?js_string}"
 }).setMessages(${messages});
//]]></script>

<#assign el=args.htmlid>
<div id="${el}-body" class="permissions">
   <!-- Authority Picker -->
   <div class="authority-picker" id="${el}-authoritypicker"></div>
   <!-- Add User/Group button -->
   <div class="floatright">
      <div class="addusergroup-button">
         <span class="yui-button yui-push-button" id="${el}-addusergroup-button">
            <span class="first-child"><button>${msg("button.addUserGroup")}</button></span>
         </span>
      </div>
   </div>
   <div class="title">${msg("label.title", '${page.url.args.itemName!""}')?html}</div>
   
   <!-- Permissions List -->
   <div class="permlist">
      <div class="permlist-border">
         <div class="list-item-header theme-bg-color-4">
            <div class="actions-header"><span class="header">${msg("label.actions")}</span></div>
            <div class="controls-header"><span class="header">${msg("label.permissions")}</span></div>
            <div class="header">${msg("label.usersgroups")}</div>
         </div>
         <div id="${el}-list" class="theme-bg-color-3"></div>
      </div>
   </div>
   
   <!-- Finish button -->
   <div class="center">
      <div class="finish-button">
         <span class="yui-button yui-push-button" id="${el}-finish-button">
            <span class="first-child"><button>${msg("button.done")}</button></span>
         </span>
      </div>
   </div>
</div>