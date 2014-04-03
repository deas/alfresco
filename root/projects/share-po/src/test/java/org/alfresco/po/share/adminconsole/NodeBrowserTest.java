package org.alfresco.po.share.adminconsole;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.SharePage;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * @author Aliaksei Boole
 */
public class NodeBrowserTest extends AbstractTest
{

        @Test(groups = "Enterprise-only", timeOut = 400000)
        public void checkThatFactoryReturnNodeBrowserPage() throws Exception
        {
                SharePage page = loginAs("admin", "admin");
                page.getNav().getNodeBrowserPage().render();
        }

        @Test(groups = "Enterprise-only", timeOut = 400000)
        public void executeCustomNodeSearch() throws Exception
        {
                SharePage page = loginAs("admin", "admin");
                NodeBrowserPage nodeBrowserPage = page.getNav().getNodeBrowserPage();
                nodeBrowserPage.selectStore("workspace://SpacesStore");
                nodeBrowserPage.selectQueryType("storeroot");
                nodeBrowserPage.clickSearchButton();
                assertTrue(nodeBrowserPage.isSearchResults());
        }
}
