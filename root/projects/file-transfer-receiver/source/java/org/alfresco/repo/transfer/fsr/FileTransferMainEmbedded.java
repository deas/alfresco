package org.alfresco.repo.transfer.fsr;

import java.io.File;

import org.apache.catalina.startup.Tomcat;

public class FileTransferMainEmbedded
{
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        String currentDir = new File(".").getCanonicalPath();
        String webApp = "file-transfer-receiver.war";

        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir(currentDir);
        tomcat.setPort(9090);
        tomcat.addWebapp("/alfresco-ftr", webApp);
        tomcat.start();
        tomcat.getServer().await();
    }
}
