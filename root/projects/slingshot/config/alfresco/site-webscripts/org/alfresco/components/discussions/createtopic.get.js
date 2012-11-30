function main()
{
   // Widget instantiation metadata...
   var createTopic = {
      id : "CreateTopic", 
      name : "Alfresco.CreateTopic",
      options : {
         topicId : (page.url.args.topicId != null) ? page.url.args.topicId : "",
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         containerId : (page.url.args.containerId != null) ? page.url.args.containerId : "discussions",
         editMode : (page.url.args.topicId != null),
         editorConfig : {
            width: "700",
            height: "300",
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
            language: this.locale.substring(0, 2)
         }
      }
   };
   
   model.widgets = [createTopic];
}

main();
