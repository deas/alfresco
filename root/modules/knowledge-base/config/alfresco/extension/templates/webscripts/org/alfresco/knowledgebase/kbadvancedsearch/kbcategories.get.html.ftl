{
<#compress>
<#list companyhome.childrenByLuceneSearch["PATH:\"/cm:generalclassifiable/cm:Alfresco_x0020_Knowledge_x0020_Base\""] as child>            
                 <#list classification.getRootCategories("cm:generalclassifiable") as rootnode>
                       <#if rootnode.nodeRef = child.nodeRef>
                               <#list rootnode.immediateSubCategories  as all>
                                      "${all.properties.name}":[
                                        <#list all.subCategories?sort_by("name") as mylist>
                                            {
                                                   "noderef":"${mylist.nodeRef}",
                                                   "name":"${mylist.properties.name}"
                                             }<#if mylist_index+1 &lt; all.subCategories?size>,</#if>
                                       </#list>]<#if all_index+1 &lt; rootnode.immediateSubCategories?size>,</#if>
                               </#list>
                         </#if>
                 </#list>
</#list>
</#compress>
}