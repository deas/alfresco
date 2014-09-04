<#macro editorParameters field>
   <#if field.control.params.editorAppearance?exists><#assign appearance=field.control.params.editorAppearance><#else><#assign appearance="default"></#if>
   <#if field.control.params.editorHeight?exists><#assign height=field.control.params.editorHeight><#else><#assign height=100></#if>
   <#if field.control.params.editorWidth?exists><#assign width=field.control.params.editorWidth><#else><#assign width=400></#if>

   editorParameters:
   {
<#if appearance != "none">
      height: ${height},
      width: ${width},
      theme: 'modern',
      language: "${locale?js_string}",
   <#if appearance == "full"> 
      toolbar: "bold italic underline strikethrough | fontselect fontsizeselect | link unlink image | justifyleft justifycenter justifyright justifyfull | bullist numlist | undo redo | forecolor backcolor"
   <#elseif appearance == "explorer">
      height: 400,
      width: '',
      forced_root_block : false,
      force_p_newlines : false,
      valid_children : "+body[style]",
      extended_valid_elements: "a[href|target|name|style],font[face|size|color|style],span[id|class|align|style],meta[*],style[type]"
   <#elseif appearance == "webeditor">
      width:'',
      toolbar: "bold italic underline strikethrough | fontselect fontsizeselect | link unlink image | justifyleft justifycenter justifyright justifyfull | bullist numlist | undo redo | forecolor backcolor",
      menu: {}
   <#elseif appearance == "custom">
      ${field.control.params.editorParameters!""}
   <#else>
      toolbar: "bold italic underline | bullist numlist | forecolor backcolor | undo redo removeformat",
      menu: {}
   </#if>
</#if>
   }
</#macro>