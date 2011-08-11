<#--
   Configured dependencies.
   TODO: Temporary code to be removed when config reader implemented.
-->
<#if dependencies??>
   <#if dependencies.css??>
      <#list dependencies.css as cssFile>
<link rel="stylesheet" type="text/css" href="${page.url.context}/res${cssFile}" />
      </#list>
   </#if>
   <#if dependencies.js??>
      <#list dependencies.js as jsFile>
<script type="text/javascript" src="${page.url.context}/res${jsFile}"></script>
      </#list>
   </#if>
</#if>

<#macro documentlistTemplate>
<!--[if IE]>
   <iframe id="yui-history-iframe" src="${url.context}/res/yui/history/assets/blank.html"></iframe>
<![endif]-->
<input id="yui-history-field" type="hidden" />
<#nested>
<#assign id = args.htmlid?html>
<div id="${id}-body" class="doclist no-check-bg">

   <#--
      INFORMATION TEMPLATES
   -->
   <div id="${id}-main-template" class="hidden">
      <div>
      </div>
   </div>

   <#-- No items message -->
   <div id="${id}-no-items-template" class="hidden">
      <div class="docListInstructionTitle">${msg("no.items.title")}</div>
   </div>

   <#-- Hidden sub-folders message -->
   <div id="${id}-hidden-subfolders-template" class="hidden">
      <div class="docListInstructionTitle">${msg("no.items.title")}</div>
      <div id="${id}-show-folders-template" class="docListInstructionColumn">
         <img class="docListInstructionImage docListLinkedInstruction" src="${url.context}/res/components/documentlibrary/images/help-folder-48.png">
         <a class="docListInstructionTextSmall docListLinkedInstruction"><#-- We don't know the number of hidden subfolders at this point so this needs to be inserted --></a>
      </div>
   </div>

   <#-- HTML 5 drag and drop instructions -->
   <div id="${id}-dnd-instructions-template" class="hidden">
      <div id="${id}-dnd-instructions">
         <span class="docListInstructionTitle">${msg("dnd.drop.title")}</span>
         <div>
            <div class="docListInstructionColumn docListInstructionColumnRightBorder">
               <img class="docListInstructionImage" src="${url.context}/res/components/documentlibrary/images/help-drop-list-target-96.png">
               <span class="docListInstructionText">${msg("dnd.drop.doclist.description")}</span>
            </div>
            <div class="docListInstructionColumn">
               <img class="docListInstructionImage" src="${url.context}/res/components/documentlibrary/images/help-drop-folder-target-96.png">
               <span class="docListInstructionText">${msg("dnd.drop.folder.description")}</span>
            </div>
            <div style="clear:both"></div>
         </div>
      </div>
   </div>

   <#-- Standard upload instructions -->
   <div id="${id}-upload-instructions-template" class="hidden">
      <div class="docListInstructionTitle">${msg("standard.upload.title")}</div>
      <div id="${id}-standard-upload-link-template" class="docListInstructionColumn">
         <img class="docListInstructionImage docListLinkedInstruction" src="${url.context}/res/components/documentlibrary/images/help-upload-96.png">
         <span class="docListInstructionText"><a class="docListLinkedInstruction">${msg("standard.upload.description")}</a></span>
      </div>
   </div>

   <#-- Other options? -->
   <div id="${id}-other-options-template" class="hidden">
      <div class="docListOtherOptions">${msg("other.options")}</div>
   </div>

   <#-- The following DOM structures should be editing with respect to documentlist.js function
        fired by the Doclists "tableMsgShowEvent" as it uses this structure to associate the
        image and anchor with the appropriate actions. NOTE: This is only a template that will
        be cloned, during the cloning the id will be appended with "-instance" to ensure uniqueness
        within the page, this allows us to locate each DOM node individually. -->

   <#-- Standard upload (when user has create access) -->
   <div id="${id}-standard-upload-template" class="hidden">
     <div id="${id}-standard-upload-link-template">
        <img class="docListOtherOptionsImage docListLinkedInstruction" src="${url.context}/res/components/documentlibrary/images/help-upload-48.png">
        <span class="docListOtherOptionsText"><a class="docListLinkedInstruction">${msg("dnd.upload.description")}</a></span>
     </div>
   </div>
   
   <#-- New Folder (when user has create access) -->
   <div id="${id}-new-folder-template" class="hidden">
     <div id="${id}-new-folder-link-template">
        <img class="docListOtherOptionsImage docListLinkedInstruction" src="${url.context}/res/components/documentlibrary/images/help-new-folder-48.png">
        <span class="docListOtherOptionsText"><a class="docListLinkedInstruction">${msg("dnd.newfolder.description")}</a></span>
     </div>
   </div>

   <#-- Hidden sub-folders message -->
   <div id="${id}-show-folders-template" class="hidden">
      <img class="docListOtherOptionsImage docListLinkedInstruction" src="${url.context}/res/components/documentlibrary/images/help-folder-48.png">
      <span class="docListOtherOptionsText"><a class="docListLinkedInstruction"><#-- We don't know the number of hidden subfolders at this point so this needs to be inserted --></a></span>
   </div>
   <#--
      END OF INFORMATION TEMPLATES
   -->

   <#-- Top Bar: Select, Pagination, Sorting & View controls -->
   <div id="${id}-doclistBar" class="yui-gc doclist-bar flat-button no-check-bg">
      <div class="yui-u first">
         <div class="file-select">
            <button id="${id}-fileSelect-button" name="doclist-fileSelect-button">${msg("menu.select")}</button>
            <div id="${id}-fileSelect-menu" class="yuimenu">
               <div class="bd">
                  <ul>
                     <li><a href="#"><span class="selectDocuments">${msg("menu.select.documents")}</span></a></li>
                     <li><a href="#"><span class="selectFolders">${msg("menu.select.folders")}</span></a></li>
                     <li><a href="#"><span class="selectAll">${msg("menu.select.all")}</span></a></li>
                     <li><a href="#"><span class="selectInvert">${msg("menu.select.invert")}</span></a></li>
                     <li><a href="#"><span class="selectNone">${msg("menu.select.none")}</span></a></li>
                  </ul>
               </div>
            </div>
         </div>
         <div id="${id}-paginator" class="paginator"></div>
      </div>
      <div class="yui-u align-right">
         <div id="${id}-simpleDetailed" class="simple-detailed yui-buttongroup inline">
            <#-- Don't insert linefeeds between these <input> tags -->
            <input id="${id}-simpleView" type="radio" name="simpleDetailed" title="${msg("button.view.simple")}" value="" /><input id="${id}-detailedView" type="radio" name="simpleDetailed" title="${msg("button.view.detailed")}" value="" />
         </div>
         <div class="show-folders">
            <span id="${id}-showFolders-button" class="yui-button yui-checkbox-button">
               <span class="first-child">
                  <button name="doclist-showFolders-button"></button>
               </span>
            </span>
            <span class="separator">&nbsp;</span>
         </div>
         <div class="sort-field">
            <span id="${id}-sortField-button" class="yui-button yui-push-button">
               <span class="first-child">
                  <button name="doclist-sortField-button"></button>
               </span>
            </span>
            <span class="separator">&nbsp;</span>
            <select id="${id}-sortField-menu">
            <#list sortOptions as sort>
               <option value="${(sort.value!"")?html}" <#if sort.direction??>title="${sort.direction?string}"</#if>>${msg(sort.label)}</option>
            </#list>
            </select>
         </div>
         <div class="sort-direction">
            <span id="${id}-sortAscending-button" class="yui-button yui-push-button">
               <span class="first-child">
                  <button name="doclist-sortAscending-button"></button>
               </span>
            </span>
         </div>
      </div>
   </div>

   <#-- Main Panel: Document List -->
   <div id="${id}-documents" class="documents"></div>

   <#-- Bottom Bar: Paginator -->
   <div id="${id}-doclistBarBottom" class="yui-gc doclist-bar doclist-bar-bottom flat-button">
      <div class="yui-u first">
         <div class="file-select">&nbsp;</div>
         <div id="${id}-paginatorBottom" class="paginator"></div>
      </div>
   </div>

   <#--
      RENDERING TEMPLATES
   -->
   <div style="display: none">

      <#-- Action Set "More" template -->
      <div id="${id}-moreActions">
         <div class="internal-show-more" title="onActionShowMore"><a href="#" class="show-more" title="${msg("actions.more")}"><span>${msg("actions.more")}</span></a></div>
         <div class="more-actions hidden"></div>
      </div>

   </div>

</div>
</#macro>