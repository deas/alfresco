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

import java.io.InputStream;
import java.util.Properties;

import org.alfresco.util.resource.HierarchicalResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.w3c.dom.Node;

import com.ibatis.common.resources.Resources;
import com.ibatis.common.xml.Nodelet;
import com.ibatis.common.xml.NodeletParser;
import com.ibatis.common.xml.NodeletUtils;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapException;
import com.ibatis.sqlmap.engine.builder.xml.SqlMapClasspathEntityResolver;
import com.ibatis.sqlmap.engine.builder.xml.SqlMapConfigParser;
import com.ibatis.sqlmap.engine.builder.xml.SqlMapParser;
import com.ibatis.sqlmap.engine.builder.xml.XmlParserState;
import com.ibatis.sqlmap.engine.config.SqlMapConfiguration;
import com.ibatis.sqlmap.engine.datasource.DataSourceFactory;
import com.ibatis.sqlmap.engine.mapping.result.ResultObjectFactory;
import com.ibatis.sqlmap.engine.transaction.TransactionConfig;
import com.ibatis.sqlmap.engine.transaction.TransactionManager;

/**
 * Extends the SqlMapConfigParser to allow the selection of a {@link ResourceLoader}
 * that will be used to load the resources specified in the <b>sqlMap</b>'s <b>resource</b>.
 * <p>
 * By using the <b>resource.dialect</b> placeholder with hierarchical resource loading,
 * different resource files can be picked up for different dialects. This reduces duplication
 * when supporting multiple database configurations.
 * <pre>
 * &lt;sqlMapConfig&gt;
 *    &lt;sqlMap resource=&quot;org/x/y/#resource.dialect#/View1.xml&quot;/&gt;
 *    &lt;sqlMap resource=&quot;org/x/y/#resource.dialect#/View2.xml&quot;/&gt;
 * &lt;/sqlMapConfig&gt;
 * <p/>
 * Much of the implementation is a direct copy of the iBatis 2.x {@link SqlMapConfigParser}; some
 * of the <tt>protected</tt> methods do not have access to the object's state and can therefore
 * not be overridden successfully: <a href=https://issues.apache.org/jira/browse/IBATIS-589>IBATIS-589</a>
 * 
 * @deprecated see HierarchicalXMLConfigBuilder (for MyBatis 3.x)
 * 
 * @author Derek Hulley
 * @since 3.2
 */
public class HierarchicalSqlMapConfigParser
{
    final private HierarchicalResourceLoader resourceLoader;

    protected final NodeletParser parser = new NodeletParser();
    protected XmlParserState state = new XmlParserState();

    /**
     * @param resourceLoader the resource loader that supports the
     */
    public HierarchicalSqlMapConfigParser(HierarchicalResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;

        parser.setValidation(true);
        parser.setEntityResolver(new SqlMapClasspathEntityResolver());

        addSqlMapConfigNodelets();
        addGlobalPropNodelets();
        addSettingsNodelets();
        addTypeAliasNodelets();
        addTypeHandlerNodelets();
        addTransactionManagerNodelets();
        addSqlMapNodelets();
        addResultObjectFactoryNodelets();
    }

    public SqlMapClient parse(InputStream inputStream, Properties props)
    {
        if (props != null)
            state.setGlobalProps(props);
        return parse(inputStream);
    }

    public SqlMapClient parse(InputStream inputStream)
    {
        try
        {
            parser.parse(inputStream);
            return state.getConfig().getClient();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error occurred.  Cause: " + e, e);
        }
    }

    private void addSqlMapConfigNodelets()
    {
        parser.addNodelet("/sqlMapConfig/end()", new Nodelet()
        {
            public void process(Node node) throws Exception
            {
                state.getConfig().finalizeSqlMapConfig();
            }
        });
    }

    private void addGlobalPropNodelets()
    {
        parser.addNodelet("/sqlMapConfig/properties", new Nodelet()
        {
            public void process(Node node) throws Exception
            {
                Properties attributes = NodeletUtils.parseAttributes(node, state.getGlobalProps());
                String resource = attributes.getProperty("resource");
                String url = attributes.getProperty("url");
                state.setGlobalProperties(resource, url);
            }
        });
    }

