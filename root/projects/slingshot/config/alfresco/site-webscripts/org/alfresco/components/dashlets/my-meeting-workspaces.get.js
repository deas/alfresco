<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/dashlets/my-workspaces.inc.js">
main("meeting-workspace");

function widgets()
{
   // Widget instantiation metadata...
   model.widgets = [];
   var myMeetingWorkspaces = {
      name : "Alfresco.dashlet.MyMeetingWorkspaces",
      options : {
         imapEnabled : model.imapServerEnabled,
         sites : model.sites
      }
   };
   model.widgets.push(myMeetingWorkspaces);

   var dashletResizer = {
      name : "Alfresco.widget.DashletResizer",
      initArgs : ["\"" + args.htmlid + "\"", "\"" + instance.object.id + "\""]
   };
   model.widgets.push(dashletResizer);

   var dashletTitleBarActions = {
      name : "Alfresco.widget.DashletTitleBarActions",
      useMessages : false,
      options : {
         actions: [
            {
               cssClass: "help",
               bubbleOnClick:
               {
                  message: msg.get("dashlet.help")
               },
               tooltip: msg.get("dashlet.help.tooltip")
            }
         ]
      }
   };
   model.widgets.push(dashletTitleBarActions);
}

widgets();
