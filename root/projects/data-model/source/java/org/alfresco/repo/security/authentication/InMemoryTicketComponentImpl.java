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
package org.alfresco.repo.security.authentication;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.CRC32;

import org.alfresco.repo.cache.SimpleCache;
import org.alfresco.service.cmr.repository.datatype.Duration;
import org.alfresco.util.GUID;
import org.apache.commons.codec.binary.Hex;
import org.safehaus.uuid.UUIDGenerator;

/**
 * Store tickets in memory. They can be distributed in a cluster via the cache
 * 
 * @author andyh
 */
public class InMemoryTicketComponentImpl implements TicketComponent
{
    /**
     * Ticket prefix
     */
    public static final String GRANTED_AUTHORITY_TICKET_PREFIX = "TICKET_";

    private static ThreadLocal<String> currentTicket = new ThreadLocal<String>();

    private boolean ticketsExpire;

    private Duration validDuration;

    private boolean oneOff;
    
    private String guid;

    private SimpleCache<String, Ticket> ticketsCache; // Can't use Ticket as it's private
    
    private ExpiryMode expiryMode = ExpiryMode.AFTER_FIXED_TIME;

    /**
     * IOC constructor
     */
    public InMemoryTicketComponentImpl()
    {
        super();
        guid = GUID.generate();
    }

    /**
     * Set the ticket cache to support clustering
     * 
     * @param ticketsCache
     */
    public void setTicketsCache(SimpleCache<String, Ticket> ticketsCache)
    {
        this.ticketsCache = ticketsCache;
    }

    public String getNewTicket(String userName) throws AuthenticationException
    {
        Date expiryDate = null;
        if (ticketsExpire)
        {
            expiryDate = Duration.add(new Date(), validDuration);
        }
        Ticket ticket = new Ticket(ticketsExpire ? expiryMode : ExpiryMode.DO_NOT_EXPIRE, expiryDate, userName, validDuration);
        ticketsCache.put(ticket.getTicketId(), ticket);
        String ticketString = GRANTED_AUTHORITY_TICKET_PREFIX + ticket.getTicketId();
        currentTicket.set(ticketString);
        return ticketString;
    }

    public String validateTicket(String ticketString) throws AuthenticationException
    {
        String ticketKey = getTicketKey(ticketString); 
        Ticket ticket = ticketsCache.get(ticketKey);
        if (ticket == null)
        {
            throw new AuthenticationException("Missing ticket for " + ticketString);
        }
        Ticket newTicket = ticket.getNewEntry();
        if (newTicket == null)
        {
            throw new TicketExpiredException("Ticket expired for " + ticketString);
        }
        // TODO: Recheck the user details here
        // TODO: Strengthen ticket as GUID is predicatble
        if (oneOff)
        {
            ticketsCache.remove(ticketKey);
        }
        else if (newTicket != ticket)
        {
            ticketsCache.put(ticketKey, newTicket);
        }
        currentTicket.set(ticketString);
        return newTicket.getUserName();
    }

    /**
     * Helper method to find a ticket
     * 
     * @param ticketString
     * @return - the ticket
     */
    private Ticket getTicketByTicketString(String ticketString)
    {
        Ticket ticket = ticketsCache.get(getTicketKey(ticketString));
        return ticket;
    }

    /**
     * Helper method to extract the ticket id from the ticket string
     * 
     * @param ticketString
     * @return - the ticket key
     */
    private String getTicketKey(String ticketString)
    {
        if (ticketString == null)
        {
            return null;
        }
        else if (ticketString.length() < GRANTED_AUTHORITY_TICKET_PREFIX.length())
        {
            throw new AuthenticationException(ticketString + " is an invalid ticket format");
        }
       
        String key = ticketString.substring(GRANTED_AUTHORITY_TICKET_PREFIX.length());
        return key;
    }

    public void invalidateTicketById(String ticketString)
    {
        String key = ticketString.substring(GRANTED_AUTHORITY_TICKET_PREFIX.length());
        ticketsCache.remove(key);
    }
    
