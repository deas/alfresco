/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.po.share;

import org.alfresco.po.share.search.AllSitesResultsPage;
import org.alfresco.po.share.search.RepositoryResultsPage;
import org.alfresco.po.share.search.SearchBox;
import org.alfresco.po.share.util.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Tests the {@link Pagination} class
 * @author Michael Suzuki
 * @since 1.2
 */
@Listeners(FailedTestListener.class)
@Test(groups={"Enterprise-only"})
public class PaginationTest extends AbstractTest
{
    private DashBoardPage dashBoard;
    
    @BeforeClass(groups={"Enterprise-only"})
    public void setup() throws Exception
    {
        dashBoard = loginAs(username, password);
    }
    
    @Test
    public void pagination() throws Exception
    {
        SearchBox search = dashBoard.getSearch();
        AllSitesResultsPage result = search.search("xyz").render();
        boolean hasPagination = result.paginationDisplayed();
        Assert.assertFalse(hasPagination);

        RepositoryResultsPage repoResult = result.selectRepository().render();
        repoResult = repoResult.search("email").render();
        hasPagination = result.paginationDisplayed();
        if (!hasPagination)
        {
            saveScreenShot("PaginationTest.pagination.empty");
        }
        Assert.assertTrue(hasPagination);
        Assert.assertTrue(repoResult.count() > 76);

        boolean next = Pagination.hasPaginationButton(drone, "a.yui-pg-next");
        Assert.assertTrue(next);
        repoResult = Pagination.selectPagiantionButton(drone, "a.yui-pg-next").render();
        int paginationPosition = result.getPaginationPosition();
        Assert.assertEquals(paginationPosition, 2);
    }
}
