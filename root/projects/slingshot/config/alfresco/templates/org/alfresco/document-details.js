// Check for IMAP server status
var result = remote.call("/imap/servstatus");
var imapServerEnabled = (result.status == 200 && result == "enabled");
	
model.imapServerEnabled = imapServerEnabled;