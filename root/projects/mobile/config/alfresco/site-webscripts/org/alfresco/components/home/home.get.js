<import resource="classpath:alfresco/site-webscripts/org/alfresco/utils.js">

var overdueTasks = getUserTasks('overdue').tasks;
var todaysTasks = getUserTasks('today').tasks;
var userEvents = getUserEvents().events;

model.role=page.url.templateArgs.site;

model.numOverdueTasks = overdueTasks.length;
model.numTodaysTasks = todaysTasks.length;
model.numEvents = userEvents.length;
model.backButton = true;