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

<#assign maxcount = 4>

<div id="collabContainer">

	<div class="collabHeader">
		<span>${container.title}</span>
	</div>

	<div class="collabContent">

		<table border="0">
		<tr>
		
		<#list container.children as c>
		
			<#assign curl="">
			<#if c.linkUrl?exists>
				<#assign curl="${c.linkUrl}">
			</#if>
			
			<#assign iconUrl="${c.iconUrl}">
			<#assign detailsUrl="#">
			<#assign detailsIconUrl="${url.context}/images/common/icons/view_properties.gif">
		
			<#if c_index < maxcount>

				<td>
					<div class="collab${(c_index%2=0)?string("RowEven", "RowOdd")}">
						<div style="float:left">
							<a href="${curl}" target="new"><img src="${iconUrl}" border="0" alt="${c.title?html}" title="${c.title?html}"></a>
						</div>
						<div style="margin-left:36px;padding: 4px 0px 4px 0px">
							<div>
								<div class="collabNodeActions">
									<a href="${detailsUrl}" alt="Details" title="Details"><img src="${detailsIconUrl}" border="0"></a>
								</div>
								<a class="collabNodeLink" href="${curl}" target="new">${c.title?html}</a>
							</div>
							<div>
								<span class="metaData"><#if c.description?exists>${c.description?html}</#if></span>
							</div>
							<div>
								<span class="metaTitle">Modified:</span>&nbsp;<span class="metaData">${c.modified}</span>&nbsp;
								<span class="metaTitle">Modified&nbsp;By:</span>&nbsp;<span class="metaData">${c.modifier}</span>
								<#if c.size?exists>
									<span class="metaTitle">Size:</span>&nbsp;<span class="metaData">${c.size}&nbsp;bytes</span>
								</#if>
							</div>
						</div>
					</div>
				</td>

				<#if c_index == maxcount>
					<#break>
				</#if> 

				<#if ((c_index + 1) % 2) == 0>
					</tr>
					<tr>
				</#if>
			
			</#if>

		</#list>
		
		</tr>
		</table>

	</div>




	<div class="collabFooter">
		<span>&nbsp;</span>
	</div>
</div>

</#macro>
