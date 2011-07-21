package org.alfresco.util;

import java.util.Date;

import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

public class JMXUtils
{
	public static OpenType<?> getOpenType(Object o)
	{
		if(o instanceof Long)
		{
			return SimpleType.LONG;
		}
		else if(o instanceof String)
		{
			return SimpleType.STRING;
		}
		else if(o instanceof Date)
		{
			return SimpleType.DATE;
		}
		else if(o instanceof Integer)
		{
			return SimpleType.INTEGER;
		}
		else if(o instanceof Boolean)
		{
			return SimpleType.BOOLEAN;
		}
		else if(o instanceof Double)
		{
			return SimpleType.DOUBLE;
		}
		else if(o instanceof Float)
		{
			return SimpleType.FLOAT;
		}
		else
		{
			throw new IllegalArgumentException();
		}
	}
}
