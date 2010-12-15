-----------------------------------------------------------------------------
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.

 
 Author  Jon Cox  <jcox@alfresco.com>
 File    README.txt
-----------------------------------------------------------------------------


 
 OVERVIEW
 --------

   This AMP (see http://wiki.alfresco.com/wiki/Module_Management_Tool )
   installs a module into an alfresco.war file that allows XML files 
   that have "trailing garbage" to be repaired.

   The origin of the "trailing garbage" problem is that on linux 
   machines that used smbclient (rather than  /sbin/mount.cifs )
   the different code path taken by smbclient triggered a bug in 
   the Alfresco CIFS server that made files edited via CIFS
   not get properly truncated if the were made shorter.

   In the general case, this problem is not correctable, because
   information is lost regarding the "real" end of file.  
   
   However, XML files are correctable because the XML file format
   implies a logical end of file:  the tag that closes the root
   level node in the leading XML tree.

   Therefore, suppose you have a file myfile.xml that looks like this:

            <x>
                <y>
                  <z>
                  </z>
                </y>
            </x>...trailing garbage...

   If this utility is run on a directory that contains myfile.xml
   (directly or indirectly), then myfile.xml will become:

            <x>
                <y>
                  <z>
                  </z>
                </y>
            </x>

   This utility will then re-parse myfile.xml to verify the fix. 
   When complete, a terse 1-line sumary report will be emitted
   as an HTTP text/plain response.
   
   In the short term, the best way to run this program is by using 
   some command-line HTTP client such as 'lwp-request', but it can be
   run from a browser as well (note: running from the command line allows 
   you to redirect all output to a separate file of your choosing).  
   It can also be run programmatically (both GET and POST are supported).




 INSTALLATION
 ------------

   java -jar alfresco-mmt.jar install                                  \
             /...absolute-path-to.../alfresco-TruncateMalformedXml.amp \
             /...absolute-path-to.../alfresco.war                      \
             -verbose

   The output from alfresco-mmt.jar should look something like this:

     Installing AMP 'alfresco-TruncateMalformedXml.amp' into WAR 'alfresco.war'
     WAR has been backed up to ...
     Adding files relating to version '1.0' of module 'truncateMalformedXml'
     - File ... added to war from amp
     - Directory ... added to war
     - ...

    If you don't have a copy of alfresco-mmt.jar, it's easy to build 
    from the Alfresco source tree:

                ant -f continuous.xml distribute-mmt

    The jar file ends up in:  root/build/dist/alfresco-mmt.jar
    Alternatively, you could download a copy of the alfresco-mmt.jar
    from sourceforge, or get it from someone in support.
    If you do want to create your own developer environment, see:
    http://wiki.alfresco.com/wiki/Alfresco_SVN_Development_Environment

    In any case, the module management tool (alfresco-mmt.jar) 
    is quite nice because it helps you to keep track of what 
    you've installed into your alfresco.war.  It also helps
    with upgrades.



  CONFIGURATION
  -------------

    By default, this utility only operates on ".xml", ".xsl", ".xslt"
    and ".xhtml" files.  To change that, edit the file (whose name 
    has been wrapped for email):
   
        $TOMCAT_HOME/webapps/alfresco/WEB-INF/classes/alfresco  \
            /module/truncateMalformedXml/context/service-context.xml


    This file currently contains 3 bean definitions, but the only one
    of interest here is:
    
         <bean id="truncateMalformedXmlExtensionMatcher"
               class="org.alfresco.repo.avm.util.FileExtensionNameMatcher">
           <property name="extensions">
               <list>
                   <value>.xml</value>
                   <value>.xsl</value>
                   <value>.xslt</value>
                   <value>.xhtml</value>
               </list>
           </property>
         </bean>
       

   If you waned to add some additional file extension to the set of 
   files this utility tries to correct, add them to the <list> node.
   For example, to add  ".xltx"  (a Microsoft Excel template format),
   you'd do this:

         <bean id="truncateMalformedXmlExtensionMatcher"
               class="org.alfresco.repo.avm.util.FileExtensionNameMatcher">
           <property name="extensions">
               <list>
                   <value>.xml</value>
                   <value>.xsl</value>
                   <value>.xslt</value>
                   <value>.xhtml</value>
                   <value>.xtlx</value>
               </list>
           </property>
         </bean>

   Only put file extensions that you know always contain XML.
   Otherwise,  you'll end up spurious files marked as "unfixable"
   (it will also be slower).


        

  USAGE
  -----

   Simple Example (folded for readability):

       http://myserver.example.com:8080/
              alfresco/service/admin/truncate_malformed_xml?
              path=mysite:/www/avm_webapps/ROOT


   More complex example (folded for readability):

       http://myserver.example.com:8080/
              alfresco/service/admin/truncate_malformed_xml?
              path=mysite:/www/avm_webapps/ROOT/x/y/z
              &visit_verbosity=10000
              &incremental_snapshot_freq=8000


    Complete list of QUERY_STRING arguments:        

      o  path
            Mandatory parameter.
            The valid AVM path within your Alfresco server
            for which you want every XML file inspected for
            problems (recursively), and potentially fixed 
            via truncation.  On large repositories, it's best
            to reference a STAGING store rather than an author's 
            workarea, because it's more efficient.   By default,
            snapshots are taken before & after XML files are 
            repaired, so record keeping is equivalent to a 
            workflow-driven process anyway.

      o  visit_verbosity
            Optional parameter.
            Controls how often incremental progress is logged.
            By default, a 1 line progress report is logged for 
            every 1000 nodes (files|dirs) visited, regardless of 
            whether or not they contain XML (see earlier remarks about 
            configuring "what contains XML" via service-context.xml).

      o  trunc_verbosity 
            Optional parameter.
            Controls how often an incremental comment is logged
            for files that this utility attempts to truncate.
            By default, this set to '1'  (i.e.: every time).

      o  take_snapshots
            Optional parameter.
            Controls whether any snapshots are taken at all.
            By default,  'take_snapshots=yes'.   Any other
            setting than 'take_snapshots=no' will be ignored,
            and snapshots will be taken anyway.  Do not set this
            parameter to 'no' unless you have a good reason.

      o  incremental_snapshot_freq 
            Optional parameter.
            Controls how often incremental snapshots are taken.
            By default, in addition to taking a snapshot before
            and after repairing the XML files under 'path',
            this utility takes a snapshot for every  5000
            files it fixes.   This keeps the server working 
            efficiently for large jobs.  Note that all the
            incremental snapshots include a terse summary of
            incremental progress in the snapshot's 'description' 
            section, and have tag: "TruncateMalformedXml (incremental)".
            Of couse, if 'take_snapshots=no', then no snapshots
            will be taken at all, not even incremental ones.



   Results are emitted as a text/plain HTTP response.

   This utility will eventually have an standard command line tool
   interface, but for now the best (though somewhat awkward) way 
   of running it is via a generic command line tool such as 
   "lwp-request";  alternatively, you could just issue the request
   from a browser that has already been cookied with an 'admin' 
   login to Alfresco (however that does not make it as easy to 
   redirect the output as a command line tool).


  

  INTERPRETING OUTPUT
  -------------------

   When complete, this utility will issue a 1-line message that looks 
   something like this (the line has been wrapped for clarity here):


         TruncateMalformedXml complete.
                         visited: 15 
                         checked: 8
                              ok: 4 
                           fixed: 3 
                       unfixable: 1 
                          syserr: 0

   Definitions:

         visited   -  Total number of files or dirs encountered
         checked   -  Number of XML files inspected
         ok        -  XML files that were already well-formed
         unfixable -  XML files that could not be fixed
         syserror  -  unexpected system-level errors (e.g.: bad reads)


   An example of a an "unfixable" XML file is:

         <x>
              <unbalanced>
         </x>

   While XML like this could be fixed by a human, it's not simply a 
   matter of truncating the file at the 1st error, and re-verifying
   that it's well-formed.  Hence, it's "unfixable" by this utility.
     
   Assuming you stop using smbclient and/or move to a version of Alfresco
   that has corrected for this problem, you should not have not run this 
   utility more than once on a staging area.   However, re-running the 
   utility again is not harmful (other than being a pointless waste of
   system resources).  If you did it anyway, you'd see something like
   this the next time:

         TruncateMalformedXml complete.
                         visited: 15 
                         checked: 8
                              ok: 7 
                           fixed: 0 
                       unfixable: 1 
                          syserr: 0
    
    Note that "ok" increased because 3 files were "fixed" previously.



  MISCELLANEOUS
  -------------

    This tool creates a labeled snapshot prior to doing anything 
    in order to make it possible to review the changes made.
    It may (or may not) produce other incremental snapshots
    for efficiency.

    The 'tag' associated with the snapshot that is taken prior to 
    modifying files is: "TruncateMalformedXml (pre)"
    Its description is: "TruncateMalformedXml about to check for malformed XML files"

    Incremental snapshots have the tag: "TruncateMalformedXml (incremental)" 
    and the description:   "TruncateMalformedXml incremental:  <terse-status>" 

    The 'tag' associated with the snapshot that is taken after all files
    have been modified is: "TruncateMalformedXml (post)"
    Its description is:    "TruncateMalformedXml completed:  <terse-status>"

    The information within <terse-status> is the same logfile data
    described earlier  (e.g.:   "visited: 15   checked: 8  ...").

