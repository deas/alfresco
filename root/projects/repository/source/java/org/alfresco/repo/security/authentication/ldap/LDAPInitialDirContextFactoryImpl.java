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
package org.alfresco.repo.security.authentication.ldap;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import javax.naming.AuthenticationNotSupportedException;
import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.PagedResultsControl;
import javax.naming.ldap.PagedResultsResponseControl;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.security.authentication.AlfrescoSSLSocketFactory;
import org.alfresco.repo.security.authentication.AuthenticationDiagnostic;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.util.ApplicationContextHelper;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;

public class LDAPInitialDirContextFactoryImpl implements LDAPInitialDirContextFactory, InitializingBean
{
    private static final Log logger = LogFactory.getLog(LDAPInitialDirContextFactoryImpl.class);

    private static Set<Map<String, String>> checkedEnvs = Collections.synchronizedSet(new HashSet<Map<String, String>>(
            11));

    private Map<String, String> defaultEnvironment = Collections.<String, String> emptyMap();
    private Map<String, String> authenticatedEnvironment = Collections.<String, String> emptyMap();
    private String trustStorePath;
    private String trustStoreType;
    private String trustStorePassPhrase;

    public String getTrustStorePath()
    {
        return trustStorePath;
    }

    public void setTrustStorePath(String trustStorePath)
    {
        if (PropertyCheck.isValidPropertyString(trustStorePath))
        {
            this.trustStorePath = trustStorePath;
        }
    }

    public String getTrustStoreType()
    {
        return trustStoreType;
    }

    public void setTrustStoreType(String trustStoreType)
    {
        if (PropertyCheck.isValidPropertyString(trustStoreType))
        {
            this.trustStoreType = trustStoreType;
        }
    }

    public String getTrustStorePassPhrase()
    {
        return trustStorePassPhrase;
    }

    public void setTrustStorePassPhrase(String trustStorePassPhrase)
    {
        if (PropertyCheck.isValidPropertyString(trustStorePassPhrase))
        {
            this.trustStorePassPhrase = trustStorePassPhrase;
        }
    }

    static
    {
        System.setProperty("javax.security.auth.useSubjectCredentialsOnly", "false");
    }

    public LDAPInitialDirContextFactoryImpl()
    {
        super();
    }

    public void setInitialDirContextEnvironment(Map<String, String> initialDirContextEnvironment)
    {
        this.authenticatedEnvironment = initialDirContextEnvironment;
    }

    public Map<String, String> getInitialDirContextEnvironment()
    {
        return authenticatedEnvironment;
    }
    
    public void setDefaultIntialDirContextEnvironment(Map<String, String> defaultEnvironment)
    {
        this.defaultEnvironment = defaultEnvironment;
    }    

    public InitialDirContext getDefaultIntialDirContext() throws AuthenticationException
    {
        return getDefaultIntialDirContext(0, new AuthenticationDiagnostic());
    }
    
    public InitialDirContext getDefaultIntialDirContext(int pageSize) throws AuthenticationException
    {
        return getDefaultIntialDirContext(pageSize, new AuthenticationDiagnostic());
    }
    
    @Override
    public InitialDirContext getDefaultIntialDirContext(
            AuthenticationDiagnostic diagnostic) throws AuthenticationException
    {
        return getDefaultIntialDirContext(0, diagnostic);
    }

    public InitialDirContext getDefaultIntialDirContext(int pageSize, AuthenticationDiagnostic diagnostic) throws AuthenticationException
    {
        Hashtable<String, String> env = new Hashtable<String, String>(defaultEnvironment.size());
        env.putAll(defaultEnvironment);
        return buildInitialDirContext(env, pageSize, diagnostic);
    }

