<#if showDashlet>
   <#assign el=args.htmlid?html>
   <script type="text/javascript">//<![CDATA[
   new Alfresco.dashlet.DynamicWelcome("${el}", "${dashboardUrl}", "${dashboardType}", "${siteURL}").setMessages(${messages});
   //]]></script>
   <div class="dashlet dynamic-welcome">
      <a id="${el}-close-button" class="welcome-close-button" href="#">
         <img src="${url.context}/res/components/images/delete-16.png" />
         <span>${msg("welcome.close")}</span>
      </a>
      <div class="welcome-body">
         <div class="welcome-info">
            <h1>${msg(title, user.fullName, site)?html}</h1>
         <#if description??>
            <p class="welcome-info-text">${msg(description)}</p>
         </#if>
            <h2>${msg("get.started.message")}</h2>
         </div>
         <div>
            <div class="welcome-left-container">
               <div class="welcome-middle-container">
                  <div class="welcome-right-container">
                  <#list columns as column>
                     <#if column??>
                     <div class="welcome-details-column welcome-details-column-${column_index}">
                        <div class="welcome-details-column-image">
                           <img src="${url.context}${column.imageUrl}"/>
                         </div>
                         <div class="welcome-details-column-info">
                           <h3>${msg(column.title)}</h3>
                           <#-- The following section allows us to insert arguments into the
                                description using the "descriptionArgs" property of the column
                                data. We construct a FreeMarker expression as a string iterating
                                over any supplied arguments and then evaluate it. -->
                           <#assign descArgs = "msg(column.description" />
                           <#if column.descriptionArgs??>
                              <#list column.descriptionArgs as x>
                                 <#assign descArgs = descArgs + ",\"" + x?html + "\"">
                              </#list>
                           </#if>
                           <#assign descArgs = descArgs + ")">
                           <p class="welcome-details-column-info-text">${descArgs?eval}</p>
                        </div>
                        <div class="welcome-height-adjuster" style="height:0;">&nbsp;</div>
                     </div>
                     </#if>
                  </#list>
                  </div>
               </div>
            </div>
            <div class="welcome-height-adjuster" style="height:0;">&nbsp;</div>
         </div>

         <div class="welcome-details">
            <div class="welcome-left-container">
               <div class="welcome-middle-container">
                  <div class="welcome-right-container">
                  <#list columns as column>
                     <#if column??>
                     <div class="welcome-details-column welcome-details-column-${column_index}">
                        <div class="welcome-details-column-info">
                        <#if column.actionMsg??>
                           <div class="welcome-details-column-info-vertical-spacer"></div>
                           <a <#if column.actionId??>id="${el}${column.actionId}" </#if>
                              <#if column.actionHref??>href="${column.actionHref}" </#if>
                              <#if column.actionTarget??>target="${column.actionTarget}" </#if>>
                              <span>${msg(column.actionMsg)}</span>
                           </a>
                        <#else>
                           <div class="welcome-details-column-info-vertical-spacer"></div>
                        </#if>
                        </div>
                     </div>
                     </#if>
                  </#list>
                  </div>
               </div>
            </div>
            <div class="welcome-height-adjuster">&nbsp;</div>
         </div>
      </div>
    </div>
</#if>