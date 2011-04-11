<#include "../include/alfresco-template.ftl" />
<@templateHeader>
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/themes/${theme}/login.css" />
</@>
<@templateBody>
   <div id="alflogin" class="login-panel">
      <div class="login-logo"></div>
<#if PORTLET>
      <div class="login-portlet">${msg("message.login-portal")}</div>
<#else>
      <form id="loginform" accept-charset="UTF-8" method="post" action="${url.context}/page/dologin" onsubmit="return alfLogin();">
         <fieldset>
            <div style="padding-top:96px">
               <label id="txt-username" for="username"></label>
            </div>
            <div style="padding-top:4px">
               <input type="text" id="username" name="username" maxlength="255" style="width:200px" value="<#if lastUsername??>${lastUsername?html}</#if>" />
            </div>
            <div style="padding-top:12px">
               <label id="txt-password" for="password"></label>
            </div>
            <div style="padding-top:4px">
               <input type="password" id="password" name="password" maxlength="255" style="width:200px"/>
            </div>
            <div style="padding-top:16px">
               <input type="submit" id="btn-login" class="login-button" />
            </div>
            <input type="hidden" id="success" name="success" value="${successUrl?html}"/>
            <input type="hidden" name="failure" value="<#assign link>${url.context}/page/type/login</#assign>${link?html}?error=true"/>
         </fieldset>
      </form>
</#if>
      <div style="padding-top:32px">
         <span class="login-copyright">
            &copy; 2005-2011 Alfresco Software Inc. All rights reserved.
         </span>
      </div>
   </div>
   
   <script type="text/javascript">//<![CDATA[
   function alfLogin()
   {
      YAHOO.util.Dom.get("btn-login").setAttribute("disabled", true);
      return true;
   }
   
   YAHOO.util.Event.onContentReady("alflogin", function()
   {
      var Dom = YAHOO.util.Dom;
      
      // Prevent the Enter key from causing a double form submission
      var form = Dom.get("loginform");
      if (form)
      {
         // add the event to the form and make the scope of the handler this form.
         YAHOO.util.Event.addListener(form, "submit", this._submitInvoked, this, true);
         var fnStopEvent = function(id, keyEvent)
         {
            if (form.getAttribute("alflogin") == null)
            {
               form.setAttribute("alflogin", true);
            }
         }

         var enterListener = new YAHOO.util.KeyListener(form,
         {
            keys: YAHOO.util.KeyListener.KEY.ENTER
         }, fnStopEvent, "keydown");
         enterListener.enable();

         // set I18N labels
         Dom.get("txt-username").innerHTML = Alfresco.util.message("label.username") + ":";
         Dom.get("txt-password").innerHTML = Alfresco.util.message("label.password") + ":";
         Dom.get("btn-login").value = Alfresco.util.message("button.login");
      }
      
      // generate and display main login panel
      var panel = new YAHOO.widget.Overlay(YAHOO.util.Dom.get("alflogin"), 
      {
         modal: false,
         draggable: false, // NOTE: Don't change to "true"
         fixedcenter: YAHOO.env.ua.mobile === null,
         close: false,
         visible: true,
         iframe: false
      });
      panel.render(document.body);
      
      Dom.get("success").value += window.location.hash;
      Dom.get(<#if lastUsername??>"password"<#else>"username"</#if>).focus();
   });
   
<#if url.args["error"]??>
   Alfresco.util.PopupManager.displayPrompt(
   {
      title: Alfresco.util.message("message.loginfailure"),
      text: Alfresco.util.message("message.loginautherror"),
      buttons: [
      {
         text: Alfresco.util.message("button.ok"),
         handler: function error_onOk()
         {
            this.destroy();
            YAHOO.util.Dom.get("username").focus();
            YAHOO.util.Dom.get("username").select();
         },
         isDefault: true
      }]
   });

</#if>   
   //]]></script>
</@>
</body>
</html>