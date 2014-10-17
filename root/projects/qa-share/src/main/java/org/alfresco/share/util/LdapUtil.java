/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.share.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.io.*;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * @author Aliaksei Boole
 */
public class LdapUtil extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(LdapUtil.class);

    private static Hashtable<String, String> env = new Hashtable<>();
    private static LdapContext ctx;
    private static DirContext dCtx;
    private final static int UF_PASSWD_NOTREQD = 0x0020;
    private final static int UF_NORMAL_ACCOUNT = 0x0200;
    private final static int UF_PASSWORD_EXPIRED = 0x800000;
    private final static int UF_ACCOUNTDISABLE = 0x0002;

    //    private LdapContext context;
    private static String ldapBaseDN;
    //    private static String ldapObjClass;
    private static String storeDN;
    private static String storePassword;
    private static String ldapUrl;
    private static String userDN;
    private static String userSearchBase;
    private static String GROUPS_OU;
    private static String groupDisplayName;
    private static String groupObjectClass;
    private static List<String> ldapObjClass = new ArrayList<String>();
    public static String localTruststore;
    private static String certPass;
    protected final static String domain = "qalab.alfresco.org";
    public static String jmxScheduleCron = "Alfresco:Name=Schedule,Group=DEFAULT,Type=MonitoredCronTrigger,Trigger=syncTrigger";

    static
    {
        init();
    }

    private static void init()
    {
        try
        {
            localTruststore = System.getProperty("java.home") + SLASH + "lib" + SLASH + "security" + SLASH + "cacerts";
            certPass = "changeit";
            GROUPS_OU = SystemSummaryAdminUtil.GROUP_SEARCH_BASE_AD;
            groupDisplayName = SystemSummaryAdminUtil.GROUP_DISPLAY;
            groupObjectClass = SystemSummaryAdminUtil.GROUP_TYPE;
            ldapObjClass.add("person");
            ldapObjClass.add("top");
            ldapObjClass.add("organizationalPerson");
            ldapObjClass.add(SystemSummaryAdminUtil.PERSON_TYPE);
            ldapBaseDN = SystemSummaryAdminUtil.USER_SEARCH_BASE_AD;
            ldapUrl = "ldaps://172.30.40.61:636";
            userSearchBase = SystemSummaryAdminUtil.USER_SEARCH_BASE_AD;
            userDN = userSearchBase.substring(0, userSearchBase.indexOf("=") + 1);
            storeDN = getUserDN("Administrator");
            storePassword = "Rtg671vPw";
            importCert();
        }
        catch (Throwable ex)
        {
            logger.error(ex);
        }
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapUrl);
        env.put(Context.SECURITY_PRINCIPAL, storeDN);       //Administrator User
        env.put(Context.SECURITY_CREDENTIALS, storePassword);   //Administrator Password
        env.put(Context.SECURITY_PROTOCOL, "ssl");
    }

    /**
     * Create user in active directory on 172.30.40.61.
     *
     * @param username
     * @param password
     * @param firstName
     * @param lastName
     * @param email
     */
    public static void createUser(String username, String password, String firstName, String lastName, String email)
    {
        try
        {
            // Create the initial directory context
            ctx = new InitialLdapContext(env, null);
            // Create attributes to be associated with the new user
            // Create a container set of attributes
            Attributes container = new BasicAttributes();
            // Create the objectclass to add
            Attribute objClasses = new BasicAttribute("objectClass");
            for (String objAttrib : ldapObjClass)
            {
                objClasses.add(objAttrib);
            }
            // Assign the username, first name, last name, password e.g
            Attribute cn = new BasicAttribute("cn", username);
            Attribute mail = new BasicAttribute("mail", email); //for OpenLdap only
            Attribute givenName = new BasicAttribute("givenName", firstName);
            Attribute sn = new BasicAttribute("sn", lastName); //for openLDAP only
            Attribute userPassword = new BasicAttribute("userpassword", password);
            //            Attribute userPassword = new BasicAttribute("unicodePwd", password);
            Attribute sAMAccountName = new BasicAttribute("sAMAccountName", username);  //for ldap-AD only
            Attribute principalName = new BasicAttribute("userPrincipalName", username + "@" + domain);  //for ldap-AD only
            container.put(sAMAccountName); //for ldap-AD only
            container.put(principalName); //for ldap-AD only
            container.put("userAccountControl", Integer.toString(UF_NORMAL_ACCOUNT + UF_PASSWD_NOTREQD + UF_PASSWORD_EXPIRED + UF_ACCOUNTDISABLE));   //for ldap-AD only
            // Create New user
            // Add these to the container
            container.put(sn);
            container.put(objClasses);
            container.put(cn);
            container.put(givenName);
            container.put(mail);

            Context newUser = ctx.createSubcontext(getUserDN(username), container);
            logger.info("User successfully created: " + username);
            ModificationItem[] mods = new ModificationItem[2];

            String newQuotedPassword = "\"" + password + "\"";
            byte[] newUnicodePassword = newQuotedPassword.getBytes("UTF-16LE");

            mods[0] = new ModificationItem(ctx.REPLACE_ATTRIBUTE, new BasicAttribute("unicodePwd", newUnicodePassword));
            mods[1] = new ModificationItem(ctx.REPLACE_ATTRIBUTE, new BasicAttribute("userAccountControl", Integer.toString(UF_NORMAL_ACCOUNT + UF_PASSWORD_EXPIRED)));

            //Perform the update
            ctx.modifyAttributes(getUserDN(username), mods);

            logger.info("Unicode password for user " + username + " has been set.");
            ctx.close();
        }
        catch (NamingException e)
        {
            logger.info("Problem creating object: " + e);
        }
        catch (Throwable e)
        {
            logger.error(e);
        }
    }

    /**
     * Delete user in Active Directory on 172.30.40.61
     *
     * @param username
     */
    public static void deleteUser(String username)
    {
        try
        {
            ctx = new InitialLdapContext(env, null);
            ctx.destroySubcontext(getUserDN(username));
            logger.info("User successfully deleted: " + username);
            ctx.close();
        }
        catch (Throwable e)
        {
            logger.error(e);
        }
    }

    /**
     * Create group in Active Directory on 172.30.40.61
     *
     * @param groupName
     */
    public static void createGroup(String groupName)
    {
        try
        {
            ctx = new InitialLdapContext(env, null);
            // Create a container set of attributes
            Attributes container = new BasicAttributes();

            // Create the objectclass to add
            Attribute objClasses = new BasicAttribute("objectClass");
            Attribute description = new BasicAttribute(groupDisplayName);
            Attribute member = new BasicAttribute("member");
            objClasses.add("top");
            objClasses.add(groupObjectClass);              //"groupOfNames" - OpenLDAP
            description.add(groupName);
            member.add(GROUPS_OU);

            Attribute cn = new BasicAttribute("cn", groupName);

            // Add these to the container
            container.put(objClasses);
            container.put(member);
            container.put(description);
            container.put(cn);

            Context newGroup = ctx.createSubcontext(getGroupDN(groupName), container);
            logger.info("Group successfully created: " + groupName);
            ctx.close();
        }
        catch (Throwable e)
        {
            logger.error(e);
        }
    }

    /**
     * Delete group in Active Directory on 172.30.40.61
     *
     * @param groupName
     */
    public static void deleteGroup(String groupName)
    {
        try
        {
            ctx = new InitialLdapContext(env, null);
            ctx.destroySubcontext(getGroupDN(groupName));
            logger.info("Group successfully deleted: " + groupName);
            ctx.close();
        }
        catch (Throwable e)
        {
            logger.error(e);
        }
    }

    /**
     * Add user to group in Active Directory on 172.30.40.61
     *
     * @param username
     * @param groupName
     */
    public static void addUserToGroup(String username, String groupName)
    {
        try
        {
            ctx = new InitialLdapContext(env, null);
            ModificationItem[] mods = new ModificationItem[1];

            Attribute mod = new BasicAttribute("member", getUserDN(username));
            mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, mod);
            ctx.modifyAttributes(getGroupDN(groupName), mods);
            ctx.close();
        }
        catch (AttributeInUseException e)
        {
            logger.info("User has been already added to the group.");
        }
        catch (Throwable e)
        {
            logger.error(e);
        }
    }

    /**
     * Remove user from group in Active Directory on 172.30.40.61
     *
     * @param username
     * @param groupName
     */
    public static void removeUserFromGroup(String username, String groupName)
    {
        try
        {
            ctx = new InitialLdapContext(env, null);
            ModificationItem[] mods = new ModificationItem[1];

            Attribute mod = new BasicAttribute("member", getUserDN(username));
            mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, mod);
            ctx.modifyAttributes(getGroupDN(groupName), mods);
            ctx.close();
        }
        catch (AttributeInUseException e)
        {
            logger.info("User has been already added to the group.");
        }
        catch (Throwable e)
        {
            logger.error(e);
        }
    }

    /**
     * If your alfresco has synchronization with active directory then run sync.
     *
     */
    public static void runSynchronization()
    {
        try
        {
            JmxUtils.invokeAlfrescoServerProperty(jmxScheduleCron, "executeNow");
        }
        catch (Throwable e)
        {
            logger.error(e);
        }

    }

    private static String getGroupDN(String gropName)
    {
        return new StringBuffer()
                .append("cn=")
                .append(gropName)
                .append(",")
                .append(GROUPS_OU)
                .toString();
    }

    private static String getUserDN(String username)
    {
        return new StringBuffer()
                .append(userDN)
                .append(username)
                .append(",")
                .append(ldapBaseDN)
                .toString();
    }

    public static void importCert()
    {
        try
        {
            FileInputStream is = new FileInputStream(localTruststore);
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(is, certPass.toCharArray());
            String alias = "youralias";
            char[] password = certPass.toCharArray();
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream certstream = LdapUtil.class.getClassLoader().getResourceAsStream("client.crt");
            Certificate certs = cf.generateCertificate(certstream);
            ///
            File keystoreFile = new File(localTruststore);
            // Load the keystore contents
            FileInputStream in = new FileInputStream(keystoreFile);
            keystore.load(in, password);
            in.close();
            // Add the certificate
            keystore.setCertificateEntry(alias, certs);
            // Save the new keystore contents
            FileOutputStream out = new FileOutputStream(keystoreFile);
            keystore.store(out, password);
            out.close();
        }
        catch (Throwable e)
        {
            logger.error(e);
        }
    }

    /**
     * Return user count from alfresco sync.
     *
     * @return
     */
    public static int getUserCount()
    {
        int count = 0;
        NamingEnumeration results = null;
        try
        {
            dCtx = new InitialDirContext(env);
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            results = dCtx.search(userSearchBase, "(objectclass=person)", controls);
            while (results.hasMore())
            {
                count++;
            }
        }
        catch (Throwable e)
        {
            logger.error(e);
        }
        finally
        {
            if (results != null)
            {
                try
                {
                    results.close();
                }
                catch (Exception e)
                {
                    logger.info("Error : " + e);
                }
            }
            if (dCtx != null)
            {
                try
                {
                    dCtx.close();
                }
                catch (Exception e)
                {
                    logger.info("Error : " + e);
                }
            }
        }
        return count;
    }

    /**
     * Return groupCount from Sync
     *
     * @return
     */
    public static int getGroupCount()
    {
        int count = 0;
        NamingEnumeration results = null;
        try
        {
            dCtx = new InitialDirContext(env);
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            results = dCtx.search(GROUPS_OU, "(objectclass=group)", controls);
            while (results.hasMore())
            {
                count++;
            }
        }
        catch (Throwable e)
        {
            logger.error(e);
        }
        finally
        {
            if (results != null)
            {
                try
                {
                    results.close();
                }
                catch (Exception e)
                {
                    logger.info("Error : " + e);
                }
            }
            if (dCtx != null)
            {
                try
                {
                    dCtx.close();
                }
                catch (Exception e)
                {
                    logger.info("Error : " + e);
                }
            }
        }
        return count;
    }
}
