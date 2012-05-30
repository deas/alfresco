model.webScriptWidgets = [];
var blogPostEdit = {};
blogPostEdit.name = "Alfresco.BlogPostEdit";
blogPostEdit.provideMessages = true;
blogPostEdit.provideOptions = true;
blogPostEdit.options = {};
blogPostEdit.options.siteId = (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "";
blogPostEdit.options.containerId = "blog";
blogPostEdit.options.editMode = page.url.args.postId != null;
if (blogPostEdit.options.editMode)
{
   blogPostEdit.options.postId = page.url.args.postId;
}
else
{
   blogPostEdit.options.postId = "";
}
var locale = locale.substring(0, 2);
blogPostEdit.options.editorConfig = 
   {
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
model.webScriptWidgets.push(blogPostEdit);