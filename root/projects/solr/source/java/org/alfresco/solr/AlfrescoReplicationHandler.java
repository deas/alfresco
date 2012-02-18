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
package org.alfresco.solr;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.solr.common.params.SolrParams;
import org.apache.solr.core.SolrCore;
import org.apache.solr.handler.ReplicationHandler;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.request.SolrQueryResponse;
import org.apache.solr.util.plugin.SolrCoreAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapted from the SOLR 3.5 implementation
 * 
 * @author Andy
 */
public class AlfrescoReplicationHandler extends ReplicationHandler implements SolrCoreAware
{
    private static final Logger LOG = LoggerFactory.getLogger(AlfrescoReplicationHandler.class.getName());

    public static final String NUMBER_BACKUPS_TO_KEEP = "numberToKeep";

    SolrCore core;

    public void handleRequestBody(SolrQueryRequest req, SolrQueryResponse rsp) throws Exception
    {
        rsp.setHttpCaching(false);
        final SolrParams solrParams = req.getParams();
        String command = solrParams.get(COMMAND);
        if (command == null)
        {
            rsp.add(STATUS, OK_STATUS);
            rsp.add("message", "No command");
            return;
        }
        // This command does not give the current index version of the master
        // It gives the current 'replicateable' index version
        
        super.handleRequestBody(req, rsp);
        if (command.equalsIgnoreCase(CMD_BACKUP))
        {
            int numberToKeep = solrParams.getInt(NUMBER_BACKUPS_TO_KEEP, Integer.MAX_VALUE); // To test
            if (numberToKeep < Integer.MAX_VALUE)
            {
                new AlfrescoSnapShooter(core, solrParams.get("location")).deleteOldBackups(numberToKeep);
            }
          
        }
    }

    public void inform(SolrCore core)
    {
        this.core = core;
        super.inform(core);
    }

    /**
     * <p/>
     * Provides functionality equivalent to the snapshooter script
     * </p>
     * 
     * @version $Id: SnapShooter.java 1203003 2011-11-17 01:50:33Z hossman $
     * @since solr 1.4
     */
    public class AlfrescoSnapShooter
    {
        private String snapDir = null;

        public AlfrescoSnapShooter(SolrCore core, String location) throws IOException
        {
            if (location == null)
                snapDir = core.getDataDir();
            else
            {
                File base = new File(core.getCoreDescriptor().getInstanceDir());
                snapDir = org.apache.solr.common.util.FileUtils.resolvePath(base, location).getAbsolutePath();
                File dir = new File(snapDir);
                if (!dir.exists())
                    dir.mkdirs();
            }
        }

     
        private void deleteOldBackups(int numberToKeep)
        {
            File[] files = new File(snapDir).listFiles();
            List<OldBackupDirectory> dirs = new ArrayList<OldBackupDirectory>();
            for (File f : files)
            {
                OldBackupDirectory obd = new OldBackupDirectory(f);
                if (obd.dir != null)
                {
                    dirs.add(obd);
                }
            }
            Collections.sort(dirs);
            int i = 1;
            for (OldBackupDirectory dir : dirs)
            {
                if (i > numberToKeep - 1)
                {
                    delTree(dir.dir);
                }
                i++;
            }
        }

        private class OldBackupDirectory implements Comparable<OldBackupDirectory>
        {
            File dir;

            Date timestamp;

            final Pattern dirNamePattern = Pattern.compile("^snapshot[.](.*)$");

            OldBackupDirectory(File dir)
            {
                if (dir.isDirectory())
                {
                    Matcher m = dirNamePattern.matcher(dir.getName());
                    if (m.find())
                    {
                        try
                        {
                            this.dir = dir;
                            this.timestamp = new SimpleDateFormat(DATE_FMT).parse(m.group(1));
                        }
                        catch (Exception e)
                        {
                            this.dir = null;
                            this.timestamp = null;
                        }
                    }
                }
            }

            public int compareTo(OldBackupDirectory that)
            {
                return that.timestamp.compareTo(this.timestamp);
            }
        }

        public static final String SNAP_DIR = "snapDir";

        public static final String DATE_FMT = "yyyyMMddHHmmss";

    }

    /**
     * Delete the directory tree recursively
     */
    static boolean delTree(File dir)
    {
        if (dir == null || !dir.exists())
            return false;
        boolean isSuccess = true;
        File contents[] = dir.listFiles();
        if (contents != null)
        {
            for (File file : contents)
            {
                if (file.isDirectory())
                {
                    boolean success = delTree(file);
                    if (!success)
                    {
                        LOG.warn("Unable to delete directory : " + file);
                        isSuccess = false;
                    }
                }
                else
                {
                    boolean success = file.delete();
                    if (!success)
                    {
                        LOG.warn("Unable to delete file : " + file);
                        isSuccess = false;
                        return false;
                    }
                }
            }
        }
        return isSuccess && dir.delete();
    }

}