    private InitialDirContext buildInitialDirContext(Hashtable<String, String> env, int pageSize, AuthenticationDiagnostic diagnostic)
            throws AuthenticationException
    {
        String securityPrincipal = env.get(Context.SECURITY_PRINCIPAL);
        String providerURL = env.get(Context.PROVIDER_URL);

        if (isSSLSocketFactoryRequired())
        {
            KeyStore trustStore = initTrustStore();
            AlfrescoSSLSocketFactory.initTrustedSSLSocketFactory(trustStore);
            env.put("java.naming.ldap.factory.socket", AlfrescoSSLSocketFactory.class.getName());
        }

        if(diagnostic == null)
        {
            diagnostic = new AuthenticationDiagnostic();
        }
        try
        {
            // If a page size has been requested, use LDAP v3 paging
            if (pageSize > 0)
            {
                InitialLdapContext ctx = new InitialLdapContext(env, null);
                ctx.setRequestControls(new Control[]
                {
                    new PagedResultsControl(pageSize, Control.CRITICAL)
                });
                return ctx;
            }
            else
            {
                InitialDirContext ret = new InitialDirContext(env);
                Object[] args = {providerURL, securityPrincipal};
                diagnostic.addStep(AuthenticationDiagnostic.STEP_KEY_LDAP_CONNECTED, true, args);
                return ret;
            }
        }
        catch (javax.naming.AuthenticationException ax)
        {
            Object[] args1 = {securityPrincipal};
            Object[] args = {providerURL, securityPrincipal};
            diagnostic.addStep(AuthenticationDiagnostic.STEP_KEY_LDAP_CONNECTED, true, args);
            diagnostic.addStep(AuthenticationDiagnostic.STEP_KEY_LDAP_AUTHENTICATION, false, args1);
            
            // wrong user/password - if we get this far the connection is O.K
            Object[] args2 = {securityPrincipal, ax.getLocalizedMessage()};
            throw new AuthenticationException("authentication.err.authentication", diagnostic, args2, ax);
        }
        catch (CommunicationException ce)
        {
            Object[] args1 = {providerURL};
            diagnostic.addStep(AuthenticationDiagnostic.STEP_KEY_LDAP_CONNECTING, false, args1);

            StringBuffer message = new StringBuffer();
            
            message.append(ce.getClass().getName() + ", " + ce.getMessage());
            
            Throwable cause = ce.getCause();
            while (cause != null)
            {
                message.append(", ");
                message.append(cause.getClass().getName() + ", " + cause.getMessage());
                cause = cause.getCause();
            }
            
            // failed to connect
            Object[] args = {providerURL, message.toString()};
            throw new AuthenticationException("authentication.err.communication", diagnostic, args, cause);
        }
        catch (NamingException nx)
        {
            Object[] args = {providerURL};
            diagnostic.addStep(AuthenticationDiagnostic.STEP_KEY_LDAP_CONNECTING, false, args);
            
            StringBuffer message = new StringBuffer();
            
            message.append(nx.getClass().getName() + ", " + nx.getMessage());
            
            Throwable cause = nx.getCause();
            while (cause != null)
            {
                message.append(", ");
                message.append(cause.getClass().getName() + ", " + cause.getMessage());
                cause = cause.getCause();
            }
           
            // failed to connect
            Object[] args1 = {providerURL, message.toString()};
            throw new AuthenticationException("authentication.err.connection", diagnostic, args1, nx);
        }
        catch (IOException e)
        {
            Object[] args = {providerURL, securityPrincipal};
            diagnostic.addStep(AuthenticationDiagnostic.STEP_KEY_LDAP_CONNECTED, true, args);
            
            throw new AuthenticationException("Unable to encode LDAP v3 request controls", e);
        }
    }

    public boolean hasNextPage(DirContext ctx, int pageSize)
    {
        if (pageSize > 0)
        {
            try
            {
                LdapContext ldapContext = (LdapContext) ctx;
                Control[] controls = ldapContext.getResponseControls();

                // Retrieve the paged result cookie if there is one
                if (controls != null)
                {
                    for (Control control : controls)
                    {
                        if (control instanceof PagedResultsResponseControl)
                        {
                            byte[] cookie = ((PagedResultsResponseControl) control).getCookie();
                            if (cookie != null)
                            {
                                // Prepare for next page
                                ldapContext.setRequestControls(new Control[]
                                {
                                    new PagedResultsControl(pageSize, cookie, Control.CRITICAL)
                                });
                                return true;
                            }
                        }
                    }
                }
            }
            catch (NamingException nx)
            {
                throw new AuthenticationException("Unable to connect to LDAP Server; check LDAP configuration", nx);
            }
            catch (IOException e)
            {
                throw new AuthenticationException(
                        "Unable to encode LDAP v3 request controls; check LDAP configuration", e);
            }

        }
        return false;
    }
    