    private void addSettingsNodelets()
    {
        parser.addNodelet("/sqlMapConfig/settings", new Nodelet()
        {
            public void process(Node node) throws Exception
            {
                Properties attributes = NodeletUtils.parseAttributes(node, state.getGlobalProps());
                SqlMapConfiguration config = state.getConfig();

                String classInfoCacheEnabledAttr = attributes.getProperty("classInfoCacheEnabled");
                boolean classInfoCacheEnabled = (classInfoCacheEnabledAttr == null || "true".equals(classInfoCacheEnabledAttr));
                config.setClassInfoCacheEnabled(classInfoCacheEnabled);

                String lazyLoadingEnabledAttr = attributes.getProperty("lazyLoadingEnabled");
                boolean lazyLoadingEnabled = (lazyLoadingEnabledAttr == null || "true".equals(lazyLoadingEnabledAttr));
                config.setLazyLoadingEnabled(lazyLoadingEnabled);

                String statementCachingEnabledAttr = attributes.getProperty("statementCachingEnabled");
                boolean statementCachingEnabled = (statementCachingEnabledAttr == null || "true".equals(statementCachingEnabledAttr));
                config.setStatementCachingEnabled(statementCachingEnabled);

                String cacheModelsEnabledAttr = attributes.getProperty("cacheModelsEnabled");
                boolean cacheModelsEnabled = (cacheModelsEnabledAttr == null || "true".equals(cacheModelsEnabledAttr));
                config.setCacheModelsEnabled(cacheModelsEnabled);

                String enhancementEnabledAttr = attributes.getProperty("enhancementEnabled");
                boolean enhancementEnabled = (enhancementEnabledAttr == null || "true".equals(enhancementEnabledAttr));
                config.setEnhancementEnabled(enhancementEnabled);

                String useColumnLabelAttr = attributes.getProperty("useColumnLabel");
                boolean useColumnLabel = (useColumnLabelAttr == null || "true".equals(useColumnLabelAttr));
                config.setUseColumnLabel(useColumnLabel);

                String forceMultipleResultSetSupportAttr = attributes.getProperty("forceMultipleResultSetSupport");
                boolean forceMultipleResultSetSupport = "true".equals(forceMultipleResultSetSupportAttr);
                config.setForceMultipleResultSetSupport(forceMultipleResultSetSupport);

                String defaultTimeoutAttr = attributes.getProperty("defaultStatementTimeout");
                Integer defaultTimeout = defaultTimeoutAttr == null ? null : Integer.valueOf(defaultTimeoutAttr);
                config.setDefaultStatementTimeout(defaultTimeout);

                String useStatementNamespacesAttr = attributes.getProperty("useStatementNamespaces");
                boolean useStatementNamespaces = "true".equals(useStatementNamespacesAttr);
                state.setUseStatementNamespaces(useStatementNamespaces);
            }
        });
    }

    private void addTypeAliasNodelets()
    {
        parser.addNodelet("/sqlMapConfig/typeAlias", new Nodelet()
        {
            public void process(Node node) throws Exception
            {
                Properties prop = NodeletUtils.parseAttributes(node, state.getGlobalProps());
                String alias = prop.getProperty("alias");
                String type = prop.getProperty("type");
                state.getConfig().getTypeHandlerFactory().putTypeAlias(alias, type);
            }
        });
    }

    private void addTypeHandlerNodelets()
    {
        parser.addNodelet("/sqlMapConfig/typeHandler", new Nodelet()
        {
            public void process(Node node) throws Exception
            {
                Properties prop = NodeletUtils.parseAttributes(node, state.getGlobalProps());
                String jdbcType = prop.getProperty("jdbcType");
                String javaType = prop.getProperty("javaType");
                String callback = prop.getProperty("callback");

                javaType = state.getConfig().getTypeHandlerFactory().resolveAlias(javaType);
                callback = state.getConfig().getTypeHandlerFactory().resolveAlias(callback);

                state.getConfig().newTypeHandler(Resources.classForName(javaType), jdbcType, Resources.instantiate(callback));
            }
        });
    }

