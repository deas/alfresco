<#macro editorParameters field>
   <#if field.control.params.editorAppearance?exists><#assign appearance=field.control.params.editorAppearance><#else><#assign appearance="default"></#if>
   <#if field.control.params.editorHeight?exists><#assign height=field.control.params.editorHeight><#else><#assign height=100></#if>
   <#if field.control.params.editorWidth?exists><#assign width=field.control.params.editorWidth><#else><#assign width=400></#if>

   editorParameters:
   {
   <#if appearance != "none">
      height: ${height},
      width: ${width},
      inline_styles: false,
      convert_fonts_to_spans: false,
      theme: 'advanced',
      theme_advanced_toolbar_location: "top",
      theme_advanced_toolbar_align: "left",
      theme_advanced_statusbar_location: "bottom",
      theme_advanced_path: false,
      language: "${locale?substring(0, 2)?js_string}",
   <#if appearance == "full"> 
      plugins: "fullscreen,table,emotions",
      theme_advanced_resizing: true,
      theme_advanced_buttons1: "bold,italic,underline,strikethrough,separator,fontselect,fontsizeselect",
      theme_advanced_buttons2: "link,unlink,image,separator,justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist,separator,undo,redo,separator,forecolor,backcolor",
      theme_advanced_buttons3: "fullscreen,table,emotions"
   <#elseif appearance == "explorer">
      forced_root_block : false,
      force_p_newlines : false,
      plugins: "table",
      theme_advanced_resizing: true,
      theme_advanced_buttons1_add: "fontselect,fontsizeselect",
      theme_advanced_buttons2_add: "separator,forecolor",
      theme_advanced_buttons3_add_before: "tablecontrols,separator",
      theme_advanced_disable: "styleselect",
      valid_children : "+body[style]",
      extended_valid_elements: "a[href|target|name|style],font[face|size|color|style],span[id|class|align|style],meta[*],style[type]"
   <#elseif appearance == "webeditor">
      width:'',
      plugins: "fullscreen",
      theme_advanced_buttons1: "bold,italic,underline,strikethrough,separator,fontselect,fontsizeselect",
      theme_advanced_buttons2: "link,unlink,image,separator,justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist,separator,undo,redo,separator,forecolor,backcolor,separator,fullscreen",
      theme_advanced_buttons3: null
   <#elseif appearance == "custom">
      ${field.control.params.editorParameters!""}
   <#else>
      theme_advanced_resizing: true,
      theme_advanced_buttons1: "bold,italic,underline,separator,bullist,numlist,separator,forecolor,separator,undo,redo,removeformat",
      theme_advanced_buttons2: null,
      theme_advanced_buttons3: null
   </#if>
</#if>
   }
</#macro>