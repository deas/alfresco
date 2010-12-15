<#import "/components/include/component-editor.ftl" as editor />

<table class="AlfrescoComponentEditor">

  <@editor.properties />
  
  <@editor.spacer />
  
  <@editor.source />
  
  <@editor.spacer />
  
  <!-- Custom Properties -->
  <tr>
    <td>Image Text</td>
    <td><input type="text" name="${imageText.id}" value="${imageText.value}"/></td>
  </tr>
  
</table>
