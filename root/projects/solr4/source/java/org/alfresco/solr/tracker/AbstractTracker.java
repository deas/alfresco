/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.solr.tracker;

import java.text.ParseException;
import java.util.Properties;

import org.alfresco.encryption.KeyStoreParameters;
import org.alfresco.encryption.ssl.SSLEncryptionParameters;
import org.alfresco.httpclient.AlfrescoHttpClient;
import org.alfresco.httpclient.HttpClientFactory;
import org.alfresco.httpclient.HttpClientFactory.SecureCommsType;
import org.alfresco.opencmis.dictionary.CMISStrictDictionaryService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.solr.IndexTrackingShutdownException;
import org.alfresco.solr.InformationServer;
import org.alfresco.solr.SolrKeyResourceLoader;
import org.alfresco.solr.client.SOLRAPIClient;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Abstract base class that provides common {@link Tracker} behaviour.
 * 
 * @author Matt Ward
 */
public abstract class AbstractTracker implements Tracker
{
    protected final static Logger log = LoggerFactory.getLogger(AbstractTracker.class);
    
    protected Properties props;
    
    protected SOLRAPIClient client;
    protected InformationServer infoSrv;

    protected String coreName;

    protected String cron;

    protected StoreRef storeRef;
    protected long batchCount;

    protected boolean isSlave = false;
    
    protected boolean isMaster = true;
    
    protected String alfrescoVersion;

    protected String id;
    
    protected TrackerStats trackerStats;
    
    protected boolean runPostModelLoadInit = true;
    
    private int maxLiveSearchers;
    
    private volatile boolean shutdown = false;
    
    // encryption related parameters
    private String secureCommsType; // "none", "https"

    private String keyStoreType;

    private String keyStoreProvider;

    private String passwordFileLocation;

    private String keyStoreLocation;

    // ssl
    private String sslKeyStoreType;

    private String sslKeyStoreProvider;

    private String sslKeyStoreLocation;

    private String sslKeyStorePasswordFileLocation;

    private String sslTrustStoreType;

    private String sslTrustStoreProvider;

    private String sslTrustStoreLocation;

    private String sslTrustStorePasswordFileLocation;

    private String alfrescoHost;

    private int alfrescoPort;

    private int alfrescoPortSSL;

    private String baseUrl;

    // index contrl

    // http client
    private int maxTotalConnections = 40;

    private int maxHostConnections = 40;

    private int socketTimeout = 120000;

    /**
     * Default constructor, strictly for testing.
     */
    protected AbstractTracker()
    {
    }
    
