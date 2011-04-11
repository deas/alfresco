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
      permitToCreateEvents : '${viewArgs.permitToCreateEvents?html}'
   }).setMessages(
      ${messages}
   );
//]]></script>

<#if (viewArgs.viewType=='month')>
<h2 id="calTitle"></h2>
<div id="${el}Container" class="calendar vcalendar monthview">
    <table id="${el}View" cellspacing="0" cellpadding="0">
        <thead>
            <tr>
                <#assign days_in_week = msg("days.medium")?split(",") >
                <#list days_in_week as day>
                <th>${day}</th>
                </#list>
            </tr>
        </thead>
        
        <tbody>
             <#list 0..(viewArgs.view.num_month_rows) as row><#-- ROW -->
              <tr>
                 <#list 0..6 as column><#-- COLUMN -->
                    <#assign id = (row?number * 7) + column>
                    <#assign tdclass = ''>
                    <#if (id<viewArgs.view.startDay_month | id>(viewArgs.view.num_daysInMonth+viewArgs.view.startDay_month-1) ) >
                    <#assign tdclass = 'class="disabled"'>
                    </#if>
                    <td id="cal-${viewArgs.view.dates[id].id}" ${tdclass} style="width: 12.5%">
                       <#if (tdclass != '')>
                        <div class="day disabled theme-bg-color-3">                       
                       <#else>
                        <div class="day">
                       </#if>
                          <a class="dayLabel">${viewArgs.view.dates[id].day}</a>
                          <#if (viewArgs.view.dates[id].events??)>
                           <#assign numEvents = 0>
                           <#assign numAllDayEvents = 0>
                           <#list viewArgs.view.dates[id].events as event>
                                <#if (event.allday) >                              
                                    <div class="vevent allday theme-bg-color-1">
                                        <div>
                                             <a href="/calendar/event/${page.url.templateArgs.site!""}/${event.name}?date=${viewArgs.view.dates[id].id}" class="summary theme-color-1">${event.summary?html}</a>
                                             <p class="description">${event.description?html}</p>
                                             <p class="dates"> <span class="dtstart" title="${event.dtstart}">${event.dtstartText}</span>
                                             - <span class="dtend" title="${event.dtend}">${event.dtendText}</span></p>                                
                                             <span class="location">${event.location?html}</span>
                                             <span class="duration" title="${event.duration}">1h</span>
                                             <span class="category" >${event.tags?html}</span> 
                                        </div>
                                    </div>
                                    <#assign numAllDayEvents = numAllDayEvents + 1>
                                 <#else>
                                    <#assign outputtedUl = false>
                                        <#if numEvents==0>
                                         <ul class="dayEvents">
                                         <#assign outputtedUl = true>
                                        </#if>
                                         <#if (numEvents > (4 - numAllDayEvents)) >
                                            <#assign class="hidden">
                                         <#else>
                                            <#assign class="">
                                         </#if>

                                         <li class="vevent ${class}">
                                               <a href="/calendar/event/${page.url.templateArgs.site!""}/${event.name}?date=${event.dtstart?split('T')[0]}" class="summary theme-color-1">${event.summary?html}</a>
                                               <p class="description">${event.description?html}</p>
                                               <p class="dates"><span class="dtstart" title="${event.dtstart}">${event.dtstartText}</span>
                                               - <span class="dtend" title="${event.dtend}">${event.dtendText}</span></p>                                
                                               <span class="location">${event.location?html}</span>
                                               <span class="duration" title="${event.duration}">${event.duration}</span>
                                               <span class="category" >${event.tags?html}</span>
                                             </li>
                                         <#assign numEvents=numEvents + 1>    
                                 </#if>
                           </#list>
                           <#if (numEvents>5)>
                                  <li class="moreEvents"><a href="#" class="theme-color-1">${msg('label.show-more')}</a></li>                                
                              </#if>
                            <#if (numEvents>0)>
                                </ul>
                            </#if>
                          </#if>
                       </div>
                    </td>
                    </#list>
              </tr>
              </#list>
            
        </tbody>
    </table>
