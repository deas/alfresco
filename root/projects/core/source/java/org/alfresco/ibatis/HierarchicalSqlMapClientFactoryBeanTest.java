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

import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;

import junit.framework.TestCase;

import org.alfresco.util.resource.HierarchicalResourceLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapException;
import com.ibatis.sqlmap.engine.impl.ExtendedSqlMapClient;
import com.ibatis.sqlmap.engine.mapping.statement.MappedStatement;

/**
 * @see HierarchicalSqlMapClientFactoryBean
 * @see HierarchicalSqlMapConfigParser
 * @see HierarchicalResourceLoader
 * 
 * @author Derek Hulley
 * @since 3.2 (Mobile)
 */
@SuppressWarnings("deprecation")
public class HierarchicalSqlMapClientFactoryBeanTest extends TestCase
{
    private static final String QUERY_OBJECT = Object.class.getName();
    private static final String QUERY_ABSTRACTCOLLECTION = AbstractCollection.class.getName();
    private static final String QUERY_ABSTRACTLIST = AbstractList.class.getName();
    private static final String QUERY_TREESET = TreeSet.class.getName();
    
    private static Log logger = LogFactory.getLog(HierarchicalSqlMapClientFactoryBeanTest.class);
    
    private ClassPathXmlApplicationContext ctx;
    private TestDAO testDao;
    
    @Override
    public void setUp() throws Exception
    {
        testDao = new TestDAO();
        testDao.setId(5L);
        testDao.setPropOne("prop-one");
        testDao.setPropTwo("prop-two");
    }
    
    @Override
    public void tearDown() throws Exception
    {
        try
        {
            if (ctx != null)
            {
                ctx.close();
            }
        }
        catch (Throwable e)
        {
            logger.error("Failed to neatly close application context", e);
        }
    }
    
    /**
     * Pushes the dialect class into the system properties, closes an current context and
     * recreates it; the SqlMapClient is then returned.
     */
    @SuppressWarnings("unchecked")
    private SqlMapClient getSqlMapClient(Class dialectClass) throws Exception
    {
        System.setProperty("hierarchy-test.dialect", dialectClass.getName());
        if (ctx != null)
        {
            try
            {
                ctx.close();
                ctx = null;
            }
            catch (Throwable e)
            {
                logger.error("Failed to neatly close application context", e);
            }
        }
        ctx = new ClassPathXmlApplicationContext("ibatis/hierarchy-test/hierarchy-test-context.xml");
        return (SqlMapClient) ctx.getBean("sqlMapClient");
    }
    
    /**
     * Check context startup and shutdown
     */
    public void testContextStartup() throws Exception
    {
        getSqlMapClient(TreeSet.class);
        getSqlMapClient(HashSet.class);
        getSqlMapClient(ArrayList.class);
        getSqlMapClient(AbstractCollection.class);
        try
        {
            getSqlMapClient(Collection.class);
            fail("Failed to detect incompatible class hierarchy");
        }
        catch (Throwable e)
        {
            // Expected
        }
    }
    
    public void testHierarchyTreeSet() throws Exception
    {
        ExtendedSqlMapClient sqlMapClient = (ExtendedSqlMapClient) getSqlMapClient(TreeSet.class);
        MappedStatement stmt = sqlMapClient.getMappedStatement(QUERY_TREESET);
        assertNotNull("Query missing for " + QUERY_TREESET + " using " + TreeSet.class, stmt);
        try
        {
            sqlMapClient.getMappedStatement(QUERY_ABSTRACTCOLLECTION);
            fail("Query not missing for " + QUERY_ABSTRACTCOLLECTION + " using " + TreeSet.class);
        }
        catch (SqlMapException e)
        {
            // Expected
        }
    }

    public void testHierarchyHashSet() throws Exception
    {
        ExtendedSqlMapClient sqlMapClient = (ExtendedSqlMapClient) getSqlMapClient(HashSet.class);
        MappedStatement stmt = sqlMapClient.getMappedStatement(QUERY_ABSTRACTCOLLECTION);
        assertNotNull("Query missing for " + QUERY_ABSTRACTCOLLECTION + " using " + HashSet.class, stmt);
        try
        {
            sqlMapClient.getMappedStatement(QUERY_OBJECT);
            fail("Query not missing for " + QUERY_OBJECT + " using " + HashSet.class);
        }
        catch (SqlMapException e)
        {
            // Expected
        }
    }

    public void testHierarchyArrayList() throws Exception
    {
        ExtendedSqlMapClient sqlMapClient = (ExtendedSqlMapClient) getSqlMapClient(ArrayList.class);
        MappedStatement stmt = sqlMapClient.getMappedStatement(QUERY_ABSTRACTLIST);
        assertNotNull("Query missing for " + QUERY_ABSTRACTLIST + " using " + ArrayList.class, stmt);
        try
        {
            sqlMapClient.getMappedStatement(QUERY_ABSTRACTCOLLECTION);
            fail("Query not missing for " + QUERY_ABSTRACTCOLLECTION + " using " + ArrayList.class);
        }
        catch (SqlMapException e)
        {
            // Expected
        }
    }

    public void testHierarchyAbstractCollection() throws Exception
    {
        ExtendedSqlMapClient sqlMapClient = (ExtendedSqlMapClient) getSqlMapClient(AbstractCollection.class);
        MappedStatement stmt = sqlMapClient.getMappedStatement(QUERY_ABSTRACTCOLLECTION);
        assertNotNull("Query missing for " + QUERY_ABSTRACTCOLLECTION + " using " + AbstractCollection.class, stmt);
        try
        {
            sqlMapClient.getMappedStatement(QUERY_OBJECT);
            fail("Query not missing for " + QUERY_OBJECT + " using " + AbstractCollection.class);
        }
        catch (SqlMapException e)
        {
            // Expected
        }
    }

    /**
     * Helper class that iBatis will use in the test mappings
     * @author Derek Hulley
     */
    public static class TestDAO
    {
        private Long id;
        private String propOne;
        private String propTwo;
        
        public Long getId()
        {
            return id;
        }
        public void setId(Long id)
        {
            this.id = id;
        }
        public String getPropOne()
        {
            return propOne;
        }
        public void setPropOne(String propOne)
        {
            this.propOne = propOne;
        }
        public String getPropTwo()
        {
            return propTwo;
        }
        public void setPropTwo(String propTwo)
        {
            this.propTwo = propTwo;
        }
    }
}
