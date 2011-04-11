/**
 * 
 */
package org.alfresco.module.org_alfresco_module_dod5015.model;

import javax.transaction.UserTransaction;

import junit.framework.TestCase;

import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.ApplicationContextHelper;
import org.springframework.context.ApplicationContext;

/**
 * File plan component aspect test
 * 
 * @author Roy Wetherall
 */
public class FilePlanComponentAspectTest extends TestCase
{
    private ApplicationContext appContext;
    private AuthenticationComponent authenticationComponent;
    private RecordsManagementService rmService;
    private TransactionService transactionService;
    private NodeService nodeService;
    private ContentService contentService;
    private RetryingTransactionHelper retryingTransactionHelper;
    
    UserTransaction userTransaction;
    
    @Override
    protected void setUp() throws Exception
    {
        appContext = ApplicationContextHelper.getApplicationContext();
        authenticationComponent = (AuthenticationComponent)appContext.getBean("authenticationComponent");
        transactionService = (TransactionService)appContext.getBean("transactionService");
        nodeService = (NodeService)appContext.getBean("nodeService");
        rmService = (RecordsManagementService)appContext.getBean("recordsManagementService");
        contentService = (ContentService)appContext.getBean("contentService");
        retryingTransactionHelper = (RetryingTransactionHelper)appContext.getBean("retryingTransactionHelper");        
    
        // Set authentication       
        authenticationComponent.setCurrentUser("admin");    
    }
    
    @Override
    protected void tearDown() throws Exception
    {
    }
    
    public void testFilePlanComponentAspect() throws Exception
    {
        //StoreRef storeRef = nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "testSpace123");
        
        // TODO fill this in please!!!
        
    }

}
