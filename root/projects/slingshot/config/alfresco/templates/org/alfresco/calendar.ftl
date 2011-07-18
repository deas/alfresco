<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <script type="text/javascript">//<![CDATA[
      new Alfresco.widget.Resizer("Calendar").setOptions(
      {
         divLeft: "divCalendarFilters",
         divRight: "divCalendarContent",
         initialWidth: 215
      });
   //]]></script>
</@>

<@templateBody>
   <div id="alf-hd">
      <@region id="header" scope="global" />
      <@region id="title" scope="template" />
      <@region id="navigation" scope="template" />
   </div>
   <div id="bd">
      <div class="yui-t1" id="alfresco-calendar">
         <div id="yui-main">
            <div id="divCalendarContent">
               <@region id="toolbar" scope="template" class="toolbar" />
               <@region id="view" scope="template" class="view" />
            </div>
         </div>
         <div id="divCalendarFilters">
            <@region id="calendar" scope="template" />
            <@region id="tags" scope="template" />
         </div>
      </div>
   </div>
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" />
   </div>
</@>

