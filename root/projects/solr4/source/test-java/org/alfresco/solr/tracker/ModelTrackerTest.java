package org.alfresco.solr.tracker;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Properties;

import org.alfresco.httpclient.AuthenticationException;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.NamespaceDAO;
import org.alfresco.service.namespace.QName;
import org.alfresco.solr.InformationServer;
import org.alfresco.solr.client.AlfrescoModel;
import org.alfresco.solr.client.AlfrescoModelDiff;
import org.alfresco.solr.client.AlfrescoModelDiff.TYPE;
import org.alfresco.solr.client.SOLRAPIClient;
import org.json.JSONException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ModelTrackerTest
{
    private ModelTracker modelTracker;

    @Mock
    private SolrTrackerScheduler scheduler;
    private String id = null;
    @Mock
    private SOLRAPIClient repositoryClient;
    private String coreName = "theCoreName";
    @Mock
    private InformationServer srv;
    @Mock
    private Properties props;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {
    }

    @Before
    public void setUp() throws Exception
    {
        when(props.getProperty("alfresco.stores")).thenReturn("workspace://SpacesStore");
        when(props.getProperty("alfresco.batch.count", "1000")).thenReturn("1000");
        when(props.getProperty("alfresco.maxLiveSearchers", "2")).thenReturn("2");
        when(props.getProperty("enable.slave", "false")).thenReturn("false");
        when(props.getProperty("enable.master", "true")).thenReturn("true");
        
        this.modelTracker = new ModelTracker(scheduler, id, props, repositoryClient, coreName, srv);
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void testDoTrack() throws AuthenticationException, IOException, JSONException
    {
        this.modelTracker.doTrack();
        
        verify(this.srv).getRegisteredSearcherCount();
    }


    @Test
    public void testCheckIndex()
    {
        // TODO
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testTrackModels() throws AuthenticationException, IOException, JSONException
    {
        QName modelName = QName.createQName("qname");
        TYPE type = TYPE.CHANGED;
        Long oldChecksum = new Long(0);
        Long newChecksum = new Long(1);
        AlfrescoModelDiff diff = new AlfrescoModelDiff(modelName , type, oldChecksum, newChecksum);
        List<AlfrescoModelDiff> modelDiffs = new ArrayList<>();
        modelDiffs.add(diff);
        when(this.repositoryClient.getModelsDiff(any(List.class))).thenReturn(modelDiffs);
        
        final String name = "a model name";
        M2Model model = M2Model.createModel(name);
        M2Model spiedModel = spy(model);
        model.createNamespace("uri", "prefix");
        AlfrescoModel alfrescoModel = new AlfrescoModel(spiedModel , newChecksum);
        when(this.repositoryClient.getModel(modelName)).thenReturn(alfrescoModel);
        
        NamespaceDAO namespaceDao = mock(NamespaceDAO.class);
        Collection<String> values = new ArrayList<>();
        values.add("prefix");
        when(namespaceDao.getPrefixes(anyString())).thenReturn(values);
        when(this.srv.getNamespaceDAO()).thenReturn(namespaceDao);
        when(this.srv.getM2Model(modelName)).thenReturn(spiedModel);
        when(this.srv.putModel(spiedModel)).thenReturn(true);
        
        this.modelTracker.trackModels(false);
        
        // Verification
        verify(this.srv).afterInitModels();
        
        File alfrescoModelDir = new File("alfrescoModels");
        assertTrue(alfrescoModelDir.isDirectory());
        
        File[] modelRepresentations = alfrescoModelDir.listFiles(new FileFilter()
            {
                @Override
                public boolean accept(File pathname)
                {
                    return pathname.getName().startsWith(name);
                }
            });
        assertEquals(1, modelRepresentations.length);
    }

    @Test
    public void testEnsureFirstModelSync()
    {
        // TODO
    }

    @Test
    public void testExpandQName()
    {
        // TODO
    }

    @Test
    public void testExpandQNameImpl()
    {
        // TODO
    }

    @Test
    public void testExpandName()
    {
        // TODO
    }

}
