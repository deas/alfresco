<#assign editMode = ((page.url.args.topicId!"") != "") />
<script type="text/javascript">//<![CDATA[
   new Alfresco.CreateTopic("${args.htmlid}").setOptions(
   {
      topicId: "${(page.url.args.topicId!'')?js_string}",
      siteId: "${page.url.templateArgs.site!''}",
      containerId: "${(page.url.args.containerId!'discussions')?js_string}",
      editorConfig:
      {
         width: "700",
         height: "180",
         inline_styles: false,
         convert_fonts_to_spans: false,
         theme: "advanced",
         theme_advanced_buttons1 : "bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,|,formatselect,fontselect,fontsizeselect,forecolor",
         theme_advanced_buttons2 :"bullist,numlist,|,outdent,indent,blockquote,|,undo,redo,|,link,unlink,anchor,image,cleanup,help,code,removeformat",
         theme_advanced_toolbar_location : "top",
         theme_advanced_toolbar_align : "left",
         theme_advanced_statusbar_location : "bottom",
         theme_advanced_path : false,
         theme_advanced_resizing : true,
         theme_advanced_buttons3 : null,
         language: "${locale?substring(0, 2)?js_string}"
      },
      editMode: ${editMode?string}
   }).setMessages(
      ${messages}
   );
//]]></script>
<div class="page-form-header">
   <h1><#if editMode>${msg("header.edit")}<#else>${msg("header.create")}</#if></h1>
   <hr/>
</div>
<div class="page-form-body hidden" id ="${args.htmlid}-topic-create-div">
   <form id="${args.htmlid}-form" method="post" action="">
      <fieldset>
         <input type="hidden" id="${args.htmlid}-topicId" name="topic" value="" />
         <input type="hidden" id="${args.htmlid}-site" name="site" value="" />
         <input type="hidden" id="${args.htmlid}-container" name="container" value="" />
         <input type="hidden" id="${args.htmlid}-page" name="page" value="discussions-topicview" />

         <div class="yui-gd">
            <div class="yui-u first">
               <label for="${args.htmlid}-title">${msg("label.title")}:</label>
            </div>
            <div class="yui-u">
               <input class="wide" type="text" id="${args.htmlid}-title" name="title" size="80" value=""/>
            </div>
         </div>

         <div class="yui-gd">
            <div class="yui-u first">
               <label for="${args.htmlid}-content">${msg("topicText")}:</label>
            </div>
            <div class="yui-u">
               <textarea rows="8" cols="80" id="${args.htmlid}-content" name="content" class="yuieditor"></textarea>
            </div>
         </div>

         <div class="yui-gd">
            <div class="yui-u first">
               <label for="${htmlid}-tag-input-field">${msg("label.tags")}:</label>
            </div>
            <div class="yui-u">
               <#import "/org/alfresco/modules/taglibrary/taglibrary.lib.ftl" as taglibraryLib/>
               <@taglibraryLib.renderTagLibraryHTML htmlid=args.htmlid />
            </div>
         </div>

         <div class="yui-gd">
            <div class="yui-u first">&nbsp;</div>
            <div class="yui-u">
               <input type="submit" id="${args.htmlid}-submit" value="${msg('action.save')}" />
               <input type="reset" id="${args.htmlid}-cancel" value="${msg('action.cancel')}" />
            </div>
         </div>
      </fieldset>
   </form>
</div>