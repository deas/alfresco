<#escape x as jsonUtils.encodeJSONString(x)>
{
   "totalResults": ${treenode.items?size?c},
   "items":
   [
   <#list treenode.items as item>
      <#assign t = item.node>
      {
      <#if item.permissions??>
         "userAccess":
         {
         <#list item.permissions?keys as perm>
            <#if item.permissions[perm]?is_boolean>
            "${perm?string}": ${item.permissions[perm]?string}<#if perm_has_next>,</#if>
            </#if>
         </#list>
         },
      </#if>
         "nodeRef": "${t.nodeRef}",
         "name": "${t.name}",
         "description": "${(t.properties.description!"")}",
         "hasChildren": ${item.hasSubfolders?string}
      }<#if item_has_next>,</#if>
   </#list>
   ]
}
</#escape>
