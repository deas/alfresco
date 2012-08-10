<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
   new Alfresco.UserCloudAuth("${el}").setMessages(${messages});
//]]></script>

<div id="${el}-body" class="cloudAuth notifications <#if cloudConnected>connected</#if>">

   <div class="header-bar">${msg("label.cloud-auth")}</div>
   <div class="not-connected">
      <p>${msg("message.not-connected")}</p>
      <div class="signin">
         <button id="${el}-button-signIn" name="signIn">${msg("label.signIn")}</button>
      </div>
      <p>
         ${msg("message.not-connected-meta")}
      </p>
   </div>
   <div class="existing-connection">
      <p><strong>${msg("label.connected-heading")}</strong></p>
      <p>
         ${msg("label.connected", email)}

<#if !lastLoginSuccessful>

      </p>
      <p>
         ${msg("label.lastLoginFailed")}

</#if>

      <div class="buttons">
         <button id="${el}-button-edit" name="edit">${msg("label.edit")}</button>
         <button id="${el}-button-delete" name="delete">${msg("label.delete")}</button>
      </div>

      </p>
   </div>
</div>