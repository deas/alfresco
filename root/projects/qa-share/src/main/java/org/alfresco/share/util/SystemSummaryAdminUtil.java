/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General public static License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General public static License for more details.
 *
 * You should have received a copy of the GNU Lesser General public static License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.share.util;

import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.systemsummary.SystemSummaryPage;
import org.alfresco.po.share.systemsummary.directorymanagement.AuthType;
import org.alfresco.po.share.systemsummary.directorymanagement.DirectoryInfoRow;
import org.alfresco.po.share.systemsummary.directorymanagement.DirectoryManagementPage;
import org.alfresco.po.share.systemsummary.directorymanagement.EditLdapFrame;
import org.alfresco.webdrone.WebDrone;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.alfresco.po.share.systemsummary.AdminConsoleLink.DirectoryManagement;

/**
 * @author Aliaksei Boole
 */
public class SystemSummaryAdminUtil extends AbstractUtils
{
    //Open LDAP
    public static final String LDAP_OPEN_URL = "ldap://172.30.40.61:3268";
    public static final String USER_NAME_FORMAT_OPEN = "%s,CN=Users,DC=qalab,DC=alfresco,DC=org";
    public static final String ADMIN_USER_NAME_OPEN = "admin";
    public static final String SECURITY_NAME_PRINCIPAL_OPEN = "CN=admin,CN=Users,DC=qalab,DC=alfresco,DC=org";
    public static final String USER_SEARCH_BASE_OPEN = "CN=Users,DC=qalab,DC=alfresco,DC=org";
    public static final String GROUP_SEARCH_BASE_OPEN = "CN=Users,DC=qalab,DC=alfresco,DC=org";

    //LDAP Query
    public static final String GROUP_QUERY = "(objectclass=group)";
    public static final String USER_QUERY = "(objectclass=user)";

    //Security Credentials
    public static final String SECURITY_CREDENTIALS = "alfresco";

    //AD LDAP
    public static final String LDAP_AD_URL = "ldap://172.30.40.61:389";
    public static final String USER_NAME_FORMAT_AD = "%s@qalab.alfresco.org";
    public static final String ADMIN_USER_NAME_AD = "admin";
    public static final String SECURITY_NAME_PRINCIPAL_AD = "admin";
    public static final String USER_SEARCH_BASE_AD = "CN=Users,DC=qalab,DC=alfresco,DC=org";
    public static final String GROUP_SEARCH_BASE_AD = "CN=Users,DC=qalab,DC=alfresco,DC=org";

    //Sync options
    public static final String GROUP_TYPE = "group";
    public static final String PERSON_TYPE = "user";
    public static final String MODIFY_TS_ATTR = "whenChanged";
    public static final String TIMESTAMP_FORMAT = "yyyyMMddHHmmss'.0Z'";
    public static final String USER_ID_ATTR = "sAMAccountName";
    public static final String USER_ORG_ID = "company";
    public static final String USER_LAST_NAME = "sn";
    public static final String USER_FIRST_NAME = "givenName";
    public static final String GROUP_DISPLAY = "displayName";

