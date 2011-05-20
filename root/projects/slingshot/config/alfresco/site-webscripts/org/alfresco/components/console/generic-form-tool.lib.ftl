<#macro renderPanel cfg name="">
   <!--[if IE]>
   <iframe id="yui-history-iframe" src="${url.context}/res/yui/history/assets/blank.html"></iframe>
   <![endif]-->
   <input id="yui-history-field" type="hidden" />

   <#assign el=args.htmlid?html>
   <script type="text/javascript">//<![CDATA[
      new Alfresco.GenericFormTool("${el}").setOptions(
      {
         itemId: "${cfg.form.itemId?js_string}",
         itemKind: "${cfg.form.itemKind?js_string}",
         showCancelButton: <#if cfg.form.showCancelButton?size != 0>${cfg.form.showCancelButton?js_string}<#else>true</#if>,
         showSubmitButton: <#if cfg.form.showSubmitButton?size != 0>${cfg.form.showSubmitButton?js_string}<#else>true</#if>
      });
   //]]></script>
   <div id="${el}-body" class="console-tool form-generic-tool ${name}">

      <!-- View panel -->
      <div id="${el}-view" class="hidden">
         <h1 class="thin dark">${msg(cfg.title)}</h1>
         <div class="share-form">
            <div class="form-container">
               <#if cfg.link?size == 1 || (cfg.editable!"false")?matches("true")>
               <div class="caption">
                  <#if cfg.link?size == 1>
                     <a href="${cfg.link}" title="${msg(cfg.link.@label)}" target="${cfg.link.@target}">${msg(cfg.link.@label)}</a>
                  </#if>
                  <#if (cfg.editable!"false")?matches("true")>
                     <button class="alfresco-button" name=".showEditPanel">${msg("button.edit")}</button>
                  </#if>
               </div>
               </#if>
            </div>
         </div>
         <div id="${el}-view-form" class="share-form"></div>
      </div>

      <!-- Edit panel -->
      <div id="${el}-edit" class="hidden">
         <h1 class="thin dark">${msg('label.editType', msg(cfg.title))}</h1>
         <div id="${el}-edit-form" class="share-form"></div>
         <#if cfg.form.showCancelLink?size != 0>
         <div class="form-links">
            <a href="#" name=".showViewPanel">${msg("button.cancel")}</a>
         </div>
         </#if>
      </div>

   </div>
</#macro>