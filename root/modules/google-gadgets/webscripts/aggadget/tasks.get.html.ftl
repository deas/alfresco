<#assign weekms=1000*60*60*24*7>
<#assign returl=url.service?url + "?f="?url + filter + "&m="?url + mode>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
   <head>
      <title>User Tasks</title>
      <link rel="stylesheet" href="${url.context}/css/gg.css" TYPE="text/css"/>
      <style type="text/css">
a.filterLink:link, a.filterLink:visited
{
   color: #8EA1B3;
   font-size: 12px;
   font-weight: bold;
   padding-left: 4px;
   padding-right: 4px;
   white-space: nowrap;
}

a.filterLink:hover
{
   background-color: #f0edcf;
}

a.filterLinkSelected:link, a.filterLinkSelected:visited
{
   color: #f9f8f3 !important;
   background-color: #c4be83 !important;
}

div.header
{
   background-image: url(${url.context}/images/parts/ggtasks_headerbg.png);
   height: 26px;
}

div.taskRow
{
   padding-top: 4px;
   border-top: 1px solid #F0EDCF;
}

div.taskIndicator
{
   float: left;
   padding-top: 2px;
   padding-left: 8px;
}

div.taskItem
{
   font-size: 13px;
   color: #5A5741;
   margin-left: 24px;
   padding: 0px 8px 8px;
}

div.taskItemOverdue
{
   color: #DF3704;
   font-weight: bold;
}

div.taskItemToday
{
   color: #399DF7;
}
      </style>
      
      <script type="text/javascript" src="${url.context}/scripts/ajax/common.js"></script>
      <script type="text/javascript">setContextPath('${url.context}');</script> 
   </head>
   
   <body>

   <div class="main">
   
   <!-- filters -->
   <div class="header">
      <table border="0" cellspacing="4" cellpadding="0" width="100%">
         <tr>
            <th><a class="filterLink <#if filter=0>filterLinkSelected</#if>" href="${url.service}?f=0&m=${mode}">All</a></th>
            <th><a class="filterLink <#if filter=1>filterLinkSelected</#if>" href="${url.service}?f=1&m=${mode}"><span <#if filter!=1>style="color: #399DF7"</#if>>Due Today</span></a></th>
            <th><a class="filterLink <#if filter=2>filterLinkSelected</#if>" href="${url.service}?f=2&m=${mode}">Next 7 days</a></th>
            <th><a class="filterLink <#if filter=3>filterLinkSelected</#if>" href="${url.service}?f=3&m=${mode}">No due date</a></th>
            <th><a class="filterLink <#if filter=4>filterLinkSelected</#if>" href="${url.service}?f=4&m=${mode}"><span <#if filter!=4>style="color: #DF3704"</#if>>Overdue</span></a></th>
         </tr>
      </table>
   </div>
   
   <!-- toolbar -->
   <div class="toolbar">
      <div style="float:right">
         <!-- View Mode toggle -->
         <#if mode=0><a href="${url.service}?f=${filter}&m=1">Mini&nbsp;View</a></#if>
         <#if mode=1><a href="${url.service}?f=${filter}&m=0">Full&nbsp;View</a></#if>
      </div>
   </div>
   
   <!-- main user task list -->
   <#assign count=0>
   <#list workflow.assignedTasks as t>
      <#assign hasDue=t.properties["bpm:dueDate"]?exists>
      <#if hasDue>
         <#assign due=t.properties["bpm:dueDate"]>
      </#if>
      <#-- filters: 0=all, 1=today, 2=next week, 3=no due date, 4=overdue -->
      <#if (filter=0) ||
           (filter=3 && !hasDue) ||
           (filter=1 && hasDue && (dateCompare(date?date, due?date, 0, "==") == 1)) ||
           (filter=2 && hasDue && ((dateCompare(due?date, date?date) == 1 || dateCompare(date?date, due?date, 0, "==") == 1) && dateCompare(date?date, due?date, weekms) == 1)) ||
           (filter=4 && hasDue && (dateCompare(date?date, due?date) == 1))>
         <#assign count=count+1>
         <div class="taskRow">
            <div class="taskIndicator">
            <#-- items due today? -->
            <#if hasDue && filter<3 && (dateCompare(date?date, due?date, 0, "==") == 1)>
               <img src="${url.context}/images/icons/task_duetoday.png" alt=""/>
            </div>
            <div class="taskItem taskItemToday">
            <#-- items overdue? -->
            <#elseif hasDue && (filter=0 || filter=4) && (dateCompare(date?date, due?date) == 1)>
               <img src="${url.context}/images/icons/task_overdue.png" alt=""/>
            </div>
            <div class="taskItem taskItemOverdue">
            <#else>
               <img src="${url.context}/images/icons/task_any.png" alt=""/>
            </div>
            <div class="taskItem">
               </#if>
               <span style="cursor:pointer" onclick="javascript:window.location.href='${url.serviceContext}/aggadget/taskdetails?id=${t.id}&returl=${returl}';">
               <#if t.description?exists>
               ${t.description?html}
               <#else>
               ${t.type?html}
               </#if>
               (${t.type?html})
               </span>
               <#if mode=0>
               <div class="taskMeta">
                  <span class="metaTitle">Due:</span>&nbsp;<span class="metaData"><#if hasDue>${due?date}<#else><i>None</i></#if></span>&nbsp;
                  <span class="metaTitle">Priority:</span>&nbsp;<span class="metaData">${t.properties["bpm:priority"]}</span>&nbsp;
                  <span class="metaTitle">Status:</span>&nbsp;<span class="metaData">${t.properties["bpm:status"]}</span>&nbsp;
                  <span class="metaTitle">Complete:</span>&nbsp;<span class="metaData">${t.properties["bpm:percentComplete"]}%</span>&nbsp;
                  <span class="metaTitle">Start&nbsp;Date:</span>&nbsp;<span class="metaData">${t.startDate?date}</span>&nbsp;
               </div>
               </#if>
            </div>
         </div>
      </#if>
   </#list>
   
   </div>
   
   </body>
</html>