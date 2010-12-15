<#import "/components/include/component-editor.ftl" as editor />

<table class="AlfrescoComponentEditor">

  <@editor.properties />
    
  <@editor.spacer />

  <!-- Custom Properties -->
  <tr>
    <td>Style</td>
    <td>
    	<select name="${style.id}">
    	    <option value="gray" <#if style.value == "gray">selected</#if> >Gray</option>
    	    <option value="tabbed" <#if style.value == "tabbed">selected</#if> >Tabbed</option>
    	</select>
    </td>
  </tr>
  <tr>
    <td>Orientation</td>
    <td>
    	<select name="${orientation.id}">
    	    <option value="horizontal" <#if orientation.value == "horizontal">selected</#if> >Horizontal</option>
    	    <option value="vertical" <#if orientation.value == "vertical">selected</#if> >Vertical</option>
    	</select>
    </td>
  </tr>
  <tr>
    <td>Mount page</td>
    <td>
    	<select name="${startingPage.id}">
    	    <option value="siteroot" <#if startingPage.value == "siteroot">selected</#if> >The Site's Root Page</option>
    	    <option value="currentpage" <#if startingPage.value == "currentpage">selected</#if> >The Current Page</option>
    	</select>
    </td>
  </tr>
  <tr>
    <td>Top Page</td>
    <td>
    	<select name="${topPage.id}">
    	    <option value="show" <#if topPage.value == "show">selected</#if> >Show Top Page</option>
    	    <option value="hide" <#if topPage.value == "hide">selected</#if> >Hide Top Page</option>
    	</select>
    </td>
  </tr>
  <tr>
    <td>Children and Siblings</td>
    <td>
    	<input type="radio" name="${childSiblings.id}" value="showSiblings" <#if childSiblings.value == "showSiblings">checked</#if> >Show Siblings
    	<input type="radio" name="${childSiblings.id}" value="showChildren" <#if childSiblings.value == "showChildren">checked</#if> >Show Children
    	<input type="radio" name="${childSiblings.id}" value="none" <#if childSiblings.value == "none">checked</#if> >None
    </td>
  </tr>
</table>