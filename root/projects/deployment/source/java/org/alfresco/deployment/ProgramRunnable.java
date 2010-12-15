/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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

package org.alfresco.deployment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.alfresco.deployment.impl.server.DeployedFile;
import org.alfresco.deployment.impl.server.Deployment;

/**
 * FSDeployment Runnable to be used for running external programs.
 * @author britt
 */
public class ProgramRunnable implements FSDeploymentRunnable
{
    private static final long serialVersionUID = -6694971994859005513L;

    private Deployment fDeployment;
    
    /**
     * The path to the executable.
     */
    private String fProgram;
    
    /**
     * The directory that the program should run in.
     */
    private String fDirectory;
    
    /**
     * Additional arguments to the program.
     */
    private List<String> fArguments;
    
    ProgramRunnable()
    {
        fArguments = new ArrayList<String>();
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.deployment.FSDeploymentRunnable#init(org.alfresco.deployment.impl.server.Deployment)
     */
    public void init(Deployment deployment)
    {
        fDeployment = deployment;
    }
    
    public void setProgram(String program)
    {
        fProgram = program;
    }
    
    public void setDirectory(String directory)
    {
        fDirectory = directory;
    }

    public void setArguments(List<String> arguments)
    {
        fArguments = arguments;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
        try
        {
            File tempFile = File.createTempFile("deployment", "txt");
            Writer out = new FileWriter(tempFile);
            for (DeployedFile file : fDeployment)
            {
                out.write(file.getType().toString() + " '" + file.getPath().replaceAll("'", "\\\\'") + "' " + file.getGuid() + "\n");
            }
            out.close();
            Runtime runTime = Runtime.getRuntime();
            int commandLength = 2 + fArguments.size();
            String[] command = new String[commandLength];
            command[0] = fProgram;
            command[1] = tempFile.getAbsolutePath();
            int off = 2;
            for (String arg : fArguments)
            {
                command[off++] = arg;
            }
            Map<String, String> envMap = System.getenv();
            String[] env = new String[envMap.size()];
            off = 0;
            for (Map.Entry<String, String> entry : envMap.entrySet())
            {
                env[off++] = entry.getKey() + '=' + entry.getValue();
            }
            Process process = runTime.exec(command, env, new File(fDirectory));
            
            StreamDigester errorDigester = new StreamDigester(process.getErrorStream());
            StreamDigester outputDigester = new StreamDigester(process.getInputStream());
            errorDigester.start();
            outputDigester.start();
            
            process.waitFor();
            tempFile.delete();
        }
        catch (IOException ie)
        {
            // Do nothing for now.
        }
        catch (InterruptedException inte)
        {
            // Do nothing for now.
        }
    }
}

class StreamDigester extends Thread 
{
	  private InputStream is;

	  StreamDigester(InputStream is) 
	  {
	    this.is = is;
	  }

	  public void run() 
	  {
	    try 
	    {
	      InputStreamReader isr = new InputStreamReader(is);
	      BufferedReader br = new BufferedReader(isr);
	      String line = null;
	      while ((line = br.readLine()) != null) 
	      {}
	    } catch (IOException ioe) {
	    	ioe.printStackTrace();
	    }
	  }
}
