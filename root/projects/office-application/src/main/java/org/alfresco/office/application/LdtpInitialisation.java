/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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

package org.alfresco.office.application;

/**
 * Abstract util method to start LDTP and setting all the variables.
 * 
 * @author Subashni Prasanna
 */
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cobra.ldtp.LdtpExecutionError;

public class LdtpInitialisation
{

    private static Log logger = LogFactory.getLog(LdtpInitialisation.class);
    public static String applicationPath;
    public static String explorerSaveAs;
    public static String saveButton;
    public static String fileMenu;
    public static String saveAsButton;
    public static String OpenDialog;
    public static String openButton;
    public static String exitButton;
    public static String optionsButton;
    public static String optionDialog;
    public static String okButton;
    public static String startUpCheckBox;
    private Properties confOfficeProperty;

    /**
     * Util method to read all the LDTP property values
     */

    public LdtpInitialisation()
    {
        try
        {
            confOfficeProperty = new Properties();
            confOfficeProperty.load(this.getClass().getClassLoader().getResourceAsStream("office.properties"));

        }
        catch (Exception e)
        {
            logger.error("Failed to load office App properties in the AbstractUtil Class :" + this.getClass(), e);
        }

    }

    public String getOptionsButton()
    {
        return confOfficeProperty.getProperty("options.button");
    }

    public String getOptionDialog()
    {
        return confOfficeProperty.getProperty("options.dialog");
    }

    public String getOkButton()
    {
        return confOfficeProperty.getProperty("ok.button");
    }

    public String getStartUpCheckBox()
    {
        return confOfficeProperty.getProperty("startup.checkbox");
    }

    public String getApplicationPath(String officeVersion)
    {
        return confOfficeProperty.getProperty("office" + officeVersion + ".path");

    }

    public String getExplorerSaveAs()
    {
        return confOfficeProperty.getProperty("explorersave.windowname");
    }

    public String getSaveButton()
    {
        return confOfficeProperty.getProperty("save.button");
    }

    public String getFileMenu()
    {
        return confOfficeProperty.getProperty("file.menu");
    }

    public String getSaveAsButton()
    {
        return confOfficeProperty.getProperty("saveAS.button");
    }

    public String getOpenDialog()
    {
        return confOfficeProperty.getProperty("exploreropen.window");
    }

    public String getOpenButton()
    {
        return confOfficeProperty.getProperty("exploreropen.openbutton");
    }

    public String getExitButton()
    {
        return confOfficeProperty.getProperty("exit.button");
    }

}