    /*
     * (non-Javadoc)
     * @see org.alfresco.repo.security.authentication.TicketComponent#getUsersWithTickets(boolean)
     */
    public Set<String> getUsersWithTickets(boolean nonExpiredOnly) 
    {
    	Set<String> users = new HashSet<String>();
    	for (String key : ticketsCache.getKeys())
        {
    		Ticket ticket = ticketsCache.get(key);
    		if (ticket != null)
    		{
        		if ((nonExpiredOnly == false) || (ticket.getNewEntry() != null))
        		{
        			users.add(ticket.getUserName());
        		}
    		}
        }
    	return users;
    }
    
    /*
     * (non-Javadoc)
     * @see org.alfresco.repo.security.authentication.TicketComponent#countTickets(boolean)
     */
    public int countTickets(boolean nonExpiredOnly)
    {
    	if (nonExpiredOnly)
    	{
    		int count = 0;
    		for (String key : ticketsCache.getKeys())
            {
    			Ticket ticket = ticketsCache.get(key);
    			if (ticket != null && ticket.getNewEntry() != null)
    			{
    				count++;
    			}
            }
    		return count;
    	}
    	else
    	{
    		return ticketsCache.getKeys().size();
    	}
    }
    
    /*
     * (non-Javadoc)
     * @see org.alfresco.repo.security.authentication.TicketComponent#invalidateTickets(boolean)
     */
    public int invalidateTickets(boolean expiredOnly)
    {
    	int count = 0;
    	if (! expiredOnly)
    	{
    		count = ticketsCache.getKeys().size();
    		ticketsCache.clear();
    	}
    	else
    	{
	    	for (String key : ticketsCache.getKeys())
	        {
	    		Ticket ticket = ticketsCache.get(key);
	    		if (ticket == null || ticket.getNewEntry() == null)
	    		{
	    			count++;
	    			ticketsCache.remove(key);
	    		}
	        }
    	}
    	return count;
    }

    public void invalidateTicketByUser(String userName)
    {
        Set<String> toRemove = new HashSet<String>();

        for (String key : ticketsCache.getKeys())
        {
            Ticket ticket = ticketsCache.get(key);
            // Hack: The getKeys() call might return keys for null marker objects, yielding null values
            if (ticket == null)
            {
                continue;
            }
            if (ticket.getUserName().equals(userName))
            {
                toRemove.add(ticket.getTicketId());
            }
        }

        for (String id : toRemove)
        {
            ticketsCache.remove(id);
        }
    }


    @Override
    public int hashCode()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((guid == null) ? 0 : guid.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final InMemoryTicketComponentImpl other = (InMemoryTicketComponentImpl) obj;
        if (guid == null)
        {
            if (other.guid != null)
                return false;
        }
        else if (!guid.equals(other.guid))
            return false;
        return true;
    }
    
    
    /**
     * Ticket
     * 
     * @author andyh
     */
    public static class Ticket implements Serializable
    {
        private static final long serialVersionUID = -5904510560161261049L;

        private final ExpiryMode expires;

        private final Date expiryDate;

        private final String userName;

        private final String ticketId;

        private final String guid;
        
        private final Duration validDuration;

