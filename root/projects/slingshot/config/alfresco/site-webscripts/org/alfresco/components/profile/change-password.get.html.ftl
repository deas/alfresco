<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
   var userProfile = new Alfresco.ChangePassword("${el}").setOptions(
   {
      minPasswordLength: "${config.scoped['Users']['users'].getChildValue('password-min-length')}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${el}-body" class="password">
   <form id="${el}-form" action="${url.context}/service/components/profile/change-password" method="post">
   
      <div class="header-bar">${msg("label.changepassword")}</div>
      <div class="row">
         <span class="label"><label for="${el}-oldpassword">${msg("label.oldpassword")}:</label></span>
         <span class="input"><input type="password" maxlength="255" size="30" id="${el}-oldpassword" /></span>
      </div>
      <div class="row">
         <span class="label"><label for="${el}-newpassword1">${msg("label.newpassword")}:</label></span>
         <span class="input"><input type="password" maxlength="255" size="30" id="${el}-newpassword1" /></span>
      </div>
      <div class="row">
         <span class="label"><label for="${el}-newpassword2">${msg("label.confirmpassword")}:</label></span>
         <span class="input"><input type="password" maxlength="255" size="30" id="${el}-newpassword2" /></span>
      </div>
      
      <hr/>
      
      <div class="buttons">
         <button id="${el}-button-ok" name="save">${msg("button.ok")}</button>
         <button id="${el}-button-cancel" name="cancel">${msg("button.cancel")}</button>
      </div>
   
   </form>

</div>