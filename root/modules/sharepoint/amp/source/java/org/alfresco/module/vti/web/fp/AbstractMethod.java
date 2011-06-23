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
package org.alfresco.module.vti.web.fp;

import java.io.IOException;

import org.alfresco.module.vti.handler.MethodHandler;
import org.alfresco.module.vti.metadata.dic.VtiConstraint;
import org.alfresco.module.vti.metadata.dic.VtiError;
import org.alfresco.module.vti.metadata.dic.VtiProperty;
import org.alfresco.module.vti.metadata.dic.VtiType;
import org.alfresco.module.vti.metadata.model.DocMetaInfo;
import org.alfresco.module.vti.web.VtiEncodingUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract base class for all the Vti method handling classes.
 * Base realization of {@link VtiMethod}.
 * 
 * @author Michael Shavnev
 */
public abstract class AbstractMethod implements VtiMethod
{
    private final static Log logger = LogFactory.getLog(AbstractMethod.class);

    protected MethodHandler vtiHandler;

    private final static String MAGIC_STRING_IRRECOVERABLE_ERROR = "*-*-* :-| :^| :-/  :-( 8-( *-*-*";

    /**
     * @return the vtiHandler
     */
    public MethodHandler getVtiHandler()
    {
        return vtiHandler;
    }

    /**
     * @param vtiHandler the vtiHandler to set
     */
    public void setVtiHandler(MethodHandler vtiHandler)
    {
        this.vtiHandler = vtiHandler;
    }

    
    /** 
     * @see org.alfresco.module.vti.web.fp.VtiMethod#execute(org.alfresco.module.vti.web.fp.VtiFpRequest, org.alfresco.module.vti.web.fp.VtiFpResponse)
     */
    public final void execute(VtiFpRequest request, VtiFpResponse response)
    {
        try
        {
            try
            {
                doExecute(request, response);
            }
            catch (VtiMethodException e)
            {
                if (logger.isDebugEnabled())
                {
                    if (e.getErrorCode() != VtiError.V_BAD_URL.getErrorCode())
                    {
                        logger.debug(getName(), e);
                    }
                }

                if (e.getErrorCode() == VtiError.V_UNDEFINED.getErrorCode())
                {
                    response.getOutputStream().write(MAGIC_STRING_IRRECOVERABLE_ERROR.getBytes());
                }
                else
                {
                    response.beginVtiAnswer(getName(), ServerVersionMethod.version);
                    response.beginList("status");
                    response.addParameter("status", String.valueOf(e.getErrorCode()));
                    response.addParameter("osstatus", "0");
                    response.addParameter("msg", e.getMessage());
                    response.addParameter("osmsg", "");
                    response.endList();
                    response.endVtiAnswer();
                }
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Target method that will be executed in child classes  
     * 
     * @param request Vti Frontpage request ({@link VtiFpRequest})
     * @param response Vti Frontpage response ({@link VtiFpResponse})
     */    
    protected abstract void doExecute(VtiFpRequest request, VtiFpResponse response) throws VtiMethodException, IOException;

    
    /**
     * Create response for DocMetaInfo 
     * 
     * @param docMetaInfo Document Meta Information
     * @param request Vti Frontpage request
     * @param response Vti Frontpage response
     */  
    protected void processDocMetaInfo(DocMetaInfo docMetaInfo, VtiFpRequest request, VtiFpResponse response) throws VtiMethodException, IOException
    {
        response.writeMetaDictionary(VtiProperty.FILE_THICKETDIR, VtiType.BOOLEAN, VtiConstraint.R, docMetaInfo.getThicketdir());
        response.writeMetaDictionary(VtiProperty.FILE_TIMECREATED, VtiType.TIME, VtiConstraint.R, docMetaInfo.getTimecreated());
        response.writeMetaDictionary(VtiProperty.FILE_TIMELASTMODIFIED, VtiType.TIME, VtiConstraint.R, docMetaInfo.getTimelastmodified());
        response.writeMetaDictionary(VtiProperty.FILE_TIMELASTWRITTEN , VtiType.TIME, VtiConstraint.R, docMetaInfo.getTimelastwritten());

        if (docMetaInfo.isFolder())
        {
            response.writeMetaDictionary(VtiProperty.FOLDER_DIRLATESTSTAMP, VtiType.TIME, VtiConstraint.R, docMetaInfo.getDirlateststamp());
            response.writeMetaDictionary(VtiProperty.FOLDER_HASSUBDIRS, VtiType.BOOLEAN, VtiConstraint.R, docMetaInfo.getHassubdirs());
            response.writeMetaDictionary(VtiProperty.FOLDER_ISBROWSABLE, VtiType.BOOLEAN, VtiConstraint.R, docMetaInfo.getIsbrowsable());
            response.writeMetaDictionary(VtiProperty.FOLDER_ISCHILDWEB, VtiType.BOOLEAN, VtiConstraint.R, docMetaInfo.getIschildweb());
            response.writeMetaDictionary(VtiProperty.FOLDER_ISEXECUTABLE, VtiType.BOOLEAN, VtiConstraint.R, docMetaInfo.getIsexecutable());
            response.writeMetaDictionary(VtiProperty.FOLDER_ISSCRIPTABLE, VtiType.BOOLEAN, VtiConstraint.R, docMetaInfo.getIsscriptable());
            response.writeMetaDictionary(VtiProperty.FOLDER_LISTBASETYPE, VtiType.INT, VtiConstraint.R, docMetaInfo.getListbasetype());
        }
        else
        {
            response.writeMetaDictionary(VtiProperty.FILE_TITLE, VtiType.STRING, VtiConstraint.R, VtiEncodingUtils.encode(docMetaInfo.getTitle()));
            response.writeMetaDictionary(VtiProperty.FILE_FILESIZE, VtiType.INT, VtiConstraint.R, docMetaInfo.getFilesize());
            response.writeMetaDictionary(VtiProperty.FILE_METATAGS , VtiType.VECTOR, VtiConstraint.R, VtiEncodingUtils.encode(docMetaInfo.getMetatags()));
            response.writeMetaDictionary(VtiProperty.FILE_SOURCECONTROLCHECKEDOUTBY, VtiType.STRING, VtiConstraint.R, VtiEncodingUtils.encode(docMetaInfo.getSourcecontrolcheckedoutby()));
            response.writeMetaDictionary(VtiProperty.FILE_SOURCECONTROLTIMECHECKEDOUT, VtiType.TIME, VtiConstraint.R, docMetaInfo.getSourcecontroltimecheckedout());
            response.writeMetaDictionary(VtiProperty.FILE_SOURCECONTROLVERSION, VtiType.STRING, VtiConstraint.R, "V" + docMetaInfo.getSourcecontrolversion());
            response.writeMetaDictionary(VtiProperty.FILE_SOURCECONTROLLOCKEXPIRES, VtiType.TIME, VtiConstraint.R, docMetaInfo.getSourcecontrollockexpires());
            response.writeMetaDictionary(VtiProperty.FILE_THICKETSUPPORTINGFILE, VtiType.BOOLEAN, VtiConstraint.R, docMetaInfo.getThicketsupportingfile());
            response.writeMetaDictionary(VtiProperty.FILE_MODIFIEDBY, VtiType.STRING, VtiConstraint.R, VtiEncodingUtils.encode(docMetaInfo.getModifiedBy()));
            response.writeMetaDictionary(VtiProperty.FILE_AUTHOR, VtiType.STRING, VtiConstraint.R, VtiEncodingUtils.encode(docMetaInfo.getAuthor()));
        }

    }

}
