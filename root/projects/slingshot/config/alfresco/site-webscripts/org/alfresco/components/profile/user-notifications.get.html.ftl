<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
   new Alfresco.UserNotifications("${el}").setMessages(${messages});
//]]></script>

<div id="${el}-body" class="notifications">
   <form id="${el}-form" action="${url.context}/service/components/profile/user-notifications" method="post">
   
      <div class="header-bar">${msg("label.notifications")}</div>
      <div class="row">
         <span class="label"><label for="user-notifications-email">${msg("label.emailnotification")}:</label></span>
         <span class="input"><input type="checkbox" id="user-notifications-email" <#if !emailFeedDisabled>checked</#if>/></span>
      </div>
      
      <hr/>
      
      <div class="buttons">
         <button id="${el}-button-ok" name="save">${msg("button.ok")}</button>
         <button id="${el}-button-cancel" name="cancel">${msg("button.cancel")}</button>
      </div>
   
   </form>

</div>