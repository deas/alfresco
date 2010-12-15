<#include "../component.head.inc">
<!-- Calendar View -->
<@script type="text/javascript" src="${page.url.context}/res/modules/simple-dialog.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/calendar/calendar-event.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/calendar/calendar-view.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/calendar/calendar-view-${page.url.args.view!'month'?js_string}.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/calendar/eventinfo.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/calendar/microformat-parser.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/calendar/microformats/hcalendar.js"></@script>
<!-- Tag library -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/modules/taglibrary/taglibrary.css" />
<@script type="text/javascript" src="${page.url.context}/res/modules/taglibrary/taglibrary.js"></@script>