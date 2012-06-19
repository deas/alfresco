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

import java.util.Date;

import org.alfresco.module.vti.handler.MeetingServiceHandler;
import org.alfresco.module.vti.metadata.model.MeetingBean;

/**
 * Class for handling AddMeeting soap method
 * 
 * @author Nick Burch
 */
public class AddMeetingEndpoint extends AbstractMeetingEndpoint
{
    public AddMeetingEndpoint(MeetingServiceHandler handler)
    {
        super(handler);
    }
    
    @Override
    protected void executeMeetingAction(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse, String siteName,
            String uid, String organizerEmail, int sequence, String title, String location, Date dateStart,
            Date dateEnd, int recurrenceId, boolean cancelMeeting) throws Exception
    {
        // Turn the details into a MeetingBean
        MeetingBean meetingBean = new MeetingBean();
        // TODO
        
        // Perform the deletion
        handler.addMeeting(siteName, meetingBean);
        
        // Build the response
        buildMeetingResponse(soapResponse);
    }
}