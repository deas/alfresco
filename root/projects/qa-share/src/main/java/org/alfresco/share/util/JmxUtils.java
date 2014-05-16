package org.alfresco.share.util;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JmxUtils extends AbstractUtils
{

    protected static String jmxrmi_port = "50500";
    private static String jmxrmi_user = "controlRole";
    private static String jmxrmi_password = "change_asap";
    private static String alf_host = findIP(shareUrl);

    /**
     * @param objectname
     * @param attributename
     * @return Object from jmx with expected date in accordance with params
     */
    public static Object getAlfrescoServerProperty(String objectname, String attributename)
    {
        Object rv;
        JMXConnector connector;
        MBeanServerConnection mbsc;
        try
        {
            JMXServiceURL jmxurl;
            if ((jmxrmiPort == null) || (jmxrmiPort.isEmpty()))
                jmxrmiPort = jmxrmi_port;
            if ((jmxrmiUser == null) || (jmxrmiUser.isEmpty()))
                jmxrmiUser = jmxrmi_user;
            if ((jmxrmiPassword == null) || (jmxrmiPassword.isEmpty()))
                jmxrmiPassword = jmxrmi_password;
            jmxurl = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + alf_host + ":" + jmxrmiPort + "/alfresco/jmxrmi");
            Map<String, String[]> env = new HashMap<>();
            env.put(JMXConnector.CREDENTIALS, new String[] { jmxrmiUser, jmxrmiPassword });
            connector = JMXConnectorFactory.connect(jmxurl, env);
            mbsc = connector.getMBeanServerConnection();
            ObjectName objectName = new ObjectName(objectname);
            rv = mbsc.getAttribute(objectName, attributename);

            connector.close();

        }
        catch (InstanceNotFoundException ex)
        {
            // assuming that if we've faced an exception due to Alfresco installation onto IBM Webshpere
            // try to query Mbeans according to WAS implementation
            return getAlfrescoServerProperty(getWasObjectName(objectname), attributename);

        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex.getMessage());
        }

        return rv;
    }

    /**
     * @param objectname
     * @return String name from jmx with expected date in accordance with params
     */
    public static String getWasObjectName(String objectname)
    {
        String rv;
        JMXConnector connector;
        MBeanServerConnection mbsc;
        try
        {
            if ((jmxrmiPort == null) || (jmxrmiPort.isEmpty()))
                jmxrmiPort = jmxrmi_port;
            if ((jmxrmiUser == null) || (jmxrmiUser.isEmpty()))
                jmxrmiUser = jmxrmi_user;
            if ((jmxrmiPassword == null) || (jmxrmiPassword.isEmpty()))
                jmxrmiPassword = jmxrmi_password;

            JMXServiceURL jmxurl = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + alf_host + ":" + jmxrmiPort + "/alfresco/jmxrmi");
            Map<String, String[]> env = new HashMap<>();
            env.put(JMXConnector.CREDENTIALS, new String[] { jmxrmiUser, jmxrmiPassword });
            connector = JMXConnectorFactory.connect(jmxurl, env);
            mbsc = connector.getMBeanServerConnection();
            ObjectName wasobjectname = new ObjectName("*" + objectname + "*,cell=*,node=*,process=*");
            Set set = mbsc.queryMBeans(wasobjectname, null);
            ObjectInstance oi = (ObjectInstance) set.toArray()[0];
            ObjectName oname = oi.getObjectName();
            connector.close();
            rv = oname.toString();

        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex.getMessage());
        }

        return rv;
    }

    /**
     * @param in
     * @return IP from string by format in accordance with pattern
     */
    private static String findIP(String in)
    {
        Matcher m = Pattern.compile("(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})").matcher(in);
        if (m.find())
        {
            return m.group(0);
        }
        return null;
    }
}
