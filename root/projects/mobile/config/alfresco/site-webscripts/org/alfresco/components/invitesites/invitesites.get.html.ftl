<#include "../../utils.ftl" />
<div id="container">
   <div id="${htmlid}Panel" class="panel selected">
      <@toolbar title="${msg('label.selectSite')}" parentTitle="${page.url.args.site!''}" />
      <div class="content">
        <ul id="My" class="e2e list hilite">
          <#list sites as site>
          <li class="sites"><a id="${msg('label.inviteUser')}" title="${site.title}" href="#invite?site=${site.shortName}" class="panelLink">${site.title}</a></li>
          </#list>
        </ul>
      </div>
   </div>
</div>