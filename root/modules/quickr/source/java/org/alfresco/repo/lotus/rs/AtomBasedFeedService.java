/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.repo.lotus.rs;

import javax.jws.WebService;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Service;

/**
 * @author PavelYur
 */
@Path("/")
@WebService
@Produces("application/atom+xml")
public interface AtomBasedFeedService
{
    @POST
    @Path("/library/{id}/feed")
    @Produces("application/atom+xml")
    public Response createDocumentInLibrary(@PathParam("id")
    String id, @Context
    HttpHeaders headres, byte[] body, @QueryParam("submit")
    @DefaultValue("false")
    boolean submit, @QueryParam("replace")
    @DefaultValue("false")
    boolean replace);

    @GET
    @Path("/libraries/feed")
    @Produces("application/atom+xml")
    public Feed retrieveListOfLibraries();

    @HEAD
    @Path("/libraries/feed")
    @Produces("application/atom+xml")
    public Response initialRetrieveListOfLibraries();

    @GET
    @Path("/library/{id}/feed")
    @Produces("application/atom+xml")
    public Feed retrieveListOfContentInLibrary(@PathParam("id")
    String id);

    @GET
    @Path("/library/{libraryId}/folder/{folderId}/feed")
    @Produces("application/atom+xml")
    public Feed retrieveListOfContentInFolder(@PathParam("libraryId")
    String libraryId, @PathParam("folderId")
    String folderId);

    @POST
    @Path("/library/{libraryId}/folder/{folderId}/feed")
    @Produces("application/atom+xml")
    public Response createDocumentInFolder(@PathParam("libraryId")
    String libraryId, @PathParam("folderId")
    String folderId, @Context
    HttpHeaders headres, byte[] body, @QueryParam("submit")
    @DefaultValue("false")
    boolean submit, @QueryParam("replace")
    @DefaultValue("false")
    boolean replace);

    @GET
    @Path("/library/{libraryId}/document/{documentId}/entry")
    @Produces("application/atom+xml")
    public Entry retrieveDocument(@PathParam("libraryId")
    String libraryId, @PathParam("documentId")
    String documentId);

    @POST
    @Path("/library/{libraryId}/document/{documentId}/entry")
    @Produces("application/atom+xml")
    public Response deleteDocument(@PathParam("libraryId")
    String libraryId, @PathParam("documentId")
    String documentId, @Context
    HttpHeaders headres);

    @GET
    @Path("/library/{libraryId}/folder/{folderId}/entry")
    @Produces("application/atom+xml")
    public Entry retrieveFolder(@PathParam("libraryId")
    String libraryId, @PathParam("folderId")
    String folderId);

    @POST
    @Path("/library/{libraryId}/folder/{folderId}/entry")
    @Produces("application/atom+xml")
    public Response deleteFolder(@PathParam("libraryId")
    String libraryId, @PathParam("folderId")
    String folderId, @Context
    HttpHeaders headres);

    @GET
    @Path("/introspection")
    @Produces("application/atom+xml")
    public Service getLibraries();

    @GET
    @Path("/library/{libraryId}/document/{documentId}/media")
    public Response downloadDocument(@PathParam("libraryId")
    String libraryId, @PathParam("documentId")
    String documentId, @Context
    HttpHeaders headers, @QueryParam("lock")
    @DefaultValue("false")
    boolean lock);

    @GET
    @Path("/library/{libraryId}/draft/{draftId}/media")
    public Response downloadDraft(@PathParam("libraryId")
    String libraryId, @PathParam("draftId")
    String draftId, @Context
    HttpHeaders headers);


    @GET
    @Path("/library/{libraryId}/document/{documentId}/version/{versionId}/media")
    public Response downloadDocumentVersion(@PathParam("libraryId")
    String libraryId, @PathParam("documentId")
    String versionId, @PathParam("versionId")
    String documentId, @Context
    HttpHeaders headers);
}
