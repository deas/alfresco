<#import "/components/include/component.ftl" as helper />

<#if ready == false>

	<@helper.unconfigured />
	<br/>
	<#if src?exists>${src}</#if>

<#else>

    <#if isAudio>

	<#if useQuicktime>
	
		<object classid="clsid:02BF25D5-8C17-4B23-BC80-D3488ABDDC6B" codebase="http://www.apple.com/qtactivex/qtplugin.cab" width="200" height="16">
			<param name="src" value="${src}" />
			<param name="controller" value="true" />
			<param name="autoplay" value="true" />
			<param name="autostart" value="1" />
			<param name="pluginspage" value="http://www.apple.com/quicktime/download/" />

			<!--[if !IE]> <-->

			<object type="${mimetype}" data="${src}" width="200" height="16">
				<param name="src" value="${src}" />
				<param name="controller" value="true" />
				<param name="autoplay" value="true" />
				<param name="autostart" value="1" />
				<param name="pluginurl" value="http://www.apple.com/quicktime/download/" />

				<embed src="${src}" type="${mimetype}" width="200" height="16" autostart="false" controller="true" ></embed>

			</object>

			<!--> <![endif]-->

		</object>
	
	</#if>
	
	<#if useWindowsMedia>

		<object classid="6BF52A52-394A-11d3-B153-00C04F79FAA6" codebase="http://activex.microsoft.com/activex/controls/mplayer/en/nsmp2inf.cab#Version=6,0,02,902" width="200" height="16">
			<param name="src" value="${src}" />
			<param name="controller" value="true" />
			<param name="autoplay" value="true" />
			<param name="autostart" value="1" />

			<!--[if !IE]> <-->

			<object type="${mimetype}" data="${src}" width="200" height="16">
				<param name="src" value="${src}" />
				<param name="controller" value="true" />
				<param name="autoplay" value="true" />
				<param name="autostart" value="1" />

				<embed src="${src}" type="${mimetype}" width="200" height="16" autostart="false" controller="true" ></embed>

			</object>

			<!--> <![endif]-->

		</object>
	
	</#if>
	
	<#if useReal>

		<object id=RVOCX classid="clsid:CFCDAA03-8BE4-11CF-B84B-0020AFBBCCFA" width="200" height="16">
			<param name="src" value="${src}" />
			<param name="autoplay" value="true" />
			<param name="autostart" value="1" />
			<param name="controls" value="ControlPanel" />
			
			<!--[if !IE]> <-->

			<object type="${mimetype}" data="${src}" width="200" height="16">
				<param name="src" value="${src}" />
				<param name="controller" value="true" />
				<param name="autoplay" value="true" />
				<param name="autostart" value="1" />

				<embed src="${src}" type="${mimetype}" width="200" height="16" autostart="false" controller="true" ></embed>

			</object>

			<!--> <![endif]-->

		</object>
	
	</#if>
	
	<#if useShockwave>

		<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=8,0,22,0" width="200" height="16">
			<param name="src" value="${src}" />
			<param name="controller" value="true" />
			<param name="autoplay" value="true" />
			<param name="autostart" value="1" />

			<!--[if !IE]> <-->

			<object type="${mimetype}" data="${src}" width="200" height="16">
				<param name="src" value="${src}" />
				<param name="controller" value="true" />
				<param name="autoplay" value="true" />
				<param name="autostart" value="1" />

				<embed src="${src}" type="${mimetype}" width="200" height="16" autostart="false" controller="true" ></embed>

			</object>

			<!--> <![endif]-->

		</object>
	
	</#if>

    <#else>
    
	${instance.id}
	<br/>
	An appropriate audio player has not been configured

    </#if>

</#if>
