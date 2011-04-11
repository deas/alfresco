function main()
{
   // Check for IMAP server status
   var result = remote.call("/imap/servstatus"),
      imapServerEnabled = (result.status == 200 && result == "enabled");

   // Prepare the model for the template
   model.imapServerEnabled = imapServerEnabled;
}

main();