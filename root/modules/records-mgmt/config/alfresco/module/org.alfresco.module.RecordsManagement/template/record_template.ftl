<#-- Records Report - Template to apply to a records space to report on status of records -->

<style>
body {font:small/1.2em arial,helvetica,clean,sans-serif;font:x-small;margin-top: 10px; margin-right: 10px; margin-bottom: 0px; margin-left: 10px;min-width:500px;}
</style>

<#assign datetimeformat="dd MMM yyyy HH:mm">

<#assign currentpath="+PATH:\"${space.qnamePath}//*\"">
<#assign rma="{http://www.alfresco.org/model/record/1.0}">

<#macro debug errcat="" errmsg="">
    <#-- <tr><th colspan="2">${errcat}</th><td colspan="8">${errmsg}</td></tr> -->
</#macro> 

<table width="100%"  border="0" cellpadding="1" cellspacing="1">

<#if hasAspect(document, "${rma}record") = 1>
	<tr>
		<td><b>Record - ${document.properties["rma:recordIdentifier"]} <#if hasAspect(document, "${rma}cutoff") = 1>&nbsp;&nbsp;<font style='color:red'>Cutoff</font></#if></b></td>
	</tr>
	<tr>		
		<td><hr></td>
	</tr>

	<#if hasAspect(document, "${rma}vitalrecord") = 1>
		<tr>
	        <td>
		        <#if document.properties["rma:nextReviewDate"]?exists>
		        	Vital record due for next review at ${document.properties["rma:nextReviewDate"]?string("dd MMM yyyy HH:mm")}
		        <#else>
		        	Vital record with no review date set.
		        </#if>
	        </td>		
		</tr>
	</#if>
	
	<#if hasAspect(document, "${rma}scheduledCutoff") = 1 && hasAspect(document, "${rma}cutoff") = 0>
		<tr>		
			<td>
				<#if document.properties["rma:cutoffDateTime"]?exists>				
					Scheduled for cutoff at ${document.properties["rma:cutoffDateTime"]?string("dd MMM yyyy HH:mm")}
				<#else>
					Scheduled for cutoff with no cut off date set.
				</#if>
			</td>		
		</tr>	
	</#if>		

	<#if hasAspect(document, "${rma}held") = 1>
		<tr><td>
			<#if document.properties["rma:frozen"]>
			    This record is frozen
			<#else>	
				<#if document.properties["rma:holdUntil"]?exists>
					Held until ${document.properties["rma:holdUntil"]?string("dd MMM yyyy HH:mm")}
				<#else>
					Held with no held date specified
				</#if>
			</#if>
		</td></tr>	
	</#if>	

	<#if hasAspect(document, "${rma}obsolete") = 1>
		<tr>
			<td>This record is obsolete</td>				
		</tr>	
	</#if>

	<#if hasAspect(document, "${rma}superseded") = 1>
		<tr>
			<td>This record has been superseded</td>				
		</tr>	
	</#if>

	<#if hasAspect(document, "${rma}transfered") = 1>
		<tr>
			<td>This record has been transfered</td>				
		</tr>	
	</#if>

<#else>
	<tr>
		<td>This template can only be applied to a record</td>				
	</tr>
</#if>

</table>