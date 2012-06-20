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

package org.alfresco.module.vti.web.ws;

import org.alfresco.module.vti.handler.MeetingServiceHandler;
import org.alfresco.module.vti.handler.ObjectNotFoundException;
import org.alfresco.module.vti.metadata.model.MeetingBean;
import org.alfresco.repo.site.SiteDoesNotExistException;

/**
 * Class for handling RestoreMeeting soap method
 * 
 * @author PavelYur
 */
public class RestoreMeetingEndpoint extends AbstractMeetingEndpoint
{
    public RestoreMeetingEndpoint(MeetingServiceHandler handler)
    {
        super(handler);
    }

    @Override
    protected void executeMeetingAction(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse, String siteName,
            MeetingBean meetingBean, int sequence, int recurrenceId, boolean ignoreAttendees, boolean cancelMeeting) throws Exception
    {
        // Perform the restoration, if possible
        String uid = meetingBean.getId();
        try
        {
            handler.restoreMeeting(siteName, uid);
        }
        catch (SiteDoesNotExistException sne)
        {
            throw new VtiSoapException("Site '" + siteName + "' not found", 0x8102003el);
        }
        catch (ObjectNotFoundException onfe)
        {
            throw new VtiSoapException("Meeting with UID '" + uid + "' not found", 0x8102003el);
        }
        
        // Build the response
        buildMeetingResponse(soapResponse);
    }
}
