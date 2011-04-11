<script type="text/javascript">//<![CDATA[
   new Alfresco.BlogPostEdit("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site}",
      editorConfig:
      {
         inline_styles: false,
         convert_fonts_to_spans: false,
         theme: "advanced",
         theme_advanced_buttons1: "bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,|,formatselect,fontselect,fontsizeselect,forecolor",
         theme_advanced_buttons2: "bullist,numlist,|,outdent,indent,blockquote,|,undo,redo,|,link,unlink,anchor,image,cleanup,help,code,removeformat",
         theme_advanced_toolbar_location: "top",
         theme_advanced_toolbar_align: "left",
         theme_advanced_statusbar_location: "bottom",
         theme_advanced_path: false,
         theme_advanced_resizing: true,
         theme_advanced_buttons3: null,
         language: "${locale?substring(0, 2)?js_string}"
      },
      containerId: "blog",
   <#if page.url.args.postId??>
      editMode: true,
      postId: "${page.url.args.postId?js_string}"
   <#else>
      editMode: false,
      postId: ""
   </#if>
   }).setMessages(
      ${messages}
   );
//]]></script>

<div class="page-form-header">
<#if page.url.args.postId??>
   <h1>${msg("editPost")}</h1>
<#else>
   <h1>${msg("createPost")}</h1>
</#if>
   <hr/>
</div>
<div id="${args.htmlid}-div" class="page-form-body hidden">
   <form id="${args.htmlid}-form" method="post" action="">
      <fieldset>
         <input type="hidden" id="${args.htmlid}-site" name="site" value="" />
         <input type="hidden" id="${args.htmlid}-container" name="container" value="" />
         <input type="hidden" id="${args.htmlid}-page" name="page" value="blog-postview" />
         <input type="hidden" id="${args.htmlid}-draft" name="draft" value=""/>

         <div class="yui-gd">
            <div class="yui-u first">
               <label for="${args.htmlid}-title">${msg("label.title")}:</label>
            </div>
            <div class="yui-u">
               <input class="wide" type="text" id="${args.htmlid}-title" name="title" value="" />
            </div>
         </div>

         <div class="yui-gd">
            <div class="yui-u first">
               <label for="${args.htmlid}-content">${msg("text")}:</label>
            </div>
            <div class="yui-u">
               <textarea rows="8" id="${args.htmlid}-content" name="content" cols="180" class="yuieditor"></textarea> 
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
               <input type="submit" id="${args.htmlid}-save-button" value="" />         
               <input type="button" id="${args.htmlid}-publish-button" value="${msg('action.publish')}" class="hidden" />
               <input type="button" id="${args.htmlid}-publishexternal-button" value="" />
               <input type="reset" id="${args.htmlid}-cancel-button" value="${msg('action.cancel')}" />
            </div>
         </div>
      </fieldset>
   </form>
</div>
