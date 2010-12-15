<#import "/components/include/component.ftl" as helper />

<#if ready == false>

	<@helper.unconfigured />
	
<#else>
	
	<embed 
	  src="${url.context}/components/common/jw-player/player.swf" 
	  width="328"
	  height="200"
	  allowscriptaccess="always"
	  allowfullscreen="true"
	  wmode="transparent"
	  flashvars="file=${src}&image=${previewImageUrl}&wmode=transparent&autostart=true"
	/>
	
</#if>
