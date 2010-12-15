<script type="text/javascript" charset="utf-8">
<#if (inviteUserResult?string='true')>
   var inviteResult = '${msg("label.inviteSuccessful")}';
<#else>
   var inviteResult = '${msg("label.inviteUnsuccessful")}';
</#if>
   App.addMessage(inviteResult);
   window.addEventListener('DOMContentLoaded',function(){
      App.initBehaviour(Mobile.util.TabPanel.BEHAVIOUR_NAME);
      App.initBehaviour(Mobile.util.Panel.BEHAVIOUR_NAME);
      App.initBehaviour('Edge2EdgeListAction');
      App.showMessage();
   });
</script>
<div id="container">
   <div id="homePanel" class="panel selected">
      <div class="toolbar">
         <h1>${msg('label.home')}</h1>
      </div>
      <div class="content">
         <ul class="nav list">
            <li>
              <h2>${msg('label.myAlfresco')}</h2>
              <ul id="my" class="rr hilite">
              <#-- Not Implemented
                <li class="recentdocs"><a id="Recent-Documents"  class="disabled">${msg('label.myDocuments')}</a></li>
                <li class="recentactivity"><a id="Recent-Activity" class="disabled">${msg('label.myActivity')}</a></li>
              -->
                <li class="fav"><a id="Favourite-Sites" href="#sites#Fav" title="${msg('label.myFavoriteSites')}" class="panelLink">${msg('label.myFavoriteSites')}</a></li>
                <li class="sites"><a id="My-Sites" href="#sites#My" title="${msg('label.mySites')}" class="panelLink">${msg('label.mySites')}</a></li>
                <li class="all"><a id="Tasks-All" href="#tasks#All" title="${msg('label.allTasks')}" class="panelLink">${msg('label.allTasks')}</a></li>
              </ul>
            </li>
            <li>
              <h2>${msg('label.today')}</h2>
              <ul class="rr hilite">
                <li class="tasks"><a id="Tasks" href="#tasks#Today" title="${msg('label.todayTasks')}" class="panelLink">${msg('label.todayTasks')} <span>(${numTodaysTasks})</span></a>
                   <#if (numOverdueTasks>0)>
                     <p><a href="#tasks#Overdue" class="panelLink disabled">${msg('label.overdueTasks')} <span>(${numOverdueTasks})</span></a></p>
                   </#if>
                </li>
                <#-- Not Implemented
                <li class="events"><a class="disabled">${msg('label.events')} <span>(${numEvents})</span></a></li>
                -->
              </ul>
            </li>
            <#-- Not Implemented
            <li>
              <ul class="rr hilite">
                 <li class="siteactivities"><a class="disabled">${msg('label.siteActivities')}</a></li>
              </ul>
            </li>
            -->
            <li>
              <h2>${msg('Quick Actions')}</h2>

              <ul class="rr hilite">
              <#-- Not Implemented
                 <li class="newwikipage"><a class="disabled" href="#sites">${msg('label.newWikiPage')}</a></li>
              -->
                 <li class="publicsites"><a id="Public-Sites" href="#sites#All" title="${msg('label.browsePublicSites')}" class="panelLink">${msg('label.browsePublicSites')}</a></li>
                 <li class="invitetosite"><a id="Select-Site" href="#invitesites" title="${msg('label.inviteToSite')}" class="panelLink">${msg('label.inviteToSite')}</a></li>
              </ul>
            </li>
         </ul>
      </div>
   </div>
</div>