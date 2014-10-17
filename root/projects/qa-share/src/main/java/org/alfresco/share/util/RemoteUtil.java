package org.alfresco.share.util;

import org.alfresco.po.share.util.PageUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RemoteUtil extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(RemoteUtil.class);

    private static SshCommandProcessor commandProcessor;

    private static void initConnection()
    {
        commandProcessor = new SshCommandProcessor();
        commandProcessor.connect();
    }

    public static void applyIptables(String dropPocketsNode)
    {
        initConnection();
        String server = JmxUtils.getAddress(dropPocketsNode);
        commandProcessor.executeCommand("service iptables start");
        commandProcessor.executeCommand("iptables -A INPUT -p tcp -s " + server + " -j DROP");
        commandProcessor.executeCommand("iptables -A OUTPUT -p tcp -s " + server + " -j DROP");
        logger.info("Temporarily apply a rule using iptables to drop all packets coming from (outcoming to) " + server + " to " + sshHost);
    }

    public static void removeIpTables(String acceptPocketsNode)
    {

        initConnection();
        String server = PageUtils.getAddress(acceptPocketsNode).replaceAll("(:\\d{1,5})?", "");
        commandProcessor.executeCommand("iptables -D INPUT -s " + server + " -j DROP");
        commandProcessor.executeCommand("iptables -F");
        commandProcessor.executeCommand("service iptables stop");
        logger.info("Turn the filter off iptables to drop all packets coming from " + server + " to " + sshHost);
    }

    public static String getCygwinPath(String winPath)
    {
        return String.format("`cygpath -u '%s'`", winPath);
    }
}
