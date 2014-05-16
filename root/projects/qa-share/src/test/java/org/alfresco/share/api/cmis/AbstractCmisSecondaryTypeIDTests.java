/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.share.api.cmis;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.po.share.site.document.DetailsPage;
import org.alfresco.po.share.site.document.DocumentAspect;
import org.alfresco.rest.api.tests.client.PublicApiClient;
import org.alfresco.share.enums.CMISBinding;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CmisUtils;
import org.alfresco.webdrone.WebDrone;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.SecondaryType;
import org.testng.Assert;

/**
 * Class to include utils to test CMIS add aspects to document.
 * 
 * @author Ranjith Manyam
 */
public class AbstractCmisSecondaryTypeIDTests extends CmisUtils
{
    /**
     * Method to verify if an aspect is added for the given content
     * 
     * @param drone
     * @param cmisBinding
     * @param authUser
     * @param domain
     * @param documentNodeRef
     * @param fileName
     * @param siteName
     * @param documentAspect
     */
    protected void verifyAspectIsAdded(WebDrone drone, CMISBinding cmisBinding, String authUser, String domain, String documentNodeRef, String fileName,
            String siteName, DocumentAspect documentAspect)
    {
        verifyAspect(drone, cmisBinding, authUser, domain, documentNodeRef, fileName, siteName, documentAspect, true);
    }

    /**
     * Method to verify if an aspect is removed for the given content
     * 
     * @param drone
     * @param cmisBinding
     * @param authUser
     * @param domain
     * @param documentNodeRef
     * @param fileName
     * @param siteName
     * @param documentAspect
     */
    protected void verifyAspectIsRemoved(WebDrone drone, CMISBinding cmisBinding, String authUser, String domain, String documentNodeRef, String fileName,
            String siteName, DocumentAspect documentAspect)
    {
        verifyAspect(drone, cmisBinding, authUser, domain, documentNodeRef, fileName, siteName, documentAspect, false);
    }

    /**
     * Util method to verify if the aspect is added/removed for a content item
     * 
     * @param drone
     * @param cmisBinding
     * @param authUser
     * @param domain
     * @param documentNodeRef
     * @param fileName
     * @param siteName
     * @param documentAspect
     * @param isExpected
     */
    private void verifyAspect(WebDrone drone, CMISBinding cmisBinding, String authUser, String domain, String documentNodeRef, String fileName,
            String siteName, DocumentAspect documentAspect, boolean isExpected)
    {
        PublicApiClient.CmisSession cmisSession = getCmisSession(cmisBinding, authUser, domain);
        CmisObject content = cmisSession.getObject(documentNodeRef);

        List<SecondaryType> secondaryTypesList = content.getSecondaryTypes();

        if (isExpected)
        {
            Assert.assertTrue(isAspectAdded(secondaryTypesList, documentAspect), "Verifying " + documentAspect.getValue() + " aspect is added");
        }
        else
        {
            Assert.assertFalse(isAspectAdded(secondaryTypesList, documentAspect), "Verifying " + documentAspect.getValue() + " aspect is removed");
        }

        if (!alfrescoVersion.isCloud())
        {
            Set<DocumentAspect> selectedAspects = ShareUser.getSelectedAspects(drone, siteName, fileName);
            if (isExpected)
            {
                Assert.assertTrue(selectedAspects.contains(documentAspect), "Verifying " + documentAspect.getValue().toUpperCase() + "aspect is added");
            }
            else
            {
                Assert.assertFalse(selectedAspects.contains(documentAspect), "Verifying " + documentAspect.getValue().toUpperCase() + "aspect is removed");
            }
        }
    }

    /**
     * Method to check if a specified DocumentAspect is added or not
     * 
     * @param secondaryTypes
     * @param documentAspect
     * @return True if given aspect is present
     */
    private boolean isAspectAdded(List<SecondaryType> secondaryTypes, DocumentAspect documentAspect)
    {
        for (SecondaryType secondaryType : secondaryTypes)
        {
            if (secondaryType.getId().equals(documentAspect.getProperty()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Method to get Details page
     * 
     * @param drone
     * @param siteName
     * @param contentName
     * @return {@link DetailsPage}
     */
    protected DetailsPage getDetailsPage(WebDrone drone, String siteName, String contentName)
    {
        if (!alfrescoVersion.isCloud())
        {
            return ShareUser.getSharePage(drone).render();
        }
        else
        {
            ShareUser.openSitesDocumentLibrary(drone, siteName);
            return ShareUserSitePage.openDetailsPage(drone, contentName);
        }
    }

    /**
     * Method to get Properties of a content. Method assumes the user is already
     * in DetailsPage
     * 
     * @param drone
     * @param siteName
     * @param contentName
     * @return {@link Map<String, Object> propertiesMap}
     */
    // TODO: Add util to ShareUserSitePage. Remove from cmisutils
    protected Map<String, Object> getProperties(WebDrone drone, String siteName, String contentName)
    {
        return getDetailsPage(drone, siteName, contentName).getProperties();
    }
}
