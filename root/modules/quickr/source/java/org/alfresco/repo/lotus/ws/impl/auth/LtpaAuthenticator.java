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
package org.alfresco.repo.lotus.ws.impl.auth;

import java.io.Serializable;
import java.security.Key;
import java.security.MessageDigest;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.MutableAuthenticationService;
import org.alfresco.service.cmr.security.NoSuchPersonException;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.GUID;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.message.Message;

/**
 * LTPA authenticator based on LtpaToken/LtpaToken2 retrieved from request cookie
 * 
 * @author PavelYur
 *
 */
public class LtpaAuthenticator implements Authenticator
{
    private Log logger = LogFactory.getLog(LtpaAuthenticator.class);
    
    public static final String HEADER_COOKIE = "Cookie";
    public static final String LTPA_COOKIE_NAME = "LtpaToken";
    private static final String REQUEST_PARAMETER_NAME = "HTTP.REQUEST";
    
    private static final String AES_DECRIPTING_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String DES_DECRIPTING_ALGORITHM = "DESede/ECB/PKCS5Padding";
    
    private String ltpa3DESKey;
    private String ltpaPassword;    
    
    private String userHomesPath;
    
    private PersonService personService;
    private AuthenticationComponent authenticationComponent;    
    private MutableAuthenticationService mutableAuthenticationService;
    private NodeService nodeService;
    private NamespaceService namespaceService;
    private SearchService searchService;
    private PermissionService permissionService;
    private TransactionService transactionService;
    
    /**
     * Sets the 3DESKey that is used for encrypting/decrypting LTPA token.
     * 
     * @param ltpa3DESKey the 3DESKey to set
     */
    public void setLtpa3DESKey(String ltpa3DESKey)
    {
        this.ltpa3DESKey = ltpa3DESKey;
    }
    
    /**
     * Sets the password that is used for encrypting/decrypting LTPA token.
     * 
     * @param ltpaPassword the password to set
     */
    public void setLtpaPassword(String ltpaPassword)
    {
        this.ltpaPassword = ltpaPassword;
    }
    
    /**
     * Sets the path to User Homes folder
     * 
     * @param userHomesPath the userHomesPath to set
     */
    public void setUserHomesPath(String userHomesPath)
    {
        this.userHomesPath = userHomesPath;
    }
    
    /**
     * Sets personService
     * 
     * @param personService the personService to set
     */
    public void setPersonService(PersonService personService)
    {
        this.personService = personService;
    }
    
    /**
     * Sets authenticationComponent
     * 
     * @param authenticationComponent the authenticationComponent to set
     */
    public void setAuthenticationComponent(AuthenticationComponent authenticationComponent)
    {
        this.authenticationComponent = authenticationComponent;
    }    
        
    /**
     * Sets nodeService
     * 
     * @param nodeService the nodeService to set
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;    
    }
    
    /**
     * Sets searchService
     * 
     * @param searchService the searchService to set
     */
    public void setSearchService(SearchService searchService)
    {
        this.searchService = searchService;
    }
    
    /**
     * Sets permissionService
     * 
     * @param permissionService the permissionService to set
     */
    public void setPermissionService(PermissionService permissionService)
    {
        this.permissionService = permissionService;
    }
    
