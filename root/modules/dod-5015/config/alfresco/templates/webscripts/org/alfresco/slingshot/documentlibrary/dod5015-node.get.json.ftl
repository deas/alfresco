<#import "item.lib.ftl" as itemLib />
<#escape x as jsonUtils.encodeJSONString(x)>
{
   "totalRecords": ${doclist.paging.totalRecords?c},
   "startIndex": ${doclist.paging.startIndex?c},
   "metadata":
   {
      <#if doclist.filePlan??>"filePlan": "${doclist.filePlan.nodeRef}",</#if>
      "parent":
      {
      <#if doclist.parentMeta??>
         "nodeRef": "${doclist.parentMeta.nodeRef}",
         "type": "${doclist.parentMeta.type}",
         "permissions":
         {
            "userAccess":
            {
            <#list doclist.parentMeta.permissions?keys as perm>
               <#if doclist.parentMeta.permissions[perm]?is_boolean>
               "${perm?string}": ${doclist.parentMeta.permissions[perm]?string}<#if perm_has_next>,</#if>
               </#if>
            </#list>
            }
         }
      </#if>
      }
   },
   "item":
   {
      <@itemLib.itemJSON item=doclist.items[0] />
   }
}
</#escape>