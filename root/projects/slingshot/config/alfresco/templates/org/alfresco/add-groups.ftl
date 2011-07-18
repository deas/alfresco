<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/templates/invite/add-groups.css" />
</@>

<@templateBody>
   <div id="alf-hd">
      <@region id="header" scope="global" />
      <@region id="title" scope="template" />
      <@region id="navigation" scope="template" />
   </div>
   
   <div id="bd">
      <@region id="membersbar" scope="template" />
      <!-- start component code -->
      <div class="yui-g grid">
         <div class="yui-u first column1">
            <div class="yui-b">
               <@region id="group-finder" scope="template" />
            </div>
         </div>
         <div class="yui-u column2">
            <div class="yui-b">
               <@region id="groupslist" scope="template" />
            </div>
         </div>
      </div>
      <!-- end component code -->
   </div>
   <br />
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" />
   </div>
</@>