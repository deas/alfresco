<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/templates/invite/invite.css" />
</@>

<@templateBody>
   <div id="alf-hd">
      <@region id="header" scope="global" protected=true />
      <@region id="title" scope="template" protected=true />
      <@region id="navigation" scope="template" protected=true />
   </div>
   
   <div id="bd">
      <@region id="membersbar" scope="template" protected=true />
      <div class="yui-g grid">
         <div class="yui-u first column1">
            <div class="yui-b">
               <@region id="people-finder" scope="template" protected=true />
            </div>
            <div class="yui-b">
               <@region id="addemail" scope="template" protected=true />
            </div>
         </div>
         <div class="yui-u column2">
            <div class="yui-b">
               <@region id="invitationlist" scope="template" protected=true />
            </div>
         </div>
      </div>
   </div>
   <br/>
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>