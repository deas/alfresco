<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/calendar/helper.js">

function main()
{
   model.viewArgs = CalendarScriptHelper.initView();

   // Widget instantiation metadata...
   model.widgets = [];
   var calendarView = {
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
         permitToCreateEvents : model.viewArgs.permitToCreateEvents,
         truncateLength : 100,
         fcOpts : 
         {
            weekView: "agendaWeek",
            dayView: "agendaDay",
            monthView: "month",
            weekMode: "variable",
            weekends: true,
            allDaySlot: true,
            firstDay: 1,
            firstHour: 0,
            minTimeWorkHours: 7,
            maxTimeWorkHours: 19,
            minTimeToggle: 0,
            maxTimeToggle: 24,
            aspectRatio: 1.5,
            slotMinutes: 30,
            disableDragging: false,
            disableResizing: false
         }
      }
   };
   model.widgets.push(calendarView);
}

main();

