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

package org.alfresco.repo.lotus.ws.impl.helper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.alfresco.model.ContentModel;
import org.alfresco.model.QuickrModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.lotus.ws.ClbVersioning;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.lock.LockStatus;
import org.alfresco.service.cmr.lock.LockType;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Eugene Zheleznyakov
 */
public class AlfrescoQuickrDocumentHelper
{
    private LockService lockService;

    private NodeService nodeService;

    private CheckOutCheckInService checkOutCheckInService;

    private AlfrescoQuickrPathHelper pathHelper;

    private DictionaryService dictionaryService;

    private MimetypeService mimetypeService;

    public void setLockService(LockService lockService)
    {
        this.lockService = lockService;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setCheckOutCheckInService(CheckOutCheckInService checkOutCheckInService)
    {
        this.checkOutCheckInService = checkOutCheckInService;
    }

    public void setPathHelper(AlfrescoQuickrPathHelper pathHelper)
    {
        this.pathHelper = pathHelper;
    }

    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    public void setMimetypeService(MimetypeService mimetypeService)
    {
        this.mimetypeService = mimetypeService;
    }

    /**
     * Returns document status for node reference
     * 
     * @param nodeRef node reference ({@link NodeRef})
     * @return DocumentStatus document status
     */
    public DocumentStatus getDocumentStatus(NodeRef nodeRef)
    {
        DocumentStatus status = DocumentStatus.NORMAL;

        LockStatus lockStatus = lockService.getLockStatus(nodeRef);

        if (lockStatus.equals(LockStatus.LOCKED) || lockStatus.equals(LockStatus.LOCK_OWNER))
        {
            if (LockType.valueOf((String) nodeService.getProperty(nodeRef, ContentModel.PROP_LOCK_TYPE)).equals(LockType.WRITE_LOCK))
            {
                // short-term checkout
                if (lockStatus.equals(LockStatus.LOCKED))
                {
                    status = DocumentStatus.SHORT_CHECKOUT;
                }
                else
                {
                    status = DocumentStatus.SHORT_CHECKOUT_OWNER;
                }
            }
            else
            {
                NodeRef workingCopyNodeRef = checkOutCheckInService.getWorkingCopy(nodeRef);

                // checks for long-term checkout
                if (workingCopyNodeRef != null)
                {
                    // long-term checkout
                    String ownerUsername = (String) nodeService.getProperty(workingCopyNodeRef, ContentModel.PROP_WORKING_COPY_OWNER);
                    if (ownerUsername.equals(AuthenticationUtil.getFullyAuthenticatedUser()))
                    {
                        status = DocumentStatus.LONG_CHECKOUT_OWNER;
                    }
                    else
                    {
                        status = DocumentStatus.LONG_CHECKOUT;
                    }
                }
                else
                {
                    // just readonly document
                    if (lockStatus.equals(LockStatus.LOCKED))
                    {
                        status = DocumentStatus.READONLY;
                    }
                    else
                    {
                        // There is no working copy yet.
                        status = DocumentStatus.LONG_CHECKOUT_OWNER;
                    }
                }
            }
        }

        return status;
    }

    /**
     * @param id id of node.
     * @param path path to the node, or document type title
     * @return AspectDefinition for corresponding document type.
     */
    public AspectDefinition getDocumentTypeAspect(String id, String path)
    {
        AspectDefinition docType = null;
        try
        {

            NodeRef nodeRef = pathHelper.resolveNodeRef(id, path);

            Set<QName> aspects = nodeService.getAspects(nodeRef);

            for (QName qname : aspects)
            {
                if (dictionaryService.isSubClass(qname, QuickrModel.ASPECT_QUICKR_DOC_TYPE))
                {
                    docType = dictionaryService.getAspect(qname);
                    break;
                }
            }
        }
        catch (FileNotFoundException e)
        {
            QName posibleAspect = null;
            if (path != null)
            {
                posibleAspect = searchAspect(QuickrModel.ASPECT_QUICKR_DOC_TYPE, path.substring(path.indexOf("/") + 1));
            }
            else if (id != null)
            {
                posibleAspect = searchAspect(QuickrModel.ASPECT_QUICKR_DOC_TYPE, id);
            }

            if (posibleAspect != null)
            {
                docType = dictionaryService.getAspect(posibleAspect);
            }

        }

        if (docType == null)
        {
            // return default document type
            docType = dictionaryService.getAspect(QuickrModel.ASPECT_QUICKR_DOC_TYPE);
        }

        return docType;

    }

    public ClbVersioning getVersionMode(NodeRef nodeRef)
    {
        AspectDefinition docType = getDocumentTypeAspect(nodeRef.getId(), null);
        return getVersionMode(docType);
    }

    public ClbVersioning getVersionMode(AspectDefinition docType)
    {
        for (AspectDefinition aspectDefinition : docType.getDefaultAspects(false))
        {
            if (dictionaryService.isSubClass(aspectDefinition.getName(), QuickrModel.ASPECT_QUICKR_VERSION_PROP))
            {
                return ClbVersioning.fromValue(aspectDefinition.getTitle());
            }
        }

        return ClbVersioning.NONE;
    }

    public String lowerCaseFirstCharacter(String value)
    {
        String firstChar = value.substring(0, 1).toLowerCase();
        return (firstChar + value.substring(1));
    }

    /**
     * Remove specified document type and all it's property sheets from provided node.
     * 
     * @param documentNodeRef documentNodeRef
     * @param docTypeQName docTypeQName
     */
    public void removeDocType(NodeRef documentNodeRef, QName docTypeQName)
    {
        AspectDefinition docType = dictionaryService.getAspect(docTypeQName);
        nodeService.removeAspect(documentNodeRef, docType.getName());
        for (QName propSheet : docType.getDefaultAspectNames())
        {
            nodeService.removeAspect(documentNodeRef, propSheet);
        }

    }

    /**
     * @param parentAspect parent for aspect that we search
     * @param aspectTitle title of aspect that we search
     * @return Aspect with specified parent and title or null.
     */
    public QName searchAspect(QName parentAspect, String aspectTitle)
    {
        try
        {
            aspectTitle = URLDecoder.decode(aspectTitle, "UTF8");
        }
        catch (UnsupportedEncodingException e)
        {
            // Do nothing
        }

        AspectDefinition aspect = dictionaryService.getAspect(parentAspect);
        if (aspect.getTitle().equals(aspectTitle))
        {
            return aspect.getName();
        }
        Collection<QName> childs = dictionaryService.getSubAspects(parentAspect, true);
        for (QName child : childs)
        {
            AspectDefinition childAspect = dictionaryService.getAspect(child);
            if (childAspect.getTitle().equals(aspectTitle))
            {
                return child;
            }
        }

        return null;
    }

    /**
     * @param aspectQName QName of aspect
     * @param propTitle title of property from aspectQName
     * @return property Qname with specified title or null.
     */
    public QName getPropertyQName(QName aspectQName, String propTitle)
    {
        try
        {
            propTitle = URLDecoder.decode(propTitle, "UTF8");
        }
        catch (UnsupportedEncodingException e)
        {
            // Do nothing
        }
        AspectDefinition aspect = dictionaryService.getAspect(aspectQName);
        if (aspect != null)
        {
            Map<QName, PropertyDefinition> props = aspect.getProperties();
            for (PropertyDefinition prop : props.values())
            {
                if (prop.getTitle().equals(propTitle))
                {
                    return prop.getName();
                }
            }
        }

        return null;
    }

    /**
     * Check document on checkout
     * 
     * @param documentStatus status of document ({@link DocumentStatus})
     * @return <i>true</i>, if document is checkout; otherwise, <i>false</i>
     */
    public static boolean isCheckedout(DocumentStatus documentStatus)
    {
        return documentStatus.equals(DocumentStatus.NORMAL) == false && documentStatus.equals(DocumentStatus.READONLY) == false;
    }

    /**
     * Check document on owner checkout
     * 
     * @param documentStatus status of document ({@link DocumentStatus})
     * @return <i>true</i>, if document is owner checkout; otherwise, <i>false</i>
     */
    public static boolean isCheckoutOwner(DocumentStatus documentStatus)
    {
        return documentStatus.equals(DocumentStatus.LONG_CHECKOUT_OWNER) || documentStatus.equals(DocumentStatus.SHORT_CHECKOUT_OWNER);
    }

    /**
     * Check document on long term checkout
     * 
     * @param documentStatus status of document ({@link DocumentStatus})
     * @return <i>true</i>, if document is long term checkout; otherwise, <i>false</i>
     */
    public static boolean isLongCheckedout(DocumentStatus documentStatus)
    {
        return documentStatus.equals(DocumentStatus.LONG_CHECKOUT) || documentStatus.equals(DocumentStatus.LONG_CHECKOUT_OWNER);
    }

    public Document getXMLDocument(byte[] body)
    {
        ByteArrayInputStream xmlStream = new ByteArrayInputStream(body);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        Document doc = null;
        try
        {
            db = dbf.newDocumentBuilder();
            doc = db.parse(xmlStream);
        }
        catch (ParserConfigurationException e)
        {
            e.printStackTrace();
        }
        catch (SAXException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return doc;
    }

    public String getNodeValue(Document doc, String field)
    {
        NodeList fields = doc.getElementsByTagName(field);
        if (fields.getLength() > 0)
        {
            Node item = doc.getElementsByTagName(field).item(0);
            if (item != null)
            {
                Node valueItem = item.getChildNodes().item(0);
                if (valueItem != null)
                {
                    return valueItem.getNodeValue();
                }

            }
        }

        return "";
    }

    public String getNodeParam(Document doc, String field, String paramId)
    {
        NodeList fields = doc.getElementsByTagName(field);
        if (fields.getLength() > 0)
        {
            Node item = doc.getElementsByTagName(field).item(0);
            if (item != null)
            {
                Node param = item.getAttributes().getNamedItem(paramId);
                if (param != null)
                {
                    return param.getNodeValue();
                }

            }
        }

        return "";
    }

    /**
     * Return mimetype using file name
     * 
     * @param fileRef nodeRef 
     * @return mimetype 
     */
    public String getMimeType(NodeRef fileRef)
    {
        String filename = (String) nodeService.getProperty(fileRef, ContentModel.PROP_NAME);
        String mimetype = MimetypeMap.MIMETYPE_BINARY;
        int extIndex = filename.lastIndexOf('.');
        if (extIndex != -1)
        {
           String ext = filename.substring(extIndex + 1);
           mimetype = mimetypeService.getMimetype(ext);
        }
        
        return mimetype;
    }

}
