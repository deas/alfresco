/*
 * Copyright (C) 2005-2013s Alfresco Software Limited.
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
package org.alfresco.share.util.api;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import org.alfresco.rest.api.tests.client.HttpResponse;
import org.alfresco.rest.api.tests.client.PublicApiClient.ListResponse;
import org.alfresco.rest.api.tests.client.PublicApiException;
import org.alfresco.rest.api.tests.client.RequestContext;
import org.alfresco.rest.api.tests.client.data.Document;
import org.alfresco.rest.api.tests.client.data.Favourite;
import org.alfresco.rest.api.tests.client.data.FavouritesTarget;
import org.alfresco.rest.api.tests.client.data.FileFavouriteTarget;
import org.alfresco.rest.api.tests.client.data.Folder;
import org.alfresco.rest.api.tests.client.data.FolderFavouriteTarget;
import org.alfresco.rest.api.tests.client.data.Site;
import org.alfresco.rest.api.tests.client.data.SiteFavouriteTarget;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * REST api for handling {@link Favourite} requests.
 * 
 * @author Abhijeet Bharade
 * 
 */
public class FavouritesAPI extends PublicAPIAbstract
{

    private static Log logger = LogFactory.getLog(FavouritesAPI.class);

    protected enum FavType
    {
        SITE, FOLDER, FILE;
    }

   /**
     * Gets a list Favorite object for a particular person Id.
     * 
     * @param authUser
     * @param domain
     * @param favMap
     * @return ListResponse<Favourite>
     * @throws PublicApiException
     */
    public HttpResponse getFavourites(String authUser, String domain, Map<String, String> favMap) throws PublicApiException
    {
        HttpResponse response = getFavourites(authUser, authUser, domain, favMap);
        logger.info("Favourites returned - " + response);
        return response;
    }
    
    public ListResponse<Favourite> getFavouritesList(String authUser, String forUser, String domain, Map<String, String> favMap) throws PublicApiException, ParseException
    {
        HttpResponse response = getFavourites(authUser, authUser, domain, favMap);
        
        ListResponse<Favourite> favourites = Favourite.parseFavourites(response.getJsonResponse());
        logger.info("Favourites returned - " + favourites);
        return favourites;
    }

    /**
     * Gets a HttpResponse for GetFavourites Request for a particular person Id.
     * 
     * @param authUser
     * @param forUser
     * @param domain
     * @param favMap
     * @return ListResponse<Favourite>
     * @throws PublicApiException
     */
    public HttpResponse getFavourites(String authUser, String forUser, String domain, Map<String, String> favMap) throws PublicApiException
    {
        publicApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        HttpResponse response = favouriteProxy.getAll("people", forUser, "favorites", null, favMap, "Failed to get favourites");

        logger.info("Favourites returned - " + response);
        return response;
    }

    /**
     * Gets the Favourite object for a particular guid.
     * 
     * @param authUser
     *            String
     * @param domain
     *            String
     * @param guid
     *            String target guid
     * @return {@link Favourite}
     * @throws PublicApiException
     * @throws ParseException
     */
    public Favourite getFavouriteForId(String authUser, String domain, String guid) throws PublicApiException, ParseException
    {
        publicApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        Favourite response = favouriteProxy.getFavourite(authUser, guid);

        logger.info("Favourites returned - " + response);
        return response;
    }

    /**
     * Create and POST a {@link Favourite} object for a {@link Site}.
     * 
     * @param authUser
     * @param domain
     * @param id
     *            - The guid in case of folder, file. Site id in case of site.
     * @param siteId
     * @return {@link Favourite}
     * @throws PublicApiException
     * @throws ParseException
     */
    public Favourite createFavourite(String authUser, String forUser, String domain, String id, FavType type) throws PublicApiException, ParseException
    {
        publicApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));
        Favourite favourite = null;

        switch (type)
        {
        case SITE:
            Site site = sitesProxy.getSite(id);
            favourite = makeSiteFavourite(site);
            break;
        case FOLDER:
            favourite = makeFolderFavourite(id);

            break;
        case FILE:
            favourite = makeFileFavourite(id);
            break;

        default:
            throw new UnsupportedOperationException("Favourite type has to be one of file, folder or site.");
        }
        Favourite response = favouriteProxy.createFavourite(forUser, favourite);

        logger.info("Favourites returned - " + response);
        return response;
    }

    /**
     * Create and POST a {@link Favourite} object for a {@link Site}.
     * 
     * @param authUser
     * @param domain
     * @param guid
     * @param siteId
     * @return {@link Favourite}
     * @throws PublicApiException
     * @throws ParseException
     */
    public Favourite createFavouriteForTarget(String authUser, String forUser, String domain, FavouritesTarget target) throws PublicApiException,
            ParseException
    {
        publicApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));
        Favourite favourite = new Favourite(target);
        Favourite response = favouriteProxy.createFavourite(authUser, favourite);

        logger.info("Favourites returned - " + response);
        return response;
    }

    /**
     * Removed favourite for a guid.
     * 
     * @param authUser
     * @param forUser
     * @param domain
     * @param guid
     * @return {@link HttpResponse}
     * @throws PublicApiException
     */
    public HttpResponse removeFavouriteForGuid(String authUser, String forUser, String domain, String guid) throws PublicApiException
    {
        publicApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        HttpResponse response = favouriteProxy.remove("people", forUser, "favorites", guid, "Failed to remove favourite");

        logger.info("Favourites removed for - " + guid);
        return response;
    }

    protected Favourite makeSiteFavourite(Site site) throws ParseException
    {
        SiteFavouriteTarget target = new SiteFavouriteTarget(site);
        Date creationDate = new Date();
        Favourite favourite = new Favourite(creationDate, null, target);
        return favourite;
    }

    protected Favourite makeFolderFavourite(String targetGuid) throws ParseException
    {
        Folder folder = new Folder(targetGuid);
        FolderFavouriteTarget target = new FolderFavouriteTarget(folder);
        Date creationData = new Date();
        Favourite favourite = new Favourite(creationData, null, target);
        return favourite;
    }

    protected Favourite makeFileFavourite(String targetGuid) throws ParseException
    {
        Document document = new Document(targetGuid);
        FileFavouriteTarget target = new FileFavouriteTarget(document);
        Date creationData = new Date();
        Favourite favourite = new Favourite(creationData, null, target);
        return favourite;
    }
}
