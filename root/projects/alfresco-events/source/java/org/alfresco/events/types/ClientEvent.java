/*
 * Copyright 2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.events.types;

import org.alfresco.util.FileFilterMode.Client;

/**
 * An event that is generated and we are able to get information
 * about the Alfresco client, eg. webdav, ftp, cmis
 * 
 * @author Gethin James
 */
public interface ClientEvent extends Event
{
    public Client getClient();
}
