<#macro render>

<style>
/* Main Container elements */
#collabContainer {
   width: 100%;
}

.collabHeader {
   background: url(${url.context}/images/common/parts/collab_topleft.png) no-repeat left top;
   margin: 0px;
   padding: 0px 0px 0px 2px;
}
.collabHeader span {
   background: url(${url.context}/images/common/parts/collab_topright.png) no-repeat right top;
   display: block;
   float: none;
   padding: 5px 15px 4px 6px;
   font-weight: bold;
   font-size: 10pt;
}

.collabContent {
   padding: 8px;
}

.collabFooter {
   background: url(${url.context}/images/common/parts/collab_bottomleft.png) no-repeat left top;
   margin: 0px;
   padding: 0px 0px 0px 4px;
}

.collabFooter span {
   background: url(${url.context}/images/common/parts/collab_bottomright.png) no-repeat right top;
   display: block;
   float: none;
   padding: 5px 15px 4px 6px;
}

div.collabRowOdd {
   padding: 4px 2px;
}

div.collabRowEven {
   padding: 4px 2px;
   background-color: #F1F7FD;
}

span.metaTitle {
   font-size: 11px;
   color: #666677;
}

span.metaData {
   font-size: 11px;
   color: #515D6B;
}

a.collabNodeLink, a.collabNodeLink:hover {
   font-size: 12px;
   font-weight: bold;
}

div.collabNodeActions {
   float: right;
   padding: 0px 4px;
}
</style>

<div id="collabContainer">

	<div class="collabHeader">
		<span>${container.title}</span>
	</div>

	<table width="100%" cellpadding="0" cellspacing="0">

		<tr valign="top">

			<td width="1" style="background-color:#b9bec4;"></td>
			<td>

				<div class="collabContent">

				<#assign count=0>
				<#list container.children as c>
					<#assign count=count+1>
					
					<#if c.linkUrl?exists>
						<#assign curl="${c.linkUrl}">
					</#if>
					
					<#assign iconUrl="${c.iconUrl}">
					<#assign detailsUrl="#">
					<#assign detailsIconUrl="${url.context}/images/common/icons/view_properties.gif">
					
					<div class="collab${(count%2=0)?string("RowEven", "RowOdd")}">
					
						<table width="100%" cellpadding="2" cellspacing="2" border="0">
						<tr>
							<td valign="top" nowrap>
								<#if curl?exists>
									<a href="${curl}" target="new">
										<img src="${iconUrl}" border="0" alt="${c.title?html}" title="${c.title?html}">
									</a>
								<#else>
									<img src="${iconUrl}" border="0" alt="${c.title?html}" title="${c.title?html}">
								</#if>
							</td>
							<td valign="top" width="100%" style="padding-left: 3px">
							
								<#if curl?exists>
									<a class="collabNodeLink" href="${curl}" target="new">${c.title?html}</a>
								<#else>
									${c.title?html}
								</#if>
								<span class="metaData"><#if c.description?exists>${c.description?html}</#if></span>
								<br/>
								<span class="metaTitle">Modified:</span>&nbsp;<span class="metaData">${c.modified}</span>&nbsp;
								<br/>
								<span class="metaTitle">Modified&nbsp;By:</span>&nbsp;<span class="metaData">${c.modifier}</span>
								<br/>
								<#if c.size?exists>
									<span class="metaTitle">Size:</span>&nbsp;<span class="metaData">${c.size}&nbsp;bytes</span>
									<br/>
								</#if>
							</td>
							<td valign="top" nowrap>
								<a href="${detailsUrl}" alt="Details" title="Details"><img src="${detailsIconUrl}" border="0"></a>
								</div>

							</td>
						</tr>
						</table>
						
					</div>
				</#list>
				
				</div>
			</td>
	      		<td width="1" style="background-color:#b9bec4;"></td>
	   	</tr>
	</table>

   <div class="collabFooter">
      <span>&nbsp;</span>
   </div>
</div>

</#macro>
