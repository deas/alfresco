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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.alfresco.util.PropertyCheck;
import org.alfresco.util.resource.HierarchicalResourceLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.springframework.core.NestedIOException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Extends the MyBatis-Spring support by allowing a choice of {@link ResourceLoader}. The
 * {@link #setResourceLoader(HierarchicalResourceLoader) ResourceLoader} will be used to load the <b>SqlMapConfig</b>
 * file and use a {@link HierarchicalXMLConfigBuilder} to read the individual MyBatis (3.x) resources.
 * 
 * Pending a better way to extend/override, much of the implementation is a direct copy of the MyBatis-Spring 
 * {@link SqlSessionFactoryBean}; some of the <tt>protected</tt> methods do not have access to the object's state 
 * and can therefore not be overridden successfully. 
 * 
 * This is equivalent to HierarchicalSqlMapClientFactoryBean which extended iBatis (2.x).
 * See also: <a href=https://issues.apache.org/jira/browse/IBATIS-589>IBATIS-589</a>
 * and: <a href=http://code.google.com/p/mybatis/issues/detail?id=21</a>
 * 
 * @author Derek Hulley, janv
 * @since 4.0
 */
//note: effectively extends SqlSessionFactoryBean to use hierarchical resource loader
public class HierarchicalSqlSessionFactoryBean extends SqlSessionFactoryBean
{
    private HierarchicalResourceLoader resourceLoader;

    /**
     * Default constructor
     */
    public HierarchicalSqlSessionFactoryBean()
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
        
        Assert.notNull(dataSource, "Property 'dataSource' is required");
        Assert.notNull(sqlSessionFactoryBuilder, "Property 'sqlSessionFactoryBuilder' is required");
        
        this.sqlSessionFactory = buildSqlSessionFactory();
        
        // MyBatis #179 (should be fixed for 3.0.5)
        this.sqlSessionFactory.getConfiguration().buildAllStatements();
    }

        private final Log logger = LogFactory.getLog(getClass());

        private Resource configLocation;

        private Resource[] mapperLocations;

        private DataSource dataSource;

        private TransactionFactory transactionFactory;

        private Properties configurationProperties;

        private SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();

        private SqlSessionFactory sqlSessionFactory;

        private String environment = SqlSessionFactoryBean.class.getSimpleName();
        
        private boolean useLocalCaches = false;
        
        public void setUseLocalCaches(boolean useLocalCaches)
		{
			this.useLocalCaches = useLocalCaches;
		}

		/**
         * Set the location of the MyBatis {@code SqlSessionFactory} config file. A typical value is
         * "WEB-INF/mybatis-configuration.xml".
         */
        public void setConfigLocation(Resource configLocation) {
            this.configLocation = configLocation;
        }

        /**
         * Set locations of MyBatis mapper files that are going to be merged into the {@code SqlSessionFactory}
         * configuration at runtime.
         *
         * This is an alternative to specifying "&lt;sqlmapper&gt;" entries in an MyBatis config file.
         * This property being based on Spring's resource abstraction also allows for specifying
         * resource patterns here: e.g. "classpath*:sqlmap/*-mapper.xml".
         */
        public void setMapperLocations(Resource[] mapperLocations) {
            this.mapperLocations = mapperLocations;
        }

        /**
         * Set optional properties to be passed into the SqlSession configuration, as alternative to a
         * {@code &lt;properties&gt;} tag in the configuration xml file. This will be used to
         * resolve placeholders in the config file.
         */
        public void setConfigurationProperties(Properties sqlSessionFactoryProperties) {
            this.configurationProperties = sqlSessionFactoryProperties;
        }

        /**
         * Set the JDBC {@code DataSource} that this instance should manage transactions for. The {@code DataSource}
         * should match the one used by the {@code SqlSessionFactory}: for example, you could specify the same
         * JNDI DataSource for both.
         *
         * A transactional JDBC {@code Connection} for this {@code DataSource} will be provided to application code
         * accessing this {@code DataSource} directly via {@code DataSourceUtils} or {@code DataSourceTransactionManager}.
         *
         * The {@code DataSource} specified here should be the target {@code DataSource} to manage transactions for, not
         * a {@code TransactionAwareDataSourceProxy}. Only data access code may work with
         * {@code TransactionAwareDataSourceProxy}, while the transaction manager needs to work on the
         * underlying target {@code DataSource}. If there's nevertheless a {@code TransactionAwareDataSourceProxy}
         * passed in, it will be unwrapped to extract its target {@code DataSource}.
         *
         */
        public void setDataSource(DataSource dataSource) {
            if (dataSource instanceof TransactionAwareDataSourceProxy) {
                // If we got a TransactionAwareDataSourceProxy, we need to perform
                // transactions for its underlying target DataSource, else data
                // access code won't see properly exposed transactions (i.e.
                // transactions for the target DataSource).
                this.dataSource = ((TransactionAwareDataSourceProxy) dataSource).getTargetDataSource();
            } else {
                this.dataSource = dataSource;
            }
        }

        /**
         * Sets the {@code SqlSessionFactoryBuilder} to use when creating the {@code SqlSessionFactory}.
         *
         * This is mainly meant for testing so that mock SqlSessionFactory classes can be injected. By
         * default, {@code SqlSessionFactoryBuilder} creates {@code DefaultSqlSessionFactory} instances.
         *
         */
        public void setSqlSessionFactoryBuilder(SqlSessionFactoryBuilder sqlSessionFactoryBuilder) {
            this.sqlSessionFactoryBuilder = sqlSessionFactoryBuilder;
        }

        /**
         * Set the MyBatis TransactionFactory to use. Default is {@code SpringManagedTransactionFactory}
         *
         * The default {@code SpringManagedTransactionFactory} should be appropriate for all cases: 
         * be it Spring transaction management, EJB CMT or plain JTA. If there is no active transaction, 
         * SqlSession operations will execute SQL statements non-transactionally.
         *
         * <b>It is strongly recommended to use the default {@code TransactionFactory}.</b> If not used, any
         * attempt at getting an SqlSession through Spring's MyBatis framework will throw an exception if
         * a transaction is active.
         *
         * @see SpringManagedTransactionFactory
         * @param transactionFactory the MyBatis TransactionFactory
         */
        public void setTransactionFactory(TransactionFactory transactionFactory) {
            this.transactionFactory = transactionFactory;
        }

        /**
         * <b>NOTE:</b> This class <em>overrides</em> any {@code Environment} you have set in the MyBatis 
         * config file. This is used only as a placeholder name. The default value is
         * {@code SqlSessionFactoryBean.class.getSimpleName()}.
         *
         * @param environment the environment name
         */
        public void setEnvironment(String environment) {
            this.environment = environment;
        }

        /**
         * Build a {@code SqlSessionFactory} instance.
         *
         * The default implementation uses the standard MyBatis {@code XMLConfigBuilder} API to build a
         * {@code SqlSessionFactory} instance based on an Reader.
         *
         * @return SqlSessionFactory
         * @throws IOException if loading the config file failed
         */
        protected SqlSessionFactory buildSqlSessionFactory() throws IOException {

            HierarchicalXMLConfigBuilder xmlConfigBuilder;
            Configuration configuration;

            if (this.configLocation != null) {
                try {
                    
                    // note: overridden here
                    xmlConfigBuilder = new HierarchicalXMLConfigBuilder(resourceLoader, this.configLocation.getInputStream(), null,
                    		this.configurationProperties, this.useLocalCaches);

                    configuration = xmlConfigBuilder.parse();
                } catch (Exception ex) {
                    throw new NestedIOException("Failed to parse config resource: "
                            + this.configLocation, ex);
                } finally {
                    ErrorContext.instance().reset();
                }

                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Parsed configuration file: '" + this.configLocation + "'");
                }
            } else {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Property 'configLocation' not specified, using default MyBatis Configuration");
                }
                configuration = new Configuration();
            }

            if (this.transactionFactory == null) {
                this.transactionFactory = new SpringManagedTransactionFactory(this.dataSource);
            }
            
            Environment environment = new Environment(this.environment, this.transactionFactory, this.dataSource);

            configuration.setEnvironment(environment);

            if (!ObjectUtils.isEmpty(this.mapperLocations)) {
                Map<String, XNode> sqlFragments = new HashMap<String, XNode>();

                for (Resource mapperLocation : this.mapperLocations) {
                    if (mapperLocation == null) {
                        continue;
                    }

                    // MyBatis holds a Map using "resource" name as a key.
                    // If a mapper file is loaded, it searches for a mapper interface type.
                    // If the type is found then it tries to load the mapper file again looking for this:
                    //
                    //   String xmlResource = type.getName().replace('.', '/') + ".xml";
                    //
                    // So if a mapper interface exists, resource cannot be an absolute path.
                    // Otherwise MyBatis will throw an exception because
                    // it will load both a mapper interface and the mapper xml file,
                    // and throw an exception telling that a mapperStatement cannot be loaded twice.
                    String path;
                    if (mapperLocation instanceof ClassPathResource) {
                        path = ((ClassPathResource) mapperLocation).getPath();
                    } else {
                        // this won't work if there is also a mapper interface in classpath
                        path = mapperLocation.toString();
                    }

                    try {
                        XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(mapperLocation.getInputStream(), configuration, path, sqlFragments);
                        xmlMapperBuilder.parse();
                    } catch (Exception e) {
                        throw new NestedIOException("Failed to parse mapping resource: '" + mapperLocation + "'", e);
                    } finally {
                        ErrorContext.instance().reset();
                    }

                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Parsed mapper file: '" + mapperLocation + "'");
                    }
                }
            } else {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Property 'mapperLocations' was not specified, only MyBatis mapper files specified in the config xml were loaded");
                }
            }

            return this.sqlSessionFactoryBuilder.build(configuration);
        }

        /**
         * {@inheritDoc}
         */
        public SqlSessionFactory getObject() throws Exception {
            if (this.sqlSessionFactory == null) {
                afterPropertiesSet();
            }

            return this.sqlSessionFactory;
        }

        /**
         * {@inheritDoc}
         */
        public Class<? extends SqlSessionFactory> getObjectType() {
            return this.sqlSessionFactory == null ? SqlSessionFactory.class : this.sqlSessionFactory.getClass();
        }

    }
