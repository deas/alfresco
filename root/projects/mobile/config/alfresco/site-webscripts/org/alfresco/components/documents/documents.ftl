<#macro panelContent>
<div id="documentsTabs" class="tabs">
   <ul class="tablinks">
      <li><a href="#My" class="button">${msg('label.my')}</a></li>
      <li><a href="#RMod" class="button active">${msg('label.recentlyModified')}</a></li>
      <li><a href="#All" class="button">${msg('label.all')}</a></li>
   </ul>
   <div class="tabcontent">
      <#if (allDocs?size!=0)>
      <div id="RMod"  class="active">
         <ul class="list">
            <li>
               <h2>${msg('label.today')}</h2>
               <ul class="e2e">
                  <#list recentDocs as doc >
                  <li class="details ${doc.type}">
                     <p class="toenail"><a href="${url.context}/proxy/alfresco/${doc.contentUrl}"><img src="${url.context}/themes/${theme}/images/icons/${doc.type}.png" width="45" height="62"/></a></p>
                     <h3><a href="${url.context}/proxy/alfresco/${doc.contentUrl}">${doc.displayName?html}</a></h3>
                     <p><span>${msg('label.modifiedBy')}:</span> ${doc.modifiedBy}</p>
                     <p><span>${msg('label.modifiedOn')}:</span> ${doc.modifiedOn?string("dd MMM yyyy HH:mm")}</p>
                     <p><span>${msg('label.size')}:</span> ${doc.size} kb</p>
                     <a id="rDocs-${doc.domId}" title="${doc.displayName?html}" href="#document?nodeRef=${doc.nodeRef}" class="panelLink more"><img src="${url.context}/themes/${theme}/images/30-goarrow2.png" /></a>
                  </li>
                  </#list>
               </ul>
            </li>
            <#-- <li>
               <h2>${msg('Yesterday')}</h2>
               <ul>
               </ul>
            </li> -->
         </ul>
      </div>
      <#else>
         <p id="RMod" class="noContent">${msg('label.noResults')}</p>
      </#if>
      <#if (allDocs?size!=0)>
      <div id="My">
        <ul class="list">
          <li>
            <h2>${msg('label.myFavorites')}</h2>
            <ul class="e2e">
              <#list myDocs as doc >
              <li class="details ${doc.type}">
                <p class="toenail"><a href="${url.context}/proxy/alfresco/${doc.contentUrl}"><img src="${url.context}/themes/${theme}/images/icons/${doc.type}.png" width="45" height="62"/></a></p>
                <h3><a href="${url.context}/proxy/alfresco/${doc.contentUrl}">${doc.displayName?html}</a></h3>
                <p><span>${msg('label.modifiedBy')}:</span> ${doc.modifiedBy}</p>
                <p><span>${msg('label.modifiedOn')}:</span> ${doc.modifiedOn?string("dd MMM yyyy HH:mm")}</p>
                <p><span>${msg('label.size')}:</span> ${doc.size} kb</p>
                <a id="myDocs-${doc.domId}" title="${doc.displayName?html}" href="#document?nodeRef=${doc.nodeRef}" class="panelLink more"><img src="${url.context}/themes/${theme}/images/30-goarrow2.png" /></a>
              </li>
              </#list>
            </ul>
          </li>
        </ul>
      </div>
      <#else>
         <p id="My" class="noContent">${msg('label.noResults')}</p>
      </#if>
      <#if (allDocs?size!=0)>
      <div id="All">
        <ul class="list">
          <li>
            <h2>${msg('label.allDocuments')}</h2>
            <ul class="e2e">
              <#list allDocs as doc >
                <li class="details ${doc.type}">
                 <p class="toenail"><a href="${url.context}/proxy/alfresco/api/node/content/${doc.nodeRef?replace(':/','')}"><img src="${url.context}/themes/${theme}/images/icons/${doc.type}.png" width="45" height="62"/></a></p>
                 <h3><a href="${url.context}/proxy/alfresco/${doc.contentUrl}">${doc.displayName}</a></h3>
                 <p><span>${msg('label.modifiedBy')}:</span> ${doc.modifiedBy}</p>
                 <p><span>${msg('label.modifiedOn')}:</span> ${doc.modifiedOn?string("dd MMM yyyy HH:mm")}</p>
                 <p><span>${msg('label.size')}:</span> ${doc.size} kb</p>
                 <a id="allDocs-${doc.domId}" title="${doc.displayName?html}" href="#document?nodeRef=${doc.nodeRef}" class="panelLink more"><img src="${url.context}/themes/${theme}/images/30-goarrow2.png" /></a>
                </li>
              </#list>
            </ul>
          </li>
        </ul>
      </div>
      <#else>
         <p id="All" class="noContent">${msg('label.noResults')}</p>
      </#if>
      <#-- Not Implemented
      <ul class="rr list">
         <li class="allfolders"><a class="disabled">${msg('label.allFolders')}</a></li>
      </ul>
      -->
   </div>
</div>
</#macro>