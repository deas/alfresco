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

import java.util.List;

import org.alfresco.po.share.util.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify people finder page elements are in place.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
@Listeners (FailedTestListener.class)
@Test(groups ={"alfresco-one"})
public class PeopleFinderPageTest extends AbstractTest
{
   
    private DashBoardPage dashBoard;
    
    @BeforeClass(groups ={"alfresco-one"})
    public void setup() throws Exception
    {
        dashBoard = loginAs(username, password);
    }

    
    @Test(groups="alfresco-one")
    public void searchForPerson()
    {
        PeopleFinderPage page = dashBoard.getNav().selectPeople().render();
        Assert.assertTrue(page.isLogoPresent());
        Assert.assertTrue(page.isTitlePresent());
        String usersInitals = username.substring(0, 1);
        PeopleFinderPage results = page.searchFor(usersInitals).render();
        List<ShareLink> names = results.getResults();
        Assert.assertTrue(names.size() > 0);
    }

    @Test(groups={"Enterprise-only"})
    public void searchForExactPerson() throws Exception
    {
        PeopleFinderPage page = dashBoard.getNav().selectPeople().render();
        PeopleFinderPage results = page.searchFor("mike").render();
        List<ShareLink> names = results.getResults();
        Assert.assertTrue(names.size() > 0);
    }

    @Test(groups="alfresco-one")
    public void searchForWithNoResults() throws Exception
    {
        PeopleFinderPage page = dashBoard.getNav().selectPeople().render();
        PeopleFinderPage results = page.searchFor("zsdadahdskajhsdkahDqweqweq1234721423").render();
        List<ShareLink> names = results.getResults();
        Assert.assertTrue(names.size() == 0);
    }
}
