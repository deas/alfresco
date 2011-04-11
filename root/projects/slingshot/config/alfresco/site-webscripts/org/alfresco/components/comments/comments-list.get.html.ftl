<#if nodeRef??>
   <#assign el=args.htmlid?js_string>
   <script type="text/javascript">//<![CDATA[
      new Alfresco.CommentsList("${el}").setOptions(
      {
         nodeRef: "${nodeRef?js_string}",
         siteId: <#if site??>"${site?js_string}"<#else>null</#if>,
         maxItems: ${maxItems?js_string},
         activity: <#if activityParameterJSON??>${activityParameterJSON}<#else>null</#if>,
         editorConfig:
         {
            inline_styles: false,
            convert_fonts_to_spans: false,
            theme: "advanced",
            theme_advanced_buttons1: "bold,italic,underline,|,bullist,numlist,|,forecolor,|,undo,redo,removeformat",
            theme_advanced_toolbar_location: "top",
            theme_advanced_toolbar_align: "left",
            theme_advanced_resizing: true,
            theme_advanced_buttons2: null,
            theme_advanced_buttons3: null,
            theme_advanced_path: false,
            language: "${locale?substring(0, 2)?js_string}"
         }
      }).setMessages(
         ${messages}
      );
   //]]></script>
   <div id="${el}-body" class="comments-list">

      <h2 class="thin dark">${msg("header.comments")}</h2>

      <div id="${el}-add-comment">
         <div id="${el}-add-form-container" class="theme-bg-color-4 hidden"></div>
      </div>

      <div class="comments-list-actions">
         <div class="left">
            <button class="alfresco-button" name=".onAddCommentClick">${msg("button.addComment")}</button>
         </div>
         <div class="right">
            <div id="${el}-paginator-top"></div>
         </div>
         <div class="clear"></div>
      </div>

      <hr class="hidden"/>

      <div id="${el}-comments-list"></div>

      <hr class="hidden"/>

      <div class="comments-list-actions">
         <div class="left">
         </div>
         <div class="right">
            <div id="${el}-paginator-bottom"></div>
         </div>
         <div class="clear"></div>
      </div>

   </div>
</#if>