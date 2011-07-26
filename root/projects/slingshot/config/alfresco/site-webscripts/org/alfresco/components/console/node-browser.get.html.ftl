<!--[if IE]>
<iframe id="yui-history-iframe" src="${url.context}/res/yui/history/assets/blank.html"></iframe> 
<![endif]-->
<input id="yui-history-field" type="hidden" />

<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
   new Alfresco.ConsoleNodeBrowser("${el}").setMessages(${messages});
//]]></script>

<div id="${el}-body" class="node-browser">

   <!-- Search panel -->
   <div id="${el}-search" class="hidden">
      <div class="yui-g">
         <div class="yui-u first">
            <div class="title"><label for="${el}-search-text">${msg("label.title-search")}</label></div>
         </div>
         <div class="yui-u align-right">
            <!-- Store select list -->
            <div class="node-store-button">
               <label for="${el}-store-menu-button">${msg("label.select-store")}</label>
               <input type="button" id="${el}-store-menu-button" name="store-button" value="workspace://SpacesStore" />
               <select id="${el}-store-menu-select" name="store-select">
                   <option value="archive://SpacesStore">archive://SpacesStore</option>
                   <option value="system://system">system://system</option>
                   <option value="user://alfrescoUserStore">user://alfrescoUserStore</option>
                   <option value="workspace://lightWeightVersionStore">workspace://lightWeightVersionStore</option>
                   <option value="workspace://SpacesStore">workspace://SpacesStore</option>
                   <option value="workspace://version2Store">workspace://version2Store</option>
                   <option value="avm://sitestore">avm://sitestore</option>
               </select>
            </div>
         </div>
      </div>
      <div class="yui-g separator">
         <div class="yui-u first">
            <div class="search-text"><input type="text" id="${el}-search-text" name="-" value="" />
               <!-- TODO add a query language drop-down -->
               <!-- Search button -->
               <div class="search-button">
                  <span class="yui-button yui-push-button" id="${el}-search-button">
                     <span class="first-child"><button>${msg("button.search")}</button></span>
                  </span>
               </div>
            </div>
         </div>
         <div class="yui-u align-right">
         </div>
      </div>
      <div class="search-main">
         <div id="${el}-search-bar" class="search-bar theme-bg-color-3">${msg("message.noresults")}</div>
         <div class="results" id="${el}-datatable"></div>
      </div>
   </div>

   <!-- View Node panel -->
   <div id="${el}-view" class="hidden">
   
      <div class="yui-g separator">
         <div class="yui-u first">
            <div class="title">${msg("label.title-view")}: <span id="${el}-view-title"></span></div>
         </div>
         <div class="yui-u">
            <!-- Edit/Delete buttons go here -->
         </div>
      </div>
      
      <div id="${el}-view-main" class="view-main separator">
      
         <!-- Each info section separated by a header-bar div -->
         <div class="header-bar">${msg("label.about")}</div>
         <div class="field-row">
            <span class="field-label-right">${msg("label.node-ref")}:</span>
            <span id="${el}-view-node-ref" class="field-value"></span>
         </div>
         <div class="field-row">
            <span class="field-label-right">${msg("label.node-path")}:</span>
            <span id="${el}-view-node-path" class="field-value"></span>
         </div>
         <div class="field-row">
            <span class="field-label-right">${msg("label.node-type")}:</span>
            <span id="${el}-view-node-type" class="field-value"></span>
         </div>
         <div class="field-row">
            <span class="field-label-right">${msg("label.parent")}:</span>
            <span id="${el}-view-node-parent" class="field-value"></span>
         </div>
         
         <div class="header-bar">${msg("label.properties")}</div>
         <div class="node-properties list" id="${el}-view-node-properties"></div>
         
         <div class="header-bar">${msg("label.aspects")}</div>
         <div class="node-aspects list" id="${el}-view-node-aspects"></div>
         
         <div class="header-bar">${msg("label.children")}</div>
         <div class="node-children list" id="${el}-view-node-children"></div>
         
         <div class="header-bar">${msg("label.parents")}</div>
         <div class="node-parents list" id="${el}-view-node-parents"></div>
         
         <div class="header-bar">${msg("label.assocs")}</div>
         <div class="node-assocs list" id="${el}-view-node-assocs"></div>
         
         <div class="header-bar">${msg("label.source-assocs")}</div>
         <div class="node-source-assocs list" id="${el}-view-node-source-assocs"></div>
         
         <div class="header-bar">${msg("label.permissions")}</div>
         <div class="node-permissions-info">
            <div class="field-row">
               <span class="field-label-right">${msg("label.node-inherits-permissions")}:</span>
               <span id="${el}-view-node-inherits-permissions" class="field-value"></span>
            </div>
            <div class="field-row">
               <span class="field-label-right">${msg("label.node-owner")}:</span>
               <span id="${el}-view-node-owner" class="field-value"></span>
            </div>
         </div>
         <div class="node-permissions list" id="${el}-view-node-permissions"></div>
      </div>

      <div class="yui-g">
         <!-- Cancel view node button -->
         <div class="goback-button">
            <span class="yui-button yui-push-button" id="${el}-goback-button">
               <span class="first-child"><button>${msg("button.goback")}</button></span>
            </span>
         </div>
      </div>
   </div>

</div>