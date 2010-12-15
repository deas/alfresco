<#--
      Note!

      This component uses key events. The component listens to key events for a
      specific element that must have focus to trigger events.
      Its possible to listen for global events, i.e. key events for the document
      but since several key listening components might live on the same page
      that can't be done.

      The browser gives focus to links or form elements, since the dashlets
      are represented by "li"-tags they will not get focus. To achieve this
      anyway a non visible "a"-tag is placed in each "li"-tag so we
      can get focus and thereafter listen to individual key events.

      Inside the a element is a transparent gif with width and height of 100%
      to make the browsers focus indication borders go around the whole dashlet.

      Since the cursor is changed using CSS selectors on the currently selected
      element a div is in front of both the a element and the image is a div,
      to make sure it becomes the selected element.

   -->
<div class="customise-dashlets">

   <script type="text/javascript">//<![CDATA[
   new Alfresco.CustomiseDashlets("${args.htmlid}").setMessages(
      ${messages}
   ).setOptions(
   {
      currentLayout:
      {
         templateId: "${currentLayout.templateId}",
         noOfColumns: ${currentLayout.noOfColumns},
         description: "${currentLayout.description}",
         icon: "${url.context}/res/components/dashboard/images/${currentLayout.templateId}.png"         
      },
      dashboardUrl: "${dashboardUrl}",
      dashboardId: "${dashboardId}"
   });
   //]]></script>

   <div id="${args.htmlid}-instructions-div" class="instructions">

      <h2>${msg("header.dashlets")}</h2>
      <hr />
      
      <div>
         <div class="text">${msg("label.instructions")}</div>
         <div class="buttons" id="${args.htmlid}-toggleDashletsButtonWrapper-div">
            <input id="${args.htmlid}-addDashlets-button" type="button" value="${msg("button.addDashlets")}" />
         </div>
      </div>

   </div>

   <div id="${args.htmlid}-available-div" class="available" style="display: none;">

      <div>
         <div class="text">
            <a class="closeLink" href="#" id="${args.htmlid}-closeAddDashlets-link">${msg("link.close")}</a>
            <h3 class="padded">${msg("section.addDashlets")}</h3>
         </div>
         <ul id="${args.htmlid}-column-ul-0" class="availableList">
         <#list availableDashlets as dashlet>
            <li class="availableDashlet">
               <input type="hidden" name="dashleturl" value="${dashlet.url}"/>
               <a href="#"><img class="dnd-draggable" src="${url.context}/res/yui/assets/skins/default/transparent.gif" alt="" /></a>
               <span >${dashlet.shortName}</span>
               <div class="dnd-draggable" title="${dashlet.description}"></div>
            </li>
         </#list>
         </ul>
      </div>

   </div>

   <div class="used">

      <div id="${args.htmlid}-wrapper-div" class="noOfColumns${currentLayout.noOfColumns}">

         <div class="usedActions">&nbsp;</div>
         <#list columns as column>
            <div class="column" id="${args.htmlid}-column-div-${column_index + 1}" <#if (column_index >= currentLayout.noOfColumns)>style="display: none;"</#if>>
            <h3 class="padded">${msg("header.column", column_index + 1)}</h3>
            <ul id="${args.htmlid}-column-ul-${column_index + 1}" class="usedList">
            <#list column as dashlet>
               <li class="usedDashlet">
                  <input type="hidden" name="dashleturl" value="${dashlet.url}"/>
                  <input type="hidden" name="originalregionid" value="${dashlet.originalRegionId}"/>
                  <a href="#"><img class="dnd-draggable" src="${url.context}/res/yui/assets/skins/default/transparent.gif" alt="" /></a>
                  <span>${dashlet.shortName}</span>
                  <div class="dnd-draggable" title="${dashlet.description}"></div>
               </li>
            </#list>
            </ul>                                            
            </div>
         </#list>
         <div class="usedActions">
            <span id="${args.htmlid}-trashcan-img" class="trashcan" title="${msg("help.trashcan")}">&nbsp;</span>
         </div>

      </div>

    </div>

<div class="actions">
      
   <hr />
      <div>
         <div class="buttons">
            <input id="${args.htmlid}-save-button" type="button" value="${msg("button.save")}" />
            <input id="${args.htmlid}-cancel-button" type="button" value="${msg("button.cancel")}" />
         </div>
      </div>
   </div>

   <div style="display: none;">
      <ul>
         <!-- The shadow dashlet that is used during drag n drop to "make space" for the dragged dashlet -->
         <li class="usedDashlet dnd-shadow" id="${args.htmlid}-dashlet-li-shadow"></li>
      </ul>
   </div>
</div>
