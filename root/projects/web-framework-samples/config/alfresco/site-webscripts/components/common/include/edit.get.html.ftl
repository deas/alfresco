<#import "/components/include/component-editor.ftl" as editor />

<table class="AlfrescoComponentEditor">

  <@editor.properties />
  
  <@editor.spacer />
  
  <@editor.source />
  
  <@editor.spacer />

  <!-- Custom Properties -->
  <tr>
    <td>Container</td>
    <td>
    	<select name="${container.id}">
    		<option value="iframe" <#if container.value =="iframe">selected</#if> >IFRAME</option>
    		<option value="div" <#if container.value =="div">selected</#if>>DIV</option>
    	</select>
    </td>
  </tr>
  
</table>

