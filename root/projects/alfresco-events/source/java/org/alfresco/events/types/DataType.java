/*
 * Copyright 2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.events.types;



/**
 * 
 * @author sglover
 *
 */
public enum DataType
{
	Text()
	{
		public String toString()
		{
			return "d:text";
		}
	},
	Any()
	{
		public String toString()
		{
			return "d:any";
		}
	},
	Encrypted()
	{
		public String toString()
		{
			return "d:encryped";
		}
	},
	Content()
	{
		public String toString()
		{
			return "d:content";
		}
	},
	Mltext()
	{
		public String toString()
		{
			return "d:mltext";
		}
	},
	Int()
	{
		public String toString()
		{
			return "d:int";
		}
	},
	Long()
	{
		public String toString()
		{
			return "d:long";
		}
	},
	Float()
	{
		public String toString()
		{
			return "d:float";
		}
	},
	Double()
	{
		public String toString()
		{
			return "d:double";
		}
	},
	Date()
	{
		public String toString()
		{
			return "d:date";
		}
	},
	Datetime()
	{
		public String toString()
		{
			return "d:datetime";
		}
	},
	Boolean()
	{
		public String toString()
		{
			return "d:boolean";
		}
	},
	Qname()
	{
		public String toString()
		{
			return "d:text";
		}
	},
	Noderef()
	{
		public String toString()
		{
			return "d:noderef";
		}
	},
	Childassocref()
	{
		public String toString()
		{
			return "d:childassocref";
		}
	},
	Assocref()
	{
		public String toString()
		{
			return "d:assocref";
		}
	},
	Path()
	{
		public String toString()
		{
			return "d:path";
		}
	},
	Category()
	{
		public String toString()
		{
			return "d:category";
		}
	},
	Locale()
	{
		public String toString()
		{
			return "d:locale";
		}
	},
	Version()
	{
		public String toString()
		{
			return "d:version";
		}
	},
	Period()
	{
		public String toString()
		{
			return "d:period";
		}
	};
}
