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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.alfresco.module.vti.handler.Error;
import org.alfresco.module.vti.metadata.dic.Permission;
import org.alfresco.module.vti.metadata.model.AssigneeBean;
import org.alfresco.module.vti.metadata.model.DocumentBean;
import org.alfresco.module.vti.metadata.model.DwsBean;
import org.alfresco.module.vti.metadata.model.DwsData;
import org.alfresco.module.vti.metadata.model.DwsMetadata;
import org.alfresco.module.vti.metadata.model.LinkBean;
import org.alfresco.module.vti.metadata.model.ListInfoBean;
import org.alfresco.module.vti.metadata.model.MemberBean;
import org.alfresco.module.vti.metadata.model.SchemaBean;
import org.alfresco.module.vti.metadata.model.SchemaFieldBean;
import org.alfresco.module.vti.metadata.model.TaskBean;
import org.alfresco.module.vti.metadata.model.UserBean;
import org.alfresco.module.vti.web.VtiRequestDispatcher;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jaxen.XPath;

/**
 * Abstract base class for all the {@link VtiEndpoint} realizations.
 * 
 * @author Stas Sokolovsky
 *
 */
public abstract class AbstractEndpoint implements VtiEndpoint
{
   private static Log logger = LogFactory.getLog(AbstractEndpoint.class);

    public static final String DWS = "VTI_DWS";
    private static final StringBuilder LBRACKET = new StringBuilder("<");
    private static final StringBuilder RBRACKET = new StringBuilder(">");
    private static final StringBuilder LCLOSEBRACKET = new StringBuilder("</");
    private static final StringBuilder RCLOSEBRACKET = new StringBuilder("/>");

    protected static String soapPart = "/s:Envelope/s:Body";
    protected static String soapUriPrefix = "s";
    protected static String soapUri = "http://schemas.xmlsoap.org/soap/envelope/";

    protected String name;
    protected String namespace;

    /**
     * Endpoint name setter
     * 
     * @param name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Endpoint namespace setter
     * 
     * @param namespace
     */
    public void setNamespace(String namespace)
    {
        this.namespace = namespace;
    }

    /**
     * @see org.alfresco.module.vti.web.ws.VtiEndpoint#getName()
     */
    public String getName()
    {
        return name;
    }

    /**
     * @see org.alfresco.module.vti.web.ws.VtiEndpoint#getNamespace()
     */
    public String getNamespace()
    {
        return namespace;
    }
    
    /**
     * @return the response tag name
     */
    public static String getResponseTagName(String name)
    {
        return name + "Response";
    }
    
    /**
     * @return the result tag name
     */
    public static String getResultTagName(String name)
    {
        return name + "Result";
    }

    /**
     * Build XPath
     * 
     * @param prefix prefix      
     * @param searchPath source path
     */    
    public static String buildXPath(String prefix, String searchPath)
    {
        return soapPart + searchPath.replaceAll("/", "/" + prefix + ":");
    }
    
    /**
     * Return current context
     * 
     * @param request Vti Soap Request ({@link VtiSoapRequest})      
     */ 
    public static String getContext(VtiSoapRequest request)
    {
        Object alfrescoContext = request.getAttribute(VtiRequestDispatcher.VTI_ALFRESCO_CONTEXT);

        if (alfrescoContext != null)
        {
            return alfrescoContext.toString();
        }
        else
        {
            return "";
        }
    }
    
    /**
     * Return current host
     * 
     * @param request Vti Soap Request ({@link VtiSoapRequest})      
     */ 
    public static String getHost(VtiSoapRequest request)
    {
        return request.getScheme() + "://" + request.getHeader("Host");
    }

