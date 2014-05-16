/*
 * Copyright (C) 2005-2013 Alfresco Software Limited. This file is part of Alfresco Alfresco is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. Alfresco is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General
 * Public License along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.share.util.api;

import java.io.IOException;

import java.util.Properties;

import org.alfresco.json.JSONUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

public class DPAPIUtils extends AlfrescoHttpClient
{
    private static Log logger = LogFactory.getLog(DPAPIUtils.class);
    private static String dpURL = "https://dp.alfresco.me";
    private static String DP_USER;
    private static String DP_PASS;
    private static long dpMaxTime = 20;

    // DP Properties
    protected static String envName;
    protected static String productType;
    protected static String buildNo;
    protected static String configType;
    protected static String configVersion;
    protected static Boolean premiumMode;
    protected static String baseAMIID = "ami-57c8fb3e&U1hPHWGs";
    protected static Boolean layer7Enabled;

    public DPAPIUtils() throws Exception
    {
        super();
        readProperties();
        testName = this.getClass().getSimpleName();
        logger.info("Instantiated Class: " + testName);
    }

    /**
     * Util to create dp environment with properties specified in dpconfig.properties file
     * @return HttpResponse
     * @throws Exception
     */
    public static HttpResponse createDPEnv() throws Exception
    {
        return createDPEnv(envName, productType, buildNo, configType, configVersion, premiumMode, baseAMIID, layer7Enabled);
    }

    /**
     * Util to create dp environment by passing environment config options 
     * @param envName
     * @param product
     * @param buildNo
     * @param configType
     * @param configVersion
     * @param premiumMode
     * @param baseAMIID
     * @param layer7Enabled
     * @return HttpResponse
     * @throws Exception
     */
    public static HttpResponse createDPEnv(String envName, String product, String buildNo, String configType, String configVersion, Boolean premiumMode, String baseAMIID, Boolean layer7Enabled)
            throws Exception
    {
        if (envName != null && envName.equalsIgnoreCase("RANDOM"))
        {
            envName = "alf-" + ("dp" + System.currentTimeMillis()).substring(8, 16);
        }

        String reqURL = dpURL + "/service/environment?name=" + envName + "&productType=" + product + "&productVersion=" + buildNo + "&configType=" + configType
                + "&configVersion=" + configVersion + "&pmode=" + premiumMode + "&layer7enable=" + layer7Enabled;

        // Add optional params to reqURL: if specified
        if (!(baseAMIID == null || baseAMIID.isEmpty()))
        {
            reqURL = reqURL.concat("&imageId=" + baseAMIID);
        }

        String[] authDetails = getAuthDetails(DP_USER);
        String[] headers = {};
        String[] body = {};

        logger.info("Using Url - " + reqURL + " for Create DP Environment");

        HttpPost request = generatePostRequest(reqURL, headers, body);
        HttpClient client = getHttpClientWithBasicAuth(reqURL, authDetails[0], DP_PASS);
        HttpResponse response = executeRequestHttpResp(client, request);

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
        {
            logger.info("New DP Environment Requested: " + envName);
        }
        else
        {
            logger.error("New DP Environment Request failed: " + envName + response.getStatusLine());
        }

        return response;
    }

    /**
     * Util to delete the specified dp environment
     * @param envName
     * @return HttpResponse
     * @throws Exception
     */
    public static HttpResponse deleteDPEnv(String envName) throws Exception
    {

        String reqURL = dpURL + "/service/environment?name=" + envName;
        String[] authDetails = getAuthDetails(DP_USER);
        String[] headers = { "" };
        String[] body = { "" };

        logger.info("Using Url - " + reqURL + " for Delete DP Environment");

        HttpDelete request = generateDeleteRequest(reqURL, headers, body);
        HttpClient client = getHttpClientWithBasicAuth(reqURL, authDetails[0], DP_PASS);
        HttpResponse response = executeRequestHttpResp(client, request);

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
        {
            logger.info("DP Environment Deleted: " + envName);
        }
        else
        {
            logger.error("DP Environment Delete failed: " + envName + response.getStatusLine());
        }
        return response;
    }
    
    /**
     * Util to get dp environment info for the specified entityName / parameter for the given dp environment
     * @param envName
     * @param entityName
     * @param paramName
     * @return String value of the specified entityName / parameter
     * @throws Exception
     */
    public static String getDPEnvInfo(String envName, String entityName, String paramName) throws Exception
    {

        String reqURL = dpURL + "/service/environment/" + envName;
        String[] authDetails = getAuthDetails(DP_USER);
        String[] headers = { "" };

        logger.info("Using Url - " + reqURL + " for Getting DP Environment");

        HttpGet request = generateGetRequest(reqURL, headers);
        HttpClient client = getHttpClientWithBasicAuth(reqURL, authDetails[0], DP_PASS);
        HttpEntity response = executeRequest(client, request);

        String result = JSONUtil.readStream(response).toJSONString();

        String status = getParameterValue(entityName, paramName, result);

        logger.info("DP Environment: " + envName + " - Values Retrieved: " + status);

        return status;
    }

    /**
     * Util to return dp environment status: built, complete, destroy etc
     * @param envName
     * @return String value of dp status
     * @throws Exception
     */
    public static String getDPStatus(String envName) throws Exception
    {
        String status = getDPEnvInfo(envName, "status", "");

        logger.info("DP Environment Status: " + envName);

        return status;
    }

    /**
     * Util to return if dp environment is running
     * @param envName
     * @return <tt>true</tt> if dp is running. false if not
     * @throws Exception
     */
    public static Boolean isDPRunning(String envName) throws Exception
    {
        String status = getDPEnvInfo(envName, "status", "");

        logger.info("DP Environment: " + envName + " - Status:" + status);
        
        status = getDPEnvInfo(envName, "running", "");

        logger.info("DP Environment: " + envName + " - Running:" + status);

        return Boolean.parseBoolean(status);
    }

    /**
     * Util to wait for the dp environment to go to running (only usable status)
     * Util waits for max time = dpMaxTime specified in dpconfig.propeties file and keeps checking the status every minute
     * Returns true if dp comes up after dpMaxTime false if not
     * 
     * @param envName
     * @return
     * @throws Exception
     */
    public static Boolean waitFoDP(String envName) throws Exception
    {
        // Code to wait for dp to come up or Timeout is hit
        for (int waitingFor = 1; waitingFor < dpMaxTime; waitingFor++)
        {
            if (isDPRunning(envName))
            {
                return true;
            }
            else
            {
                // Wait for DP
                logger.info("Wait for DP to come up: " + envName);
                Thread.sleep(60000);
            }
        }
        return isDPRunning(envName);
    }

    /**
     * Util reads the dpconfig.properties file and initialises variables
     */
    private void readProperties()
    {

        Properties properties = new Properties();

        try
        {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("dpconfig.properties"));

            logger.info("do environment configuration properties : " + properties.entrySet());

            DP_USER = properties.getProperty("DP_USER").trim();
            DP_PASS = properties.getProperty("DP_PASS").trim();

            dpMaxTime = Integer.parseInt(properties.getProperty("dpMaxTime"));

            envName = properties.getProperty("envName").trim();
            productType = properties.getProperty("productType").trim();
            buildNo = properties.getProperty("buildNo").trim();
            configType = properties.getProperty("configType").trim();
            configVersion = properties.getProperty("configVersion").trim();
            premiumMode = Boolean.parseBoolean(properties.getProperty("premiumMode"));
            baseAMIID = properties.getProperty("baseAMIID").trim();
            layer7Enabled = Boolean.parseBoolean(properties.getProperty("layer7Enabled"));
        }
        catch (IOException e)
        {
            throw new RuntimeException("Properties file not found for the given input: " + this, e);
        }
        catch (NullPointerException ne)
        {
            logger.error("No matching properties file was found");
        }

    }

}
