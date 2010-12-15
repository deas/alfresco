<fb:dashboard/>

<fb:tabs>
  <fb:tab-item href='${facebook.pageURL}' title='Create Library' selected='true'/>
</fb:tabs>

<fb:editor action="${facebook.canvasURL}/createlib" labelwidth="100">
  <fb:editor-text label="Name" name="name" value=""/>
  <fb:editor-text label="Description" name="desc" value=""/>
  <fb:editor-buttonset>
    <fb:editor-button value="Create"/>
    <fb:editor-cancel href="${facebook.canvasURL}">
  </fb:editor-buttonset>
</fb:editor>
