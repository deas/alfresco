/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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

import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.mail.*;
import java.util.Properties;

/**
 * @author Aliaksei Boole
 */
public class MailUtil
{
    private static Store store;
    private static Folder inbox;
    private static Log logger = LogFactory.getLog(MailUtil.class);

    private static final String POP_MAIL_SERVER = "pop.yandex.com";
    private static final String SMTP_MAIL_SERVER = "smtp.gmail.com";
    private static final Integer SMTP_PORT = 587;
    private static final String BOTS_PASSWORD = "XUOhVLZ4QQjkLT9wNqoE";

    public static final String MAIL_OUTBOUND_ALFRESCO = "alfrescoautoqa@gmail.com";
    public static final String PASSWORD_OUTBOUND_ALFRESCO = "parkh0use";

    public static final String BASE_BOT_MAIL = "alfrescowebdrone.bot@yandex.com";
    public static final String BOT_MAIL_1 = "alfrescowebdrone.bot1@yandex.com";
    public static final String BOT_MAIL_2 = "alfrescowebdrone.bot2@yandex.com";
    public static final String BOT_MAIL_3 = "alfrescowebdrone.bot3@yandex.com";
    public static final String MAIL_BOT_BASE_NAME = "alfrescowebdrone.bot";


    private static final String JMX_OUTBOUND_OBJ_NAME = "Alfresco:Type=Configuration,Category=email,id1=outbound";
    private static final String SMTP_MAIL_HOST = "mail.host";
    private static final String SMTP_MAIL_PORT = "mail.port";
    private static final String SMTP_MAIL_PASSWORD = "mail.password";
    private static final String SMTP_MAIL_FROM = "mail.from.default";
    private static final String SMTP_MAIL_USERNAME = "mail.username";
    private static final String SMTP_MAIL_AUTH = "mail.smtp.auth";
    private static final String SMTP_TLS = "mail.smtp.starttls.enable";
    private static final String SMTP_STOP = "stop";
    private static final String SMTP_START = "start";


    private MailUtil()
    {
    }

    private static void connect(final String email)
    {
        try
        {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.host", POP_MAIL_SERVER);
            props.put("mail.smtp.port", "25");
            props.put("mail.store.protocol", "pop3");
            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator()
                    {
                        protected PasswordAuthentication getPasswordAuthentication()
                        {
                            return new PasswordAuthentication(email, BOTS_PASSWORD);
                        }
                    }
            );
            store = session.getStore();
            store.connect(POP_MAIL_SERVER, email, BOTS_PASSWORD);
        }
        catch (Exception e)
        {
            logger.error("Can't connect to email.", e);
            throw new PageException("Can't connect to email.", e);
        }
    }

    private static void close()
    {
        try
        {
            inbox.close(true);
            store.close();
        }
        catch (MessagingException e)
        {
            logger.error("Can't close connect to email.", e);
            throw new PageException("Can't close connect to email.", e);
        }
    }

    /**
     * Config outBound emails use default options or from config:
     * jmxrmiPort, jmxrmiUser, jmxrmiPassword and address from shareUrl(AbstractUtils)
     */
    public static void configOutBoundEmail()
    {
        JmxUtils.invokeAlfrescoServerProperty(JMX_OUTBOUND_OBJ_NAME, SMTP_STOP);
        JmxUtils.setAlfrescoServerProperty(JMX_OUTBOUND_OBJ_NAME, SMTP_MAIL_HOST, SMTP_MAIL_SERVER);
        JmxUtils.setAlfrescoServerProperty(JMX_OUTBOUND_OBJ_NAME, SMTP_MAIL_PORT, SMTP_PORT);
        JmxUtils.setAlfrescoServerProperty(JMX_OUTBOUND_OBJ_NAME, SMTP_MAIL_USERNAME, MAIL_OUTBOUND_ALFRESCO);
        JmxUtils.setAlfrescoServerProperty(JMX_OUTBOUND_OBJ_NAME, SMTP_MAIL_FROM, MAIL_OUTBOUND_ALFRESCO);
        JmxUtils.setAlfrescoServerProperty(JMX_OUTBOUND_OBJ_NAME, SMTP_MAIL_PASSWORD, PASSWORD_OUTBOUND_ALFRESCO);
        JmxUtils.setAlfrescoServerProperty(JMX_OUTBOUND_OBJ_NAME, SMTP_MAIL_AUTH, true);
        JmxUtils.setAlfrescoServerProperty(JMX_OUTBOUND_OBJ_NAME, SMTP_TLS, true);
        JmxUtils.invokeAlfrescoServerProperty(JMX_OUTBOUND_OBJ_NAME, SMTP_START);
    }

    /**
     * Check that mail get and present in mail box(after 60 sec waiting)
     * If has - deleted that mail(if once return true for mail - next return false)
     *
     * @param mailSubject
     * @return true is mail present in mail box.
     */
    public static boolean isMailPresent(String email, String mailSubject)
    {
        return !getMailAsString(email, mailSubject).isEmpty();
    }

    /**
     * Returned mail with title[mailSubject] content as String (after 60 sec waiting)
     * If mail with title not found return empty string.
     *
     * @param email
     * @param mailSubject
     * @return
     */
    public static String getMailAsString(String email, String mailSubject)
    {
        sleep();
        connect(email);
        try
        {
            inbox = store.getDefaultFolder().getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            Message[] msg = inbox.getMessages();
            for (int i = msg.length - 1; i >= 0; i--)
            {
                if (msg[i].getSubject().contains(mailSubject))
                {
                    msg[i].setFlag(Flags.Flag.SEEN, true);
                    msg[i].setFlag(Flags.Flag.DELETED, true);
                    return (String) msg[i].getContent();
                }
            }
            return "";
        }
        catch (Exception e)
        {
            logger.error("Can't check email.", e);
            throw new PageException("Can't check email.", e);
        }
        finally
        {
            close();
        }
    }

    private static void sleep()
    {
        try
        {
            Thread.sleep(60000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

}
