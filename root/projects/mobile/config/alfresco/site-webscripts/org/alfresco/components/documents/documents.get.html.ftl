<#include "../../utils.ftl" />
<#include "documents.ftl" />
<div id="container">
   <div id="${htmlid}Panel" class="panel selected">
      <@toolbar title="${msg('label.documents')}" parentTitle="${page.url.args.site}" />
      <div class="content">
         <@panelContent/>
      </div>
   </div>
</div>