***********************
*                     *
*  ABOUT THIS FOLDER  *
*                     *
***********************


 1. FILES
==========

src/                     - Alfresco sources for using the org.alfresco.previewer.Previewer as a Flex component
src/assets               - Skinning graphics: icons & cursors
src/assets/test          - Pregenerated .swf files from pdf2swf to test against
src/com/
src/com/wayne_dash_marsh - Support for simulating threads in Flash Player
src/madebypi             - Adds support for non english keyboard input on windows
src/org
src/org/alfresco         - The Alfresco AS3 source files
src/org/hasseg           - Adds support for scrolling mouse wheel on mac
copy-to-html-template    - Files to copy to flash builder project's html-template folder


 2. SETUP DEV ENV EXAMPLE
==========================

1. Install Flash Builder

2. File > Switch Workspace > Other:
   /Users/erikwinlof/Documents/Adobe Flash Builder 4/Alfresco_HEAD

3. File > New > Flex Project

   Create a Flex project
   ---------------------
   Project Name: WebPreviewer
   Project location: /Users/erikwinlof/Development/projects/head/code/root/projects/slingshot/source/as/webpreviewer
   Application type: Web
   Use a specific JDK: 3.5 (This will make it possible to use a Flash PLayer of version 9.0.124) 
   Application server type: None/Other
   [Next]

   Configure output
   ----------------
   Output folder: /Users/erikwinlof/Development/projects/head/software/tomcat-app/webapps/share/WebPreviewer_Test
   [Next]

   Create a Flex project
   ---------------------
   Main source folder: src
   Main application file: WebPreviewer.mxml
   Output folder: http://localhost:8081/share/WebPreviewer_Test
   [Finish]

   Project build....

4. Once buil dis finsihed copy files int "copy-to-html-template" into "html-template" folder (overwrite duplicates).
   This will make sure extra javascript resources is imported to the test page and that we apply all WebPreviewer
   specific input parameters when the previewer is debuggeed and displayed.

5. Project > Clean
   Run > Run > WebPreviewer (will open browser w previewer)

   Note!
   Ignore the error "ReferenceError: Error #1065: Variabeln stop_fla:MainTimeline har inte definierats."
   Which is thrown when you load external .swf-files (the document to view) into the WebPreviewer.swf using the Flash Debug Player

6. Develop... (use Run > Debug > WebPreviewer to debug)

7. When satisfied, make sure to include it in Share by replacing ...
   /Users/erikwinlof/Development/projects/head/software/tomcat-app/webapps/share/WebPreviewer_Test/WebPreviewer.swf
   ... with ...
   /Users/erikwinlof/Development/projects/head/software/tomcat-app/webapps/share/WebPreviewer_Test/WebPreviewer.swf
   ... then test in Share!

8. Commit and make sure you DO NOT include any Flash Builder / Eclipse project files   

Note!
If you hit a security violation its probably because you view the files on your local filesystem instead of on a web browser.
This will not happen if you follow the instructions below.
