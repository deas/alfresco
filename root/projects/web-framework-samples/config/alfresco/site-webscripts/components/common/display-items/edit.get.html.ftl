<#import "/components/include/component-editor.ftl" as editor />

<table class="AlfrescoComponentEditor">

  <@editor.properties />
  
  <@editor.spacer />
  
  <@editor.source />
  
  <@editor.spacer />

  <!-- Custom Properties -->
  <tr>
    <td class="label">View</td>
    <td>
    	<select name="${view.id}">
    	    <option value="views/list" <#if view.value == "views/list">selected</#if> >List View</option>
    	    <option value="views/4grid" <#if view.value == "views/4grid">selected</#if> >Four-by-four View</option>
    	</select>
    </td>
  </tr>
  <tr>
    <td class="label">Icon Size</td>
    <td>
    	<select name="${iconSize.id}">
    	    <option value="16" <#if iconSize.value == "16">selected</#if> >16 pixels</option>
    	    <option value="24" <#if iconSize.value == "24">selected</#if> >24 pixels</option>
    	    <option value="32" <#if iconSize.value == "32">selected</#if> >32 pixels</option>
    	    <option value="48" <#if iconSize.value == "48">selected</#if> >48 pixels</option>
    	    <option value="64" <#if iconSize.value == "64">selected</#if> >64 pixels</option>
    	    <option value="72" <#if iconSize.value == "72">selected</#if> >72 pixels</option>
    	    <option value="96" <#if iconSize.value == "96">selected</#if> >96 pixels</option>
    	    <option value="128" <#if iconSize.value == "128">selected</#if> >128 pixels</option>
    	</select>
    </td>
  </tr>
</table>