<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/calendar/helper.js">

function main()
{
   model.viewArgs = CalendarScriptHelper.initView();

   // Widget instantiation metadata...
   var calendarView = {
      id : "CalendarView",
      name : "Alfresco.CalendarView",
      assignTo : "calendarView",
      initArgs : ["\"" + args.htmlid + "Container\""],
      options : {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         view : model.viewArgs.viewType,
         id : args.htmlid + "View",
         startDate : model.viewArgs.view.startDate,
         endDate : model.viewArgs.view.endDate,
         titleDate : model.viewArgs.view.titleDate,
         permitToCreateEvents : Boolean(model.viewArgs.permitToCreateEvents),
         truncateLength : parseInt(100),
         fcOpts : 
         {
            weekView: "agendaWeek",
            dayView: "agendaDay",
            monthView: "month",
            weekMode: "variable",
            weekends: true,
            allDaySlot: true,
            firstDay: parseInt(1),
            firstHour: parseInt(0),
            minTimeWorkHours: parseInt(7),
            maxTimeWorkHours: parseInt(19),
            minTimeToggle: parseInt(0),
            maxTimeToggle: parseInt(24),
            aspectRatio: parseInt(1.5),
            slotMinutes: parseInt(30),
            disableDragging: false,
            disableResizing: false
         }
      }
   };
   model.widgets = [calendarView];
}

main();

