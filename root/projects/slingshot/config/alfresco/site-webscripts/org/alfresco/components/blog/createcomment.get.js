function main()
{
   var height = (args.editorHeight != null) ? args.editorHeight : 250,
         width = (args.editorWidth != null) ? args.editorWidth : 538,
         locale = locale.substring(0, 2);

   var createComment = {
      id: "CreateComment",
      name : "Alfresco.CreateComment",
      options : {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         containerId : template.properties.container != null ? template.properties.container : "documentLibrary",
         height : height,
         width : width,
         editorConfig : {
            height: height,
            width: width,
            inline_styles: false,
            convert_fonts_to_spans: false,
            theme: "advanced",
            theme_advanced_buttons1: "bold,italic,underline,|,bullist,numlist,|,forecolor,|,undo,redo,removeformat",
            theme_advanced_toolbar_location: "top",
            theme_advanced_toolbar_align: "left",
            theme_advanced_statusbar_location: "bottom",
            theme_advanced_resizing: true,
            theme_advanced_buttons2: null,
            theme_advanced_buttons3: null,
            theme_advanced_path: false,
            language: locale
         }
      }
   };
   model.widgets = [createComment];
   odel.widgets.push();
}

main();