    private void addTransactionManagerNodelets()
    {
        parser.addNodelet("/sqlMapConfig/transactionManager/property", new Nodelet()
        {
            public void process(Node node) throws Exception
            {
                Properties attributes = NodeletUtils.parseAttributes(node, state.getGlobalProps());
                String name = attributes.getProperty("name");
                String value = NodeletUtils.parsePropertyTokens(attributes.getProperty("value"), state.getGlobalProps());
                state.getTxProps().setProperty(name, value);
            }
        });
        parser.addNodelet("/sqlMapConfig/transactionManager/end()", new Nodelet()
        {
            public void process(Node node) throws Exception
            {
                Properties attributes = NodeletUtils.parseAttributes(node, state.getGlobalProps());
                String type = attributes.getProperty("type");
                boolean commitRequired = "true".equals(attributes.getProperty("commitRequired"));

                state.getConfig().getErrorContext().setActivity("configuring the transaction manager");
                type = state.getConfig().getTypeHandlerFactory().resolveAlias(type);
                TransactionManager txManager;
                try
                {
                    state.getConfig().getErrorContext().setMoreInfo("Check the transaction manager type or class.");
                    TransactionConfig config = (TransactionConfig) Resources.instantiate(type);
                    config.setDataSource(state.getDataSource());
                    state.getConfig().getErrorContext().setMoreInfo("Check the transactio nmanager properties or configuration.");
                    config.setProperties(state.getTxProps());
                    config.setForceCommit(commitRequired);
                    config.setDataSource(state.getDataSource());
                    state.getConfig().getErrorContext().setMoreInfo(null);
                    txManager = new TransactionManager(config);
                }
                catch (Exception e)
                {
                    if (e instanceof SqlMapException)
                    {
                        throw (SqlMapException) e;
                    }
                    else
                    {
                        throw new SqlMapException("Error initializing TransactionManager.  Could not instantiate TransactionConfig.  Cause: " + e, e);
                    }
                }
                state.getConfig().setTransactionManager(txManager);
            }
        });
        parser.addNodelet("/sqlMapConfig/transactionManager/dataSource/property", new Nodelet()
        {
            public void process(Node node) throws Exception
            {
                Properties attributes = NodeletUtils.parseAttributes(node, state.getGlobalProps());
                String name = attributes.getProperty("name");
                String value = NodeletUtils.parsePropertyTokens(attributes.getProperty("value"), state.getGlobalProps());
                state.getDsProps().setProperty(name, value);
            }
        });
        parser.addNodelet("/sqlMapConfig/transactionManager/dataSource/end()", new Nodelet()
        {
            public void process(Node node) throws Exception
            {
                state.getConfig().getErrorContext().setActivity("configuring the data source");

                Properties attributes = NodeletUtils.parseAttributes(node, state.getGlobalProps());

                String type = attributes.getProperty("type");
                Properties props = state.getDsProps();

                type = state.getConfig().getTypeHandlerFactory().resolveAlias(type);
                try
                {
                    state.getConfig().getErrorContext().setMoreInfo("Check the data source type or class.");
                    DataSourceFactory dsFactory = (DataSourceFactory) Resources.instantiate(type);
                    state.getConfig().getErrorContext().setMoreInfo("Check the data source properties or configuration.");
                    dsFactory.initialize(props);
                    state.setDataSource(dsFactory.getDataSource());
                    state.getConfig().getErrorContext().setMoreInfo(null);
                }
                catch (Exception e)
                {
                    if (e instanceof SqlMapException)
                    {
                        throw (SqlMapException) e;
                    }
                    else
                    {
                        throw new SqlMapException("Error initializing DataSource.  Could not instantiate DataSourceFactory.  Cause: " + e, e);
                    }
                }
            }
        });
    }

    protected void addSqlMapNodelets()
    {
        parser.addNodelet("/sqlMapConfig/sqlMap", new Nodelet()
        {
            public void process(Node node) throws Exception
            {
                state.getConfig().getErrorContext().setActivity("loading the SQL Map resource");

                Properties attributes = NodeletUtils.parseAttributes(node, state.getGlobalProps());

                String resourcePath = attributes.getProperty("resource");
                String url = attributes.getProperty("url");

                InputStream inputStream = null;
                if (resourcePath != null)
                {
                    state.getConfig().getErrorContext().setResource(resourcePath);
                    Resource resource = resourceLoader.getResource(resourcePath);
                    if (resource != null && resource.exists())
                    {
                        inputStream = resource.getInputStream();
                    }
                }
                else if (url != null)
                {
                    state.getConfig().getErrorContext().setResource(url);
                    inputStream = Resources.getUrlAsStream(url);
                }
                else
                {
                    throw new SqlMapException("The <sqlMap> element requires either a resource or a url attribute.");
                }
                // Check the stream
                if (inputStream == null)
                {
                    throw new SqlMapException(
                            "The <sqlMap> resource is missing: " +
                            (resourcePath == null ? "" : resourcePath) +
                            (url == null ? "" : url));
                }

                new SqlMapParser(state).parse(inputStream);
            }
        });
    }

    private void addResultObjectFactoryNodelets()
    {
        parser.addNodelet("/sqlMapConfig/resultObjectFactory", new Nodelet()
        {
            public void process(Node node) throws Exception
            {
                Properties attributes = NodeletUtils.parseAttributes(node, state.getGlobalProps());
                String type = attributes.getProperty("type");

                state.getConfig().getErrorContext().setActivity("configuring the Result Object Factory");
                ResultObjectFactory rof;
                try
                {
                    rof = (ResultObjectFactory) Resources.instantiate(type);
                    state.getConfig().setResultObjectFactory(rof);
                }
                catch (Exception e)
                {
                    throw new SqlMapException("Error instantiating resultObjectFactory: " + type, e);
                }

            }
        });
        parser.addNodelet("/sqlMapConfig/resultObjectFactory/property", new Nodelet()
        {
            public void process(Node node) throws Exception
            {
                Properties attributes = NodeletUtils.parseAttributes(node, state.getGlobalProps());
                String name = attributes.getProperty("name");
                String value = NodeletUtils.parsePropertyTokens(attributes.getProperty("value"), state.getGlobalProps());
                state.getConfig().getDelegate().getResultObjectFactory().setProperty(name, value);
            }
        });
    }

}
