<table class="AlfrescoComponentEditor">
  <tr>
    <td>
    	<div class="yui-skin-sam">
    		<textarea name="${html.id}" id="${html.id}" cols="50" rows="8">${html.value}</textarea> 
    	</div>
    </td>
  </tr>
</table>

<script language="JavaScript">

	var myEditor = null;
	
	var myFunction = function(e)
	{
		if(!myEditor)
		{
			var myConfig = { 
				height: '95%', 
				width: '99%', 
				handleSubmit: true,
				animate: true
			}; 

			myEditor = new YAHOO.widget.SimpleEditor('${html.id}', myConfig);
			myEditor.render();
			
			window.removeEvent(load, this);
		}
	};
	
	window.addEvent('load', myFunction);
	
</script>
