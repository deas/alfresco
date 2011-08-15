<#-- HTML template for the Social Publishing Dialogue -->
<#assign el=args.htmlid?html>
<#if (publishChannels?size > 0)>
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
					<p class="channel-info">${msg("socialPublish.dialogue.channel.info")}</p>
					
					<div class="channel-select-button">
	               <span class="yui-button yui-menu-button" id="${el}-channel-select-button">
	                  <span class="first-child"><button>${msg("socialPublish.button.channel")}</button></span>
	               </span>
	            </div>
					<div id="${el}-publishChannel-menu" class="yuimenu menu-with-icons" style="visibility: hidden;">
	               <div class="bd">
	                  <ul id="${el}-publishChannels" class="channelTypes">
	                     <#list publishChannels as publishChannel>
	                     <li>
	                        <span>
	                           <a href="#" class="publishChannel" rel="${publishChannel.id?html}">
	                           	<img src="${url.context}/proxy/alfresco/${publishChannel.channelType.icon?html}/20" alt="${publishChannel.title?html}" />
	                              <span>
	                              	${publishChannel.title?html}
											</span>
	                           </a>
	                        </span>
	                     </li>
	                     </#list>
	                  </ul>
	               </div>
	            </div>
					
					
	         </div>
	         <#if (statusUpdateChannels?size > 0) >
					<div id="${el}-statusUpdate-div" class="formsection">
		            <h3 class="channel-header">${msg("socialPublish.dialogue.statusUpdate.title")}</h3>
						<div class="yui-gd">
		               <div class="yui-u first">
		                  <div class="align-right flat-button statusSelect-container">
				               <!-- Select All/None Dropdown Button -->
				               <div class="statusSelect-button">
				                  <span class="yui-button yui-menu-button" id="${el}-statusSelect-button">
				                     <span class="first-child"><button>${msg("socialPublish.button.select")}</button></span>
				                  </span>
				               </div>
				               <div id="${el}-statusSelect-menu" class="yuimenu menu-with-icons" style="visibility: hidden; position:absolute;">
				                  <div class="bd">
				                     <ul id="${el}-statusSelectActions" class="statusSelectActions">
				                        <li id="${el}-statusSelectActionAll">
				                           <span>
				                              <a href="#" class="statusSelectAction">
				                                 ${msg("socialPublish.button.select.all")}
				                              </a>
				                           </span>
				                        </li>
				                        <li id="${el}-statusSelectActionNone">
				                           <span>
				                              <a href="#" class="statusSelectActions">
				                                 ${msg("socialPublish.button.select.none")}
				                              </a>
				                           </span>
				                        </li>
				                     </ul>
				                  </div>
				               </div>
				            </div>
								<p class="statusUpdate-info">${msg("socialPublish.dialogue.statusUpdate.info")}</p>
		                  <div class="statusUpdate-checkboxes">
		                     <#assign count = 0>
		                     <#list statusUpdateChannels as statusUpdateChannel>
		                        <#assign count = count + 1>
		                        <div class="status-channel">
		                           <input class="statusUpdate-checkboxes" type="checkbox" name="${statusUpdateChannel.channelType.title?html}" value="${statusUpdateChannel.id?html}" id="${el}-statusUpdate-checkbox-channel-${count}" rel="${statusUpdateChannel.channelType.maxStatusLength?html}"/>
		                           <label title="${statusUpdateChannel.title?html}" for="${el}-statusUpdate-checkbox-channel-${count}"><img src="${url.context}/proxy/alfresco/${statusUpdateChannel.channelType.icon?html}/20" width="20" height="20"/>${statusUpdateChannel.title?html}</label>
		                        </div>
		                     </#list>
		                  </div>
								<div id="${el}-statusUpdate-channel-count" class="statusCheckboxes-count">${msg("socialPublish.dialogue.statusUpdate.select.count", "0", count)}</div>
						   </div>
							<div class="yui-u update-container">    
		                  <div class="yui-ge">
									<div class="yui-u first statusUpdate-message-container">
										<p class="statusUpdate-message-info">${msg("socialPublish.dialogue.statusUpdate.message")}</p>
				                  <textarea id="${el}-statusUpdate-text" disabled="disabled"></textarea>
				                  <div id="${el}-statusUpdate-includeURL" class="includeURL-container">
				                     <input type="checkbox" name="includeURL" id="${el}-statusUpdate-checkbox-url" checked="checked" rel="${urlLength}" disabled="disabled" />
				                     <label for="${el}-statusUpdate-checkbox-url">${msg("socialPublish.dialogue.statusUpdate.includeURL")}</label>
				                  </div>
									</div>
									<div class="yui-u statusUpdate-count-container">
										<div id="${el}-statusUpdate-count" class="status-count"></div>
										<div id="${el}-statusUpdate-countMessage" class="status-count-message"></div>
										<div id="${el}-statusUpdate-count-urlMessage" class="statusUpdate-count-urlMessage"></div>
		                     </div>
								</div>
					      </div>
						</div>
		         </div>
				</#if>
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
</#if>