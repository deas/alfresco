<fb:dashboard/>

<fb:tabs>
  <fb:tab-item href='${facebook.pageURL}' title='Add Document (${library.name} Library)' selected='true'/>
</fb:tabs>

<fb:editor/>

<form action="${absurl(url.serviceContext)}/${facebook.canvasPath}/adddoc/${library.id}" method="post" enctype="multipart/form-data" charset="utf-8">
  <table class="editorkit" border="0" cellspacing="0" style="width:450px">
    <tr class="width_setter">
      <th style="width:100px"></th>
      <td></td>
    </tr>
    <tr>
      <th><label>Name:</label></th>
      <td class="editorkit_row"><input type="text" value="" name="name"/></td>
      <td class="right_padding"></td>
    </tr>
    <tr>
      <th><label>Description:</label></th>
      <td class="editorkit_row"><input type="text" value="" name="desc"/></td>
      <td class="right_padding"></td>
    </tr>
    <tr>
      <th class="detached_label"><label>Document:</label></th>
      <td class="editorkit_row"><input type="file" name="file" /></td>
      <td class="right_padding"></td>
    </tr>
    <tr>
      <th></th>
      <td class="editorkit_buttonset">
      <input type="submit" class="editorkit_button action" value="Add" />
      <span class="cancel_link"><span>or</span><a href="${facebook.canvasURL}/library/${library.id}">Cancel</a></span></td>
      <td class="right_padding"></td>
    </tr>
  </table>
</form>