        Ticket(ExpiryMode expires, Date expiryDate, String userName, Duration validDuration)
        {
            this.expires = expires;
            this.expiryDate = expiryDate;
            this.userName = userName;
            this.validDuration = validDuration;
            this.guid = UUIDGenerator.getInstance().generateRandomBasedUUID().toString();

            String encode = (expires.toString()) + ((expiryDate == null) ? new Date().toString() : expiryDate.toString()) + userName + guid;
            MessageDigest digester;
            String ticketId;
            try
            {
                digester = MessageDigest.getInstance("SHA-1");
                ticketId = new String(Hex.encodeHex(digester.digest(encode.getBytes())));
            }
            catch (NoSuchAlgorithmException e)
            {
                try
                {
                    digester = MessageDigest.getInstance("MD5");
                    ticketId = new String(Hex.encodeHex(digester.digest(encode.getBytes())));
                }
                catch (NoSuchAlgorithmException e1)
                {
                    CRC32 crc = new CRC32();
                    crc.update(encode.getBytes());
                    byte[] bytes = new byte[4];
                    long value = crc.getValue();
                    bytes[0] = (byte) (value & 0xFF);
                    value >>>= 4;
                    bytes[1] = (byte) (value & 0xFF);
                    value >>>= 4;
                    bytes[2] = (byte) (value & 0xFF);
                    value >>>= 4;
                    bytes[3] = (byte) (value & 0xFF);
                    ticketId = new String(Hex.encodeHex(bytes));
                }
            }
            this.ticketId = ticketId;
        }

        Ticket getNewEntry()
        {
            switch (expires)
            {
                case AFTER_FIXED_TIME:
                    if ((expiryDate != null) && (expiryDate.compareTo(new Date()) < 0))
                    {
                        return null;
                    }
                    else
                    {
                        return this;
                    }
                    
                case AFTER_INACTIVITY:
                    Date now = new Date();
                    if ((expiryDate != null) && (expiryDate.compareTo(now) < 0))
                    {
                        return null;
                    }
                    else
                    {
                        return new Ticket(expires, Duration.add(now, validDuration), userName, validDuration);
                    }
                    
                case DO_NOT_EXPIRE:
                default:
                    return this;
            }            
        }

        public boolean equals(Object o)
        {
            if (o == this)
            {
                return true;
            }
            if (!(o instanceof Ticket))
            {
                return false;
            }
            Ticket t = (Ticket) o;
            return (this.expires == t.expires) && this.expiryDate.equals(t.expiryDate) && this.userName.equals(t.userName) && this.ticketId.equals(t.ticketId);
        }

        public int hashCode()
        {
            return ticketId.hashCode();
        }

        protected ExpiryMode getExpires()
        {
            return expires;
        }

        protected Date getExpiryDate()
        {
            return expiryDate;
        }

        protected String getTicketId()
        {
            return ticketId;
        }

        protected String getUserName()
        {
            return userName;
        }

    }

    /**
     * Are tickets single use
     * 
     * @param oneOff
     */
    public void setOneOff(boolean oneOff)
    {
        this.oneOff = oneOff;
    }

    /**
     * Do tickets expire
     * 
     * @param ticketsExpire
     */
    public void setTicketsExpire(boolean ticketsExpire)
    {
        this.ticketsExpire = ticketsExpire;
    }

    
    /**
     * How should tickets expire.
     * @param exipryMode
     */
    public void setExpiryMode(String expiryMode)
    {
        this.expiryMode = ExpiryMode.valueOf(expiryMode);
    }

    /**
     * How long are tickets valid (XML duration as a string)
     * 
     * @param validDuration
     */
    public void setValidDuration(String validDuration)
    {
        this.validDuration = new Duration(validDuration);
    }

    public String getAuthorityForTicket(String ticketString)
    {
        Ticket ticket = getTicketByTicketString(ticketString);
        if (ticket == null)
        {
            return null;
        }
        return ticket.getUserName();
    }

    public String getCurrentTicket(String userName, boolean autoCreate)
    {
        String ticket = currentTicket.get();
        if (ticket == null)
        {
            return autoCreate ? getNewTicket(userName) : null;
        }
        String ticketUser = getAuthorityForTicket(ticket);
        if (userName.equals(ticketUser))
        {
            return ticket;
        }
        else
        {
            return autoCreate ? getNewTicket(userName) : null;
        }
    }

    public void clearCurrentTicket()
    {
        clearCurrentSecurityContext();
    }

    public static void clearCurrentSecurityContext()
    {
        currentTicket.set(null);
    }

    public enum ExpiryMode
    {
        AFTER_INACTIVITY, AFTER_FIXED_TIME, DO_NOT_EXPIRE;
    }
}
