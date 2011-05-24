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
package org.alfresco.ibatis;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.alfresco.util.PropertyCheck;
import org.alfresco.util.resource.HierarchicalResourceLoader;
import org.springframework.core.NestedIOException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.orm.ibatis.SqlMapClientFactoryBean;
import org.springframework.util.ObjectUtils;

import com.ibatis.common.xml.NodeletException;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.engine.builder.xml.SqlMapParser;

/**
 * Extends Spring's support for iBatis 2.x by allowing a choice of {@link ResourceLoader}. The
 * {@link #setResourceLoader(HierarchicalResourceLoader) ResourceLoader} will be used to load the <b>SqlMapConfig</b>
 * file, but will also be injected into a {@link HierarchicalSqlMapConfigParser} that will read the individual iBatis
 * resources.
 * 
 * @deprecated see HierarchicalSqlSessionFactoryBeanTest (for MyBatis 3.x)
 * 
 * @author Derek Hulley
 * @since 3.2
 */
public class HierarchicalSqlMapClientFactoryBean extends SqlMapClientFactoryBean
{
    private HierarchicalResourceLoader resourceLoader;

    /**
     * Default constructor
     */
    public HierarchicalSqlMapClientFactoryBean()
    {
    }

    /**
     * Set the resource loader to use. To use the <b>&#35;resource.dialect&#35</b> placeholder, use the
     * {@link HierarchicalResourceLoader}.
     * 
     * @param resourceLoader
     *            the resource loader to use
     */
    public void setResourceLoader(HierarchicalResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        PropertyCheck.mandatory(this, "resourceLoader", resourceLoader);
        super.afterPropertiesSet();
    }

    @Override
    protected SqlMapClient buildSqlMapClient(Resource[] configLocations, Resource[] mappingLocations,
            Properties properties) throws IOException
    {

        if (ObjectUtils.isEmpty(configLocations))
        {
            throw new IllegalArgumentException("At least 1 'configLocation' entry is required");
        }

        SqlMapClient client = null;
        HierarchicalSqlMapConfigParser configParser = new HierarchicalSqlMapConfigParser(resourceLoader);
        for (Resource configLocation : configLocations)
        {
            InputStream is = configLocation.getInputStream();
            try
            {
                client = properties == null ? configParser.parse(is) : configParser.parse(is, properties);
            }
            catch (RuntimeException ex)
            {
                throw new NestedIOException("Failed to parse config resource: " + configLocation, ex.getCause());
            }
        }

        if (mappingLocations != null)
        {
            SqlMapParser mapParser = new SqlMapParser(configParser.state);
            for (Resource mappingLocation : mappingLocations)
            {
                try
                {
                    mapParser.parse(mappingLocation.getInputStream());
                }
                catch (NodeletException ex)
                {
                    throw new NestedIOException("Failed to parse mapping resource: " + mappingLocation, ex);
                }
            }
        }

        return client;
    }
}