    protected AbstractTracker(Scheduler scheduler, String id, Properties p, SolrKeyResourceLoader keyResourceLoader, 
                String coreName, InformationServer informationServer)
    {
        this.props = p;
        alfrescoHost = p.getProperty("alfresco.host", "localhost");
        alfrescoPort = Integer.parseInt(p.getProperty("alfresco.port", "8080"));
        alfrescoPortSSL = Integer.parseInt(p.getProperty("alfresco.port.ssl", "8443"));
        baseUrl = p.getProperty("alfresco.baseUrl", "/alfresco");
        keyStoreType = p.getProperty("alfresco.encryption.keystore.type", "JCEKS");
        keyStoreProvider = p.getProperty("alfresco.encryption.keystore.provider");
        passwordFileLocation = p.getProperty("alfresco.encryption.keystore.passwordFileLocation");
        keyStoreLocation = p.getProperty("alfresco.encryption.keystore.location");
        sslKeyStoreType = p.getProperty("alfresco.encryption.ssl.keystore.type");
        sslKeyStoreProvider = p.getProperty("alfresco.encryption.ssl.keystore.provider", "");
        sslKeyStoreLocation = p.getProperty("alfresco.encryption.ssl.keystore.location", "ssl.repo.client.keystore");
        sslKeyStorePasswordFileLocation = p.getProperty("alfresco.encryption.ssl.keystore.passwordFileLocation", "ssl-keystore-passwords.properties");
        sslTrustStoreType = p.getProperty("alfresco.encryption.ssl.truststore.type", "JCEKS");
        sslTrustStoreProvider = p.getProperty("alfresco.encryption.ssl.truststore.provider", "");
        sslTrustStoreLocation = p.getProperty("alfresco.encryption.ssl.truststore.location", "ssl.repo.client.truststore");
        sslTrustStorePasswordFileLocation = p.getProperty("alfresco.encryption.ssl.truststore.passwordFileLocation", "ssl-truststore-passwords.properties");
        secureCommsType = p.getProperty("alfresco.secureComms", "https");
        maxTotalConnections = Integer.parseInt(p.getProperty("alfresco.maxTotalConnections", "40"));
        maxHostConnections = Integer.parseInt(p.getProperty("alfresco.maxHostConnections", "40"));
        socketTimeout = Integer.parseInt(p.getProperty("alfresco.socketTimeout", "0"));
        
        this.id = id;
        this.coreName = coreName;
        this.infoSrv = informationServer;

        cron =  p.getProperty("alfresco.cron", "0/15 * * * * ? *");
        storeRef = new StoreRef(p.getProperty("alfresco.stores"));
        batchCount = Integer.parseInt(p.getProperty("alfresco.batch.count", "1000"));
        maxLiveSearchers =  Integer.parseInt(p.getProperty("alfresco.maxLiveSearchers", "2"));
        isSlave =  Boolean.parseBoolean(p.getProperty("enable.slave", "false"));
        isMaster =  Boolean.parseBoolean(p.getProperty("enable.master", "true"));

        this.trackerStats = new TrackerStats(this.infoSrv);
        
        alfrescoVersion = p.getProperty("alfresco.version", "4.2.2");

        client = new SOLRAPIClient(getRepoClient(keyResourceLoader), 
                    this.infoSrv.getDictionaryService(CMISStrictDictionaryService.DEFAULT), 
                    this.infoSrv.getNamespaceDAO());
        initCoreTrackerJob(scheduler);
    }
    
    private void initCoreTrackerJob(Scheduler scheduler)
    {
        JobDetail job = new JobDetail("CoreTracker-" + coreName, "Solr", TrackerJob.class);
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("TRACKER", this);
        job.setJobDataMap(jobDataMap);
        Trigger trigger;
        try
        {
            trigger = new CronTrigger("CoreTrackerTrigger" + coreName, "Solr", cron);
            scheduler.scheduleJob(job, trigger);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        catch (SchedulerException e)
        {
            e.printStackTrace();
        }
        log.info("Solr built for Alfresco version: " + alfrescoVersion);
    }
    
    protected AlfrescoHttpClient getRepoClient(SolrKeyResourceLoader keyResourceLoader)
    {
        // TODO i18n
        KeyStoreParameters keyStoreParameters = new KeyStoreParameters("SSL Key Store", sslKeyStoreType, sslKeyStoreProvider, sslKeyStorePasswordFileLocation, sslKeyStoreLocation);
        KeyStoreParameters trustStoreParameters = new KeyStoreParameters("SSL Trust Store", sslTrustStoreType, sslTrustStoreProvider, sslTrustStorePasswordFileLocation, sslTrustStoreLocation);
        SSLEncryptionParameters sslEncryptionParameters = new SSLEncryptionParameters(keyStoreParameters, trustStoreParameters);
        
        HttpClientFactory httpClientFactory = new HttpClientFactory(SecureCommsType.getType(secureCommsType),
                sslEncryptionParameters, keyResourceLoader, null, null, alfrescoHost, alfrescoPort, alfrescoPortSSL, maxTotalConnections, maxHostConnections, socketTimeout);
        // TODO need to make port configurable depending on secure comms, or just make redirects
        // work
        AlfrescoHttpClient repoClient = httpClientFactory.getRepoClient(alfrescoHost, alfrescoPortSSL);
        repoClient.setBaseUrl(baseUrl);
        return repoClient;
    }
    

    public int getMaxLiveSearchers()
    {
        return maxLiveSearchers;
    }

    protected void checkShutdown()
    {
        if(shutdown)
        {
            throw new IndexTrackingShutdownException();
        }
    }
    
    public void setShutdown(boolean shutdown)
    {
        this.shutdown = shutdown;
    }
    
    public void close()
    {
        client.close();
    }

    /**
     * @return Alfresco version Solr was built for
     */
    @Override
    public String getAlfrescoVersion()
    {
        return alfrescoVersion;
    }
}
