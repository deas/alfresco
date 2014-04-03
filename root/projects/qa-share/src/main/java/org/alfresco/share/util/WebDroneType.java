package org.alfresco.share.util;

public enum WebDroneType
{

    DownLoadDrone("downloadWebDrone"),
    FrenchDrone("frenchDrone"),
    HybridDrone("hybridDrone");
    
    private String name;
    
    private WebDroneType(String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        return this.name;
    }
    
}
