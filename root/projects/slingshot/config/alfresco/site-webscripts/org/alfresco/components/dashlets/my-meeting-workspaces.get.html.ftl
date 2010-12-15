<script type="text/javascript">//<![CDATA[
   new Alfresco.dashlet.MyMeetingWorkspaces("${args.htmlid}").setOptions(
   {
      imapEnabled: ${imapServerEnabled?string},
      sites: [
<#if sites??>
   <#assign first=true>
   <#list sites as site>
	  <#if site.sitePreset == "meeting-workspace">
         <#if (!first)>
		    ,
		 <#else>
		    <#assign first=false>
		 </#if>
		 {
		    shortName: '${site.shortName?js_string}',
			title: '${site.title?js_string}',
			description: '${site.description?js_string}',			 
			isFavourite: ${site.isFavourite?string},
			<#if imapServerEnabled>isIMAPFavourite: ${site.isIMAPFavourite?string},</#if>
			isSiteManager: ${site.isSiteManager?string}
		 }
      </#if>
   </#list>
</#if>
      ]
   }).setMessages(
      ${messages}
   );
   new Alfresco.widget.DashletResizer("${args.htmlid}", "${instance.object.id}");
//]]></script>

<div class="dashlet my-meeting-workspaces">
   <div class="title">${msg("header.myMeetingWorkspaces")}</div>
<#if sites??>
   <div id="${args.htmlid}-meeting-workspaces" class="body scrollableList" <#if args.height??>style="height: ${args.height}px;"</#if>>
<#else>
   <div class="body scrollableList" <#if args.height??>style="height: ${args.height}px;"</#if>>
      <div class="detail-list-item first-item last-item">
         <span>${msg("label.noMeetingWorkspaces")}</span>
      </div>
</#if>
   </div>
</div>