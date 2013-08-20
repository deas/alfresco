package org.alfresco.module.vti.management;

import org.alfresco.repo.admin.SysAdminParams;

public class SPPMBean implements SPP
{
    private int vtiServerPort = 0;
    private String vtiServerHost = "${localname}";
    private String vtiServerProtocol = "http";
    private String contextPath;

    @Override
    public boolean isEnabled()
    {
        return true;
    } 
    
    public int getPort()
    {
        return vtiServerPort;
    }
    
    public void setPort(int vtiServerPort)
    {
        this.vtiServerPort = vtiServerPort;
    }

    public void setHost(String vtiServerHost)
    {
        this.vtiServerHost = vtiServerHost;
    }
    
    public String getHost()
    {
        return vtiServerHost;
    }
    
    public void setProtocol(String vtiServerProtocol)
    {
        this.vtiServerProtocol = vtiServerProtocol;
    }
    



}
