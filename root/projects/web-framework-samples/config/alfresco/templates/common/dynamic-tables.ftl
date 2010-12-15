<#macro body>

   <#if templateConfig.rows?exists>   

      <table border="0" cellpadding="0" cellspacing="0" <#if templateConfig.width?exists && templateConfig.width?length &gt; 0> width="${templateConfig.width}%"<#else> width="75%"</#if> >

      <#list templateConfig.rows as row>
      
         <tr id="${row.id}">
         
         <td width="100%">
         
            <table width="100%" cellspacing="0" cellpadding="0">

            <tr>

            <#list row.panels as col>
         
               <td id="${col.id}" valign="top" align="left">

                  <#if col.regions?exists>
	             <#list col.regions as r>
	          
                     <@region id="${r.name}" scope="${r.scope}"/>
                  
		     </#list>
		  </#if>
		  
	       </td>
	       
            </#list>
         
            </tr>
         
         </table>
         
         </td>
         
         </tr>
      
      </#list>
      
      </table>

   </#if>

</#macro>
