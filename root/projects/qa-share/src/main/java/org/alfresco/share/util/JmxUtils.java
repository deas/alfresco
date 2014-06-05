package org.alfresco.share.util;

import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

public class JmxUtils extends AbstractUtils
{
    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
    private static final Pattern DOMAIN_PATTERN = Pattern.compile("\\w+(\\.\\w+)*(\\.\\w{2,})");

    private static final String JMX_URL_PATTERN = "service:jmx:rmi:///jndi/rmi://%s:%s/alfresco/jmxrmi";
    private static final String JMX_RMI_PORT = "50500";
    private static final String JMX_RMI_USER = "controlRole";
    private static final String JMX_RMI_PASSWORD = "change_asap";

    /**
     * @param objectName
     * @param attributeName
     * @return Object from jmx with expected date in accordance with params
     */
    public static Object getAlfrescoServerProperty(String objectName, String attributeName)
    {
        try
        {
            JMXConnector connector = makeJmxConnector();
            MBeanServerConnection mBSC = connector.getMBeanServerConnection();
            ObjectName objectJmx = new ObjectName(objectName);
            Object result = mBSC.getAttribute(objectJmx, attributeName);
            connector.close();
            return result;
        }
        catch (InstanceNotFoundException ex)
        {
            // assuming that if we've faced an exception due to Alfresco installation onto IBM Webshpere
            // try to query Mbeans according to WAS implementation
            return getAlfrescoServerProperty(getWasObjectName(objectName), attributeName);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex.getMessage());
        }
    }

    /**
     * Set some property in JMX.
     *
     * @param objectName
     * @param attributeName
     * @param attributeValue
     * @return
     */
    public static Object setAlfrescoServerProperty(String objectName, String attributeName, Object attributeValue)
    {
        try
        {
            JMXConnector connector = makeJmxConnector();
            MBeanServerConnection mBSC = connector.getMBeanServerConnection();
            ObjectName objectJmx = new ObjectName(objectName);

            mBSC.setAttribute(ObjectName.getInstance(objectName), new Attribute(attributeName, attributeValue));
            Object result = mBSC.getAttribute(objectJmx, attributeName);
            connector.close();
            return result;
        }
        catch (InstanceNotFoundException ex)
        {
            //assuming that if we've faced an exception due to Alfresco installation onto IBM Webshpere
            //try to query Mbeans according to WAS implementation
            return setAlfrescoServerProperty(getWasObjectName(objectName), attributeName, attributeValue);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * @param objectName
     * @param operation
     */
    public static void invokeAlfrescoServerProperty(String objectName, String operation)
    {
        try
        {
            JMXConnector connector = makeJmxConnector();
            MBeanServerConnection mBSC = connector.getMBeanServerConnection();
            ObjectName objectJmx = new ObjectName(objectName);
            mBSC.invoke(objectJmx, operation, new Object[] { }, new String[] { });
            connector.close();
        }
        catch (InstanceNotFoundException ex)
        {
            //assuming that if we've faced an exception due to Alfresco installation onto IBM Webshpere
            //try to query Mbeans according to WAS implementation
            invokeAlfrescoServerProperty(getWasObjectName(objectName), operation);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * @param objectName
     * @return String name from jmx with expected date in accordance with params
     */
    public static String getWasObjectName(String objectName)
    {
        try
        {
            JMXConnector connector = makeJmxConnector();
            MBeanServerConnection mBSC = connector.getMBeanServerConnection();
            ObjectName wasObjectName = new ObjectName("*" + objectName + "*,cell=*,node=*,process=*");
            Set set = mBSC.queryMBeans(wasObjectName, null);
            ObjectInstance oi = (ObjectInstance) set.toArray()[0];
            ObjectName oName = oi.getObjectName();
            String result = oName.toString();
            connector.close();
            return result;
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex.getMessage());
        }
    }

    private static JMXConnector makeJmxConnector()
    {
        resolveProp();
        try
        {
            String jmxUrlStr = String.format(JMX_URL_PATTERN, getAddress(shareUrl), jmxrmiPort);
            JMXServiceURL jmxUrl = new JMXServiceURL(jmxUrlStr);
            Map<String, String[]> env = new HashMap<>();
            env.put(JMXConnector.CREDENTIALS, new String[] { jmxrmiUser, jmxrmiPassword });
            JMXConnector connector = JMXConnectorFactory.connect(jmxUrl, env);
            return connector;
        }
        catch (Exception e)
        {
            throw new PageException("Can't establish connect with jmx.");
        }
    }

    private static void resolveProp()
    {
        if ((jmxrmiPort == null) || (jmxrmiPort.isEmpty()))
            jmxrmiPort = JMX_RMI_PORT;
        if ((jmxrmiUser == null) || (jmxrmiUser.isEmpty()))
            jmxrmiUser = JMX_RMI_USER;
        if ((jmxrmiPassword == null) || (jmxrmiPassword.isEmpty()))
            jmxrmiPassword = JMX_RMI_PASSWORD;
    }

    private static String getAddress(String url)
    {
        checkNotNull(url);
        Matcher m = IP_PATTERN.matcher(url);
        if (m.find())
        {
            return m.group();
        }
        else
        {
            m = DOMAIN_PATTERN.matcher(url);
            if (m.find())
            {
                return m.group();
            }
        }
        throw new PageOperationException(String.format("Can't parse address from url[%s]", url));
    }

}
