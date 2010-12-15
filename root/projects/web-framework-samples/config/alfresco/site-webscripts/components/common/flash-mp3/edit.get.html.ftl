<#import "/components/include/component-editor.ftl" as editor />

<table class="AlfrescoComponentEditor">

  <@editor.properties />
  
  <@editor.spacer />
  
  <@editor.source />
  
  <@editor.spacer />

  <!-- Custom Properties -->
  <tr>
    <td>Song Title</td>
    <td>
    	<input name="${songTitle.id}" value="${songTitle.value}" />
    </td>
  </tr>
  <tr>
    <td>Appearance</td>
    <td>
    	<select name="${appearance.id}">
    		<option value="slim" <#if appearance.value =="slim">selected</#if> >Slim</option>
    		<option value="full" <#if appearance.value =="full">selected</#if> >Full</option>
    	</select>
    </td>
  </tr>
  
</table>

