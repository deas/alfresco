

function main()
{

	// Get list of channelTypes:
	var connector = remote.connect("alfresco"),
	   remoteUrl = "/api/publishing/channel-types",
		result = connector.get(remoteUrl);
	   if (result.status != status.STATUS_OK)
	   {
	      status.setCode(status.STATUS_INTERNAL_SERVER_ERROR, "Unable to call doclist data webscript. " +
	                     "Status: " + result.status + ", response: " + result.response);
	      return null;
	   }
	   
	   var data = eval('(' + result.response + ')');
	   model.channelTypes = data.data;
	   
	   // Widget instantiation metadata...
	   var widget = {
	      id : "ConsoleChannels", 
	      name : "Alfresco.ConsoleChannels",
	      options : {
	         acceptMessagesFrom: "http://www.alfresco.com"
	      }
	   };
	   model.widgets = [widget];
}

main();
