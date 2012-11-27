<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/links/linksview.css" group="links"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/links/linksview.js" group="links"/>
</@>

<@markup id="widgets">
   <@createWidgets group="links"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign el=args.htmlid?html>
      <div class="linksview-header theme-bg-2">
         <div class="navigation-bar theme-bg-3">
            <div>
               <span class="<#if (page.url.args.listViewLinkBack! == "true")>back-link<#else>forward-link</#if>">
                  <a href="${url.context}/page/site/${page.url.templateArgs.site}/links">${msg("header.back")}</a>
               </span>
            </div>
         </div>
         <div class="action-bar"></div>
      </div>
      <div id="${el}-link">
         <div id="${el}-link-view-div"></div>
      </div>
   </@>
</@>