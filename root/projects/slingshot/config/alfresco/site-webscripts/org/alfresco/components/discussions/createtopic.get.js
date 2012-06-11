// Widget instantiation metadata...
model.webScriptWidgets = [];

var createTopic = {};
createTopic.name = "Alfresco.CreateTopic";
createTopic.provideOptions = true;
createTopic.provideMessages = true;
createTopic.options = {};
createTopic.options.topicId = (page.url.args.topicId != null) ? page.url.args.topicId : "";
createTopic.options.siteId = (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "";
createTopic.options.containerId = (page.url.args.containerId != null) ? page.url.args.containerId : "discussions";
createTopic.options.editMode = createTopic.options.topicId != "";
var editorConfig =
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
   language: this.locale.substring(0, 2)
};
createTopic.options.editorConfig = editorConfig;
model.webScriptWidgets.push(createTopic);