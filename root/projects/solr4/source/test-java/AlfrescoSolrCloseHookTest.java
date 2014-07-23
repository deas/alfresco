import static org.junit.Assert.*;

import org.alfresco.solr.AlfrescoCoreAdminHandler;
import org.alfresco.solr.AlfrescoSolrCloseHook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class AlfrescoSolrCloseHookTest
{

    private @Mock AlfrescoCoreAdminHandler adminHandler;
    private AlfrescoSolrCloseHook hook;

    @Before
    public void setUp() throws Exception
    {
        hook = new AlfrescoSolrCloseHook(adminHandler);
    }

    @Test
    public void testPostCloseSolrCore()
    {
        
    }

    @Test
    public void testPreCloseSolrCore()
    {
        
    }

}
