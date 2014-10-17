package org.alfresco.share.clustering.adminconsole;

import org.alfresco.po.share.RepositoryPage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.adminconsole.replicationjobs.ReplicationJob;
import org.alfresco.po.share.adminconsole.replicationjobs.ReplicationJobStatus;
import org.alfresco.po.share.adminconsole.replicationjobs.ReplicationJobsPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.systemsummary.AdminConsoleLink;
import org.alfresco.po.share.systemsummary.RepositoryServerClusteringPage;
import org.alfresco.po.share.systemsummary.SystemSummaryPage;
import org.alfresco.po.share.util.PageUtils;
import org.alfresco.po.share.workflow.CompanyHome;
import org.alfresco.po.share.workflow.Content;
import org.alfresco.po.share.workflow.Site;
import org.alfresco.share.util.*;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.alfresco.po.share.adminconsole.replicationjobs.RepeatEveryValue.MINUTE;
import static org.alfresco.po.share.adminconsole.replicationjobs.ReplicationJobStatus.*;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Holds tests related to replication jobs running on cluster
 *
 * @author Marina.Nenadovets
 */
public class ReplicationJobsClusterTest extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(UsersClusterTest.class);
    private static String node1Url;
    private static String node2Url;
    private static final String regexUrl = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(:\\d{1,5})?";

    private String transferTargetFolderName;
    private String transferTargetFolderPath;
    String transferTargetFolderDesc;
    String rootName;
    String[] transferRootName;
    String sourceFolderPath;
    String DATA_DICTIONARY;
    String TRANSFERS;
    String TRANSFER_TARGET_GROUP;
    String DEFAULT_GROUP;

    String[] fileInfo_big;
    String[] fileInfo_small;
    String folderName;
    String fileName;
    String fileName1;
    String incorrectEdnpointPort = "9999";
    CompanyHome companyHomePL;
    Set<Content> contentsToAdd;
    Set<Content> fileToAdd = new HashSet<>();
    Content transferRoot;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        username = dronePropertiesMap.get(drone).getadminUsername();
        password = dronePropertiesMap.get(drone).getadminPassword();

        DATA_DICTIONARY = drone.getValue("system.folder.data.dictionary");
        TRANSFERS = drone.getValue("system.folder.transfers");
        TRANSFER_TARGET_GROUP = drone.getValue("system.folder.transfer.target.groups");
        DEFAULT_GROUP = drone.getValue("system.folder.transfer.default.group");
        rootName = "Root to transfer" + System.currentTimeMillis();
        transferTargetFolderName = "Transfer_target" + System.currentTimeMillis();
        transferTargetFolderDesc = "Transfer_target_description";
        transferTargetFolderPath = REPO + SLASH + DATA_DICTIONARY + SLASH + TRANSFERS + SLASH + TRANSFER_TARGET_GROUP + SLASH + DEFAULT_GROUP;

        fileName = getRandomString(5);
        getFileWithSize(DATA_FOLDER + SLASH + fileName, 200);
        fileInfo_big = new String[] { fileName, sourceFolderPath + rootName };
        fileName1 = fileName + "1";
        folderName = getRandomString(5);

        transferRootName = new String[] { rootName };
        sourceFolderPath = REPO + SLASH;

        fileInfo_small = new String[] { fileName1, sourceFolderPath + rootName };
        fileInfo_big = new String[] { fileName, sourceFolderPath + rootName };

        logger.info("Starting Tests: " + testName);

        SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();

        RepositoryServerClusteringPage clusteringPage = sysSummaryPage.openConsolePage(AdminConsoleLink.RepositoryServerClustering).render();

        Assert.assertTrue(clusteringPage.isClusterEnabled(), "Cluster isn't enabled");

        List<String> clusterMembers = clusteringPage.getClusterMembers();
        if (clusterMembers.size() >= 2)
        {
            node1Url = shareUrl.replaceFirst(regexUrl, clusterMembers.get(0) + ":" + nodePort);
            node2Url = shareUrl.replaceFirst(regexUrl, clusterMembers.get(1) + ":" + nodePort);
        }
        else
        {
            throw new PageOperationException("Number of cluster members is less than two");
        }

        //Creating root folder in both servers
        for (int i = 0; i < 2; i++)
        {
            ShareUser.login(drone, ADMIN_USERNAME);
            ShareUserRepositoryPage.openRepositoryDetailedView(drone).render();
            ShareUserRepositoryPage.createFolderInRepository(drone, rootName, testName, testName);
            dronePropertiesMap.get(drone).setShareUrl(replicationEndPointHost);
        }

        //Creating root contents and transfer target at source server
        dronePropertiesMap.get(drone).setShareUrl(shareUrl);
        ShareUser.login(drone, ADMIN_USERNAME);
        ShareUserRepositoryPage.openRepositoryDetailedView(drone).render();
        ShareUserRepositoryPage.createFolderInFolderInRepository(drone, folderName, folderName, sourceFolderPath + rootName);
        ShareUserRepositoryPage.uploadFileInFolderInRepository(drone, fileInfo_big);
        ShareUserRepositoryPage.uploadFileInFolderInRepository(drone, fileInfo_small);
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, transferTargetFolderPath);

        transferRoot = new Content();
        transferRoot.setName(rootName);
        transferRoot.setFolder(false);

        contentsToAdd = new HashSet<>();
        contentsToAdd.add(transferRoot);
        companyHomePL = new CompanyHome();
        companyHomePL.setContents(contentsToAdd);

        ShareUserRepositoryPage.createTransferTarget(drone, transferTargetFolderName, transferTargetFolderDesc, companyHomePL, username, password, true);
    }

    /**
     * Verify start of the scheduled job at the both servers
     *
     * @throws Exception
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9195() throws Exception
    {
        String jobName = "job1_" + System.currentTimeMillis();
        String jobDescription = "This is a replication job_" + System.currentTimeMillis();

        //setting the right date format
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date now = new Date();
        String dueDateAndTime = sdf.format(now);
        String[] date = dueDateAndTime.split(" ");
        String dueDate = date[0];
        String dueTime = date[1];
        String repeatEvery = "30";

        //Admin is logged in to ServerA
        dronePropertiesMap.get(drone).setShareUrl(node1Url);
        ShareUser.login(drone, ADMIN_USERNAME);

        //Create job with Transfer1 as 'Transfer Target', root folder as 'Payload' and scheduler to start job in a 2 minutes;
        //Adding two minutes to current time value and changing time format
        Time time = new Time(System.currentTimeMillis());
        now = new Date(time.getTime());
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.MINUTE, 3);
        sdf = new SimpleDateFormat("HH:mm");
        dueTime = sdf.format(cal.getTime());
        ReplicationJobsPage jobsPage = ReplicationJobUtil.createReplicationJob(drone, jobName, jobDescription, companyHomePL,
            transferTargetFolderName, dueDate, dueTime, repeatEvery, MINUTE, true);

        //Job created successfully
        assertTrue(jobsPage.isJobExists(jobName), "Job wasn't created");

        dronePropertiesMap.get(drone).setShareUrl(node2Url);
        ShareUser.login(drone, ADMIN_USERNAME);

        //Created at the ServerA job is available from the ServerB
        ShareUser.getSharePage(drone).getNav().getAdminConsolePage().navigateToReplicationJobs().render();
        assertTrue(jobsPage.isJobExists(jobName), "Job wasn't created");

        //Wait until job starts at the ServerA;
        dronePropertiesMap.get(drone).setShareUrl(node1Url);
        ShareUser.login(drone, ADMIN_USERNAME);
        ShareUser.getSharePage(drone).getNav().getAdminConsolePage().navigateToReplicationJobs().render();
        jobsPage = jobsPage.getJobDetails(jobName).waitUntilJobStarts(drone).render();
        while (jobsPage.getJobDetails(jobName).getStatus().equals(NEW) || jobsPage.getJobDetails(jobName).getStatus().equals(PENDING))
        {
            jobsPage.getJobDetails(jobName).clickRefresh();
        }
        assertTrue(jobsPage.getJobDetails(jobName).getStatus().equals(RUNNING), "The job status is incorrect");

        //Job is displayed at the ServerB with 'running' status;
        dronePropertiesMap.get(drone).setShareUrl(node2Url);
        ShareUser.login(drone, ADMIN_USERNAME);
        ShareUser.getSharePage(drone).getNav().getAdminConsolePage().navigateToReplicationJobs().render();
        assertTrue(jobsPage.getJobDetails(jobName).getStatus().equals(RUNNING), "The job status is incorrect");
        assertTrue(waitUntilJobComplete(jobName));

    }

    /**
     * Verify 'Running' status at both servers
     *
     * @throws Exception
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9196() throws Exception
    {
        String jobName = "job1_" + System.currentTimeMillis();
        String jobDescription = "This is a replication job_" + System.currentTimeMillis();

        Content fileContent = new Content();
        fileContent.setName(fileName);
        fileContent.setFolder(false);
        fileToAdd.clear();
        fileToAdd.add(fileContent);

        transferRoot.setFolder(true);
        transferRoot.setContents(fileToAdd);

        //Create job with Transfer1 as 'Transfer Target', 'fileName' as 'Payload';
        dronePropertiesMap.get(drone).setShareUrl(node1Url);
        ShareUser.login(drone, ADMIN_USERNAME);
        ReplicationJobsPage jobsPage = ReplicationJobUtil.createReplicationJob(drone, jobName, jobDescription, companyHomePL, transferTargetFolderName, null, null, null,
            null, true);

        //Job created successfully at server A
        assertTrue(jobsPage.isJobExists(jobName), "Job wasn't created");

        //Click 'Run Job' for the just created job at the ServerA;
        jobsPage.getJobDetails(jobName).clickRunButton();

        //Job started successfully, job's status is 'running';
        assertTrue(jobsPage.getJobDetails(jobName).getStatus().equals(RUNNING), "The job hasn't start");

        //At the ServerB created job is available with status 'running';
        dronePropertiesMap.get(drone).setShareUrl(node2Url);
        ShareUser.login(drone, ADMIN_USERNAME);
        jobsPage = ShareUser.getSharePage(drone).getNav().getAdminConsolePage().navigateToReplicationJobs().render();
        assertTrue(jobsPage.getJobDetails(jobName).getStatus().equals(RUNNING), "The status is incorrect on server B");
        assertTrue(waitUntilJobComplete(jobName));
    }

    /**
     * Verify job created at ServerA is available from ServerB
     *
     * @throws Exception
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9197() throws Exception
    {
        String jobName = "job1_" + System.currentTimeMillis();
        String jobDescription = "This is a replication job_" + System.currentTimeMillis();
        Content fileContent = new Content();
        fileContent.setName(fileName1);
        fileContent.setFolder(false);

        fileToAdd.clear();
        fileToAdd.add(fileContent);
        transferRoot.setContents(fileToAdd);
        transferRoot.setFolder(true);

        //Create job with Transfer1 as 'Transfer Target', root folder as 'Payload';
        dronePropertiesMap.get(drone).setShareUrl(node1Url);
        ShareUser.login(drone, ADMIN_USERNAME);
        ReplicationJobsPage jobsPage = ReplicationJobUtil.createReplicationJob(drone, jobName, jobDescription, companyHomePL,
            transferTargetFolderName, null, null, null, null, true);

        //Job created successfully at server A
        assertTrue(jobsPage.isJobExists(jobName), "Job wasn't created");

        //At the ServerB go to the 'Replication Jobs' form;
        dronePropertiesMap.get(drone).setShareUrl(node2Url);
        ShareUser.login(drone, ADMIN_USERNAME);

        //Job created successfully at server B
        ShareUser.getSharePage(drone).getNav().getAdminConsolePage().navigateToReplicationJobs().render();
        assertTrue(jobsPage.isJobExists(jobName), "Job wasn't created");
    }

    /**
     * Verify 'Cancelled' status at both servers
     *
     * @throws Exception
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9198() throws Exception
    {
        String jobName = "job1_" + System.currentTimeMillis();
        String jobDescription = "This is a replication job_" + System.currentTimeMillis();
        fileName = getRandomString(5);
        getFileWithSize(DATA_FOLDER + SLASH + fileName, 30);
        fileInfo_big = new String[] { fileName, sourceFolderPath + rootName };

        Content fileContent = new Content();
        fileContent.setName(fileName);
        fileContent.setFolder(false);
        fileToAdd.clear();
        fileToAdd.add(fileContent);

        transferRoot.setContents(fileToAdd);
        transferRoot.setFolder(true);

        ////Uploading large file into repo
        ShareUser.login(drone, ADMIN_USERNAME);
        ShareUserRepositoryPage.uploadFileInFolderInRepository(drone, fileInfo_big);

        //Create job with Transfer1 as 'Transfer Target', file1 as 'Payload';
        dronePropertiesMap.get(drone).setShareUrl(node1Url);
        ShareUser.login(drone, ADMIN_USERNAME);
        ReplicationJobsPage jobsPage = ReplicationJobUtil.createReplicationJob(drone, jobName, jobDescription, companyHomePL,
            transferTargetFolderName, null, null, null, null, true).render();

        //Job created successfully;
        assertTrue(jobsPage.isJobExists(jobName), "Job wasn't created");

        //Click 'Run Job' for the just created job at the ServerA;
        ReplicationJob theJob = jobsPage.getJobDetails(jobName).clickRunButton();

        //Job started successfully, job's status is 'running';
        assertTrue(jobsPage.getJobDetails(jobName).getStatus().equals(RUNNING), "The job hasn't start");

        //While job is running click 'Cancel Job' at the ServerA;
        theJob.clickCancelButton();

        //Cancel request is sent, after a some seconds job will be in 'cancelled' status (need to refresh);
        ReplicationJobStatus theStatus = jobsPage.getJobDetails(jobName).getStatus();
        assertTrue(theStatus.equals(CANCEL_REQUESTED), "Cancel request wasn't sent");

        do
        {
            theJob.clickRefresh();
        }
        while (jobsPage.getJobDetails(jobName).getStatus().equals(CANCEL_REQUESTED));
        assertTrue(jobsPage.getJobDetails(jobName).getStatus().equals(CANCELLED), "Failed to cancel the job");

        //Verify job status at the ServerB;
        dronePropertiesMap.get(drone).setShareUrl(node2Url);
        ShareUser.login(drone, ADMIN_USERNAME);
        jobsPage = ShareUser.getSharePage(drone).getNav().getAdminConsolePage().navigateToReplicationJobs().render();

        //At the ServerB job is available with 'cancelled' status;
        assertTrue(jobsPage.getJobDetails(jobName).getStatus().equals(CANCELLED), "The status is incorrect on server B");
    }

    /**
     * Verify 'Run successfully' status at both servers
     *
     * @throws Exception
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9199() throws Exception
    {
        String jobName = "job1_" + System.currentTimeMillis();
        String jobDescription = "This is a replication job_" + System.currentTimeMillis();
        Content fileContent = new Content();
        fileContent.setName(fileName1);
        fileContent.setFolder(false);

        fileToAdd.clear();
        fileToAdd.add(fileContent);
        transferRoot.setContents(fileToAdd);
        transferRoot.setFolder(true);

        //Create job with Transfer1 as 'Transfer Target', file1 as 'Payload';
        dronePropertiesMap.get(drone).setShareUrl(node1Url);
        ShareUser.login(drone, ADMIN_USERNAME);
        ReplicationJobsPage jobsPage = ReplicationJobUtil.createReplicationJob(drone, jobName, jobDescription, companyHomePL,
            transferTargetFolderName, null, null, null, null, true);

        //Job created successfully;
        assertTrue(jobsPage.isJobExists(jobName), "Job wasn't created");

        //Click 'Run Job' for the just created job at the ServerA;
        jobsPage.getJobDetails(jobName).clickRunButton();

        //Job started successfully, job's status is 'running';
        ReplicationJobStatus theStatus = jobsPage.getJobDetails(jobName).getStatus();
        assertTrue(theStatus.equals(RUNNING), "The job hasn't started");

        //Wait while job run and click 'Refresh' button at the ServerA;
        do
        {
            jobsPage.getJobDetails(jobName).clickRefresh();
        }
        while (jobsPage.getJobDetails(jobName).getStatus().equals(RUNNING));

        //Verify job status at the ServerB;
        dronePropertiesMap.get(drone).setShareUrl(node2Url);
        ShareUser.login(drone, ADMIN_USERNAME);
        jobsPage = ShareUser.getSharePage(drone).getNav().getAdminConsolePage().navigateToReplicationJobs().render();

        //At the ServerB job has 'run successfully' status;
        assertTrue(jobsPage.getJobDetails(jobName).getStatus().equals(COMPLETED), "The status is incorrect on server B");
    }

    /**
     * Verify 'Failed' status at both servers
     *
     * @throws Exception
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9200() throws Exception
    {
        String jobName = "job1_" + System.currentTimeMillis();
        String jobDescription = "This is a replication job_" + System.currentTimeMillis();
        Content fileContent = new Content();
        fileContent.setName(fileName1);
        fileContent.setFolder(false);

        fileToAdd.clear();
        fileToAdd.add(fileContent);

        transferRoot.setContents(fileToAdd);
        transferRoot.setFolder(true);

        //Via Share modify transfer target folder  and set the incorrect values
        ShareUser.login(drone, ADMIN_USERNAME);
        RepositoryPage page = ShareUserRepositoryPage.navigateToFolderInRepository(drone, transferTargetFolderPath);
        EditDocumentPropertiesPage editDocumentPropertiesPage = page.getFileDirectoryInfo(transferTargetFolderName).selectEditProperties().render();
        editDocumentPropertiesPage.setEndpointPort(incorrectEdnpointPort);
        editDocumentPropertiesPage.clickSave();

        //Create job with Transfer1 as 'Transfer Target', file1 as 'Payload';
        dronePropertiesMap.get(drone).setShareUrl(node1Url);
        ShareUser.login(drone, ADMIN_USERNAME);
        ReplicationJobsPage jobsPage = ReplicationJobUtil.createReplicationJob(drone, jobName, jobDescription, companyHomePL,
            transferTargetFolderName, null, null, null, null, true);

        //Job created successfully;
        assertTrue(jobsPage.isJobExists(jobName), "Job wasn't created");

        //Click 'Run Job' for the just created job at the ServerA;
        ReplicationJob theJob = jobsPage.getJobDetails(jobName).clickRunButton();

        //Job started successfully, job's status is 'running' (or 'failed' if run happens too fast);
        ReplicationJobStatus theStatus = jobsPage.getJobDetails(jobName).getStatus();
        assertTrue(theStatus.equals(RUNNING) || theStatus.equals(FAILED), "The job hasn't started");

        //Wait while job run and click 'Refresh' button at the ServerA;
        do
        {
            theJob.clickRefresh();
        }
        while (jobsPage.getJobDetails(jobName).getStatus().equals(RUNNING));

        //Job failed and has status 'failed' at the ServerA;
        assertTrue(jobsPage.getJobDetails(jobName).getStatus().equals(FAILED), "Status isn't correct");

        //Verify job status at the ServerB;
        dronePropertiesMap.get(drone).setShareUrl(node2Url);
        ShareUser.login(drone, ADMIN_USERNAME);
        jobsPage = ShareUser.getSharePage(drone).getNav().getAdminConsolePage().navigateToReplicationJobs().render();

        //At the ServerB job has 'failed' status;
        assertTrue(jobsPage.getJobDetails(jobName).getStatus().equals(FAILED), "The status is incorrect on server B");
    }

    /**
     * Run job - folder as source
     *
     * @throws Exception
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9201() throws Exception
    {
        String jobName = "job1_" + System.currentTimeMillis();
        String jobDescription = "This is a replication job_" + System.currentTimeMillis();
        Content folderContent = new Content();
        folderContent.setName(folderName);
        folderContent.setFolder(false);

        fileToAdd.clear();
        fileToAdd.add(folderContent);

        transferRoot.setContents(fileToAdd);
        transferRoot.setFolder(true);

        //Getting the correct port value
        String addressWithPort = PageUtils.getAddress(replicationEndPointHost);
        String[] splitted = addressWithPort.split(":");
        String portOfTarget = splitted[1];

        //Via Share modify transfer target folder  and set the correct values
        ShareUser.login(drone, ADMIN_USERNAME);
        RepositoryPage page = ShareUserRepositoryPage.navigateToFolderInRepository(drone, transferTargetFolderPath);
        EditDocumentPropertiesPage editDocumentPropertiesPage = page.getFileDirectoryInfo(transferTargetFolderName).selectEditProperties().render();
        editDocumentPropertiesPage.setEndpointPort(portOfTarget);
        editDocumentPropertiesPage.clickSave();

        ShareUserRepositoryPage.openRepositoryDetailedView(drone).render();

        //Create job with Transfer1 as 'Transfer Target', folder1 as 'Payload';
        dronePropertiesMap.get(drone).setShareUrl(node1Url);
        ShareUser.login(drone, ADMIN_USERNAME);
        ReplicationJobsPage jobsPage = ReplicationJobUtil.createReplicationJob(drone, jobName, jobDescription, companyHomePL,
            transferTargetFolderName, null, null, null, null, true);

        //Job created successfully;
        assertTrue(jobsPage.isJobExists(jobName), "Job wasn't created");

        //'Run Job' button became disabled, 'Cancel Job' button became enabled,
        // 'Status' section contains text 'This job is currently running. Job started: %date%';
        ReplicationJob theJob = jobsPage.getJobDetails(jobName).clickRunButton();
        assertFalse(theJob.isRunButtonEnabled(), "Run button is still enabled");
        assertTrue(theJob.isCancelButtonEnabled(), "Cancel button isn't enabled");
        assertTrue(theJob.getPayloadNames().contains(folderName), "Folder wasn't added as payload");
        assertTrue(jobsPage.getJobDetails(jobName).getStatus().equals(RUNNING) || theJob.getStatus().equals(COMPLETED), "The job has failed");

        //Checking that date is displayed and is before current date
        Date dateStarted = jobsPage.getJobDetails(jobName).getDateStarted();
        assertTrue(dateStarted.before(Calendar.getInstance().getTime()));

        //Verify job status at the ServerB;
        dronePropertiesMap.get(drone).setShareUrl(node2Url);
        ShareUser.login(drone, ADMIN_USERNAME);
        jobsPage = ShareUser.getSharePage(drone).getNav().getAdminConsolePage().navigateToReplicationJobs().render();

        //Created at the ServerA job is available from the ServerB;
        assertTrue(jobsPage.getJobDetails(jobName).getStatus().equals(COMPLETED), "The status is incorrect on server B");
    }

    /**
     * Run job - file as source
     *
     * @throws Exception
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9202() throws Exception
    {
        String jobName = "job1_" + System.currentTimeMillis();
        String jobDescription = "This is a replication job_" + System.currentTimeMillis();
        Content folderContent = new Content();
        folderContent.setName(fileName1);
        folderContent.setFolder(false);

        fileToAdd.clear();
        fileToAdd.add(folderContent);

        transferRoot.setContents(fileToAdd);
        transferRoot.setFolder(true);

        //Create job with Transfer1 as 'Transfer Target', file as 'Payload';
        dronePropertiesMap.get(drone).setShareUrl(node1Url);
        ShareUser.login(drone, ADMIN_USERNAME);
        ReplicationJobsPage jobsPage = ReplicationJobUtil.createReplicationJob(drone, jobName, jobDescription, companyHomePL,
            transferTargetFolderName, null, null, null, null, true);

        //Job created successfully;
        assertTrue(jobsPage.isJobExists(jobName), "Job wasn't created");

        //'Run Job' button became disabled, 'Cancel Job' button became enabled,
        // 'Status' section contains text 'This job is currently running. Job started: %date%';
        ReplicationJob theJob = jobsPage.getJobDetails(jobName).clickRunButton();
        assertFalse(theJob.isRunButtonEnabled(), "Run button is still enabled");
        assertTrue(theJob.isCancelButtonEnabled(), "Cancel button isn't enabled");
        assertTrue(theJob.getPayloadNames().contains(fileName1), "File wasn't added as payload");
        assertTrue(jobsPage.getJobDetails(jobName).getStatus().equals(RUNNING) || theJob.getStatus().equals(COMPLETED), "The job has failed");

        //Checking that date is displayed and is before current date
        Date dateStarted = theJob.getDateStarted();
        assertTrue(dateStarted.before(Calendar.getInstance().getTime()));

        //Verify job status at the ServerB;
        dronePropertiesMap.get(drone).setShareUrl(node2Url);
        ShareUser.login(drone, ADMIN_USERNAME);
        jobsPage = ShareUser.getSharePage(drone).getNav().getAdminConsolePage().navigateToReplicationJobs().render();

        //At the ServerB job has 'run successfully' status;
        assertTrue(jobsPage.getJobDetails(jobName).getStatus().equals(COMPLETED), "The status is incorrect on server B");
    }

    /**
     * Edit job
     *
     * @throws Exception
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9203() throws Exception
    {
        String jobName1 = "job1_" + System.currentTimeMillis();
        String jobDescription1 = "This is a replication job_" + System.currentTimeMillis();
        String jobName2 = jobName1 + "2";
        String jobDescription2 = jobDescription1 + "2";
        String folder1 = "folder1" + System.currentTimeMillis();
        String folder2 = "folder2" + System.currentTimeMillis();
        String transfer1 = "transfer1" + System.currentTimeMillis();
        String transfer2 = "transfer2" + System.currentTimeMillis();
        String[] folder1Arr = { folder1 };
        String siteName = getSiteName(getRandomString(8));

        //adding folder1 to 'source' hash set
        Content folderContent = new Content();
        folderContent.setName(folder1);
        folderContent.setFolder(false);

        fileToAdd.clear();
        fileToAdd.add(folderContent);
        Site site = new Site();
        site.setContents(fileToAdd);
        site.setName(siteName);

        Set<Site> siteSet = new HashSet<>();
        siteSet.add(site);
        companyHomePL = new CompanyHome();
        companyHomePL.setSites(siteSet);

        //Any Site1 with Folder1 and Folder2 in it is created at the ServerA and ServerC
        dronePropertiesMap.get(drone).setShareUrl(node1Url);
        for (int i = 1; i <= 2; i++)
        {
            ShareUser.login(drone, ADMIN_USERNAME);
            ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();
            ShareUser.openDocumentLibrary(drone);
            ShareUserSitePage.createFolder(drone, folder1, "");
            ShareUserSitePage.createFolder(drone, folder2, "");
            dronePropertiesMap.get(drone).setShareUrl(replicationEndPointHost);
        }

        //At the ServerA any Transfer1 and Transfer2 folder is created in Data Dictionary->Transfers->Transfer Target Groups->Default Group
        dronePropertiesMap.get(drone).setShareUrl(node1Url);
        ShareUser.login(drone, ADMIN_USERNAME);
        ShareUserRepositoryPage.openRepositoryDetailedView(drone).render();
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, transferTargetFolderPath);
        ShareUserRepositoryPage.createTransferTarget(drone, transfer1, null, companyHomePL, username,
            password, true);
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, transferTargetFolderPath);
        ShareUserRepositoryPage.createTransferTarget(drone, transfer2, null, companyHomePL, username,
            password, true);

        //At the ServerA Job1 with Description1, Folder1 as source and Transfer1 as target is created;
        ReplicationJobUtil.createReplicationJob(drone, jobName1, jobDescription1, companyHomePL, transfer1, null, null, null, null, true);

        //Admin is logged in to ServerA;
        dronePropertiesMap.get(drone).setShareUrl(node1Url);
        ShareUser.login(drone, ADMIN_USERNAME);

        //Click 'Edit' button; Set new name and description; Select Folder2 as source and delete Folder1 from source items;
        // Select Transfer2 folder as target and delete Transfer1 from target items;
        folderContent = new Content();
        folderContent.setName(folder2);
        folderContent.setFolder(false);

        fileToAdd = new HashSet<>();
        fileToAdd.add(folderContent);
        site = new Site();
        site.setContents(fileToAdd);
        site.setName(siteName);

        siteSet = new HashSet<>();
        siteSet.add(site);
        companyHomePL = new CompanyHome();
        companyHomePL.setSites(siteSet);

        ReplicationJobUtil.editReplicationJob(drone, jobName1, jobName2, jobDescription2, folder1Arr, companyHomePL, transfer2, null, null, null,
            null, true);

        //Verify Replication Jobs page at the ServerB;
        dronePropertiesMap.get(drone).setShareUrl(node2Url);
        ShareUser.login(drone, ADMIN_USERNAME);

        //Select Job1 from the 'Jobs' list;
        ReplicationJobsPage jobsPage = ShareUser.getSharePage(drone).getNav().getAdminConsolePage().navigateToReplicationJobs().render();
        ReplicationJob theJob = jobsPage.getJobDetails(jobName2);

        //Edited at the ServerA job is available from the ServerB;
        assertTrue(theJob.getName().equals(jobName2) && theJob.getDescription().equals(jobDescription2) && theJob.getPayloadNames().contains(folder2) &&
            theJob.getTransferTargetName().equals(transfer2), "The Job wasn't edited at ServerB");
    }

    /**
     * Delete job
     *
     * @throws Exception
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9204() throws Exception
    {
        String jobName = "job1_" + System.currentTimeMillis();
        String jobDescription = "This is a replication job_" + System.currentTimeMillis();
        Content folderContent = new Content();
        folderContent.setName(fileName1);
        folderContent.setFolder(false);

        fileToAdd.add(folderContent);
        transferRoot.setContents(fileToAdd);
        transferRoot.setFolder(true);

        ShareUser.login(drone, ADMIN_USERNAME);
        ReplicationJobUtil.createReplicationJob(drone, jobName, jobDescription, companyHomePL,
            transferTargetFolderName, null, null, null, null, true);

        //Admin is logged in to ServerA;
        dronePropertiesMap.get(drone).setShareUrl(node1Url);
        ShareUser.login(drone, ADMIN_USERNAME);

        //Click 'Delete' button and confirm;
        ReplicationJobsPage jobsPage = ReplicationJobUtil.deleteReplicationJob(drone, jobName).render();

        //Job1 deleted successfully and no more available from the 'Jobs' list
        assertFalse(jobsPage.isJobExists(jobName), "The job wasn't deleted");

        //Admin is logged in to ServerB;
        dronePropertiesMap.get(drone).setShareUrl(node2Url);
        ShareUser.login(drone, ADMIN_USERNAME);

        //Deleted at the ServerA job isn't available from the ServerB;
        ShareUser.getSharePage(drone).getNav().getAdminConsolePage().navigateToReplicationJobs().render();
        assertFalse(jobsPage.isJobExists(jobName), "The job wasn't deleted");
    }

    @AfterMethod(alwaysRun = true)
    public void deleteFile() throws Exception
    {
        String remotePathToFolder = "Alfresco" + "/" + rootName + "/";
        File fileToDelete = new File(DATA_FOLDER + SLASH + fileName);
        File fileToDelete2 = new File(DATA_FOLDER + SLASH + fileName1);
        if (fileToDelete.delete() && fileToDelete2.delete())
            logger.info("Files were deleted on source server");
        else
            logger.info("Delete operation has failed on source server");
        boolean isFileExists = FtpUtil.isObjectExists(replicationEndPointHost, ADMIN_USERNAME, ADMIN_PASSWORD, fileName, remotePathToFolder);
        if (isFileExists)
        {
            boolean isDelete = FtpUtil.deleteContentItem(replicationEndPointHost, ADMIN_USERNAME, ADMIN_PASSWORD, fileName, remotePathToFolder);
            if (isDelete)
                logger.info("Files were deleted on target server");
            else
                logger.info("Delete operation has failed on target server");
        }
    }

    private boolean waitUntilJobComplete(String jobTitle)
    {
        boolean jobComplete = false;
        ReplicationJobsPage replicationJobsPage = ShareUser.getSharePage(drone).getNav().getAdminConsolePage().navigateToReplicationJobs().render();
        while (replicationJobsPage.getJobDetails(jobTitle).getStatus().equals(RUNNING))
        {
            webDriverWait(drone, 3000);
            replicationJobsPage.getJobDetails(jobTitle).clickRefresh();
        }
        if (replicationJobsPage.getJobDetails(jobTitle).getStatus().equals(COMPLETED))
        {
            jobComplete = true;
        }
        return jobComplete;
    }
}
