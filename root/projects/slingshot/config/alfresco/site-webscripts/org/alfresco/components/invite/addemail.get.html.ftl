<#if allowEmailInvite>
<script type="text/javascript">//<![CDATA[
   new Alfresco.AddEmailInvite("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${args.htmlid}-body" class="inviteusersbyemail">
   <div class="title">${msg("addemail.title")}</div>
   <div class="byemailbody">  
      <table class="byemailuser">
         <tr>
            <td class="elabel"><label for="${args.htmlid}-firstname">${msg("addemail.firstname")}:</label></td>
            <td class="einput"><input type="text" id="${args.htmlid}-firstname" tabindex="0" /></td>
            <td class="byemailadd" colspan="3">
               <span id="${args.htmlid}-add-email-button" class="yui-button yui-push-button"><span class="first-child"><button tabindex="0">${msg("addemail.add")} &gt;&gt;</button></span></span>
            </td>
         </tr>
         <tr>
            <td class="elabel"><label for="${args.htmlid}-lastname">${msg("addemail.lastname")}:</label></td>
            <td class="einput"><input type="text" id="${args.htmlid}-lastname" tabindex="0" /></td>
         </tr>
         <tr>
            <td class="elabel"><label for="${args.htmlid}-email">${msg("addemail.email")}:</label></td>
            <td class="einput"><input type="text" id="${args.htmlid}-email" tabindex="0" /></td>
         </tr>
      </table>
   </div>
</div>
</#if>