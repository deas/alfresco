<#import "/components/include/component-editor.ftl" as editor />

<table class="AlfrescoComponentEditor">

  <@editor.properties />
  
  <@editor.spacer />
  
  <@editor.source />
  
  <@editor.spacer />

  <!-- Custom Properties -->
  <tr>
    <td class="label">Mimetype</td>
    <td>
    	<input name="${mimetype.id}" value="${mimetype.value}" />
    </td>
  </tr>
  <tr>
    <td class="label">Player</td>
    <td>
    	<select name="${player.id}">
    	    <option value="quicktime" <#if player.value == "quicktime">selected</#if> >Quick Time</option>
    	    <option value="windowsmedia" <#if player.value == "windowsmedia">selected</#if> >Windows Media</option>
    	    <option value="shockwave" <#if player.value == "shockwave">selected</#if> >Shockwave</option>
    	    <option value="real" <#if player.value == "real">selected</#if> >Real Player</option>
    	</select>
    </td>
  </tr>
</table>