    /**
     * Sets namespaceService
     * 
     * @param namespaceService the namespaceService to set
     */
    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }
    
    /**
     * Sets transactionService
     * 
     * @param transactionService the transactionService to set
     */
    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }
    
    /**
     * Sets mutableAuthenticationService
     * 
     * @param mutableAuthenticationService the mutableAuthenticationService to set
     */
    public void setMutableAuthenticationService(MutableAuthenticationService mutableAuthenticationService)
    {
        this.mutableAuthenticationService = mutableAuthenticationService;
    }
    
    @Override
    public boolean authenticate(Message message)
    {
        // firstly check if basic authentication parameters are present in request
        if (message.get(AuthorizationPolicy.class) != null)
        {
            return Boolean.FALSE;
        }
        
        // ok. this is ltpa authentication
        HttpServletRequest request = (HttpServletRequest)message.get(REQUEST_PARAMETER_NAME);
        
        if (logger.isDebugEnabled())
        {
            logger.debug("Start LTPA authentication for request: " + request.getRequestURL());            
        }
        
        String ltpaToken = null;
        
        // get Cookie header
        String cookieHeader = request.getHeader(HEADER_COOKIE);
        
        if (cookieHeader != null && cookieHeader.length() > 0)
        {
            // looking for LtpaToken cookie
            String[] cookies = cookieHeader.split(";");
            
            for (String cookie : cookies)
            {
                cookie = cookie.trim();
                if (cookie.startsWith(LTPA_COOKIE_NAME))
                {
                    ltpaToken = cookie.substring(LTPA_COOKIE_NAME.length() + 1);
                    
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("LtpaToken presents in request.");
                    }
                    
                    break;
                }
            }
        }
        
        if (ltpaToken == null)
        {
            // check the request parameters
            ltpaToken = request.getParameter(LTPA_COOKIE_NAME);
            if (ltpaToken != null)
            {
                ltpaToken = ltpaToken.replaceAll(" ", "+");
            }
        }
        
        // if ltpa token was founded and configuration is ok, start the decrypting mechanism
        if (ltpaToken != null && isActive())
        {   
            try
            {
                // get the secret key for decrypting
                byte[] secretKey = getSecretKey(ltpa3DESKey, ltpaPassword);
                
                if (logger.isDebugEnabled())
                {
                    logger.debug("Secret key was successfully extracted.");
                }
                
                byte[] ltpaTokenBytes = Base64.decodeBase64(ltpaToken.getBytes());
                
                String username;
                try
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Start decrypting using " + DES_DECRIPTING_ALGORITHM + " algorithm.");
                    }
                    // get user's name from ltpa token using DES algorithm
                    username = getUserName(decrypt(ltpaTokenBytes, secretKey, DES_DECRIPTING_ALGORITHM));
                }
                catch (Exception e) 
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Start decrypting using " + AES_DECRIPTING_ALGORITHM + " algorithm.");
                    }
                    // DES algorithm fail, look like it is LtpaToken2, try AES algorithm
                    username = getUserName(decrypt(ltpaTokenBytes, secretKey, AES_DECRIPTING_ALGORITHM));
                }
                
                final String userName = username;
                
                // find user with given name or create new one
                String createdUserName = AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<String>()
                {
                    @Override
                    public String doWork() throws Exception
                    {
                        NodeRef person = createOrGetPerson(userName);
                        
                        return (String)nodeService.getProperty(person, ContentModel.PROP_USERNAME);
                    }
            
                }, authenticationComponent.getSystemUserName());
                
                // set retrieved user as current
                authenticationComponent.setCurrentUser(createdUserName);
                
                String currentUser = authenticationComponent.getCurrentUserName();
                
                // check that current user match the user from ltpa token
                if ( currentUser == null || !currentUser.equalsIgnoreCase(username))
                {
                    throw AuthenticationException.create("Couldn't login user: " + username);
                }
            }
            catch (Exception e)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("LTPA authentication failed with message: " + e.getMessage());
                }
                
                return Boolean.FALSE;
            }
        }
        else
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("LTPA authentication was not performed. Check that it is configured properly.");
            }
            return Boolean.FALSE;
        }
        
        if(logger.isDebugEnabled())
        {
            logger.debug("LTPA authentication successfully finished. User: " + authenticationComponent.getCurrentUserName());            
        }
        
        return Boolean.TRUE;
    }
    
    @Override
    public boolean isActive()
    {
        return ltpa3DESKey !=null && ltpa3DESKey.length() > 0 && ltpaPassword !=null && ltpaPassword.length() > 0;
    }
    
    private NodeRef createOrGetPerson(final String username)
    {
        final Map<QName, Serializable> props = new HashMap<QName, Serializable>();
        
        props.put(ContentModel.PROP_USERNAME, username);
        props.put(ContentModel.PROP_FIRSTNAME, username);
        props.put(ContentModel.PROP_LASTNAME, username);
        props.put(ContentModel.PROP_EMAIL, "");
        props.put(ContentModel.PROP_ORGID, "");
        props.put(ContentModel.PROP_ORGANIZATION, "");
        props.put(ContentModel.PROP_JOBTITLE, "");
        props.put(ContentModel.PROP_LOCATION, "");
        props.put(ContentModel.PROP_PRESENCEPROVIDER, "");
        props.put(ContentModel.PROP_PRESENCEUSERNAME, "");
            
        NodeRef person = null;
        
        try
        {
            person = personService.getPerson(username, false);
        }
        catch (NoSuchPersonException e)
        {            
            person = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>()
            {
                @Override
                public NodeRef execute() throws Throwable
                {        
                    NodeRef rootNodeRef = nodeService.getRootNode(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
                    
                    List<NodeRef> select = searchService.selectNodes(rootNodeRef, userHomesPath, null, namespaceService, false);
                    
                    NodeRef userHomes = select.get(0);
                    
                    NodeRef userHomeSpace = nodeService.getChildByName(userHomes, ContentModel.ASSOC_CONTAINS, username);
                    
                    if (userHomeSpace == null)
                    {
                        userHomeSpace = nodeService.createNode(userHomes, ContentModel.ASSOC_CONTAINS, QName.createQName(NamespaceService.SYSTEM_MODEL_1_0_URI, username), ContentModel.TYPE_FOLDER).getChildRef();
                        nodeService.setProperty(userHomeSpace, ContentModel.PROP_NAME, username);
                        permissionService.setPermission(userHomeSpace, username, permissionService.getAllPermission(), true);
                        permissionService.setInheritParentPermissions(userHomeSpace, false);
                    }
                    
                    props.put(ContentModel.PROP_HOMEFOLDER, userHomeSpace);
                    
                    NodeRef person = personService.createPerson(props);
                    
                    String password = GUID.generate();                    
                    mutableAuthenticationService.createAuthentication(username, password.toCharArray());
                    return person;
                }                      
            });        
        }
        
        return person;    
    }
    
    private byte[] getSecretKey(String ltpa3DESKey, String ltpaPassword) throws Exception
    {
        MessageDigest md = MessageDigest.getInstance("SHA");
        
        md.update(ltpaPassword.getBytes());
        
        byte[] hash3DES = new byte[24];
        
        System.arraycopy(md.digest(), 0, hash3DES, 0, 20);
        
        Arrays.fill(hash3DES, 20, 24, (byte) 0);
        
        final Cipher cipher = Cipher.getInstance(DES_DECRIPTING_ALGORITHM);
        
        final KeySpec keySpec = new DESedeKeySpec(hash3DES);
        
        final Key secretKey = SecretKeyFactory.getInstance("DESede").generateSecret(keySpec);

        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        
        byte[] secret = cipher.doFinal(Base64.decodeBase64(ltpa3DESKey.getBytes()));
        
        return secret;
    }
    
    private byte[] decrypt(byte[] token, byte[] key, String algorithm) throws Exception
    {
        SecretKey sKey = null;

        if (algorithm.indexOf("AES") != -1)
        {
            sKey = new SecretKeySpec(key, 0, 16, "AES");
        }
        else
        {
            DESedeKeySpec kSpec = new DESedeKeySpec(key);
            SecretKeyFactory kFact = SecretKeyFactory.getInstance("DESede");
            sKey = kFact.generateSecret(kSpec);
        }
        Cipher cipher = Cipher.getInstance(algorithm);

        if (algorithm.indexOf("ECB") == -1)
        {
            if (algorithm.indexOf("AES") != -1)
            {
                IvParameterSpec ivs16 = generateIvParameterSpec(key, 16);
                cipher.init(Cipher.DECRYPT_MODE, sKey, ivs16);
            }
            else
            {
                IvParameterSpec ivs8 = generateIvParameterSpec(key, 8);
                cipher.init(Cipher.DECRYPT_MODE, sKey, ivs8);
            }
        }
        else
        {
            cipher.init(Cipher.DECRYPT_MODE, sKey);
        }
        return cipher.doFinal(token);
    }
    
    private IvParameterSpec generateIvParameterSpec(byte key[], int size)
    {
        byte[] row = new byte[size];
        
        for (int i = 0; i < size; i++)
        {
            row[i] = key[i];
        }
        
        return new IvParameterSpec(row);
    }
    
    private String getUserName(byte[] decryptedToken)
    {
        String token = new String(decryptedToken);
        String username = token.substring(token.indexOf("uid=") + 4, token.indexOf(","));
        
        return username;
    }

}
