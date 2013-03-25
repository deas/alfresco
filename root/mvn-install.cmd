mvn install:install-file -Dfile=projects/core/build/dist/alfresco-core-4.1.3.jar -DpomFile=projects/core/pom.xml
mvn install:install-file -Dfile=projects/data-model/build/dist/alfresco-data-model-4.1.3.jar -DpomFile=projects/data-model/pom.xml
mvn install:install-file -Dfile=projects/deployment/build/dist/alfresco-deployment-4.1.3.jar -DpomFile=projects/deployment/pom.xml
mvn install:install-file -Dfile=projects/alfresco-jlan/build/dist/alfresco-jlan-embed-4.1.3.jar -DpomFile=projects/alfresco-jlan/pom.xml
mvn install:install-file -Dfile=projects/mbeans/build/dist/alfresco-mbeans-4.1.3.jar -DpomFile=projects/mbeans/pom.xml
mvn install:install-file -Dfile=projects/remote-api/build/dist/alfresco-remote-api-4.1.3.jar -DpomFile=projects/remote-api/pom.xml
mvn install:install-file -Dfile=projects/repository/build/dist/alfresco-repository-4.1.3.jar -DpomFile=projects/repository/pom.xml
mvn install:install-file -Dfile=projects/wdr-deployment/build/dist/alfresco-wdr-deployment.jar -DpomFile=projects/wdr-deployment/pom.xml
mvn install:install-file -Dfile=projects/web-framework-commons/build/dist/alfresco-web-framework-commons-4.1.3.jar -DpomFile=projects/web-framework-commons/pom.xml
mvn install:install-file -Dfile=projects/web-client/build/dist/alfresco-web-client-4.1.3.jar -DpomFile=projects/web-client/pom.xml -Dclassifier=classes -Dpackaging=jar
mvn install:install-file -Dfile=projects/web-client/build/dist/alfresco.war -DpomFile=projects/web-client/pom.xml
mvn install:install-file -Dfile=projects/slingshot/build/dist/alfresco-share-4.1.3.jar -DpomFile=projects/slingshot/pom.xml -Dclassifier=classes -Dpackaging=jar
mvn install:install-file -Dfile=projects/slingshot/build/dist/share.war -DpomFile=projects/slingshot/pom.xml

mvn install:install-file -Dfile=enterpriseprojects/repository/build/dist/alfresco-enterprise-repo-4.1.3.jar -DpomFile=enterpriseprojects/repository/pom.xml
mvn install:install-file -Dfile=enterpriseprojects/remote-api/build/dist/alfresco-enterprise-remote-api-4.1.3.jar -DpomFile=enterpriseprojects/remote-api/pom.xml
