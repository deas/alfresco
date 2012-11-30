function main()
{
   var locale = this.locale.substring(0, 2);

   var blogPostEdit = {
      id : "BlogPostEdit",
      name : "Alfresco.BlogPostEdit",
      options : {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         containerId : "blog",
         editMode : (page.url.args.postId != null),
         postId : (page.url.args.postId != null) ? page.url.args.postId : "",
         editorConfig : {
            width: "700",
            height: "300",
            inline_styles: false,
            convert_fonts_to_spans: false,
            theme: "advanced",
            theme_advanced_buttons1 : "bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,|,formatselect,fontselect,fontsizeselect,forecolor",
            theme_advanced_buttons2 :"bullist,numlist,|,outdent,indent,blockquote,|,undo,redo,|,link,unlink,anchor,image,cleanup,help,code,removeformat",
            theme_advanced_toolbar_location: "top",
            theme_advanced_toolbar_align: "left",
            theme_advanced_statusbar_location: "bottom",
            theme_advanced_resizing: true,
            theme_advanced_buttons3: null,
            theme_advanced_path: false,
            language: locale
         }
      }
   };
   model.widgets = [blogPostEdit];
}

main();
