package org.alfresco.repo.module.tool;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.alfresco.repo.module.ModuleDetailsImpl;
import org.alfresco.service.cmr.module.ModuleDependency;
import org.alfresco.service.cmr.module.ModuleDetails;
import org.alfresco.util.VersionNumber;

import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileInputStream;


/**
 * Performs logic for the Module Management Tool.
 *
 * @author Gethin James
 */
public class WarHelperImpl implements WarHelper
{

    public static final String VERSION_PROPERTIES = "/WEB-INF/classes/alfresco/version.properties";
    private LogOutput log = null;
    
  
    public WarHelperImpl(LogOutput log)
    {
        super();
        this.log = log;
    }

    @Override
    public void checkCompatibleVersion(TFile war, ModuleDetails installingModuleDetails)
    {
        //Version check
        TFile propsFile = new TFile(war+VERSION_PROPERTIES);
        if (propsFile != null && propsFile.exists())
        {
            Properties warVers = loadProperties(propsFile);
            VersionNumber warVersion = new VersionNumber(warVers.getProperty("version.major")+"."+warVers.getProperty("version.minor")+"."+warVers.getProperty("version.revision"));
            if(warVersion.compareTo(installingModuleDetails.getRepoVersionMin())==-1) {
                throw new ModuleManagementToolException("The module ("+installingModuleDetails.getTitle()+") must be installed on a repo version greater than "+installingModuleDetails.getRepoVersionMin());
            }
            if(warVersion.compareTo(installingModuleDetails.getRepoVersionMax())==1) {
                throw new ModuleManagementToolException("The module ("+installingModuleDetails.getTitle()+") cannot be installed on a repo version greater than "+installingModuleDetails.getRepoVersionMax());
            }
        }
        else 
        {
            log.info("No valid version found, is this a share war?");
        }
    }

    @Override
    public void checkCompatibleEdition(TFile war, ModuleDetails installingModuleDetails)
    {

        List<String> installableEditions = installingModuleDetails.getEditions();

        if (installableEditions != null && installableEditions.size() > 0) {
            
            TFile propsFile = new TFile(war+VERSION_PROPERTIES);
            if (propsFile != null && propsFile.exists())
            {
                Properties warVers = loadProperties(propsFile);
                String warEdition = warVers.getProperty("version.edition");
                
                for (String edition : installableEditions)
                {
                    if (warEdition.equalsIgnoreCase(edition))
                    {
                        return;  //successful match.
                    }
                }
                throw new ModuleManagementToolException("The module ("+installingModuleDetails.getTitle()
                            +") can only be installed in one of the following editions"+installableEditions);
            } else {
                log.info("No valid editions found, is this a share war?");
            }
        }
    }

    @Override
    public void checkModuleDependencies(TFile war, ModuleDetails installingModuleDetails)
    {
        // Check that the target war has the necessary dependencies for this install
        List<ModuleDependency> installingModuleDependencies = installingModuleDetails.getDependencies();
        List<ModuleDependency> missingDependencies = new ArrayList<ModuleDependency>(0);
        for (ModuleDependency dependency : installingModuleDependencies)
        {
            String dependencyId = dependency.getDependencyId();
            ModuleDetails dependencyModuleDetails = getModuleDetails(war, dependencyId);
            // Check the dependency.  The API specifies that a null returns false, so no null check is required
            if (!dependency.isValidDependency(dependencyModuleDetails))
            {
                missingDependencies.add(dependency);
                continue;
            }
        }
        if (missingDependencies.size() > 0)
        {
            throw new ModuleManagementToolException("The following modules must first be installed: " + missingDependencies);
        }
    }
    
    @Override
    public ModuleDetails getModuleDetailsOrAlias(TFile war, ModuleDetails installingModuleDetails)
    {
        ModuleDetails installedModuleDetails = getModuleDetails(war, installingModuleDetails.getId());
        if (installedModuleDetails == null)
        {
            // It might be there as one of the aliases
            List<String> installingAliases = installingModuleDetails.getAliases();
            for (String installingAlias : installingAliases)
            {
                ModuleDetails installedAliasModuleDetails = getModuleDetails(war, installingAlias);
                if (installedAliasModuleDetails == null)
                {
                    // There is nothing by that alias
                    continue;
                }
                // We found an alias and will treat it as the same module
                installedModuleDetails = installedAliasModuleDetails;
                //outputMessage("Module '" + installingAlias + "' is installed and is an alias of '" + installingModuleDetails + "'", false);
                break;
            }
        }
        return installedModuleDetails;
    }

    /**
     * Gets the module details for the specified module from the war.
     * @param war   a valid war file or exploded directory from a war
     * @param moduleId
     * @return
     */
    protected ModuleDetails getModuleDetails(TFile war, String moduleId)
    {
        ModuleDetails moduleDets = null;
        TFile theFile = getModuleDetailsFile(war, moduleId);
        if (theFile != null && theFile.exists())
        {
            moduleDets =  new ModuleDetailsImpl(loadProperties(theFile));
        }
        return moduleDets;
    }
    
    /**
     * Reads a .properites file from the war and returns it as a Properties object
     * @param propertiesPath Path to the properties file (including .properties)
     * @return Properties object or null
     */
    private Properties loadProperties(TFile propertiesFile)
    {
        Properties result = null;
        InputStream is = null;
        try
        {
            if (propertiesFile.exists())
            {
                is = new TFileInputStream(propertiesFile);
                result = new Properties();
                result.load(is);
            }
        }
        catch (IOException exception)
        {
            throw new ModuleManagementToolException("Unable to load properties from the war file; "+propertiesFile.getPath(), exception);
        }
        finally
        {
            if (is != null)
            {
                try { is.close(); } catch (Throwable e ) {}
            }
        }
        return result;
       
    }
    
    private TFile getModuleDetailsFile(TFile war, String moduleId)
    {
        return new TFile(war.getAbsolutePath()+MODULE_NAMESPACE_DIR+ "/" + moduleId+MODULE_CONFIG_IN_WAR);
    }
    

}
