README - Draft Notes

Disclaimer
- this preliminary "dev" tool is intended for internal testing
- any results are subject to validation of appropriate environment, tests and procedures

Pre-requisites

- Apache JMeter 2.4
- Alfresco 3.4 or 4.0
- Apache Ant 1.7.1
- JDK 1.6.0_22 (or higher)

Setup

- note: to avoid OutOfMemory error when generating result report (xslt target) set ANT_OPTS=-Xmx1G (or higher) 

- edit "build.properties" and change settings such as:

  - edit "jmeter.install.dir" and change to absolute directory for JMeter 2.4, eg. /my/path/to/jakarta-jmeter-2.4
 
  - edit "user.dir" and change to absolute directory for "repository-bm" project, eg. /my/path/to/alfresco/root/projects/repository-bm

  - edit "baseurl.webdav" and change hostname

    eg. http://myhost:8080/alfresco/webdav

  - edit "baseurl.cmis" and change hostname and, optionally, service context

    http://myhost:8080/alfresco/service/cmis    (for CMIS AtomPub - using CMIS server impl, since Alfresco 3.3)
    http://myhost:8080/alfresco/cmisatom     (for CMIS AtomPub - using OpenCMIS server impl, since Alfresco 4.0)

  - optionally change number of "threads"
  - optionally change "duration" of each thread grouop (in secs)

- run "ant deploy" to build and deploy "repository-bm.jar" (and dependent libs) to JMeter lib/junit

- the test data will be loaded on the first test run (by auto-importing "testdata_mini.zip" into "/Company Home")
  alternatively it can be pre-loaded manually as Alfresco admin (eg. using Alfresco Explorer or FTP or WebDAV or …)


Run test(s)

- make sure that the Alfresco server has been started and is remotely accessible (on the configured URLs , eg. http://myhost:8080/alfresco)

- either from the JMeter UI - open and run individual JMeter test plans (see source/test-resourcesi/*.jmx)
- or from command-line - run "ant run-jmeter-XXX" (see "ant -p" for XXX options)

Review results / reports

- either from JMeter UI - see "Summary Report" or add/enable other listeners, such as "Graph Results"
- or from cmdline - see build/jmeter-results/*.html (or *.xml for raw results)

