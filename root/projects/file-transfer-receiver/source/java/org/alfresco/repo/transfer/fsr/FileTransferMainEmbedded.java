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
        String tomcatDir = currentDir + File.separatorChar + "apache-tomcat-7.0.16";
        String webRoot = currentDir + File.separatorChar + "apache-tomcat-7.0.16/webapps/ftreceiver";

        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir(tomcatDir);
        tomcat.setPort(9090);
        // tomcat.addWebapp("/examplewebapp", webRoot);
        // or we could do this for root context:
        tomcat.addWebapp("/ftreceiver", webRoot);
        tomcat.start();
        tomcat.getServer().await();

    }

}
