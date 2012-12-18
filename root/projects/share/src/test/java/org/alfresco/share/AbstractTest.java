package org.alfresco.share;
/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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


import org.alfresco.share.util.ShareTestProperty;
import org.alfresco.webdrone.AlfrescoVersion;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneUtil;
import org.alfresco.webdrone.share.DashBoardPage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Abstract test holds all common methods and functionality to test against
 * Benchmark Grid tests.
 * 
 * @author Michael Suzuki
 */
public abstract class AbstractTest
{
    private static Log logger = LogFactory.getLog(AbstractTest.class);
    private static ApplicationContext ctx;
    protected static String shareUrl;
    protected static WebDrone drone;
    protected static boolean isCloud;
    protected static Integer maxIteration;
    protected static String username;
    protected static String password;
    protected static AlfrescoVersion alfrescoVersion;
    
    
    @BeforeClass
    public static void setupContext()
    {
        ctx = new ClassPathXmlApplicationContext(new String[] {"share-test-context.xml"});
        ShareTestProperty t = ctx.getBean(ShareTestProperty.class);
        shareUrl = t.getShareUrl();
        username = t.getUsername();
        password = t.getPassword();
        alfrescoVersion = t.getAlfrescoVersion();
        isCloud = alfrescoVersion.isCloud();
        maxIteration = t.getMaxIteration();
    }

    public static void getWebDrone() throws Exception
    {
        drone = (WebDrone) ctx.getBean("webDrone");
    }

    public static void quitWebDrone()
    {
        if (logger.isTraceEnabled())
        {
            logger.trace("shutting web drone");
        }
        // Close the browser
        if (drone != null)
        {
            WebDroneUtil.logout(drone);
            drone.quit();
        }
    }
    /**
     * Helper to log admin user into dashboard.
     * 
     * @return DashBoardPage page object.
     * @throws Exception if error
     */
    public static DashBoardPage loginAs(final String... userInfo) throws Exception
    {
        if(shareUrl == null)
        {
         logger.info("null shareUrl");   
        }
        return WebDroneUtil.loginAs(drone, shareUrl, userInfo).render();
    }
}
