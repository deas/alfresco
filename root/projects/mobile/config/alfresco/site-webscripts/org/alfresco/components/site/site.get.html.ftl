<#include "../../utils.ftl" />
<div id="container">
   <div id="${htmlid}Panel" class="panel selected">
      <@toolbar title="${site.title}" />
      <div class="content">   
         <div class="rr details">
            <img src="${url.context}/themes/${theme}/images/64-siteicon.png" width="64" height="64" ><!-- replace with background img-->
            <div>
               <h2>${site.title}</h2>
               <p>${site.description?html}</p>
            </div>
            <#-- Not Implemented
            <a href="#TODO" class="more"><img src="${url.context}/themes/${theme}/images/30-goarrow2.png" /></a>
            -->
         </div>
         <ul class="nav list">
           <li>
             <h2>${msg('label.documentLibrary')}</h2>
             <ul class="rr">
               <li class="documents"><a id="Documents" title="${msg('label.recentlyModified')}" href="#documents?site=${page.url.args.site}#RMod" class="panelLink">${msg('label.recentlyModified')}</a></li>
               <li class="mydocuments"><a id="My-Documents" title="${msg('label.myFavorites')}" href="#documents?site=${page.url.args.site}#My" class="panelLink">${msg('label.myFavorites')}</a></li>
               <li class="alldocuments"><a id="All-Documents" title="${msg('label.allDocuments')}" href="#documents?site=${page.url.args.site}#All" class="panelLink" >${msg('label.allDocuments')}</a></li>
               <#-- Not Implemented
               <li class="allfolders"><a id="All-Folders" class="disabled">${msg('label.allFolders')}</a></li>
               -->
             </ul>
           </li>
           <#--  Not Implemented
           <li>
             <h2>${msg('label.wiki')}</h2>
             <ul id="my" class="rr">

               <li class="mainwikipage"><a id="Main-Page" href="#wiki" class="disabled">${msg('label.mainPage')}</a></li>
               <li class="wikipagelist"><a id="Wiki-Page-List" class="disabled">${msg('label.wikiPageList')}</a></li>
               <li class="newwikipage"><a id="New-Wiki-Page" class="disabled" href="#newwikipage">${msg('label.newWikiPage')}</a></li>
             </ul>
           </li>
           -->
           <li>
             <h2>${msg('label.siteActions')}</h2>
             <ul id="my" class="rr">
               <li class="invitetosite"><a id="Invite-To-Site" title="${msg('label.inviteToSite')}" href="#${url.context}/p/invite?site=${site.shortName}" class="panelLink">${msg('label.inviteToSite')}</a></li>
               <#-- Not Implemented
               <li class="leavesite"><a id="Leave-Site" class="disabled">${msg('label.leaveSite')}</a></li>
               -->
             </ul>
           </li>
         </ul>
      </div>
   </div>
</div>