<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
   <head> 
      <title>${page.title}</title> 
      <link rel="stylesheet" href="${url.context}/css/login.css" type="text/css"/>
      ${head}
   </head>
   <body>
   
      <div class="login-panel">
         <div class="login-logo"></div>
         <form accept-charset="UTF-8" method="post" action="${url.context}/login">
            <fieldset>
               <div style="padding-top:16px">
                  <span id="txt-username">Username:</span>
               </div>
               <div style="padding-top:4px">
                  <input type="text" id="username" name="username" maxlength="256" style="width:200px"/>
               </div>
               <div style="padding-top:12px">
                  <span id="txt-password">Password:</span>
               </div>
               <div style="padding-top:4px">
                  <input type="password" id="password" name="password" maxlength="256" style="width:200px"/>
               </div>
               <div style="padding-top:16px">
                  <input type="submit" value="Login" id="btn-login" class="login-button" />
               </div>
               <div style="padding-top:32px">
                  <span class="login-copyright">
                     &copy; 2005-2014 Alfresco Software Inc. All rights reserved.
                  </span>
               </div>
               <input type="hidden" id="success" name="success" value="${successUrl}"/>
               <input type="hidden" name="failure" value="<#assign link><@pagelink pageType='login'/></#assign>${url.servletContext}${link?html}&amp;error=true"/>
            </fieldset>
         </form>
      </div>
      
   </body>
</html>