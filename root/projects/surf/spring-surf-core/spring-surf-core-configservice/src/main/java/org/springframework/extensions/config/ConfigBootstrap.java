/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.extensions.config;

import java.util.List;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.extensions.config.source.UrlConfigSource;

/**
 * Spring bean used to deploy additional config files into the
 * injected config service.
 *
 * @author Gavin Cornwell
 */
public class ConfigBootstrap implements BeanNameAware, ConfigDeployer
{
    /** The bean name. */
    private String beanName;
    
    protected ConfigService configService;
    protected List<String> configs;
    
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.BeanNameAware#setBeanName(java.lang.String)
     */
    public void setBeanName(String name)
    {
        this.beanName = name;
    }
    
    /**
     * Set the configs
     * 
     * @param configs the configs
     */
    public void setConfigs(List<String> configs)
    {
        this.configs = configs;
    }
    
    /**
     * Sets the ConfigService instance to deploy to
     * 
     * @param configService ConfigService instance to deploy to
     */
    public void setConfigService(ConfigService configService)
    {
        this.configService = configService;
    }
    
    /**
     * Method called by ConfigService when the configuration files
     * represented by this ConfigDeployer need to be initialised.
     * 
     *  @return List of ConfigDeployment objects
     */
    public List<ConfigDeployment> initConfig()
    {
        List<ConfigDeployment> deployed = null;
        
        if (configService != null && this.configs != null && this.configs.size() != 0)
        {
            UrlConfigSource configSource = new UrlConfigSource(this.configs, true);
            deployed = configService.appendConfig(configSource);
        }
        
        return deployed;
    }

    /**
     * Registers this object with the injected ConfigService
     */
    public void register()
    {
        if (configService == null)
        {
            throw new ConfigException("Config service must be provided");
        }
        
        configService.addDeployer(this);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.config.ConfigDeployer#getSortKey()
     */
    public String getSortKey()
    {
        return this.beanName;
    }
}
