The following vital records are due for review:

<#list records as record>   
     - ${record.properties["rma:identifier"]!} ${record.properties["cm:name"]!}<#if record_has_next>,
     </#if>
</#list>

