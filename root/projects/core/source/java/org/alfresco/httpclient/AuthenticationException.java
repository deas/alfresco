package org.alfresco.httpclient;

import org.apache.commons.httpclient.HttpMethod;

public class AuthenticationException extends Exception
{
	private HttpMethod method;

	public AuthenticationException(HttpMethod method)
	{
		this.method = method;
	}

	public HttpMethod getMethod()
	{
		return method;
	}

}
