package org.alfresco.webdrone.testng.listener;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.alfresco.share.util.AbstractUtils;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.io.FileUtils;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.uncommons.reportng.HTMLReporter;

/**
 * @author Ranjith Manyam
 */
public class ScreenshotHTMLReporter extends HTMLReporter implements ITestListener
{

    protected static final ScreenshotReportNGUtils SS_UTILS = new ScreenshotReportNGUtils();
    public static final String SLASH = File.separator;

    private static final Logger logger = LoggerFactory.getLogger(ScreenshotHTMLReporter.class);

    protected VelocityContext createContext()
    {
        VelocityContext context = super.createContext();
        context.put("utils", SS_UTILS);
        return context;
    }

    @Override
    public void onTestStart(ITestResult result)
    {

    }

    @Override
    public void onTestSuccess(ITestResult result)
    {

    }

    @Override
    public void onTestFailure(ITestResult tr)
    {
        Object instace = tr.getInstance();
        if (instace instanceof AbstractUtils)
        {
            AbstractUtils abstractTests = (AbstractUtils) instace;
            Map<String, WebDrone> droneMap = abstractTests.getDroneMap();
            saveScreenShots(tr, droneMap);
        }
    }

    private void saveScreenShots(ITestResult tr, Map<String, WebDrone> droneMap)
    {
        for (Map.Entry<String, WebDrone> entry : droneMap.entrySet())
        {
            if (entry.getValue() != null)
            {
                try
                {
                    File file = entry.getValue().getScreenShot();

                    logger.debug("File: {} ", file.hashCode());
                    // output dir includes suite, so go up one level
                    String outputDir = tr.getTestContext().getOutputDirectory();
                    logger.debug("Output Directory: {}", outputDir);
                    outputDir = outputDir.substring(0, outputDir.lastIndexOf(SLASH)) + SLASH + "html";
                    File saved = new File(outputDir, entry.getKey() + tr.getMethod().getMethodName() + ".png");
                    FileUtils.copyFile(file, saved);
                    // save screenshot path as result attribute so generateReport can access it
                    tr.setAttribute(entry.getKey() + tr.getMethod().getMethodName(), saved.getName());
                }
                catch (IOException ex)
                {
                    logger.error("Error generating screenshot" + ex);
                }
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result)
    {

    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result)
    {

    }

    @Override
    public void onStart(ITestContext context)
    {

    }

    @Override
    public void onFinish(ITestContext context)
    {

    }
}