    /**
     * Get current DWS name from requested URI 
     * 
     * @param request Vti Soap Request ({@link VtiSoapRequest})      
     */
    public static String getDwsFromUri(VtiSoapRequest request)
    {
        String uri = request.getRequestURI();
        if (uri.startsWith(request.getAlfrescoContextName() + "/_vti_bin"))
            return "";
        
        String dws = uri;
        String contextName = request.getAlfrescoContextName();
        if (uri.startsWith(contextName)) 
        {
        	int contextLength = contextName.length();
        	dws = uri.substring(contextLength, uri.indexOf("/_vti_bin"));
        }
        
        try
        {
            dws = URLDecoder.decode(dws, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            // ignore
        }
        return dws;
    }
    
    /**
     * Get the (relative) file name that was requested.
     *  
     */
    public static String getFileName(VtiSoapRequest soapRequest, XPath fileNamePath) throws Exception
    {
       Element fileNameE = (Element) fileNamePath.selectSingleNode(soapRequest.getDocument().getRootElement());
       String fileName = fileNameE.getText();
       
       // Is it relative or absolute?
       String host = getHost(soapRequest);
       if(fileName.startsWith(host))
       {
          String context = soapRequest.getAlfrescoContextName();
          String dws = getDwsFromUri(soapRequest);
          
          String splitWith = context + dws;
          int splitAt = fileName.indexOf(splitWith);
          
          if(splitAt == -1)
          {
             logger.warn("Unable to find " + splitWith + " in absolute path " + fileName);
          }
          else
          {
             fileName = fileName.substring(splitAt + splitWith.length() + 1);
          }
       }
       
       // All done
       return fileName;
    }
    
    /**
     * Get current DWS name for DWS creation from requested URI 
     * 
     * @param request Vti Soap Request ({@link VtiSoapRequest})      
     */
    public static String getDwsForCreationFromUri(VtiSoapRequest request)
    {
        return "";
    }
    
    /**
     * Create xml tag presentation  
     * 
     * @param tagName name of tag      
     */
    protected StringBuilder startTag(String tagName)
    {
        StringBuilder result = new StringBuilder("");
        return result.append(LBRACKET).append(tagName).append(RBRACKET);
    }

    /**
     * Create xml tag presentation with attributes
     * 
     * @param tagName name of tag
     * @param attributes map or the attributes for the tag     
     */
    protected StringBuilder startTag(String tagName, Map<String, Object> attributes)
    {
        StringBuilder result = new StringBuilder("");
        result.append(LBRACKET).append(tagName).append(" ");
        for (String key : attributes.keySet())
        {
            if (attributes.get(key) != null)
            {
                if (!attributes.get(key).equals(""))
                {
                    result.append(key).append("=\"").append(attributes.get(key)).append("\" ");
                }
            }
        }
        result.append(RBRACKET);
        return result;
    }

    /**
     * Creates xml closing tag presentation
     * 
     * @param tagName name of the closing tag    
     */
    protected StringBuilder endTag(String tagName)
    {
        StringBuilder result = new StringBuilder("");
        return result.append(LCLOSEBRACKET).append(tagName).append(RBRACKET);
    }

    /**
     * Creates xml tag presentation without body
     * 
     * @param tagName name of tag
     */
    protected StringBuilder singleTag(String tagName)
    {
        StringBuilder result = new StringBuilder("");
        return result.append(LBRACKET).append(tagName).append(RCLOSEBRACKET);
    }

    /**
     * Creates xml tag with attributes presentation without body
     * 
     * @param tagName name of tag
     * @param attributes map of tag attributes
     */
    protected StringBuilder singleTag(String tagName, Map<String, Object> attributes)
    {
        StringBuilder result = new StringBuilder("");
        result.append(LBRACKET).append(tagName).append(" ");
        for (String key : attributes.keySet())
        {
            if (attributes.get(key) != null)
            {
                if (!attributes.get(key).equals(""))
                {
                    result.append(key).append("=\"").append(attributes.get(key)).append("\" ");
                }
            }
        }
        result.append(RCLOSEBRACKET);
        return result;
    }

    /**
     * Creates xml tag presentation with body that contain <code>value</code> parameter
     * 
     * @param tagName name of tag
     * @param value that will be placed to the body of the tag
     */
    protected StringBuilder processTag(String tagName, Object value)
    {
        StringBuilder result = new StringBuilder("");

        if (value == null)
        {
            return result;
        }
        else if (value.toString().equals(""))
        {
            return result.append(singleTag(tagName));
        }
        else
        {
            return result.append(startTag(tagName)).append(value).append(endTag(tagName));
        }
    }

    /**
     * Convert AssigneeBean type to the correct xml presentation
     * 
     * @param assigneeBean object to convert
     * @return xml presentation
     */
    public String generateXml(AssigneeBean assigneeBean)
    {
        StringBuilder result = new StringBuilder("");
        result.append(startTag("Member")).append(processTag("ID", assigneeBean.getId())).append(processTag("Name", assigneeBean.getName())).append(
                processTag("LoginName", assigneeBean.getLoginName())).append(endTag("Member"));
        return result.toString();
    }

    /**
     * Convert DocumentBean type to the correct xml presentation
     * 
     * @param documentBean object to convert
     * @return xml presentation
     */
    public String generateXml(DocumentBean documentBean)
    {
        StringBuilder result = new StringBuilder("");
        Map<String, Object> attributes = new LinkedHashMap<String, Object>();

        attributes.put("ows_FileRef", documentBean.getFileRef());
        attributes.put("ows_FSObjType", documentBean.getObjType());
        attributes.put("ows_Created", documentBean.getCreated());
        attributes.put("ows_Author", documentBean.getAuthor());
        attributes.put("ows_Modified", documentBean.getModified());
        attributes.put("ows_Editor", documentBean.getEditor());
        attributes.put("ows_ID", documentBean.getId());
        attributes.put("ows_ProgID", documentBean.getProgID());
        attributes.put("xmlns:z", "#RowsetSchema");
        result.append(singleTag("z:row", attributes));
        return result.toString();
    }

    /**
     * Convert DwsBean type to the correct xml presentation
     * 
     * @param dwsBean object to convert
     * @return xml presentation
     */
    public String generateXml(DwsBean dwsBean)
    {
        StringBuilder result = new StringBuilder("");
        result.append(startTag("Results")).append(processTag("Url", dwsBean.getUrl())).append(processTag("DoclibUrl", dwsBean.getDoclibUrl())).append(
                processTag("ParentWeb", dwsBean.getParentWeb())).append(startTag("FailedUsers"));
        if (dwsBean.getFailedUsers() != null)
        {
            for (String user : dwsBean.getFailedUsers())
            {
                Map<String, Object> attributes = new LinkedHashMap<String, Object>();

                attributes.put("Email", user);
                result.append(singleTag("User", attributes));
            }
        }
        result.append(endTag("FailedUsers"));
        result.append(endTag("Results"));
        return result.toString();
    }

    /**
     * Convert DwsData type to the correct xml presentation
     * 
     * @param dwsData object to convert
     * @return xml presentation
     */
    public String generateXml(DwsData dwsData)
    {
        StringBuilder result = new StringBuilder("");
        result.append(startTag("Results")).append(processTag("Title", dwsData.getTitle())).append(processTag("LastUpdate", dwsData.getLastUpdate())).append(
                generateXml(dwsData.getUser()));

        result.append(startTag("Members"));
        if (dwsData.getMembers() != null)
        {
            for (MemberBean member : dwsData.getMembers())
            {
                result.append(generateXml(member));
            }
        }
        result.append(endTag("Members"));

        if (!dwsData.isMinimal())
        {
            result.append(startTag("Assignees"));
            if (dwsData.getAssignees() != null)
            {

                for (AssigneeBean assignee : dwsData.getAssignees())
                {
                    result.append(generateXml(assignee));
                }
            }
            result.append(endTag("Assignees"));

            Map<String, Object> docAttr = new HashMap<String, Object>();
            docAttr.put("Name", "Documents");
            result.append(startTag("List", docAttr));
            
            if (dwsData.getDocumentsList() != null)
            {
                if (dwsData.getDocumentsList().size() > 99)
                {
                    docAttr.clear();
                    docAttr.put("DefaultUrl", dwsData.getDocLibUrl());
                    result.append(startTag("ID", docAttr));
                    result.append(endTag("ID"));
                    docAttr.clear();
                    docAttr.put("ID", "8");
                    result.append(startTag("Error", docAttr));
                    result.append(endTag("Error"));
                }
                else
                {
                    result.append(processTag("ID", ""));
                    for (DocumentBean document : dwsData.getDocumentsList())
                    {
                        result.append(generateXml(document));
                    }
                }
            }
            result.append(endTag("List"));
            
            if (dwsData.getLinksList() != null)
            {
                docAttr.clear();
                docAttr.put("Name", "Links");
                result.append(startTag("List", docAttr));
                result.append(processTag("ID", ""));
                for (LinkBean link : dwsData.getLinksList())
                {
                    result.append(generateXml(link));
                }
                result.append(endTag("List"));                
            }

        }

        result.append(endTag("Results"));
        return result.toString();
    }

    /**
     * Convert DwsMetadata type to the correct xml presentation
     * 
     * @param dwsMetadata object to convert
     * @return xml presentation
     */
    public String generateXml(DwsMetadata dwsMetadata)
    {
        StringBuilder result = new StringBuilder("");
        result.append(startTag("Results")).append(processTag("SubscribeUrl", dwsMetadata.getSubscribeUrl())).append(processTag("MtgInstance", dwsMetadata.getMtgInstance()))
                .append(processTag("SettingUrl", dwsMetadata.getSettingsUrl())).append(processTag("PermsUrl", dwsMetadata.getPermsUrl())).append(
                        processTag("UserInfoUrl", dwsMetadata.getUserInfoUrl())).append(startTag("Roles"));
        for (String role : dwsMetadata.getRoles())
        {
            result.append("<Role Name=\"" + role + "\" Description=\"\" Type=\"" + role + "\" />");
        }
        result.append(endTag("Roles"));
        if (!dwsMetadata.isMinimal())
        {
            for (SchemaBean schema : dwsMetadata.getSchemaItems())
            {
                result.append(generateXml(schema));
            }
            for (ListInfoBean listInfo : dwsMetadata.getListInfoItems())
            {
                result.append(generateXml(listInfo));
            }
        }
        result.append(startTag("Permissions"));
        for (Permission permission : dwsMetadata.getPermissions())
        {
            result.append(singleTag(permission.toString()));
        }
        result.append(endTag("Permissions")).append(processTag("HasUniquePerm", dwsMetadata.isHasUniquePerm())).append(processTag("WorkspaceType", dwsMetadata.getWorkspaceType()))
                .append(processTag("IsADMode", dwsMetadata.isADMode())).append(processTag("DocUrl", dwsMetadata.getDocUrl()))
                .append(processTag("Minimal", dwsMetadata.isMinimal())).append(generateXml(dwsMetadata.getDwsData())).append(endTag("Results"));

        return result.toString();
    }

    /**
     * Convert LinkBean type to the correct xml presentation
     * 
     * @param linkBean object to convert
     * @return xml presentation
     */
    public String generateXml(LinkBean linkBean)
    {
        StringBuilder result = new StringBuilder("");
        Map<String, Object> attributes = new LinkedHashMap<String, Object>();

        attributes.put("ows_URL", linkBean.getUrl() + ", " + linkBean.getDescription());
        attributes.put("ows_Comments", linkBean.getComments());
        attributes.put("ows_Created", linkBean.getCreated());
        attributes.put("ows_Author", linkBean.getAuthor());
        attributes.put("ows_Modified", linkBean.getModified());
        attributes.put("ows_Editor", linkBean.getEditor());
        attributes.put("ows_owshiddenversion", linkBean.getOwshiddenversion());
        attributes.put("ows_ID", linkBean.getId());
        attributes.put("xmlns:z", "#RowsetSchema");
        result.append(singleTag("z:row", attributes));
        return result.toString();
    }

    /**
     * Convert ListInfoBean type to the correct xml presentation
     * 
     * @param listInfoBean object to convert
     * @return xml presentation
     */
    public String generateXml(ListInfoBean listInfoBean)
    {
        StringBuilder result = new StringBuilder("");
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("Name", listInfoBean.getName());

        result.append(startTag("ListInfo", attributes));
        result.append(processTag("Moderated", listInfoBean.isModerated()));
        result.append(startTag("ListPermissions"));
        for (Permission permission : listInfoBean.getPermissionList())
        {
            result.append(singleTag(permission.toString()));
        }
        result.append(endTag("ListPermissions"));
        result.append(endTag("ListInfo"));
        return result.toString();
    }

    /**
     * Convert MemberBean type to the correct xml presentation
     * 
     * @param memberBean object to convert
     * @return xml presentation
     */
    public String generateXml(MemberBean memberBean)
    {
        StringBuilder result = new StringBuilder("");
        result.append(startTag("Member")).append(processTag("ID", memberBean.getId())).append(processTag("Name", memberBean.getName())).append(
                processTag("LoginName", memberBean.getLoginName())).append(processTag("Email", memberBean.getEmail())).append(
                processTag("IsDomainGroup", memberBean.isDomainGroup())).append(endTag("Member"));
        return result.toString();
    }

    /**
     * Convert SchemaBean type to the correct xml presentation
     * 
     * @param schemaBean object to convert
     * @return xml presentation
     */
    public String generateXml(SchemaBean schemaBean)
    {
        StringBuilder result = new StringBuilder("");
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("Name", schemaBean.getName());
        attributes.put("Url", schemaBean.getUrl());

        result.append(startTag("Schema", attributes));
        for (SchemaFieldBean field : schemaBean.getFields())
        {
            result.append(generateXml(field));
        }
        result.append(endTag("Schema"));
        return result.toString();
    }

    /**
     * Convert SchemaFieldBean type to the correct xml presentation
     * 
     * @param schemaFieldBean object to convert
     * @return xml presentation
     */
    public String generateXml(SchemaFieldBean schemaFieldBean)
    {
        StringBuilder result = new StringBuilder("");
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("Name", schemaFieldBean.getName());
        attributes.put("Type", schemaFieldBean.getType());
        attributes.put("Required", schemaFieldBean.isRequired());

        result.append(startTag("Field", attributes));
        if (schemaFieldBean.getChoices().size() > 0)
        {
            result.append(startTag("Choices"));
            for (String choice : schemaFieldBean.getChoices())
            {
                result.append(processTag("Choice", choice));
            }
            result.append(endTag("Choices"));
        }
        else
        {
            result.append(singleTag("Choices"));
        }
        result.append(endTag("Field"));

        return result.toString();
    }

    /**
     * Convert TaskBean type to the correct xml presentation
     * 
     * @param taskBean object to convert
     * @return xml presentation
     */
    public String generateXml(TaskBean taskBean)
    {
        StringBuilder result = new StringBuilder("");
        Map<String, Object> attributes = new LinkedHashMap<String, Object>();

        attributes.put("ows_Title", taskBean.getTitle());
        attributes.put("ows_AssignedTo", taskBean.getAssignedTo());
        attributes.put("ows_Status", taskBean.getStatus());
        attributes.put("ows_Priority", taskBean.getPriority());
        attributes.put("ows_DueDate", taskBean.getDueDate());
        attributes.put("ows_Body", taskBean.getBody());
        attributes.put("ows_Created", taskBean.getCreated());
        attributes.put("ows_Author", taskBean.getAuthor());
        attributes.put("ows_Modified", taskBean.getModified());
        attributes.put("ows_Editor", taskBean.getEditor());
        attributes.put("ows_owshiddenversion", taskBean.getOwshiddenversion());
        attributes.put("ows_ID", taskBean.getId());
        attributes.put("xmlns:z", "#RowsetSchema");
        result.append(singleTag("z:row", attributes));
        return result.toString();
    }

    /**
     * Convert UserBean type to the correct xml presentation
     * 
     * @param userBean object to convert
     * @return xml presentation
     */
    public String generateXml(UserBean userBean)
    {
        StringBuilder result = new StringBuilder("");
        result.append(startTag("User")).append(processTag("ID", userBean.getId())).append(processTag("Name", userBean.getName())).append(
                processTag("LoginName", userBean.getLoginName())).append(processTag("Email", userBean.getEmail())).append(processTag("IsDomainGroup", userBean.isDomainGroup()))
                .append(processTag("IsSiteAdmin", userBean.isSiteAdmin())).append(endTag("User"));
        return result.toString();
    }
    
    protected String generateXml(Error error)
    {
        return new StringBuilder()
            .append(startTag("Error"))
            .append(error.getId())
            .append(endTag("Error"))
            .toString();
    }

}
