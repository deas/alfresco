<#assign id=args.htmlid>
<script type="text/javascript">//<![CDATA[
   new Alfresco.component.ManagePermissions("${id}").setOptions(
   {
      nodeRef: new Alfresco.util.NodeRef("${args.nodeRef?js_string}")
   }).setMessages(
      ${messages}
   );
//]]></script>
<div id="${id}-body" class="permissions">

   <div id="${id}-authorityFinder" class="authority-finder-container"></div>
   <div id="${id}-headerBar" class="header-bar flat-button">
      <div class="left">
         <span id="${id}-title"></span>
      </div>
      <div class="right">
         <div id="${id}-inheritedButtonContainer" class="inherited">
            <span id="${id}-inheritedButton" class="yui-button yui-push-button">
               <span class="first-child">
                  <button>${msg("button.inherited")}</button>
               </span>
            </span>
         </div>
         <div class="add-user-group">
            <span id="${id}-addUserGroupButton" class="yui-button yui-push-button">
               <span class="first-child">
                  <button>${msg("button.addUserGroup")}</button>
               </span>
            </span>
         </div>
      </div>
   </div>

   <div id="${id}-inheritedContainer" class="container hidden">
      <div class="title">${msg("title.inherited")}</div>
      <div id="${id}-inheritedPermissions" class="permissions-list"></div>
   </div>

   <div id="${id}-directContainer" class="container">
      <div class="title">${msg("title.direct")}</div>
      <div id="${id}-directPermissions" class="permissions-list"></div>
   </div>

   <div class="center">
      <span id="${id}-okButton" class="yui-button yui-push-button">
         <span class="first-child">
            <button>${msg("button.save")}</button>
         </span>
      </span>
      <span id="${id}-cancelButton" class="yui-button yui-push-button">
         <span class="first-child">
            <button>${msg("button.cancel")}</button>
         </span>
      </span>
   </div>

</div>
