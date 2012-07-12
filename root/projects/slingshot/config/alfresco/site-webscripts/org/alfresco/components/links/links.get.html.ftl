<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/links/links.css" group="links"/>
   <@link href="${url.context}/res/modules/taglibrary/taglibrary.css" group="links"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/links/links.js" group="links"/>
   <@script src="${url.context}/res/modules/taglibrary/taglibrary.js" group="links"/>
</@>

<@markup id="widgets">
   <@createWidgets group="links"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <div id="${args.htmlid}-links-header" class="links-header" style="visibility:hidden">
         
         <div id="${args.htmlid}-linksBar" class="toolbar links-toolbar flat-button theme-bg-2">
            <div>
               <div id="${args.htmlid}-create-link-container" class="createLink">
                  <div style="float:left"><button id="${args.htmlid}-create-link-button" name="linklist-create-link-button">${msg("header.createLink")}</button></div>
                  <div class="separator hideable"> </div>
               </div>
               <div style="float:left" class="btn-selected-items">
               <button id="${args.htmlid}-selected-i-dd" name="linklist-create-link-button">${msg("header.selectedItems")}</button>
               <div id="${args.htmlid}-selectedItems-menu" class="yuimenu">
                  <div class="bd">
                     <ul>
                        <li><a class="delete-item" rel="delete" href="#"><span class="links-action-delete">${msg("links.delete")}</span></a></li>
                        <li><a class="deselect-item" rel="" href="#"><span class="links-action-deselect-all">${msg("links.deselectAll")}</span></a></li>
                     </ul>
                  </div>
                 </div>
             </div>
            </div>
            <div class="rss-feed"><button id="${args.htmlid}-rss-feed" name="rss-feed">${msg("header.rssFeed")}</button></div>
         </div>
      
         <div id="${args.htmlid}-links-titleBar" class="links-titlebar" >
            <div id="${args.htmlid}-listTitle" class="list-title">
                  ${msg("title.generic")}
              </div>
         </div>
      
         <div id="${args.htmlid}-links-infoBar" class="links-infobar flat-button" >
            <div class="vm-button-container">
               <button id="${args.htmlid}-viewMode-button"
                       name="topiclist-simpleView-button">${msg("header.simpleList")}</button>
            </div>
            <div class="separator hideable"> </div>
             <div id="${args.htmlid}-paginator" class="paginator"></div>
            <div  class="select-button-container">
               <button id="${args.htmlid}-select-button">${msg("header.select")}</button>
             <div id="${args.htmlid}-selecItems-menu" class="yuimenu">
                  <div class="bd">
                     <ul>
                        <li><a rel="" href="#"><span class="links-action-select-all">${msg("links.selectAll")}</span></a></li>
                        <li><a rel="" href="#"><span class="links-action-invert-selection">${msg("links.invertSelection")}</span></a></li>
                        <li><a rel="" href="#"><span class="links-action-deselect-all">${msg("links.none")}</span></a></li>
                     </ul>
                  </div>
               </div>
            </div>
         </div>
      </div>
      
      <div id="${args.htmlid}-body" class="links-body" style="visibility:hidden">
         <div  id="${args.htmlid}-links"> </div>
      </div>
      <p/>
      <p/>
      <p/>
   </@>
</@>