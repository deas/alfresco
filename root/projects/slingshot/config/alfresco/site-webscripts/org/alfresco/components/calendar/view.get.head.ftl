<#include "../component.head.inc">
<!-- Calendar View -->
<@script type="text/javascript" src="${page.url.context}/res/modules/simple-dialog.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/calendar/calendar-view.js"></@script>
<#assign view=context.properties.filteredView />
<#if (view=='agenda')>
   <@script type="text/javascript" src="${page.url.context}/res/components/calendar/calendar-view-${context.properties.filteredView?js_string}.js"></@script>
<#else>
   <@script type="text/javascript" src="${page.url.context}/res/jquery/jquery-1.6.2.custom.js"></@script>
   <@script type="text/javascript" src="${page.url.context}/res/jquery/jquery-ui-1.8.11.custom.min.js"></@script>
   <@script type="text/javascript" src="${page.url.context}/res/jquery/fullcalendar/fullcalendar.js"></@script>
   <@link rel="stylesheet" type="text/css" href="${page.url.context}/res/jquery/fullcalendar/fullcalendar.css" />
   <@script type="text/javascript" src="${page.url.context}/res/components/calendar/calendar-view-fullCalendar.js"></@script>
</#if>
<@script type="text/javascript" src="${page.url.context}/res/components/calendar/eventinfo.js"></@script>
<!-- Tag library -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/modules/taglibrary/taglibrary.css" />
<@script type="text/javascript" src="${page.url.context}/res/modules/taglibrary/taglibrary.js"></@script>