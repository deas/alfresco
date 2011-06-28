<#-- HTML template for the Social Publishing Dialogue -->
<#assign el=args.htmlid?html>
<div id="${el}-dialog" class="social-publishing">
   <div class="hd">
      <span id="${el}-header-span" class="social-publishing-header"></span>
   </div>
   <div class="bd">
      
      <form id="${el}-publish-form" method="POST"
            action="${url.context}/proxy/alfresco/api/publishing">
         <input type="hidden" id="${el}-nodeRef-hidden" name="nodeRef" value=""/>

         <div id="${el}-channelSection-div" class="formsection">
            <h3 class="channel-header">${msg("socialPublish.dialogue.channel.title")}</h3>
            <select id="${el}-channel-select">
            	<#list publishChannels as publishChannel>
                  <option value="${publishChannel.name?html}" rel="${publishChannel.channelType.title?html}"><img src="${publishChannel.channelType.icon?html}" width="16" height="16"/>${publishChannel.title?html}</option>
					</#list>
				</select>
         </div>
         <div id="${el}-statusUpdate-div" class="formsection">
            <h3 class="status-header">${msg("socialPublish.dialogue.statusUpdate.title")}</h3>
            
				<div id="${el}-statusUpdate-count" class="status-count"></div>
		   	<textarea id="${el}-statusUpdate-text"></textarea>
				<div id="${el}-statusUpdate-lengths">
				   <label for="${el}-statusUpdate-text">message lengths</label>	
				</div>
            <div id="${el}-statusUpdate-includeURL">
               <input type="checkbox" name="includeURL" id="${el}-statusUpdate-checkbox-url" checked="checked" rel="${urlLength}" />
               <label for="${el}-statusUpdate-checkbox-url">${msg("socialPublish.dialogue.statusUpdate.includeURL", urlLength)}</label>
            </div>
				            
				<div class="statusUpdate-checkboxes">
					<h3>${msg("socialPublish.dialogue.statusUpdate.select")}</h3>
					<#list statusUpdateChannels as statusUpdateChannel>
						<div class="status-channel">
	   				   <input class="statusUpdate-checkboxes" type="checkbox" name="" value="${statusUpdateChannel.name?html}" id="${el}-statusUpdate-checkbox-channel-${statusUpdateChannel.name?html}"/>
		     			   <label for="${el}-statusUpdate-checkbox-channel-${statusUpdateChannel.name?html}"><img src="${statusUpdateChannel.channelType.icon?html}" width="16" height="16"/>${statusUpdateChannel.title?html}</label>
				   	</div>
               </#list>
				</div>
         </div>
         <div class="bdft">
		      <input id="${el}-publish-button" type="button" value="${msg("socialPublish.button.publish")}" />
		      <input id="${el}-cancel-button" type="button" value="${msg("socialPublish.button.cancel")}" />
         </div>

      </form>
   </div>
</div>

<script type="text/javascript">//<![CDATA[
Alfresco.util.addMessages(${messages}, "Alfresco.module.socialPublishing");
//]]></script>
