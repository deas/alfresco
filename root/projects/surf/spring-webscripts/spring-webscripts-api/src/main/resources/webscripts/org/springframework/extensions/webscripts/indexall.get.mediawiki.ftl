Back to [[Alfresco Labs 3]].


= Introduction =

Welcome to the reference documentation for the Alfresco Repository <#if description??>${description} </#if>[[RESTful API]] - ${server.edition} v${server.version}.

This document was generated on ${date?datetime} via the Alfresco Repository URI...

 <nowiki>http://</nowiki><host>:<port>${url.full?html}

'''NOTE: This document is under construction and the APIs are subject to change while still in Labs Beta.'''


= Web Script Reference =

This section provides reference information for each [[Web Scripts|Web Script]], organized by Web Script Package. 

Documentation for each Web Script includes:

* Short Name
* Description
* Available URI templates
* Default [[Web Scripts Framework#HTTP Response Formats|response format]]
* How to specify an alternative response
* Authentication requirements
* Transaction requirements
* Location of Web Script description document

<#macro recursepackage package>
<#if package.scripts?size &gt; 0>
== Package: ${package.path} ==
<#list package.scripts as webscript>
<#assign desc = webscript.description>

=== ${desc.shortName} ===

<#if desc.description??><#if desc.description?ends_with(".")>${desc.description}<#else>${desc.description}.</#if><#else><i>[No description supplied]</i></#if>

<#list desc.URIs as uri>
 [http://localhost:8080${url.serviceContext}${uri} ${desc.method} ${url.serviceContext}${uri}]
</#list>

Requirements:
* Default Format: ${desc.defaultFormat!"<i>Determined at run-time</i>"}
* Authentication: ${desc.requiredAuthentication}
* Transaction: ${desc.requiredTransaction}
* Format Style: ${desc.formatStyle}

Definition:
* Id: ${desc.id}
* Description: ${desc.storePath}/${desc.descPath}
</#list>
</#if>
<#list package.children as childpath>
  <@recursepackage package=childpath/>
</#list>
</#macro>
    
<@recursepackage package=rootpackage/>

[[Category:3.0]]
