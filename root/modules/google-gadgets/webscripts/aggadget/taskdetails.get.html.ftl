<#assign task=workflow.getTaskById(args.id)>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
   <head>
      <title>Task Details</title>
      <link rel="stylesheet" href="${url.context}/css/gg.css" TYPE="text/css"/>
      <style type="text/css">
div.details
{
   padding: 2px 4px 6px 4px;
}

div.taskResources
{
   border: 1px solid #CCCCCC;
   background-color: #ffffff;
   margin: 2px 6px 8px 6px;
   height: 84px;
   display: block;
   overflow: auto;
}

div.resourceHeader
{
   padding-left: 6px;
}

div.taskActions
{
   padding: 6px 4px 12px 6px;
}

div.taskResource
{
   padding: 1px;
}

div.taskResourceOdd
{
   padding: 1px;
   background-color: #F1F7FD;
}

a.resourceLink
{
   font-size: 12px;
   color: #5A5741;
}

div.resourceLink
{
   font-size: 12px;
   color: #5A5741;
}

tr
{
   vertical-align: top;
}

a.taskAction
{
   padding: 0px 4px 0px 4px;
   border: 1px solid #CCD4DB;
}
      </style>
   </head>
   
   <body>
   
   <div class="main">
      <div class="titlebar">Details for task '<#if task.description?exists>${task.description?html}<#else>${task.type?html}</#if>'</div>
      <div class="dialog">
         <div class="details">
            <table cellspacing="0" cellpadding="0" border="0">
               <tr><td><span class="metaTitle">Type:</span></td><td rowspan="99" width="8"></td><td><span class="metaData">${task.type?html}</span></td></tr>
               <tr><td><span class="metaTitle">Priority:</span></td><td><span class="metaData">${task.properties["bpm:priority"]}</span></td></tr>
               <tr><td><span class="metaTitle">Due Date:</span></td><td><span class="metaData"><#if task.properties["bpm:dueDate"]?exists>${task.properties["bpm:dueDate"]?date}<#else><i>None</i></#if></span></td></tr>
               <tr><td><span class="metaTitle">Status:</span></td><td><span class="metaData">${task.properties["bpm:status"]}</span></td></tr>
               <tr><td><span class="metaTitle">Start Date:</span></td><td><span class="metaData">${task.startDate?date}</span></td></tr>
            </table>
         </div>
         
         <div class="resourceHeader">Task resources:</div>
         
         <div class="taskResources">
            <#assign count=0>
            <#assign resources=task.packageResources>
            <#if resources?size != 0>
               <#list resources as res>
                  <#assign count=count+1>
                  <div class="taskResource${(count%2=0)?string("Odd", "")}">
                     <div style="float:left"><#if res.isDocument><a href="${url.serviceContext}${res.serviceUrl}" target="new"></#if><img src="${url.context}${res.icon16}" border=0/><#if res.isDocument></a></#if></div>
                     <div style="margin-left:20px;height:19px">
                     <#if res.isDocument>
                        <a class="resourceLink" href="${url.serviceContext}${res.serviceUrl}" target="alfnew">${res.name}</a>
                     <#else>
                        ${res.name}
                     </#if>
                     </div>
                  </div>
               </#list>
            <#else>
               <div class="resourceLink">No task resources</div>
            </#if>
         </div>
         
         <div class="taskActions">
            Task Actions:
	      <#list task.transitions as wt>
            <a class="taskAction" href="${url.serviceContext}/aggadget/transitiontask?id=${task.id}<#if wt.id?exists>&t=${wt.id}</#if>&m=${wt.label?url}&returl=${args.returl}">${wt.label?html}</a>
         </#list>
         </div>
         
         <div style="padding-left:4px"><input style="width:auto" type="button" onclick="javascript:window.location.href='${args.returl}';" value="Continue"/></div>
      </div>
   </div>
   
   </body>
</html>