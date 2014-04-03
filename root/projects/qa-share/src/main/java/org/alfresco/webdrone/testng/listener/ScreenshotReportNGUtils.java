package org.alfresco.webdrone.testng.listener;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.alfresco.share.util.AbstractUtils;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.lang.WordUtils;
import org.testng.ITestResult;
import org.uncommons.reportng.ReportNGUtils;

/**
 * @author Ranjith Manyam
 */
public class ScreenshotReportNGUtils extends ReportNGUtils
{
    public static final String SLASH = File.separator;

    @Override
    public List<String> getTestOutput(ITestResult result)
    {
        List<String> output = super.getTestOutput(result);

        output.add("<script>\n" +
                "    function clickImage(imageDiv, imgID, imgBtn)\n" +
                "    {\n" +
                "        document.getElementById(imageDiv).style.display=\"inline\";\n" +
                "        document.getElementById(imgID).style.display=\"inline\";\n" +
                "        document.getElementById(imgBtn).style.display=\"none\";\n" +
                "    }\n" +
                "    function hideImage(imageDiv, imgID, imgBtn)\n" +
                "    {\n" +
                "        document.getElementById(imgID).style.display=\"none\";\n" +
                "        document.getElementById(imgBtn).style.display=\"inline\";\n" +
                "        document.getElementById(imageDiv).style.display=\"none\";\n" +
                "    }\n" +
                "</script>\n" +
                "\n");
        Object instace = result.getInstance();
        if (instace instanceof AbstractUtils)
        {
            AbstractUtils abstractTests = (AbstractUtils) instace;
            Map<String, WebDrone> droneMap = abstractTests.getDroneMap();
            for (Map.Entry<String, WebDrone> entry : droneMap.entrySet())
            {
                String fileName = entry.getKey() + result.getMethod().getMethodName() + ".png";

                System.setProperty("org.uncommons.reportng.escape-output", "false");

                // add screenshot if there is one
                String screenshot = (String) result.getAttribute(entry.getKey() + result.getMethod().getMethodName());
                if (screenshot != null)
                {
                    String imageDivID = "imgDivID" + entry.getKey() + result.getMethod().getMethodName();
                    String imageID = "imageID" + entry.getKey() + result.getMethod().getMethodName();
                    String imageButtonID = "imageBtnID" + entry.getKey() + result.getMethod().getMethodName();
                    String buttonName = WordUtils.capitalize(entry.getKey()) + "-" + "ScreenShot";

                    output.add("<div>\n" +
                            "    <div id=\""+imageDivID+"\" style=\"display: none\">\n" +
                            "        <img id=\""+imageID+"\" src=" + fileName  + " style=\"display: none; \" onclick=\"hideImage('" + imageDivID + "', '"+imageID+"', '"+imageButtonID+"');\"/>\n" +
                            "    </div>\n" +
                            "    <div align=\"right\">\n" +
                            "        <input id=\""+imageButtonID+"\" type=\"button\" value=\""+buttonName+"\" onclick=\"clickImage('" + imageDivID + "', '"+imageID+"', '"+imageButtonID+"');\" style=\"display: inline;\">\n" +
                            "    </div>\n" +
                            "</div>");

                }
            }

        }
        return output;
    }
}
