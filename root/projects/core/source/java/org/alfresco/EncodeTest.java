package org.alfresco;

public class EncodeTest
{

	public static void main(String[] args)
	{
		try
		{
			String s = "XAlfresco";
			byte[] bytes = s.getBytes("US-ASCII");
			System.out.println(new String(bytes, "US-ASCII"));
			System.out.println(new String(bytes, "UTF-8"));
			
			bytes = new byte[] {123, 34, 109, 111, 100, 101, 108, 115, 34, 58, 91, 93, 125};
			System.out.println(new String(bytes, "UTF-8"));                
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