    @Override
    public InitialDirContext getInitialDirContext(String principal,
            String credentials)
            throws AuthenticationException
    {
        return getInitialDirContext(principal, credentials, null);
    }

    public InitialDirContext getInitialDirContext(String principal, String credentials, AuthenticationDiagnostic diagnostic) throws AuthenticationException
    {
        if(diagnostic == null)
        {
            diagnostic = new AuthenticationDiagnostic();
        }
        
        if (principal == null)
        {
            // failed before we tried to do anything
            diagnostic.addStep(AuthenticationDiagnostic.STEP_KEY_VALIDATION, false, null);
            throw new AuthenticationException("Null user name provided.", diagnostic);
        }

        if (principal.length() == 0)
        {
            diagnostic.addStep(AuthenticationDiagnostic.STEP_KEY_VALIDATION, false, null);
            throw new AuthenticationException("Empty user name provided.", diagnostic);
        }

        if (credentials == null)
        {
            diagnostic.addStep(AuthenticationDiagnostic.STEP_KEY_VALIDATION, false, null);
            throw new AuthenticationException("No credentials provided.", diagnostic);
        }

        if (credentials.length() == 0)
        {
            diagnostic.addStep(AuthenticationDiagnostic.STEP_KEY_VALIDATION, false, null);
            throw new AuthenticationException("Empty credentials provided.", diagnostic);
        }
        
        diagnostic.addStep(AuthenticationDiagnostic.STEP_KEY_VALIDATION, true, null);

        Hashtable<String, String> env = new Hashtable<String, String>(authenticatedEnvironment.size());
        env.putAll(authenticatedEnvironment);
        env.put(Context.SECURITY_PRINCIPAL, principal);
        env.put(Context.SECURITY_CREDENTIALS, credentials);

        return buildInitialDirContext(env, 0, diagnostic);
    }
    


