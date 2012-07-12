<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/invite/sent-invites.css" group="invite"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/invite/sent-invites.js" group="invite"/>
</@>

<@markup id="widgets">
   <@createWidgets group="invite"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <div id="${args.htmlid}-sentinvites" class="sent-invites">
         <div class="title">
            <label for="${args.htmlid}-search-text">${msg("sentinvites.title")}</label>
         </div>
         <div id="${args.htmlid}-wrapper" class="sent-invites-wrapper">
            <div class="search-bar theme-bg-color-3">
               <div class="search-label"><label for="${args.htmlid}-search-text">${msg("label.search")}</label></div>
               <div class="search-text"><input type="text" id="${args.htmlid}-search-text" name="-" value="" /></div>
               <div class="search-button"><button id="${args.htmlid}-search-button">${msg("button.search")}</button></div>
            </div>
            <#--
            <div class="tool-bar yui-gc">
               <div id="${args.htmlid}-paginator" class="paginator yui-b first">
               </div>
               <div class="tools yui-b">
                  <button name="select-clear-button">Clear All...</button>
                  <button name="clear-pending-button">Clear All Pending</button>
               </div>
            </div>
            -->
            <div id="${args.htmlid}-results" class="results"></div>
         </div>
      </div>
   </@>
</@>