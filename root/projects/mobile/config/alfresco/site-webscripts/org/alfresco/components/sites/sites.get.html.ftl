<#include "../../utils.ftl" />
<div id="container">
   <div id="${htmlid}Panel" class="panel selected">
      <@toolbar title="${msg('label.sites')}" />
      <div class="content">
         <div id="sitesTabs" class="tabs">
            <ul class="tablinks">
              <li><a href="#Fav" class="button active">${msg('label.favorites')}</a></li>
              <li><a href="#My" class="button">${msg('label.mySites')}</a></li>
              <li><a href="#All" class="button">${msg('label.all')}</a></li>
            </ul>
            <div class="tabcontent">
              <#if (favSites?size!=0)>
              <ul id="Fav" class="e2e list active hilite">
                <#list favSites as site>
                <li class="fav"><a id="${site.title?replace(' ','-')}" title="${site.title?html}" href="#site?site=${site.shortName}" class="panelLink">${site.title?html}</a></li>
                </#list>
              </ul>
              <#else>
              <p id="Fav" class="noContent">${msg('label.noFavs')}</p>
              </#if>
              <#if (sites?size!=0)>              
              <ul id="My" class="e2e list hilite">
                <#list sites as site>
                <li class="sites"><a id="${site.title?replace(' ','-')}" title="${site.title?html}" href="#site?site=${site.shortName}" class="panelLink">${site.title?html}</a></li>
                </#list>
              </ul>
              <#else>
              <p id="My" class="noContent">${msg('label.noSites')}</p>
              </#if>
              <#if (allSites?size!=0)>              
              <ul id="All" class="e2e list hilite">
                <#list allSites as site>
                <li class="sites"><a id="${site.title?replace(' ','-')}" title="${site.title?html}"  href="#site?site=${site.shortName}" class="panelLink">${site.title?html}</a></li>
                </#list>
              </ul>
              <#else>
              <p  id="All" class="noContent">${msg('label.noSites')}</p>
              </#if>              
            </div>
         </div>
      </div>
   </div>
</div>