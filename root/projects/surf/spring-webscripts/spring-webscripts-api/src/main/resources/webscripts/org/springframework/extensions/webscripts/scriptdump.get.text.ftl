<webscript scriptid="${script.id}" xmlns="http://www.alfresco.org/webscript/1.0">
<#list stores as store>
<#list store.files as file>
<file path="${file.path}" store="${store.path}"><![CDATA[${file.content}]]></file>
</#list>
</#list>
</webscript>