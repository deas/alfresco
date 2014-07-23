import static org.junit.Assert.*;

import org.alfresco.solr.AlfrescoCoreAdminHandler;
import org.alfresco.solr.AlfrescoSolrCloseHook;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.CoreDescriptor;
import org.apache.solr.core.SolrCore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class AlfrescoSolrCloseHookTest
{
    private SolrCore core;
    private @Mock AlfrescoCoreAdminHandler adminHandler;
    private AlfrescoSolrCloseHook hook;

    @Before
    public void setUp() throws Exception
    {
        core = new SolrCore("name", new CoreDescriptor(new CoreContainer(), "coreName", "instanceDir"));
        hook = new AlfrescoSolrCloseHook(adminHandler);
    }

    @Test
    public void testPostCloseSolrCore()
    {
        hook.postClose(core);
    }

    @Test
    public void testPreCloseSolrCore()
    {
        hook.preClose(core);
    }

}
