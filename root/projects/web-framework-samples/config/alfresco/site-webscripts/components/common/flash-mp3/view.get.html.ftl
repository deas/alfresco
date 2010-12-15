<#import "/components/include/component.ftl" as helper />

<#if ready == false>

	<@helper.unconfigured />
	
<#else>

	<#if appearance == "slim">
	
		<object classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000" codebase="http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=7,0,0,0" width="400" height="15" id="xspf_player" align="middle">
			<param name="allowScriptAccess" value="sameDomain" />
			
			<param name="song_url" value="${src}" />
			<param name="song_title" value="${songTitle}" />
			
			<param name="quality" value="high" />
			<param name="bgcolor" value="#e6e6e6" />
			
			<param name="autoplay" value="true" />
			
			<param name="wmode" value="transparent" />
			
			<embed src="${url.context}/components/common/flash-mp3/xspf_player_slim.swf?autoplay=true&song_title=${songTitle}&song_url=${src}&wmode=transparent" quality="high" bgcolor="#e6e6e6" width="400" height="15" wmode="transparent"  name="xspf_player" align="middle" allowScriptAccess="sameDomain" type="application/x-shockwave-flash" pluginspage="http://www.macromedia.com/go/getflashplayer" />
		</object>
	
	</#if>	
	
	<#if appearance == "full">	
	
		<object classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000" codebase="http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=7,0,0,0" width="400" height="170" id="xspf_player" align="middle">
			<param name="allowScriptAccess" value="sameDomain" />
			
			<param name="song_url" value="${src}" />
			<param name="song_title" value="${songTitle}" />
			
			<param name="quality" value="high" />	
			<param name="bgcolor" value="#e6e6e6" />
			
			<param name="autoplay" value="true" />
			
			<param name="wmode" value="transparent" />
			
			<embed src="${url.context}/components/common/flash-mp3/xspf_player.swf?autoplay=true&song_title=${songTitle}&song_url=${src}&wmode=transparent" quality="high" bgcolor="#e6e6e6" wmode="transparent" width="400" height="170" name="xspf_player" align="middle" allowScriptAccess="sameDomain" type="application/x-shockwave-flash" pluginspage="http://www.macromedia.com/go/getflashplayer" />
		</object>
		
	</#if>

</#if>
