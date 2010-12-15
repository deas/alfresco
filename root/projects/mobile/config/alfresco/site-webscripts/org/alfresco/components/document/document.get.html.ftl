<#include "../../utils.ftl" />
<div id="container">
   <div id="${htmlid}Panel" class="panel selected">
      <@toolbar title="${doc.title}" parentTitle="${siteTitle}" />
    <div class="content">
       <div class="panelBar">
            <a class="button" href="${url.context}/p/workflow?nodeRef=${doc.nodeRef}&site=${doc.location.site}">${msg('label.assignWorkflow')}</a>
            <#-- Not Implemented
            <form action="${url.context}/p/document" method="post">
               <input type="submit" name="delete" value="${msg('Delete')}" id="delete" class="button delete">
            </form>
            -->
          </div>
          <a class="preview" href="${url.context}/proxy/alfresco/${doc.contentUrl}"><img src="${url.context}/proxy/alfresco/${doc.thumbnailUrl}" width="77" height="100" /></a>
          <ul class="rr info">
              <li><span>${msg('label.name')}:</span> ${doc.displayName}</li>
              <li><span>${msg('label.description')}:</span> ${doc.description}</li>
              <li><span>${msg('label.size')}:</span> ${doc.size} Kb</li> <!-- todo kb text fix-->
              <li><span>${msg('label.type')}:</span> ${doc.type}</li>
              <li><span>${msg('label.tags')}:</span> ${doc.tags}</li>              
          </ul>
      </div>
   </div>
</div>