<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
   new Alfresco.CalendarView('${args.htmlid?js_string}Container').setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      //view type
      view : '${viewArgs.viewType?html}',
      id : '${el}View',
      /*
      * The start date of the week/month if week or month
      * a Date object or a ISO string
      **/
      startDate : Alfresco.util.fromISO8601('${viewArgs.view.startDate?html}'),
      endDate : Alfresco.util.fromISO8601('${viewArgs.view.endDate?html}'),
      titleDate : Alfresco.util.fromISO8601('${viewArgs.view.titleDate?html}'),
      permitToCreateEvents : '${viewArgs.permitToCreateEvents?html}',
      truncateLength: ${config.script.config.truncateLength!100},
      /* FullCalendar Options */
      fcOpts:
      {
         dayView: ${config.script.config.dayView},
         weekView: ${config.script.config.weekView},
         monthView: ${config.script.config.monthView},
         weekMode: ${config.script.config.weekMode},
         weekends: ${config.script.config.weekends},
         allDaySlot: ${config.script.config.allDaySlot},
         firstDay: ${config.script.config.firstDay},
         firstHour:${config.script.config.firstHour},
         //TODO: This should be a config option, but needs to be accessible by the toolbar webscript too. 
         showWorkHours: true,
         minTimeWorkHours: ${config.script.config.minTimeWorkHours},
         maxTimeWorkHours: ${config.script.config.maxTimeWorkHours},
         minTimeToggle: ${config.script.config.minTimeToggle},
         maxTimeToggle: ${config.script.config.maxTimeToggle},
         aspectRatio: ${config.script.config.aspectRatio},
         slotMinutes: ${config.script.config.slotMinutes},
         disableDragging:${config.script.config.disableDragging},
         disableResizing:${config.script.config.disableResizing}
      }
   }).setMessages(
      ${messages}
   );
   /* These need setting against EventInfo until that component is integrated into the CalendarView scripts fully */
   Alfresco.util.addMessages(${messages}, "Alfresco.EventInfo");
//]]></script>


<#if (viewArgs.viewType=='agenda')>

   <!-- agenda -->
   <a href="" class="previousEvents hidden agendaNav">${msg("agenda.previous")}</a>
   <h2 id="calTitle">&nbsp;</h2>
   <div id="${el}Container" class="alf-calendar agendaview">
       <div id="${el}View">
          <div id="${el}View-noEvent" class="noEvent">
             <p id="${el}View-defaultText" class="instructionTitle">${msg("agenda.initial-text")}</p>
          </div>
       </div>
   </div>
   <div class="nextEventsContainer">&nbsp;<a href="" class="nextEvents hidden agendaNav">${msg("agenda.next")}</a></div>

<#else>
   <!--[if IE]>
   <iframe id="yui-history-iframe" src="${url.context}/res/yui/history/assets/blank.html"></iframe>
   <![endif]-->
   <input id="yui-history-field" type="hidden" />

   <div id="${el}Container" class="alf-calendar fullCalendar">
       <div id="${el}View">
       </div>
   </div>
</#if>