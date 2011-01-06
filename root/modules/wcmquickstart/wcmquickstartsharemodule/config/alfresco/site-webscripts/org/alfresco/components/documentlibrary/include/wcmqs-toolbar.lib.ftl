<#macro toolbarTemplate>
<#nested>
<div id="${args.htmlid}-body" class="toolbar">

   <div id="${args.htmlid}-headerBar" class="header-bar flat-button theme-bg-2">
      <div class="left">
         <div class="hideable toolbar-hidden DocListTree">
            <div class="create-content">
               <button id="${args.htmlid}-createContent-button" name="createContent">${msg("button.create-content")}</button>
               <div id="${args.htmlid}-createContent-menu" class="yuimenu">
                  <div class="bd">
                     <ul>
                     <#list createContent as content>
                        <li><a href="create-content?mimeType=${content.mimetype?html}&amp;destination={nodeRef}&amp;itemId=${content.itemid}<#if (content.formid!"") != "">&amp;formId=${content.formid?html}</#if>" rel="${content.permission!""}"><span class="${content.icon}-file">${msg(content.label)}</span></a></li>
                     </#list>
                     </ul>
                  </div>
               </div>
            </div>
            <div class="separator">&nbsp;</div>
         </div>         
         <div class="hideable toolbar-hidden DocListTree hidden">
            <div class="new-article"><button id="${args.htmlid}-newArticle-button" name="newArticle">${msg("button.new-article")}</button></div>
            <div class="separator">&nbsp;</div>
         </div>         
         <div class="hideable toolbar-hidden DocListTree">
            <div class="new-folder"><button id="${args.htmlid}-newFolder-button" name="newFolder">${msg("button.new-folder")}</button></div>
            <div class="separator">&nbsp;</div>
         </div>
         <div class="hideable toolbar-hidden DocListTree">
            <div class="file-upload"><button id="${args.htmlid}-fileUpload-button" name="fileUpload">${msg("button.upload")}</button></div>
            <div class="separator">&nbsp;</div>
         </div>
         <div class="selected-items hideable toolbar-hidden DocListTree DocListFilter TagFilter DocListCategories">
            <button class="no-access-check" id="${args.htmlid}-selectedItems-button" name="doclist-selectedItems-button">${msg("menu.selected-items")}</button>
            <div id="${args.htmlid}-selectedItems-menu" class="yuimenu">
               <div class="bd">
                  <ul>
                  <#list actionSet as action>
                     <li><a type="${action.asset!""}" rel="${action.permission!""}" href="${action.href}"><span class="${action.id}">${msg(action.label)}</span></a></li>
                  </#list>
                     <li><a href="#"><hr /></a></li>
                     <li><a href="#"><span class="onActionDeselectAll">${msg("menu.selected-items.deselect-all")}</span></a></li>
                  </ul>
               </div>
            </div>
         </div>
      </div>
      <div class="right">
         <div class="customize" style="display: none;"><button id="${args.htmlid}-customize-button" name="customize">${msg("button.customize")}</button></div>
         <div class="hide-navbar"><button id="${args.htmlid}-hideNavBar-button" name="hideNavBar">${msg("button.navbar.hide")}</button></div>
         <div class="rss-feed"><button id="${args.htmlid}-rssFeed-button" name="rssFeed">${msg("link.rss-feed")}</button></div>
      </div>
   </div>

   <div id="${args.htmlid}-navBar" class="nav-bar flat-button theme-bg-4">
      <div class="hideable toolbar-hidden DocListTree DocListCategories">
         <div class="folder-up"><button class="no-access-check" id="${args.htmlid}-folderUp-button" name="folderUp">${msg("button.up")}</button></div>
         <div class="separator">&nbsp;</div>
      </div>
      <div id="${args.htmlid}-breadcrumb" class="breadcrumb hideable toolbar-hidden DocListTree DocListCategories"></div>
      <div id="${args.htmlid}-description" class="description hideable toolbar-hidden DocListFilter TagFilter"></div>
   </div>

</div>
</#macro>