    /**
     * Config OPEN LDAP in your Alfresco(172.30.40.61:3268)
     *
     * @param drone
     * @param name
     * @return
     */
    public static DirectoryManagementPage addOpenLdapAuthChain(WebDrone drone, String name)
    {
        checkNotNull(drone);
        checkNotNull(name);
        SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
        DirectoryManagementPage directoryManagement = sysSummaryPage.openConsolePage(DirectoryManagement).render();
        directoryManagement.addAuthChain(AuthType.OPEN_LDAP, name);
        DirectoryInfoRow directoryInfoRow = directoryManagement.getDirectoryInfoRowBy(name);
        EditLdapFrame editLdapFrame = directoryInfoRow.clickEdit().render();
        editLdapFrame.fillLdapUrl(LDAP_OPEN_URL);
        editLdapFrame.fillAdminUserName(ADMIN_USER_NAME_OPEN);
        editLdapFrame.fillUserNameFormat(USER_NAME_FORMAT_OPEN);
        editLdapFrame.fillSecurityNamePrincipal(SECURITY_NAME_PRINCIPAL_OPEN);
        editLdapFrame.fillUserSearchBase(USER_SEARCH_BASE_OPEN);
        editLdapFrame.fillGroupSearchBase(GROUP_SEARCH_BASE_OPEN);
        editLdapFrame.fillSecurityCredentials(SECURITY_CREDENTIALS);
        editLdapFrame.fillGroupQuery(GROUP_QUERY);
        editLdapFrame.fillPersonQuery(USER_QUERY);
        EditLdapFrame.AdvSettings advSettings = editLdapFrame.openAdvSettings();
        advSettings.fillGroupType(GROUP_TYPE);
        advSettings.fillPersonType(PERSON_TYPE);
        advSettings.fillModifyTS(MODIFY_TS_ATTR);
        advSettings.fillTimeStampFormat(TIMESTAMP_FORMAT);
        advSettings.fillUserIdAttr(USER_ID_ATTR);
        advSettings.fillUserOrganisationId(USER_ORG_ID);
        advSettings.fillUserLastNameAttr(USER_LAST_NAME);
        advSettings.fillUserFirstNameAttr(USER_FIRST_NAME);
        advSettings.fillGroupDisplayNameAttr(GROUP_DISPLAY);
        return editLdapFrame.clickSave();
    }

    /**
     * Config AD LDAP in your Alfresco(172.30.40.61:389)
     *
     * @param drone
     * @param name
     * @return
     */
    public static DirectoryManagementPage addAdLdapAuthChain(WebDrone drone, String name)
    {
        checkNotNull(drone);
        checkNotNull(name);
        SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
        DirectoryManagementPage directoryManagement = sysSummaryPage.openConsolePage(DirectoryManagement).render();
        directoryManagement.addAuthChain(AuthType.AD_LDAP, name);
        DirectoryInfoRow directoryInfoRow = directoryManagement.getDirectoryInfoRowBy(name);
        EditLdapFrame editLdapFrame = directoryInfoRow.clickEdit().render();
        editLdapFrame.fillLdapUrl(LDAP_AD_URL);
        editLdapFrame.fillAdminUserName(ADMIN_USER_NAME_AD);
        editLdapFrame.fillUserNameFormat(USER_NAME_FORMAT_AD);
        editLdapFrame.fillSecurityNamePrincipal(SECURITY_NAME_PRINCIPAL_AD);
        editLdapFrame.fillUserSearchBase(USER_SEARCH_BASE_AD);
        editLdapFrame.fillGroupSearchBase(GROUP_SEARCH_BASE_AD);
        editLdapFrame.fillSecurityCredentials(SECURITY_CREDENTIALS);
        editLdapFrame.fillGroupQuery(GROUP_QUERY);
        editLdapFrame.fillPersonQuery(USER_QUERY);
        EditLdapFrame.AdvSettings advSettings = editLdapFrame.openAdvSettings();
        advSettings.fillGroupType(GROUP_TYPE);
        advSettings.fillPersonType(PERSON_TYPE);
        advSettings.fillModifyTS(MODIFY_TS_ATTR);
        advSettings.fillTimeStampFormat(TIMESTAMP_FORMAT);
        advSettings.fillUserIdAttr(USER_ID_ATTR);
        advSettings.fillUserOrganisationId(USER_ORG_ID);
        advSettings.fillUserLastNameAttr(USER_LAST_NAME);
        advSettings.fillUserFirstNameAttr(USER_FIRST_NAME);
        advSettings.fillGroupDisplayNameAttr(GROUP_DISPLAY);
        return editLdapFrame.clickSave();
    }

    /**
     * Remove selected Auth Chain.
     *
     * @param drone
     * @param name
     */
    public static void deleteAuthChain(WebDrone drone, String name)
    {
        checkNotNull(drone);
        checkNotNull(name);
        SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
        DirectoryManagementPage directoryManagement = sysSummaryPage.openConsolePage(DirectoryManagement).render();
        directoryManagement.removeAuthChain(name);
    }
}
