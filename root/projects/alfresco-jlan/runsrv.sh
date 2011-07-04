# ! /bin/sh

echo "Alfresco JLAN Server starting, enter 'x' to shutdown server, 'r' to restart server ..."
java -Djava.library.path=./jni -cp ./jars/alfresco-jlan.jar:./libs/cryptix-jce-provider.jar:./service/wrapper.jar:./hazelcast/hazelcast-1.9.2.1.jar org.alfresco.jlan.app.JLANServer jlanConfig.xml
