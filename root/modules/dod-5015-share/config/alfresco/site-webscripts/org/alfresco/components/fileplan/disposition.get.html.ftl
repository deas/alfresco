<script type="text/javascript">//<![CDATA[
   new Alfresco.Disposition("${args.htmlid}").setOptions(
   {
      nodeRef: "${(page.url.args.nodeRef!"")?js_string}",
      siteId: "${page.url.templateArgs.site!""}",
      dipositionScheduleNodeRef: "${dipositionScheduleNodeRef!""}"
   }).setMessages(
      ${messages}
   );

//]]></script>
<#assign el=args.htmlid>

<div class="disposition">

   <div class="heading">${msg("disposition-schedule.heading")}</div>

   <div>
      <div class="header">
         <div class="title">${msg("title.properties")}</div>
         <div class="buttons">
            <span id="${el}-editproperties-button" class="yui-button inline-button">
               <span class="first-child">
                  <button type="button">${msg("button.edit")}</button>
               </span>
            </span>
         </div>
      </div>

      <div class="properties">
         <div class="field">
            <span class="label">${msg("label.dispositionAuthority")}:</span>
            <span class="value">${(authority!"")?html}</span>
         </div>
         <div class="field">
            <span class="label">${msg("label.dispositionInstructions")}:</span>
            <span class="value">${(instructions!"")?html}</span>
         </div>
         <div class="field">
            <span class="label">${msg("label.appliedTo")}:</span>
            <span class="value"><#if (recordLevelDisposition)>${msg("label.appliedTo.record")}<#else>${msg("label.appliedTo.folder")}</#if></span>
         </div>
         <div class="field">
            <span class="label">${msg("label.unpublishedUpdates")}:</span>
            <span class="value">${unpublishedUpdates?string("Yes", "No")}</span>
         </div>	
         
      </div>

      <div class="header">
         <div class="title">${msg("title.actions")}</div>
         <div class="buttons">
            <span id="${el}-editschedule-button" class="yui-button inline-button">
               <span class="first-child">
                  <button type="button">${msg("button.edit")}</button>
               </span>
            </span>
         </div>
      </div>

      <div id="${el}-actions" class="actions">
      <#if (actions?size > 0)>
         <#list actions as action>
         <div class="action">
            <div class="no">${action.index + 1}</div>
            <div class="more collapsed"><a href="#">${msg("link.description")}</a></div>
            <div class="name">${action.title}</div>
            <div class="description" style="display: none;"><#if (action.description?has_content)>${action.description?html}<#else>${msg("label.nodescription")}</#if></div>
         </div>
         </#list>
      <#else>
         ${msg("label.noactions")}
      </#if>
      </div>
   </div>

</div>