<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/links/linksview.css" group="links"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/links/linksview.js" group="links"/>
</@>

<@markup id="pre">
   <#-- No pre-instantiation JavaScript required -->
</@>

<@markup id="widgets">
   <@createWidgets group="links"/>
</@>

<@markup id="post">
   <#-- No post-instantiation JavaScript required -->
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <div class="linksview-header">
         <div class="navigation-bar">
            <div>
               <span class="<#if (page.url.args.listViewLinkBack! == "true")>back-link<#else>forward-link</#if>">
                  <a href="${url.context}/page/site/${page.url.templateArgs.site}/links">${msg("header.back")}</a>
               </span>
            </div>
         </div>
         <div class="action-bar">
         </div>
      </div>
      <div id="${args.htmlid}-link">
         <div id="${args.htmlid}-link-view-div">
         </div>
      </div>
   </@>
</@>