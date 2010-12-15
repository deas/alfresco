Alfresco Google Gadgets
=======================

The Alfresco Google Gadgets are completely powered by Web Scripts.  To use them you
need to get the Web Scripts installed inside your Alfresco Server.  Details follow 
below.  For additional information on the Gadgets and installing them, please visit
http://wiki.alfresco.com/wiki/Google_Gadgets


Step 1
======
If you already have a zip of the Alfresco Google Gadgets, go to Step 2.
In the 'modules' directory in the source code repository, you will find a
'google-gadgets' directory.  Within that is a directory named 'aggadget'.
Zip up this complete directory.


Step 2
======
As admin, navigate to the "Data Dictionary" and into the "Web Scripts" space.
Then navigate further into the "org" and "alfresco" spaces.  From 'More Actions'
select 'Import' and locate the 'aggagets.zip' file, upload and then import it.
At this stage, you should now have a space named 'aggadget' containing all the
Web Scripts.

Step 3
======
The Web Scripts need to be registered with the Alfresco server to become active.
To do this, open a browser at 'http://localhost:8080/alfresco/service/index' or
the equivalent for your server.  This will tell you how many scripts are currently
active.  Click the 'Refresh list of Web Scripts' button to register the new scripts.
You will see a message highlighting how many new scripts were found and are registered.

Step 4
======
You may now use these Web Scripts within iGoogle.  The XML definitions can be used to
register the gadgets with iGoogle, but you will need to modify the server name in the
href setting to be your Alfresco server.