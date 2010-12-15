<#macro properties>

  <!-- Core Properties -->
  <tr>
    <td class="label">Title</td>
    <td>
    	<input type="text" size="40" name="${title.id}" value="${title.value}">
    </td>
  </tr>
  <tr>
    <td class="label">Description</td>
    <td>
    	<textarea rows="3" cols="40" name="${description.id}">${description.value}</textarea>
    </td>
  </tr>

</#macro>

<#macro spacer>

  <!-- Spacer -->
  <tr>
    <td colspan="2">
      <hr/>
    </td>
  </tr>

</#macro>

<#macro source>

  <!-- Source Properties -->
  <tr>
    <td>Source Type</td>
    <td>
       <select name="${sourceType.id}" style="width: 270px">
          <option value="url" <#if sourceType.value == 'url'>selected</#if> >URL</option>
          <option value="space" <#if sourceType.value == 'space'>selected</#if> >Space Content</option>
          <option value="site" <#if sourceType.value == 'site'>selected</#if> >Site Content</option>
          <option value="webapp" <#if sourceType.value == 'webapp'>selected</#if> >Web Application Content</option>
          <option value="current" <#if sourceType.value == 'current'>selected</#if> >Currently Selected Content</option>
       </select>
    </td>
  </tr>
  <tr>
    <td>Source Endpoint</td>
    <td><input type="text" size="40" name="${sourceEndpoint.id}" value="${sourceEndpoint.value}"/></td>
  </tr>
  <tr>
    <td>Source Value</td>
    <td><input type="text" size="40" name="${sourceValue.id}" value="${sourceValue.value}"/></td>
  </tr>  

</#macro>