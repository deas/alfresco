@echo off
echo "Alfresco JLAN Server starting, enter 'x' to shutdown server, 'r' to restart server ..."
java -Xms512m -Xmx1024m -Djava.library.path=.\jni -cp .\jars/alfresco-jlan-full.jar;.\libs\cryptix-jce-provider.jar;.\service\wrapper.jar;.\hazelcast\hazelcast-1.9.4.4.jar;.\libs\mysql-connector-java-5.1.15-bin.jar -Djava.net.preferIPv4Stack=true org.alfresco.jlan.app.JLANServer jlanConfigCluster.xml
