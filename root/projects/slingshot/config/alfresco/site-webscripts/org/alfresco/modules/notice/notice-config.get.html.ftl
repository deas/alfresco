<script type="text/javascript>//<![CDATA[
   new Alfresco.util.RichEditor("tinyMCE", "${args.htmlid}-text",
   {
         height: 150,
         width: 404,
         inline_styles: false,
         convert_fonts_to_spans: false,
         theme: 'advanced',
         theme_advanced_buttons1: "bold,italic,underline,|,bullist,numlist,|,forecolor,|,undo,redo,removeformat",
         theme_advanced_toolbar_location: "top",
         theme_advanced_toolbar_align: "left",
         theme_advanced_statusbar_location: "bottom",
         theme_advanced_resizing: true,
         theme_advanced_buttons2: null,
         theme_advanced_buttons3: null,
         theme_advanced_path: false,
         language: 'en'
      });
//]]></script>

<div id="${args.htmlid}-configDialog">
   <div class="hd">${msg("label.header")}</div>
   <div class="bd">
      <form id="${args.htmlid}-form" action="" method="POST">
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-title">${msg("label.title")}:</label></div>
            <div class="yui-u" >
               <input type="text" name="title" id="${args.htmlid}-title" />
            </div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-text">${msg("label.text")}:</label></div>
            <div class="yui-u" >
               <textarea name="text" id="${args.htmlid}-text" rows="8" cols="80"></textarea>
            </div>
         </div>
         <div class="bdft">
            <input type="submit" id="${args.htmlid}-ok" value="${msg("button.ok")}" />
            <input type="button" id="${args.htmlid}-cancel" value="${msg("button.cancel")}" />
         </div>
      </form>
   </div>
</div>