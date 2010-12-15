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
package org.alfresco.ibatis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.sql.Types;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

/**
 * TypeHandler for <tt>java.io.Serializable</tt> to <b>BLOB</b> types.
 * 
 * @author Derek Hulley
 * @since 3.2
 */
public class SerializableTypeHandlerCallback implements TypeHandlerCallback
{
    public static final int DEFAULT_SERIALIZABLE_TYPE = Types.LONGVARBINARY;
    private static volatile int serializableType = DEFAULT_SERIALIZABLE_TYPE;

    /**
     * @see Types
     */
    public static void setSerializableType(int serializableType)
    {
        SerializableTypeHandlerCallback.serializableType = serializableType;
    }

    /**
     * @return      Returns the SQL type to use for serializable columns
     */
    public static int getSerializableType()
    {
        return serializableType;
    }

    /**
     * @throws DeserializationException if the object could not be deserialized
     */
    public Object getResult(ResultGetter getter) throws SQLException
    {
        final Serializable ret;
        try
        {
            InputStream is = getter.getResultSet().getBinaryStream(getter.getColumnName());
            if (is == null || getter.wasNull())
            {
                return null;
            }
            // Get the stream and deserialize
            ObjectInputStream ois = new ObjectInputStream(is);
            Object obj = ois.readObject();
            // Success
            ret = (Serializable) obj;
        }
        catch (Throwable e)
        {
            throw new DeserializationException(e);
        }
        return ret;
    }

    public void setParameter(ParameterSetter setter, Object parameter) throws SQLException
    {
        if (parameter == null)
        {
            setter.setNull(SerializableTypeHandlerCallback.serializableType);
        }
        else
        {
            try
            {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(parameter);
                byte[] bytes = baos.toByteArray();
                ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                setter.setBinaryStream(bais, bytes.length);
            }
            catch (Throwable e)
            {
                throw new SerializationException(e);
            }
        }
    }

    /**
     * @return          Retruns the value given
     */
    public Object valueOf(String s)
    {
        return s;
    }
    
    /**
     * Marker exception to allow deserialization issues to be dealt with by calling code.
     * If this exception remains uncaught, it will be very difficult to find and rectify
     * the data issue.
     * 
     * @author Derek Hulley
     * @since 3.2
     */
    public static class DeserializationException extends RuntimeException
    {
        private static final long serialVersionUID = 4673487701048985340L;

        public DeserializationException(Throwable cause)
        {
            super(cause);
        }
    }
    
    /**
     * Marker exception to allow serialization issues to be dealt with by calling code.
     * Unlike with {@link DeserializationException deserialization}, it is not important
     * to handle this exception neatly.
     *   
     * @author Derek Hulley
     * @since 3.2
     */
    public static class SerializationException extends RuntimeException
    {
        private static final long serialVersionUID = 962957884262870228L;

        public SerializationException(Throwable cause)
        {
            super(cause);
        }
    }
}
