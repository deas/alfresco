<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/console/application.css" group="console"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/console/consoletool.js" group="console"/>
   <@script src="${url.context}/res/components/console/application.js" group="console"/>
</@>

<@markup id="widgets">
   <@createWidgets group="console"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign el=args.htmlid?html>
      <#assign defaultlogo=msg("header.logo")><#if defaultlogo="header.logo"><#assign defaultlogo="app-logo.png"></#if>
      <!--[if IE]>
      <iframe id="yui-history-iframe" src="${url.context}/res/yui/history/assets/blank.html"></iframe> 
      <![endif]-->
      <input id="yui-history-field" type="hidden" />
      <div id="${el}-body" class="application">
         <!-- Options panel -->
         <div id="${el}-options" class="hidden">
            <div class="title">${msg("page.adminConsole.description")}</div>
            <div class="row info">${msg("message.new-admin-console")}</div>
            <form id="${el}-options-form" action="${url.context}/service/components/console/application" method="post">
               <div class="title">${msg("label.options")}</div>
               <!-- Theme -->
               <div class="row">
                  <div class="label">${msg("label.theme")}:</div>
                  <div class="flat-button">
                     <select id="console-options-theme-menu">
                        <#list themes as t>
                        <option value="${t.id}"<#if t.selected> selected="selected"</#if>>${t.title?html}</option>
                        </#list>
                     </select>
                  </div>
               </div>
               <!-- Logo -->
               <div class="row">
                  <div class="label">${msg("label.logo")}:</div>
                  <div class="logo"><img id="${el}-logoimg" src="${url.context}<#if logo?? && logo?length!=0>/proxy/alfresco/api/node/${logo?replace('://','/')}/content<#else>/res/themes/${theme}/images/${defaultlogo}</#if>" /></div>
                  <div>
                     <button id="${el}-upload-button" name="upload">${msg("button.upload")}</button>&nbsp;
                     <button id="${el}-reset-button" name="reset">${msg("button.reset")}</button>
                     <div class="logonote">${msg("label.logonote")}</div>
                     <input type="hidden" id="console-options-logo" value="" />
                  </div>
               </div>
               <!-- Apply changes -->
               <div class="apply">
                  <button id="${el}-apply-button" name="apply">${msg("button.apply")}</button>
               </div>
            </form>
         </div>
      </div>
   </@>
</@>
