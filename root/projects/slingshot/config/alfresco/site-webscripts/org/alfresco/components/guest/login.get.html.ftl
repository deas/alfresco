<#assign el=args.htmlid?js_string/>
<#assign error=((args.error!"false") == "true")/>
<#assign errorDisplay=args.errorDisplay!"container"/>

<div id="${el}-body" class="theme-overlay login hidden">

   <@markup id="header">
      <div class="theme-company-logo"></div>
   </@markup>

   <#if errorDisplay == "container">
   <@markup id="error">
      <#if error>
      <div class="error">${msg("message.loginautherror")}</div>
      <#else>
      <script type="text/javascript">//<![CDATA[
         document.cookie = "_alfTest=_alfTest";
         var cookieEnabled = (document.cookie.indexOf("_alfTest") != -1);
         if (cookieEnabled == false)
         {
            document.write('<div class="error">${msg("message.cookieserror")}</div>');
         }
      //]]></script>
      </#if>
   </@markup>
   </#if>

   <@markup id="form">
   <form id="${el}-form" accept-charset="UTF-8" method="post" action="${url.context}/page/dologin" class="form-fields">
      <@markup id="fields">
      <input type="hidden" id="${el}-success" name="success" value="${successUrl?html}"/>
      <input type="hidden" name="failure" value="${url.context}/page/type/login?error=true"/>
      <div class="form-field">
         <label for="${el}-username">${msg("label.username")}</label><br/>
         <input type="text" id="${el}-username" name="username" maxlength="255" value="<#if lastUsername??>${lastUsername?html}</#if>" />
      </div>
      <div class="form-field">
         <label for="${el}-password">${msg("label.password")}</label><br/>
         <input type="password" id="${el}-password" name="password" maxlength="255" />
      </div>
      </@markup>
      <@markup id="buttons">
      <div class="form-field">
         <input type="submit" id="${el}-submit" class="login-button" value="${msg("button.login")}"/>
      </div>
      </@markup>
   </form>
   </@markup>

   <@markup id="footer">
      <span class="faded tiny">${msg("label.copyright")}</span>
   </@markup>

</div>
<script type="text/javascript">//<![CDATA[
new Alfresco.component.Login("${el}").setOptions({
   error: ${error?string},
   errorDisplay: "${errorDisplay?js_string}",
   lastUsername: <#if lastUsername??>"${lastUsername?js_string}"<#else>null</#if>
}).setMessages(${messages});
//]]></script>