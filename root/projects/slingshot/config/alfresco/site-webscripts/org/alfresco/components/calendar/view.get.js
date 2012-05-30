<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/calendar/helper.js">

model.viewArgs = CalendarScriptHelper.initView();

// Widget instantiation metadata...
model.webScriptWidgets = [];
var calendarView = {};
calendarView.name = "Alfresco.CalendarView";
calendarView.assignToVariable = "calendarView";
calendarView.instantiationArguments = ["\"" + args.htmlid + "Container\""];
calendarView.provideMessages = true;
calendarView.provideOptions = true;
calendarView.options = {};
calendarView.options.siteId = (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "";
calendarView.options.view = model.viewArgs.viewType;
calendarView.options.id = args.htmlid + "View";
calendarView.options.startDate = model.viewArgs.view.startDate;
calendarView.options.endDate = model.viewArgs.view.endDate;
calendarView.options.titleDate = model.viewArgs.view.titleDate;
calendarView.options.permitToCreateEvents = model.viewArgs.permitToCreateEvents;
calendarView.options.truncateLength = 100;
calendarView.options.fcOpts = 
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
};
model.webScriptWidgets.push(calendarView);

