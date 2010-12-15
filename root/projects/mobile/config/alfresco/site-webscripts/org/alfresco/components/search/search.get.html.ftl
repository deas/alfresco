<div id="container">
   <div id="${htmlid}Panel" class="panel selected">
     <div class="toolbar">
      <h1>${msg('SearchResults')}</h1>
      <#if (backButton??)>
         <a class="back button">${msg('label.backText')}</a>
      </#if>
      <#if (actionUrl??)>
         <a class="button action" href="${actionUrl}">${msg('label.actionText')}</a>
      </#if>
    </div>
    <div class="content">
       <div id="searchTabs" class="tabs">
           <ul class="tablinks">
             <li><a href="#Content" class="button active">Content</a></li>
             <li><a href="#Sites" class="button">Sites</a></li>
             <li><a href="#People" class="button">People</a></li>
           </ul>
            
           <div class="tabcontent">
             <#if ((numContentResults?number>0))>              
             <div id="Content" class="active">
                   <h2>98 results in All sites <span>1 of 98</span></h2>
                   <ul class="e2e list">
                      <#list contentResults.items as content>
                      <li class="details ${content.type}">
                       <p class="toenail"><a href="${url.context}/proxy/alfresco/${content.doclink}"><img src="${url.context}/themes/${theme}/images/icons/${content.displayType}.png" /></a></p>
                       <h3><a href="${url.context}/proxy/alfresco/${content.doclink}">${content.displayName}</a></h3>
                       <p><span>${msg('label.modifiedBy')}:</span> ${content.modifiedBy}</p>
                       <p><span>${msg('label.modifiedOn')}:</span> ${content.modifiedOn?string("dd MMM yyyy HH:mm")}</p>
                       <p><span>${msg('label.size')}:</span> ${content.size}</p>
                       <a href="#document?nodeRef=${content.nodeRef}" title="${content.displayName}" class="panelLink more"><img src="${url.context}/themes/${theme}/images/30-goarrow2.png" /></a>
                     </li>
                      </#list>
                     </ul>
                     <div id="loadmore">
                        <a class="button white">Load next 25 results</a>
                     </div>
               </div>
                <#else>
                <p id="Content" class="noContent active">${msg('label.noResults')}</p>
                </#if>
                <#if ((numSiteResults?number>0))>                  
               <div id="Sites">
                  <h2>10 results <span>1 of 10</span></h2>
                  <ul class="e2e list">
                  <#list siteResults as site>
                     <li class="details">
                       <p class="toenail"><a href="${url.context}/p/site?site=${site.shortName}"><img src="${url.context}/themes/${theme}/images/64-siteicon.png"/></a></p>
                       <h3><a href="${url.context}/p/site?site=${site.shortName}">${site.title}</a></h3>
                       <p>${site.description}</p>
                       <a href="${url.context}/p/site?site=${site.shortName}" title="${site.title}" class="panelLink more" ><img src="${url.context}/themes/${theme}/images/30-goarrow2.png" /></a>
                     </li>
                  </#list>
                  </ul>
               </div>
               <#else>
                <p id="Sites" class="noContent">${msg('label.noResults')}</p>
               </#if>
               <#if ((numPplResults?number>0))>                                                   
               <div id="People"> 
                     <ul class="e2e list">
                     <#list pplResults.people as person>
                     <li class="details">
                        <#if (person.avatar??)>
                          <p class="toenail"><a href="${url.context}/p/profile?person=${person.userName}"><img src="${url.context}/proxy/alfresco/${person.avatar}"/></a></p>
                        <#else>
                          <p class="toenail"><a href="${url.context}/p/profile?person=${person.userName}"><img src="${url.context}/themes/${theme}/images/no-user-photo-64.png"/></a></p>
                        </#if>
                       <p><a href="${url.context}/p/profile?person=${person.userName}" title="${person.userName!''} "class="panelLink person">${person.firstName} ${person.lastName}</a></p>
                       <p><span>${msg('Title')}:</span> ${person.jobtitle!''}</p>
                       <a href="${url.context}/p/profile?person=${person.userName}" title="${person.userName!''}" class="panelLink more" ><img src="${url.context}/themes/${theme}/images/30-goarrow2.png" /></a>
                     </li>
                     </#list>
                  </ul>
             </div>
            <#else>
             <p id="People" class="noContent">${msg('label.noResults')}</p>
            </#if>   
           </div>
         </div>
    </div>
   </div>
</div>