    public static void main(String[] args)
    {
        // ....build a pyramid selling scheme .....

        // A group has three user members and 2 group members .... and off we go ....
        // We make the people and groups to represent this and stick them into LDAP ...used to populate a test data base for user and groups

        int userMembers = Integer.parseInt(args[3]);

        ApplicationContext applicationContext = ApplicationContextHelper.getApplicationContext();
        LDAPInitialDirContextFactory factory = (LDAPInitialDirContextFactory) applicationContext
                .getBean("ldapInitialDirContextFactory");

        InitialDirContext ctx = null;
        try
        {
            ctx = factory.getInitialDirContext("cn=" + args[0] + "," + args[2], args[1]);

            /* Values we'll use in creating the entry */
            Attribute objClasses = new BasicAttribute("objectclass");
            objClasses.add("top");
            objClasses.add("person");
            objClasses.add("organizationalPerson");
            objClasses.add("inetOrgPerson");

            for (int i = 0; i < userMembers; i++)
            {

                Attribute cn = new BasicAttribute("cn", "User" + i + " TestUser");
                Attribute sn = new BasicAttribute("sn", "TestUser");
                Attribute givenNames = new BasicAttribute("givenName", "User" + i);
                Attribute telephoneNumber = new BasicAttribute("telephoneNumber", "123");
                Attribute uid = new BasicAttribute("uid", "User" + i);
                Attribute mail = new BasicAttribute("mail", "woof@woof");
                Attribute o = new BasicAttribute("o", "Alfresco");
                Attribute userPassword = new BasicAttribute("userPassword", "bobbins");
                /* Specify the DN we're adding */
                String dn = "cn=User" + i + " TestUser," + args[2];

                Attributes orig = new BasicAttributes();
                orig.put(objClasses);
                orig.put(cn);
                orig.put(sn);
                orig.put(givenNames);
                orig.put(telephoneNumber);
                orig.put(uid);
                orig.put(mail);
                orig.put(o);
                orig.put(userPassword);

                try
                {
                    ctx.destroySubcontext(dn);
                }
                catch (NamingException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                ctx.createSubcontext(dn, orig);
            }

        }
        catch (NamingException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            if (ctx != null)
            {
                try
                {
                    ctx.close();
                }
                catch (NamingException e)
                {

                    e.printStackTrace();
                }
            }
        }

    }

    public void afterPropertiesSet() throws Exception
    {
        logger.debug("after Properties Set");
        // Check Anonymous bind

        Hashtable<String, String> env = new Hashtable<String, String>(authenticatedEnvironment.size());
        env.putAll(authenticatedEnvironment);
        env.remove(Context.SECURITY_PRINCIPAL);
        env.remove(Context.SECURITY_CREDENTIALS);
        if (isSSLSocketFactoryRequired())
        {
            KeyStore trustStore = initTrustStore();
            AlfrescoSSLSocketFactory.initTrustedSSLSocketFactory(trustStore);
            env.put("java.naming.ldap.factory.socket", AlfrescoSSLSocketFactory.class.getName());
        }
        try
        {
            new InitialDirContext(env);

            logger.warn("LDAP server supports anonymous bind " + env.get(Context.PROVIDER_URL));
        }
        catch (javax.naming.AuthenticationException ax)
        {

        }
        catch (AuthenticationNotSupportedException e)
        {

        }
        catch (NamingException nx)
        {
            logger.error("Unable to connect to LDAP Server; check LDAP configuration", nx);
            return;
        }

        // Simple DN and password

        env = new Hashtable<String, String>(authenticatedEnvironment.size());
        env.putAll(authenticatedEnvironment);
        env.put(Context.SECURITY_PRINCIPAL, "daftAsABrush");
        env.put(Context.SECURITY_CREDENTIALS, "daftAsABrush");
        if (isSSLSocketFactoryRequired())
        {
            KeyStore trustStore = initTrustStore();
            AlfrescoSSLSocketFactory.initTrustedSSLSocketFactory(trustStore);
            env.put("java.naming.ldap.factory.socket", AlfrescoSSLSocketFactory.class.getName());
        }
        try
        {

            new InitialDirContext(env);

            throw new AuthenticationException(
                    "The ldap server at "
                            + env.get(Context.PROVIDER_URL)
                            + " falls back to use anonymous bind if invalid security credentials are presented. This is not supported.");
        }
        catch (javax.naming.AuthenticationException ax)
        {
            logger.info("LDAP server does not fall back to anonymous bind for a string uid and password at " + env.get(Context.PROVIDER_URL));
        }
        catch (AuthenticationNotSupportedException e)
        {
            logger.info("LDAP server does not fall back to anonymous bind for a string uid and password at " + env.get(Context.PROVIDER_URL)); 
        }
        catch (NamingException nx)
        {
            logger.info("LDAP server does not support simple string user ids and invalid credentials at "+ env.get(Context.PROVIDER_URL));
        }

        // DN and password

        env = new Hashtable<String, String>(authenticatedEnvironment.size());
        env.putAll(authenticatedEnvironment);
        env.put(Context.SECURITY_PRINCIPAL, "cn=daftAsABrush,dc=woof");
        env.put(Context.SECURITY_CREDENTIALS, "daftAsABrush");
        if (isSSLSocketFactoryRequired())
        {
            KeyStore trustStore = initTrustStore();
            AlfrescoSSLSocketFactory.initTrustedSSLSocketFactory(trustStore);
            env.put("java.naming.ldap.factory.socket", AlfrescoSSLSocketFactory.class.getName());
        }
        try
        {

            new InitialDirContext(env);

            throw new AuthenticationException(
                    "The ldap server at "
                            + env.get(Context.PROVIDER_URL)
                            + " falls back to use anonymous bind if invalid security credentials are presented. This is not supported.");
        }
        catch (javax.naming.AuthenticationException ax)
        {
            logger.info("LDAP server does not fall back to anonymous bind for a simple dn and password at " + env.get(Context.PROVIDER_URL));
        }
        catch (AuthenticationNotSupportedException e)
        {
            logger.info("LDAP server does not fall back to anonymous bind for a simple dn and password at " + env.get(Context.PROVIDER_URL));
        }
        catch (NamingException nx)
        {
            logger.info("LDAP server does not support simple DN and invalid password at "+ env.get(Context.PROVIDER_URL));
        }

        // Check more if we have a real principal we expect to work

        String principal = defaultEnvironment.get(Context.SECURITY_PRINCIPAL);
        if (principal != null)
        {
            // Correct principal invalid password

            env = new Hashtable<String, String>(authenticatedEnvironment.size());
            env.putAll(authenticatedEnvironment);
            env.put(Context.SECURITY_PRINCIPAL, principal);
            env.put(Context.SECURITY_CREDENTIALS, "sdasdasdasdasd123123123");
            if (isSSLSocketFactoryRequired())
            {
                KeyStore trustStore = initTrustStore();
                AlfrescoSSLSocketFactory.initTrustedSSLSocketFactory(trustStore);
                env.put("java.naming.ldap.factory.socket", AlfrescoSSLSocketFactory.class.getName());
            }
            if (!checkedEnvs.contains(env))
            {

                try
                {
    
                    new InitialDirContext(env);
    
                    throw new AuthenticationException(
                            "The ldap server at "
                                    + env.get(Context.PROVIDER_URL)
                                    + " falls back to use anonymous bind for a known principal if  invalid security credentials are presented. This is not supported.");
                }
                catch (javax.naming.AuthenticationException ax)
                {
                    logger.info("LDAP server does not fall back to anonymous bind for known principal and invalid credentials at " + env.get(Context.PROVIDER_URL));
                }
                catch (AuthenticationNotSupportedException e)
                {
                    logger.info("LDAP server does not support the required authentication mechanism");
                }
                catch (NamingException nx)
                {
                    // already done
                }
                // Record this environment as checked so that we don't check it again on further restarts / other subsystem
                // instances
                checkedEnvs.add(env);
            }
        }
    }

    /**
     * Check if it required to use custom SSL socket factory with custom trustStore.
     * <br>Required for LDAPS configuration. The <code>ldap.authentication.java.naming.security.protocol</code> should be set to "ssl" for LDAPS.
     * <br>The following properties should be set:
     * <ul>
     * <li>ldap.authentication.truststore.path
     * <li>ldap.authentication.truststore.type
     * <li>ldap.authentication.truststore.passphrase
     * <li>ldap.authentication.java.naming.security.protocol
     * </ul>
     *
     * @return <code>true</code> if all the required properties are set
     */
    private boolean isSSLSocketFactoryRequired()
    {
        boolean result = false;
        // Check for LDAPS config
        String protocol = authenticatedEnvironment.get(Context.SECURITY_PROTOCOL);
        if (protocol != null && protocol.equals("ssl"))
        {
            if (getTrustStoreType() != null && getTrustStorePath() != null && getTrustStoreType() != null)
            {
                result = true;
            }
            else
            {
                logger.warn("The SSL configuration for LDAPS is not full, the default configuration will be used.");
            }
        }
        return result;
    }

    /**
     * Initialize trustStore with Spring set properties:
     * <ul>
     * <li>ldap.authentication.truststore.path
     * <li>ldap.authentication.truststore.type
     * <li>ldap.authentication.truststore.passphrase
     * </ul>
     *
     * @return {@link KeyStore} with loaded trustStore file
     */
    private KeyStore initTrustStore()
    {
        KeyStore ks;
        String trustStoreType = getTrustStoreType();
        try
        {
            ks = KeyStore.getInstance(trustStoreType);
        }
        catch (KeyStoreException kse)
        {
            throw new AlfrescoRuntimeException("No provider supports " + trustStoreType, kse);
        }
        try
        {
            ks.load(new FileInputStream(getTrustStorePath()), getTrustStorePassPhrase().toCharArray());
        }
        catch (FileNotFoundException fnfe)
        {
            throw new AlfrescoRuntimeException("The truststore file is not found.", fnfe);
        }
        catch (IOException ioe)
        {
            throw new AlfrescoRuntimeException("The truststore file cannot be read.", ioe);
        }
        catch (NoSuchAlgorithmException nsae)
        {
            throw new AlfrescoRuntimeException("Algorithm used to check the integrity of the truststore cannot be found.", nsae);
        }
        catch (CertificateException ce)
        {
            throw new AlfrescoRuntimeException("The certificates cannot be loaded from truststore.", ce);
        }
        return ks;
    }
}
