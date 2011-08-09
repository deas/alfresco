/*
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
 */
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
