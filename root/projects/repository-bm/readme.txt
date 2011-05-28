README - Draft Notes

Pre-requisites

- Apache JMeter 2.4
- Alfresco 3.4 or 4.0
- Apache Ant 1.7.1
- JDK 1.6.0_22

Setup

- edit "build.properties" and change "jmeter.install.dir"
- run "ant deploy" to build and deploy "repository-bm.jar" (and dependent libs) to JMeter lib/junit
- as Alfresco admin, import "testdata_mini.zip" into "/Company Home" (eg. using Alfresco Explorer) 
- from JMeter - open and review "User Defined Variables" for each test plan (see source/test-resourcesi/*.jmx)

  eg. 
  
  BASEURL  = http://localhost:8080/alfresco/webdav/testdata (for WebDAV)
  BASEURL  = http://localhost:8080/alfresco/service/cmis    (for CMIS AtomPub - using existing CMIS server impl)
  BASEURL  = http://localhost:8080/alfresco/cmisatom        (for CMIS AtomPub - using OpenCMIS server impl, since Alfresco 4.0)
  
Run test(s)

- either from the JMeter UI - open and run individual JMeter test plans (see source/test-resourcesi/*.jmx)
- or from command-line - run "ant run-jmeter-XXX" (see "ant -p" for XXX options)

Review results / reports

- either from JMeter UI - see "Summary Report" or add/enable other listeners, such as "Graph Results"
- or from cmdline - see build/jmeter-results/*.html (or *.xml for raw results)

