<div class="customise-layout">

   <script type="text/javascript">//<![CDATA[
   new Alfresco.CustomiseLayout("${args.htmlid}").setOptions(
   {
      currentLayout:
      {
         templateId: "${currentLayout.templateId}",
         noOfColumns: ${currentLayout.noOfColumns},
         description: "${currentLayout.description}",
         icon: "${url.context}/res/components/dashboard/images/${currentLayout.templateId}.png"
      },
      layouts:
      {
<#list layouts as layout>
         "${layout.templateId}":
         {
            templateId: "${layout.templateId}",
            noOfColumns: ${layout.noOfColumns},
            description: "${layout.description}",
            icon: "${url.context}/res/components/dashboard/images/${layout.templateId}.png"
         }<#if (layout_has_next)>,</#if>
</#list>
      }
   }
   ).setMessages(${messages});
   //]]></script>

   <div id="${args.htmlid}-hideCurrentLayout-div" style="display: none;"></div>

   <div id="${args.htmlid}-currentLayout-div" class="currentLayout">

      <h2>${msg("section.currentLayout")}
         <span id="${args.htmlid}-currentLayoutDescription-span">${currentLayout.description}</span>
      </h2>
      <hr/>

      <div>
         <br />
         <img id="${args.htmlid}-currentLayoutIcon-img" class="layoutIcon" src="${url.context}/res/components/dashboard/images/${currentLayout.templateId}.png" alt="${msg("img.currentLayout")}" />
         <div id="${args.htmlid}-changeButtonWrapper-div" class="buttons">
            <input id="${args.htmlid}-change-button" type="button" value="${msg("button.showLayouts")}" />
         </div>
      </div>

   </div>

   <div id="${args.htmlid}-layouts-div" class="layouts" style="display: none;">

      <h2 class="instructions">${msg("section.selectNewLayout")}</h2>
      <hr/>

      <div>
         <div class="text">${msg("label.layoutWarning")}</div>

         <ul id="${args.htmlid}-layout-ul">
         <#list layouts as layout>
            <li id="${args.htmlid}-layout-li-${layout.templateId}">               
               <div class="layoutDescription">${layout.description}</div>
               <div class="layoutBox">
                  <span>
                     <img id="${args.htmlid}-select-img-${layout.templateId}" class="layoutIcon" src="${url.context}/res/components/dashboard/images/${layout.templateId}.png" alt="${layout.templateId}" />
                     <input id="${args.htmlid}-select-button-${layout.templateId}" type="button" value="${msg("button.select")}" />
                  </span>
               </div>
            </li>
         </#list>
         </ul>
      </div>

      <hr/>

      <div>
         <div class="buttons">
            <input id="${args.htmlid}-cancel-button" type="button" value="${msg("button.useCurrent")}" />
         </div>
      </div>
      
   </div>

</div>