</div>


<#elseif (viewArgs.viewType=='week')>
<!-- week view -->
<h2 id="calTitle"></h2>
<div id="${el}Container" class="calendar vcalendar weekview">
    <table id="${el}View" cellspacing="0" cellpadding="0">
        <thead>
            <tr>
                <th scope="col">${msg("label.hours")}</th>
                <#assign days_in_week = msg("days.medium")?split(",") >
                <#list 0..6 as i>
                <th scope="col">${days_in_week[i]} ${viewArgs.view.columnHeaders[i]?string("d")}</th>
                </#list>
            </tr>
        </thead>
        <tbody>
            <tr class="alldayRow">
               <th scope="row" style="width: 105px"><h2>${msg("label.all-day")}</h2></th>
              
               <#list 0..6 as day>
                <#assign id = (day?number)>
                <#assign tdclass = ''>
                <#if (viewArgs.view.dayOfWeek == day)>
                    <#assign tdclass = 'current'>
                </#if>
                <#if (day==6)>
                    <#assign tdclass = tdclass + ' last'>
                </#if> 
                <td class="${tdclass}"><div id="cal-${viewArgs.view.dates[id].id}" class="target" width="12.5%"> </div></td>
               </#list>
            </tr>
            <tr id="collapseTrigger">
                <td colspan="8" style="width: 100%"><a href="" id="collapseTriggerLink">${msg("label.click-for-early-hours")}</a></td>
            </tr>
            <#assign cellcount = 0  />
                <#list 0..23 as i>
                 <#assign class = ''>
           
                 <#if i < 7>
                  <#assign class = 'early'>
                 </#if>
                 <#assign time = i?string>
                 <#if i < 10>
                    <#assign time = "0" + time>
                 </#if>
                 <tr id="hour-${time}" class="${class}">   
                 <th scope="row" style="width: 105px"><h2>${time}:00</h2></th>
                 <#list 0..6 as day>
                  <#assign id = viewArgs.view.dates[day].id>
                  <#assign divclass = ''>
                  <#if (viewArgs.view.dayOfWeek == day)>
                      <#assign divclass = 'current'>
                  </#if>
                    <td id="cal-${id}T${time}:00" style="width: 12.5%">
                        <div class="day ${divclass}">
                            <div class="hourSegment">
                            </div>
                            <div class="hourSegment last">
                            </div>
                        </div>                        
                    </td>
                 </#list>
                 </tr>
              </#list>
        </tbody>
    </table>
</div>

<#elseif (viewArgs.viewType=='day')>
<!-- day view -->
<h2 id="calTitle"></h2>
<div id="${el}Container" class="calendar vcalendar dayview">
    <table id="${el}View" cellspacing="0" cellpadding="0">
        <tbody>
            <tr class="alldayRow">
                <th scope="row"><h2>${msg("label.all-day")}</h2></th>
                <td id="alldayCell">
                </td>
            </tr>
            <tr id="collapseTrigger">
                <td colspan="2"><a href="" id="collapseTriggerLink">${msg("label.click-for-early-hours")}</a></td>
            </tr>
            <#assign cellcount = 0  />
            <#list 0..23 as i>
            <#assign time = i?string>
            <#if i < 10>
                <#assign time = "0" + time>
            </#if>
            

            <#assign class = ''>
       
             <#if i < 7>
              <#assign class = 'early'>
             </#if>
            <tr class="${class}">
                <th scope="row" style="width: 52px"><h2>${time}:00</h2></th>
                <td id="cal-${viewArgs.view.startDate}T${time}:00" style="width: 95%">
                    <div class="day">
                        <div class="hourSegment">
                        </div>
                        <div class="hourSegment last">
                        </div>
                    </div>                        
                </td>
            </tr>
            </#list>            
        </tbody>
    </table>
</div>
<#elseif (viewArgs.viewType=='agenda')>

<!-- agenda -->
<h2 id="calTitle"></h2>
<div id="${el}Container" class="alf-calendar agendaview">
    <div id="${el}View">
    </div>
</div>
</#if>

