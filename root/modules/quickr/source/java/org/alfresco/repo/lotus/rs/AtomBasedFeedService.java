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

package org.alfresco.repo.lotus.rs;

import java.net.URISyntaxException;

import javax.jws.WebService;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.alfresco.service.cmr.model.FileNotFoundException;
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
    @Produces( { "application/atom+xml", "text/html" })
    public Response createItemInLibrary(@PathParam("id")
    String id, @Context
    HttpHeaders headres, byte[] body, @QueryParam("submit")
    @DefaultValue("false")
    boolean submit, @QueryParam("draft")
    @DefaultValue("false")
    boolean isDraft, @QueryParam("replace")
    @DefaultValue("false")
    boolean replace, @QueryParam("acls")
    @DefaultValue("false")
    boolean retrievePermissions, @QueryParam("opId")
    @DefaultValue("")
    String opId, @QueryParam("doctype")
    @DefaultValue("Default")
    String docTypeTitle, @QueryParam("format")
    @DefaultValue("")
    String format) throws URISyntaxException;

    @GET
    @Path("/library/{id}/entry")
    @Produces("application/atom+xml")
    public Response retrieveLibrary(@PathParam("id")
    String id);

    @GET
    @Path("/libraries/feed")
    @Produces("application/atom+xml")
    public Response retrieveListOfLibraries();

    @HEAD
    @Path("/libraries/feed")
    @Produces("application/atom+xml")
    public Response initialRetrieveListOfLibraries();

    @GET
    @Path("/library/{id}/feed")
    @Produces("application/atom+xml")
    public Response retrieveListOfContentInLibrary(@PathParam("id")
    String id, @QueryParam("acls")
    @DefaultValue("false")
    boolean retrievePermissions, @QueryParam("category")
    @DefaultValue("document,folder")
    String category, @QueryParam("sO")
    @DefaultValue("asc")
    String sortOrder, @QueryParam("sK")
    @DefaultValue("label")
    String sortColumn, @QueryParam("page")
    @DefaultValue("1")
    int startPage, @QueryParam("pagesize")
    @DefaultValue("10")
    int pageSize, @QueryParam("hpaa.slotid")
    @DefaultValue("")
    String slotid, @QueryParam("hpaa.piid")
    @DefaultValue("")
    String piid);

    @GET
    @Path("/library/{libraryId}/folder/{folderId}/feed")
    @Produces("application/atom+xml")
    public Response retrieveListOfContentInFolder(@PathParam("libraryId")
    String libraryId, @PathParam("folderId")
    String folderId, @QueryParam("acls")
    @DefaultValue("false")
    boolean retrievePermissions, @HeaderParam("Cookie")
    String cookieHeader, @QueryParam("category")
    @DefaultValue("document,folder")
    String category, @QueryParam("sO")
    @DefaultValue("asc")
    String sortOrder, @QueryParam("sK")
    @DefaultValue("label")
    String sortColumn, @QueryParam("page")
    @DefaultValue("1")
    int startPage, @QueryParam("pagesize")
    @DefaultValue("10")
    int pageSize, @QueryParam("hpaa.slotid")
    @DefaultValue("")
    String slotid, @QueryParam("hpaa.piid")
    @DefaultValue("")
    String piid);

    @POST
    @Path("/library/{libraryId}/folder/{folderId}/feed")
    @Produces( { "application/atom+xml", "text/html" })
    public Response createItemInFolder(@PathParam("libraryId")
    String libraryId, @PathParam("folderId")
    String folderId, @Context
    HttpHeaders headres, byte[] body, @QueryParam("submit")
    @DefaultValue("false")
    boolean submit, @QueryParam("draft")
    @DefaultValue("false")
    boolean isDraft, @QueryParam("replace")
    @DefaultValue("false")
    boolean replace, @QueryParam("acls")
    @DefaultValue("false")
    boolean retrievePermissions, @QueryParam("opId")
    @DefaultValue("")
    String opId, @QueryParam("doctype")
    @DefaultValue("Default")
    String docTypeTitle, @QueryParam("format")
    @DefaultValue("")
    String format) throws URISyntaxException;

    @GET
    @Path("/library/{libraryId}/document/{documentId}/entry")
    @Produces("application/atom+xml")
    public Response retrieveDocument(@PathParam("libraryId")
    String libraryId, @PathParam("documentId")
    String documentId, @QueryParam("acls")
    @DefaultValue("false")
    boolean retrievePermissions, @QueryParam("includePropertySheets")
    @DefaultValue("false")
    boolean retrievePropSheets);

    @GET
    @Path("/library/{libraryId}/document/{documentId}/feed")
    @Produces("application/atom+xml")
    public Response retrieveDocumentResources(@PathParam("libraryId")
    String libraryId, @PathParam("documentId")
    String documentId, @QueryParam("acls")
    @DefaultValue("false")
    boolean retrievePermissions, @QueryParam("category")
    String category) throws FileNotFoundException;

    @GET
    @Path("/library/{libraryId}/documenttypes/{documentTypeTitle}/entry")
    @Produces("application/atom+xml")
    public Response retrieveDocumentType(@PathParam("libraryId")
    String libraryId, @PathParam("documentTypeTitle")
    String documentTypeTitle, @QueryParam("includePropertySheetTypes")
    @DefaultValue("false")
    boolean retrievePropSheets);

    @GET
    @Path("/library/{libraryId}/documenttypes/feed")
    @Produces("application/atom+xml")
    public Response retrieveDocumentTypes(@PathParam("libraryId")
    String libraryId);

    @POST
    @Path("/library/{libraryId}/document/{documentId}/entry")
    @Produces("application/atom+xml")
    public Response updateDocument(@PathParam("libraryId")
    String libraryId, @PathParam("documentId")
    String documentId, @Context
    HttpHeaders headres, byte[] body, @QueryParam("doctype")
    @DefaultValue("")
    String docTypeTitle, @QueryParam("opId")
    @DefaultValue("")
    String opId, @QueryParam("format")
    @DefaultValue("")
    String format, @QueryParam("createVersion")
    @DefaultValue("false")
    String createVersion) throws URISyntaxException;
    
    @POST
    @Path("/library/{libraryId}/draft/{draft}/media")
    @Produces("application/atom+xml")
    public Response updateDraft(@PathParam("libraryId")
    String libraryId, @PathParam("draft")
    String draftId, @Context
    HttpHeaders headres, byte[] body) throws URISyntaxException;

    @POST
    @Path("/library/{libraryId}/view/CheckedOut/feed")
    @Produces("application/atom+xml")
    public Response checkOutDocument(@PathParam("libraryId")
    String libraryId, byte[] body);

    @POST
    @Path("/library/{libraryId}/document/{documentId}/feed")
    @Produces("application/atom+xml")
    public Response createDocResource(@PathParam("libraryId")
    String libraryId, @PathParam("documentId")
    String documentId, byte[] body) throws FileNotFoundException;

    @POST
    @Path("/library/{libraryId}/checkedout/{documentId}/entry")
    @Produces("text/html")
    public Response cancelCheckOut(@PathParam("libraryId")
    String libraryId, @PathParam("documentId")
    String documentId, @Context
    HttpHeaders headres);

    @GET
    @Path("/library/{libraryId}/views/feed")
    @Produces("application/atom+xml")
    public Response retrieveViews(@PathParam("libraryId")
    String libraryId);

    @GET
    @Path("/library/{libraryId}/view/{viewId}/feed")
    @Produces("application/atom+xml")
    public Response applyView(@PathParam("libraryId")
    String libraryId, @PathParam("viewId")
    String viewId, @QueryParam("includePropertySheetType")
    @DefaultValue("false")
    boolean retrievePropSheets, @QueryParam("category")
    @DefaultValue("document")
    String category,@QueryParam("sO")
    @DefaultValue("asc")
    String sortOrder, @QueryParam("sK")
    @DefaultValue("label")
    String sortColumn, @QueryParam("page")
    @DefaultValue("1")
    int startPage, @QueryParam("pagesize")
    @DefaultValue("10")
    int pageSize);

    @GET
    @Path("/library/{libraryId}/folder/{folderId}/entry")
    @Produces("application/atom+xml")
    public Response retrieveFolder(@PathParam("libraryId")
    String libraryId, @PathParam("folderId")
    String folderId, @QueryParam("acls")
    @DefaultValue("false")
    boolean retrievePermissions);

    @POST
    @Path("/library/{libraryId}/folder/{folderId}/entry")
    @Produces("application/atom+xml")
    public Response updateFolder(@PathParam("libraryId")
    String libraryId, @PathParam("folderId")
    String folderId, @Context
    HttpHeaders headres, byte[] body);

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
