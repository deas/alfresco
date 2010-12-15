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
   installs a module into an alfresco.war file that allows two directories
   within the AVM to be differenced.

   In the short term, the best way to run this program is by using 
   some command-line HTTP client such as 'lwp-request', but it can be
   run from a browser as well (note: running from the command line allows 
   you to redirect all output to a separate file of your choosing).  
   It can also be run programmatically (both GET and POST are supported).




 INSTALLATION
 ------------

   java -jar alfresco-mmt.jar install                         \
             /...absolute-path-to.../alfresco-avmCompare.amp  \
             /...absolute-path-to.../alfresco.war             \
             -verbose

   The output from alfresco-mmt.jar should look something like this:

     Installing AMP 'alfresco-TruncateMalformedXml.amp' into WAR 'alfresco.war'
     WAR has been backed up to ...
     Adding files relating to version '1.0' of module 'avmCompare'
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



  CONVENTIONS
  -----------

    The following conventions are used by this utility & its documentation:


    URL Folding
    -----------

         For readability, URLs in the examples below have been folded
         into multple lines;  indented lines should be joined with
         the line above them, removing all whitespace between them.
         
         For example:  http://example.com:8080/
                              this/is/an?
                              example=of&
                              a_folded=url
         
         Is a "folded" version of: 
                http://example.com:8080/this/is/an?example=of&a_folded=url
           


    Versioning aliases
    ------------------

         In the AVM, version '-1' corresponds to the HEAD of a repository
         (i.e.:  its unsaved changes).

         This utility creates additional version aliases for convenience,
         such as '-2' for "latest snapshot".   These version aliases may 
         not currently be available by using the low-level AVMRemote API.
      
         This convention is fairly useful, and its implementation here 
         allows for some sophisticated uses.  For example, version -3 
         is an alias for the penultimate snapshot, -4 the one before that,
         and so on (clipping at 0).
      
         Thus, if the latest snapshot is 99, then -2 is an alias for 
         version 99, -3 is an alias for version 98, etc.



  DEFAULTS
  --------

     baseline_version   = latest snapshot of  baseline_path   (-2)
     changeset_version  = HEAD   version  of  changeset_path  (-1)
     baseline_path      = changeset_path

     Note: either baseline_path or changeset_path must be set (or both).

 

  USAGE 
  -----


     Example 1
     ---------

     Within the web project 'mysite', show what modifications are present
     in the ROOT webapp of the workarea 'alice' relative to the ROOT webapp
     of the corresponding staging area, given that the Alfresco repository 
     is hosted at http://myserver.example.com:8080.


     Note that:

        o  The baseline_path  is:   mysite:/www/avm_webapps/ROOT
        o  The changeset_path is:   mysite--alice:/www/avm_webapps/ROOT
        

     Therefore, the default values for baseline_version and changeset_version 
     (see DEFAULTS above) allow us to use the following URL:

               http://myserver.example.com:8080/
                      alfresco/service/admin/avm_compare?
                      baseline_path=mysite:/www/avm_webapps/ROOT&
                      changeset_path=mysite--alice:/www/avm_webapps/ROOT

     The "longhand" equivalent of this URL is:

               http://myserver.example.com:8080/
                      alfresco/service/admin/avm_compare?
                      baseline_version=-2&
                      baseline_path=mysite:/www/avm_webapps/ROOT&
                      changeset_version=-1&
                      changeset_path=mysite--alice:/www/avm_webapps/ROOT



     Example 2
     ---------

     Similar to "Example 1", only instead, show how the HEAD version of
     Alice's workarea differs from the latest snapshot of her own workarea.
     These are the "unsnapshotted differences" in her workarea.

               http://myserver.example.com:8080/
                      alfresco/service/admin/avm_compare?
                      changeset_path=mysite--alice:/www/avm_webapps/ROOT


     The "longhand" equivalent of this URL is:

               http://myserver.example.com:8080/
                      alfresco/service/admin/avm_compare?
                      baseline_version=-2&
                      baseline_path=mysite--alice:/www/avm_webapps/ROOT&
                      changeset_version=-1&
                      changeset_path=mysite--alice:/www/avm_webapps/ROOT

     Example 3
     ---------

     Similar to "Example 1", only instead, show how the latest snapshot 
     of the staging area differs from version '44' of the staging area:


               http://myserver.example.com:8080/
                      alfresco/service/admin/avm_compare?
                      baseline_version=44&
                      changeset_path=mysite:/www/avm_webapps/ROOT&
                      changeset_version=-2

     The "longhand" equivalent of this URL is:

               http://myserver.example.com:8080/
                      alfresco/service/admin/avm_compare?
                      baseline_version=44&
                      changeset_version=-2&
                      changeset_path=mysite:/www/avm_webapps/ROOT

    
     Example 4
     ---------
     Similar to "Example 1", only instead, compare the last 
     two snapshots in staging:

               http://myserver.example.com:8080/
                      alfresco/service/admin/avm_compare?
                      baseline_version=-3&
                      changeset_version=-2&
                      changeset_path=mysite:/www/avm_webapps/ROOT

     Example 5
     ---------
     Similar to "Example 1", only instead, compare staging snapshot 41 to the 
     baseline staging version 40.  This shows what's new in staging version 41.

               http://myserver.example.com:8080/
                      alfresco/service/admin/avm_compare?
                      baseline_version=40&
                      changeset_version=41&
                      changeset_path=mysite:/www/avm_webapps/ROOT


     Example 6
     ---------
     Similar to "Example 1", only instead, compare staging snapshot 10 to a 
     baseline of staging version 5.  This shows everything that's changed in 
     the staging area between starting from version 5 up to version 10.

               http://myserver.example.com:8080/
                      alfresco/service/admin/avm_compare?
                      baseline_version=5&
                      changeset_version=10&
                      changeset_path=mysite:/www/avm_webapps/ROOT




    Complete list of QUERY_STRING arguments:        

      o  changeset_version
            The version id for the source tree.
            Default: -1  (HEAD)

      o  changeset_path
            The AVM path to the source tree.
            Note:  Either the changeset_path or the baseline_path must
                   be specified (or both).  If one is not set, its value
                   defaults to the other.

            Example 1:     mysite--alice:/www/avm_webapps/ROOT
                           refers to the 'alice' repository 
                           within the 'mysite' web project.

            Example 2:     mysite:/www/avm_webapps/ROOT
                           refers to the staging repository
                           of the 'mysite' web project.

      o  baseline_version
            The version id for the source tree.
            Default: -2  (latest snapshot)

      o  baseline_path
            The AVM path to the destination tree.
            Note:  Either the changeset_path or the baseline_path must
                   be specified (or both).  If one is not set, its value
                   defaults to the other.

            Example 1:     mysite--alice:/www/avm_webapps/ROOT
                           refers to the 'alice' repository 
                           within the 'mysite' web project.

            Example 2:     mysite:/www/avm_webapps/ROOT
                           refers to the staging repository
                           of the 'mysite' web project.


      o  prune
            Values:  "yes" or "no"
            Default: "yes"
            By default, directories that are newer in the changeset
            don't have all their new children listed in the comparison.
            By setting 'prune=no', the entire new subtree is listed.


      o  show_header
            Values:  "yes" or "no"
            Default: "yes"
        
            When "show_header=yes", displays a header that makes it 
            easier to interpret the output of this utility.   
            For example, suppose this utility is invoked by the URL:

                  http://myserver.example.com:8080/ 
                         alfresco/service/admin/avm_compare?
                         baseline_path=mysite:/www/avm_webapps/ROOT&
                         changeset_path=mysite--alice:/www/avm_webapps/ROOT
            
            If the latest snapshot of "mysite" happens to be 3,
            then the following informational header/legend might
            look something like this:
             

            AvmCompare
            ----------

                       baseline_version:   3
                       baseline_path:      mysite:/www/avm_webapps/ROOT

                       changeset_version:  -1
                       changeset_path:     mysite--alice:/www/avm_webapps/ROOT


            Legend
            ------

                       [---]     no such file or directory
                       [--f]     plain   file
                       [--d]     plain   directory
                       [-lf]     layered file
                       [-ld]     layered directory
                       [x-f]     deleted plain file
                       [x-d]     deleted plain directory
                       [xlf]     deleted layered file
                       [xld]     deleted layered directory
                       c-status  status of changeset relative to baseline
                       b-meta    baseline  '[...]' metadata (e.g.: [--f])
                       c-meta    changeset '[...]' metadata (e.g.: [--f])



            c-status   b-meta  c-meta  c-path
            ---------  ------  ------  ------
            NEWER      [--f]   [--f]   mysite--alice:/www/avm_webapps/ROOT/...
            NEWER      [--f]   [--f]   mysite--alice:/www/avm_webapps/ROOT/...
            ...



   The difference reported by this tool shows what the difference
   is from the source to the destination.

   This utility will eventually have an standard command line tool
   interface, but for now the best (though somewhat awkward) way 
   of running it is via a generic command line tool such as 
   "lwp-request";  alternatively, you could just issue the request
   from a browser that has already been cookied with an 'admin' 
   login to Alfresco (however that does not make it as easy to 
   redirect the output as a command line tool).

  

  INTERPRETING OUTPUT
  -------------------

     Example 1
     ---------
        The following indictes a plain file that has been updated
        in the change set, relative to the corresponding baseline:
       
                NEWER      [--f]   [--f]   mysite--alice:/...


     Example 2
     ---------
        The following indictes a plain file that has been updated
        in the change set, relative to the corresponding baseline;
        furhter, the file did not exist in the baseline version.
       
                NEWER      [---]   [--f]   mysite--alice:/...


     Example 3
     ---------
        The following indicates a plain file that has been deleted
        in the change set, relative to the corresponding baseline;
        furhter, the file did not exist in the baseline version:
       
                NEWER      [---]   [x-f]   mysite--alice:/...


     Example 4
     ---------
        The following indicates that a layered directory has been created
        in the change set.

                NEWER      [---]   [-ld]   mysite--alice:/...


     Example 5
     ---------
        The following indicates that a layered directory has been deleted
        in the change set:

                NEWER      [--d]   [xld]   mysite--alice:/...


     Example 6
     ---------
        The following indicates a plain file that is in conflict 
        relative to the corresponding baseline;  two files are
        in a state of CONFLICT when neither has the other as
        an ancestor in its history chain (regardless of whether
        their contents happen to be equal or not):

                CONFLICT   [--f]   [--f]   mysite--alice:/...



  MISCELLANEOUS
  -------------

        Note that when a file exists in staging and is only seen via 
        transparency in a workarea, when the viewed-via-transparency
        file is modified in the workarea, the corresponding file in
        staging is its ancestor.   Therefore, ordinary edits in an
        author's workarea don't generate files in a state of CONFLICT.
        However, suppose file 'oops.txt' didn't exist in staging, and 
        both Alice and Bob created 'oops.txt' in their workareas
        (in the same repository-relative position).  If the system 
        allowed Bob to submit 'oops.txt' to staging, then 'oops.txt'
        would appear to be in a state of CONFLICT within Alice's
        area (but not Bob's).  

