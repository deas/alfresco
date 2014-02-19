/*
 * Copyright (C) 2005-2013 Alfresco Software Limited. This file is part of Alfresco Alfresco is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. Alfresco is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General
 * Public License along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.share.util.api;

import org.alfresco.json.JSONUtil;
import org.alfresco.po.share.enums.TenantTypes;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.json.JSONArray;
import org.json.JSONObject;

public class CreateUserAPI extends AlfrescoHttpClient
{
    public CreateUserAPI() throws Exception
    {
        super();
        // TODO Auto-generated constructor stub
    }

    private static Log logger = LogFactory.getLog(CreateUserAPI.class);

    @Override
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);
    }

    public static String[] signUp(WebDrone drone, String invitingUserEmail, String newUserEmailID) throws Exception
    {
        if (!isAlfrescoVersionCloud(drone))
        {
            throw new UnsupportedOperationException("Method not suitable for use for Enterprise");
        }

        String reqURL = getAPIURL(drone) + apiContextCloudInternal + "accounts/signupqueue";

        logger.info("Using Url - " + reqURL + " for activateUser");

        String[] authDetails = getAuthDetails(invitingUserEmail);

        String[] headers =
        { "Content-Type", "application/json;charset=utf-8", "key", getHeaderKey() };
        String[] body =
        { "source", "test-rest-client-script", "email", newUserEmailID };

        HttpClient client = null;
        HttpPost request = null;
        HttpEntity response = null;

        try
        {
            client = getHttpClientWithBasicAuth(reqURL, authDetails[0], authDetails[1]);
            request = generatePostRequest(reqURL, headers, body);
            response = executeRequest(client, request);

            String result = JSONUtil.readStream(response).toJSONString();

            String regKey = getParameter("registration", "key", result);
            String regId = getParameter("registration", "id", result);
            return new String[]
            { regKey, regId };
        }
        finally
        {
            releaseConnection(client, response);
        }
    }

    public static Boolean activateUser(WebDrone drone, String invitingUserEmail, String fName, String lName, String password, String regKey, String regId)
        throws Exception
    {
        if (!isAlfrescoVersionCloud(drone))
        {
            throw new UnsupportedOperationException("Method not suitable for use for Enterprise");
        }

        String reqURL = getAPIURL(drone) + apiContextCloudInternal + "account-activations";

        logger.info("Using Url - " + reqURL + " for activateUser");

        String[] authDetails = getAuthDetails(invitingUserEmail);
        String[] headers =
        { "Content-Type", "application/json;charset=utf-8", "key", getHeaderKey() };
        String[] body =
        { "firstName", fName, "lastName", lName, "password", password, "key", regKey, "id", regId };

        HttpPost request = generatePostRequest(reqURL, headers, body);
        HttpClient client = getHttpClientWithBasicAuth(reqURL, authDetails[0], authDetails[1]);
        HttpResponse response = executeRequestHttpResp(client, request);

        if (checkHttpResponse(response, HttpStatus.SC_OK))
        {
            logger.info("Activate Account Succeeds for user: " + fName + " : Password: " + password);
            return true;
        }
        logger.error("Activate Account Failed for user: " + fName + " : " + password);
        return false;
    }

    /**
     * Use this method for Enterprise and Cloud to create user with a network
     * admin role Implementation for cloud requires the tenant to be upgraded
     * from free network: This requires admin console / admin access
     * Implementation for Enterprise requires admin user who can create and add
     * users to 'Alfresco_Administrators' group
     * 
     * @param drone
     * @param invitingUserEmail
     * @param newUserDetails
     * @return
     * @throws Exception
     */
    public static boolean createActivateUserAsTenantAdmin(WebDrone drone, String invitingUserEmail, String... newUserDetails) throws Exception
    {
        Boolean result = false;
        String tenantType = TenantTypes.Premium.getTenantType();

        if (isAlfrescoVersionCloud(drone))
        {
            result = CreateActivateUser(drone, invitingUserEmail, newUserDetails);
            upgradeCloudAccount(drone, invitingUserEmail, getUserDomain(newUserDetails[0]), tenantType);
            result = promoteUserAsAdmin(drone, invitingUserEmail, newUserDetails[0], getUserDomain(newUserDetails[0]));
        }
        else
        {
            result = ShareUser.createEnterpriseUserWithGroup(drone, invitingUserEmail, newUserDetails[0], newUserDetails[0], newUserDetails[0],
                getAuthDetails(newUserDetails[0])[1], "ALFRESCO_ADMINISTRATORS");
        }

        return result;
    }

    public static boolean CreateActivateUser(WebDrone drone, String invitingUserEmail, String... newUserDetails) throws Exception
    {

        boolean result = false;
        int paramCount = newUserDetails.length;
        int paramCountMandatory = 1;

        String email = "";
        String firstName = "";
        String lastName = DEFAULT_LASTNAME;
        String userPassword = DEFAULT_PASSWORD;

        if (paramCount < paramCountMandatory)
        {
            throw new IllegalArgumentException("Mandatory Parameters Missing");
        }

        if (paramCount >= paramCountMandatory)
        {
            email = newUserDetails[0];
        }

        if (paramCount > 1)
        {
            firstName = newUserDetails[1];
        }
        else
        {
            firstName = newUserDetails[0];
        }

        if (paramCount > 2)
        {
            lastName = newUserDetails[2];
        }

        if (paramCount > 3)
        {
            userPassword = newUserDetails[3];
        }

        if (isAlfrescoVersionCloud(drone))
        {
            result = createCloudUser(drone, invitingUserEmail, email, firstName, lastName, userPassword);
        }
        else
        {
            result = ShareUser.createEnterpriseUser(drone, invitingUserEmail, email, firstName, lastName, userPassword);
        }

        return result;

    }

    /**
     * Utility to create a cloud user using signUp-Activate API
     * 
     * @param drone
     *            WebDrone Instance
     * @param invitingUsername
     *            String username of inviting user
     * @param email
     *            String email or username
     * @param fname
     *            String firstname
     * @param lname
     *            String lastname
     * @param password
     *            String password
     * @return true is user creation succeeds
     * @throws Exception
     */
    public static Boolean createCloudUser(WebDrone drone, String invitingUsername, String email, String fname, String lname, String password) throws Exception
    {
        Boolean result = false;

        String[] regInfo = signUp(drone, invitingUsername, email);
        result = activateUser(drone, invitingUsername, fname, lname, password, regInfo[0], regInfo[1]);
        return result;
    }

    /**
     * Utility to upgrade the account type (for the given domain)
     * 
     * @param drone
     *            WebDrone Instance
     * @param authUser
     *            String authenticating user
     * @param domain
     *            String domain Name to be upgraded
     * @param accountTypeID
     *            String accountType ID e.g. 1000 if enterprise, 0 if free, 101
     *            for partner
     * @return true if account upgrade succeeds
     * @throws Exception
     */
    public static HttpResponse upgradeCloudAccount(WebDrone drone, String authUser, String domain, String accountTypeID) throws Exception
    {
        if (!isAlfrescoVersionCloud(drone))
        {
            throw new UnsupportedOperationException("Method not suitable for use for Enterprise");
        }

        String reqURL = getAPIURL(drone) + apiContextCloudInternal + "domains/" + domain + "/account";

        String[] authDetails = getAuthDetails(authUser);
        String[] headers =
        { "Content-Type", "application/json;charset=utf-8", "key", getHeaderKey() };
        String[] body =
        { "accountTypeId", accountTypeID };

        HttpPut request = generatePutRequest(reqURL, headers, body);
        HttpClient client = getHttpClientWithBasicAuth(reqURL, authDetails[0], authDetails[1]);
        HttpResponse response = executeRequestHttpResp(client, request);

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
        {
            logger.info("Account Upgraded");
        }
        else
        {
            logger.info(response.toString());
        }
        return response;
    }

    /**
     * Utility to promote the user as network admin (for the given domain) On
     * Enterprise, its done by admin user via admin console, on cloud, using
     * internal API
     * 
     * @param drone
     *            WebDrone Instance
     * @param authUser
     *            String authenticating user
     * @param domain
     *            String domain for which the user is being upgraded.
     * @param userNametoBePromoted
     *            String userName to be promoted as network admin
     * @return true if succeeds
     * @throws Exception
     */
    public static Boolean promoteUserAsAdmin(WebDrone drone, String authUser, String userNametoBePromoted, String domain) throws Exception
    {
        Boolean result = false;
        HttpResponse response;
        String message = "Promote user as network admin" + userNametoBePromoted;

        if (isAlfrescoVersionCloud(drone))
        {
            response = promoteUserAsAdminCloud(drone, authUser, userNametoBePromoted, domain);
            logger.info(response.toString());
            result = (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK);
        }
        else
        {
            ShareUser.login(drone, authUser, getAuthDetails(authUser)[1]);
            result = ShareUserMembers.promoteUserAsAdminEnterprise(drone, authUser, userNametoBePromoted);
            ShareUser.logout(drone);
        }

        if (result)
        {
            logger.info("Success: " + message);
        }
        else
        {
            logger.info("Failed to: " + message);
        }
        return result;
    }

    /**
     * Utility to promote the user as network admin (for the given domain)
     * 
     * @param drone
     *            WebDrone Instance
     * @param authUser
     *            String authenticating user
     * @param domain
     *            String domain for which the user is being upgraded.
     * @param userNametoBePromoted
     *            String userName to be promoted as network admin
     * @return HttpResponse
     * @throws Exception
     */
    public static HttpResponse promoteUserAsAdminCloud(WebDrone drone, String authUser, String userNametoBePromoted, String domain) throws Exception
    {
        if (!isAlfrescoVersionCloud(drone))
        {
            throw new UnsupportedOperationException("Method not suitable for use for Enterprise");
        }

        String reqURL = getAPIURL(drone) + apiContextCloudInternal + "domains/" + domain + "/account/networkadmins";

        String[] authDetails = getAuthDetails(authUser);
        String[] headers =
        { "Content-Type", "application/json;charset=utf-8", "key", getHeaderKey() };
        String[] body =
        { "username", userNametoBePromoted };

        HttpPost request = generatePostRequest(reqURL, headers, body);
        HttpClient client = getHttpClientWithBasicAuth(reqURL, authDetails[0], authDetails[1]);
        HttpResponse response = executeRequestHttpResp(client, request);

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
        {
            logger.info("User promoted as network admin: " + userNametoBePromoted);
        }
        else
        {
            logger.info(response.toString());
        }
        return response;
    }

    /**
     * Utility to invite a cloud user to Site using invite-To-Site- And
     * Activate-Invitation via rest API request
     * 
     * @param drone
     *            WebDrone Instance
     * @param invitingUsername
     *            String username of inviting user
     * @param email
     *            String email or username
     * @param siteShortname
     *            String Name of the site to which the user is being invited
     * @param role
     *            String role to be assigned to the invited user
     * @param message
     *            String message to be sent with the invitation
     * @return true is user invitation-acceptance succeeds
     * @throws Exception
     */
    public static Boolean inviteUserToSiteWithRoleAndAccept(WebDrone drone, String invitingUsername, String email, String siteShortname, String role,
        String message) throws Exception
    {
        Boolean result = false;

        String domainName = getUserDomain(invitingUsername);

        if (!isAlfrescoVersionCloud(drone))
        {
            throw new UnsupportedOperationException("Method not suitable for use for Enterprise");
        }

        if (StringUtils.isEmpty(role))
        {
            throw new UnsupportedOperationException("Role should not be empty or null.");
        }
        else
        {
            if (role.equalsIgnoreCase("Collaborator"))
            {
                role = "SiteCollaborator";
            }
            else if (role.equalsIgnoreCase("Contributor"))
            {
                role = "SiteContributor";
            }
            else if (role.equalsIgnoreCase("Consumer"))
            {
                role = "SiteConsumer";
            }
        }

        // Invite User
        String[] regInfo = inviteUserToSite(drone, invitingUsername, email, siteShortname, role, message);

        // Accept invitation
        result = userAcceptsSiteInvite(drone, email, domainName, regInfo);

        return result;
    }

    /**
     * Utility to invite a cloud user to Site using invite-To-Site via rest API
     * request
     * 
     * @param drone
     *            WebDrone Instance
     * @param invitingUsername
     *            String username of inviting user
     * @param email
     *            String email or username
     * @param siteShortname
     *            String Name of the site to which the user is being invited
     * @param role
     *            String role to be assigned to the invited user
     * @param message
     *            String message to be sent with the invitation
     * @return String[] array of regKey and activitii id is user
     *         invitation-acceptance succeeds
     * @throws Exception
     */
    public static String[] inviteUserToSite(WebDrone drone, String invitingUsername, String email, String siteShortname, String role, String message)
        throws Exception
    {
        if (!isAlfrescoVersionCloud(drone))
        {
            throw new UnsupportedOperationException("Method not suitable for use for Enterprise");
        }

        String reqURL = getAPIURL(drone) + apiContextCloudInternal + "sites/" + siteShortname + "/invitations";

        logger.info("Request URL - " + reqURL + " for Site Invitation");

        String[] authDetails = getAuthDetails(invitingUsername);

        String[] headers =
        { "key", getHeaderKey() };

        HttpClient client = null;
        HttpPost request = null;
        HttpEntity response = null;

        JSONObject body = new JSONObject();
        body.put("inviterEmail", invitingUsername);
        body.put("inviteeEmails", (new JSONArray()).put(email));
        body.put("role", role);
        body.put("inviterMessage", message);

        try
        {
            client = getHttpClientWithBasicAuth(reqURL, authDetails[0], authDetails[1]);
            request = generatePostRequest(reqURL, headers, body);
            response = executeRequest(client, request);

            String result = JSONUtil.readStream(response).toJSONString();

            String regKey = getParameter("invitations", "key", result);
            String regId = getParameter("invitations", "id", result);
            return new String[]
            { regKey, regId };
        }
        finally
        {
            releaseConnection(client, response);
        }
    }

    /**
     * Utility to Accept or Activate-Invitation via rest API request
     * 
     * @param drone
     *            WebDrone Instance
     * @param invitedUserEmail
     *            String email of inviting user
     * @param invitedToDomain
     *            String Domain name the new user is being invited to
     * @param regInfo
     *            String[] regKey and activitii id generated via Invitation
     *            request
     * @return true if user invitation-acceptance succeeds
     * @throws Exception
     */
    public static Boolean userAcceptsSiteInvite(WebDrone drone, String invitedUserEmail, String invitedToDomain, String[] regInfo) throws Exception
    {
        String reqURL = dronePropertiesMap.get(drone).getShareUrl() + "/" + invitedToDomain + "/page/invitation?key=" + regInfo[0] + "&id=" + regInfo[1];

        logger.info("Request Url - " + reqURL);

        String[] authDetails = getAuthDetails(invitedUserEmail);
        String[] headers =
        { "key", getHeaderKey() };

        HttpGet request = generateGetRequest(reqURL, headers);
        HttpClient client = getHttpClientWithBasicAuth(reqURL, authDetails[0], authDetails[1]);
        HttpResponse response = executeRequestHttpResp(client, request);

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
        {
            logger.info("Invite is actioned by user: " + invitedUserEmail);
            return true;
        }
        logger.error("Invite could not be actioned by user: " + invitedUserEmail);
        return false;
    }
}
