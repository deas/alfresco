/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.extensions.webscripts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Element;


/**
 * Implementation of a Web Script Description
 * 
 * @author davidc
 */
public class DescriptionImpl extends AbstractBaseDescriptionDocument implements Description 
{
    // required root element name
    public static final String ROOT_ELEMENT_NAME = "webscript";

    // name pattern of schema description document
    public static final String DESC_NAME_POSTFIX ="desc.xml";

    // path pattern of schema description document
    public static final String DESC_NAME_PATTERN ="*."+DESC_NAME_POSTFIX;

    private String scriptPath;
    private Path scriptPackage;
    private String kind;
    private Lifecycle lifecycle;
    private Set<String> familys;
    private RequiredAuthentication requiredAuthentication;
    private String runAs;
    private RequiredTransactionParameters transactionParameters;
    private RequiredCache requiredCache;
    private FormatStyle formatStyle;
    private String httpMethod;
    private String[] uris;
    private String defaultFormat;
    private NegotiatedFormat[] negotiatedFormats;
    private Map<String, Serializable> extensions;
    private boolean multipartProcessing;


    private ArgumentTypeDescription[] arguments;
    private TypeDescription[] requestTypes;
    private TypeDescription[] responseTypes;

    /**
     * @return the arguments
     */
    public ArgumentTypeDescription[] getArguments() 
    {
        return arguments;
    }

    /**
     * @param arguments the arguments to set
     */
    public void setArguments(ArgumentTypeDescription[] arguments) 
    {
        this.arguments = arguments;
    }

    /**
     * @return the requestTypes
     */
    public TypeDescription[] getRequestTypes() 
    {
        return requestTypes;
    }

    /**
     * @param requestTypes the requestTypes to set
     */
    public void setRequestTypes(TypeDescription[] requestTypes) 
    {
        this.requestTypes = requestTypes;
    }

    /**
     * @return the responseTypes
     */
    public TypeDescription[] getResponseTypes() 
    {
        return responseTypes;
    }

    /**
     * @param responseTypes the responseTypes to set
     */
    public void setResponseTypes(TypeDescription[] responseTypes) 
    {
        this.responseTypes = responseTypes;
    }

    /**
     * Sets the script path
     * 
     * @param scriptPath
     */
    public void setScriptPath(String scriptPath)
    {
        this.scriptPath = scriptPath;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Description#getScriptPath()
     */
    public String getScriptPath()
    {
        return scriptPath;
    }

    /**
     * Sets the Package  (path version of getScriptPath)
     * 
     * @param package
     */
    public void setPackage(Path scriptPackage)
    {
        this.scriptPackage = scriptPackage;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Description#getPackage()
     */
    public Path getPackage()
    {
        return scriptPackage;
    }

    /**
     * Sets the service kind
     * 
     * @param kind
     */
    public void setKind(String kind)
    {
        this.kind = kind;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Description#getKind()
     */
    public String getKind()
    {
        return this.kind;
    }

    /**
     * @param family the family to set
     */
    public void setFamilys(Set<String> familys)
    {
        this.familys = familys;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Description#getFamily()
     */
    public Set<String> getFamilys()
    {
        return this.familys;
    }

    /**
     * Sets the required level of authentication
     * 
     * @param requiredAuthentication
     */
    public void setRequiredAuthentication(RequiredAuthentication requiredAuthentication)
    {
        this.requiredAuthentication = requiredAuthentication;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Description#getRequiredAuthentication()
     */
    public RequiredAuthentication getRequiredAuthentication()
    {
        return this.requiredAuthentication;
    }

    /**
     * Sets the ID of the user that the service should be run as. If not set, the service run as the authenticated user.
     * 
     * @param runAs
     *            a user name
     */
    public void setRunAs(String runAs)
    {
        this.runAs = runAs;
    }        

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Description#getRunAs()
     */
    public String getRunAs()
    {
        return this.runAs;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Description#getRequiredTransaction()
     */
    public RequiredTransaction getRequiredTransaction()
    {
        return this.transactionParameters.getRequired();
    }

    /**
     * Sets the transaction parameters
     * 
     * @param transactionParameters
     */
    public void setRequiredTransactionParameters(RequiredTransactionParameters transactionParameters)
    {
        this.transactionParameters = transactionParameters;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.web.scripts.Description#getRequiredTransactionParameters()
     */
    public RequiredTransactionParameters getRequiredTransactionParameters()
    {
        return this.transactionParameters;
    }
    
    /**
     * Sets the required cache
     * 
     * @param requiredCache
     */
    public void setRequiredCache(RequiredCache requiredCache)
    {
        this.requiredCache = requiredCache;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Description#getRequiredCache()
     */
    public RequiredCache getRequiredCache()
    {
        return this.requiredCache;
    }
    
    /**
     * Sets the format style
     * 
     * @param formatStyle
     */
    public void setFormatStyle(FormatStyle formatStyle)
    {
        this.formatStyle = formatStyle;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Description#getFormatStyle()
     */
    public FormatStyle getFormatStyle()
    {
        return this.formatStyle;
    }
    
    /**
     * Sets the service http method
     * 
     * @param httpMethod
     */
    public void setMethod(String httpMethod)
    {
        this.httpMethod = httpMethod;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Description#getMethod()
     */
    public String getMethod()
    {
        return this.httpMethod;
    }

    /**
     * Sets the service URIs
     * 
     * @param uris
     */
    public void setUris(String[] uris)
    {
        this.uris = uris;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Description#getURIs()
     */
    public String[] getURIs()
    {
        return this.uris;
    }

    /**
     * Sets the default response format
     * 
     * @param defaultFormat
     */
    public void setDefaultFormat(String defaultFormat)
    {
        this.defaultFormat = defaultFormat;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Description#getDefaultFormat()
     */
    public String getDefaultFormat()
    {
        return this.defaultFormat;
    }

    /**
     * Sets the negotiated formats
     * 
     * @param defaultFormat
     */
    public void setNegotiatedFormats(NegotiatedFormat[] negotiatedFormats)
    {
        this.negotiatedFormats = negotiatedFormats;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Description#getNegotiatedFormats()
     */
    public NegotiatedFormat[] getNegotiatedFormats()
    {
        return this.negotiatedFormats;
    }

    /**
     * Sets Web Script custom extensions
     * 
     * @param extensions
     */
    public void setExtensions(Map<String, Serializable> extensions)
    {
        this.extensions = extensions;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Description#getExtensions()
     */
    public Map<String, Serializable> getExtensions()
    {
        return extensions;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Description#getLifecycle()
     */
    public Lifecycle getLifecycle() 
    {
        return lifecycle;
    }

    /**
     * Sets the lifecycle
     * 
     * @param lifecycle
     */
    public void setLifecycle(Lifecycle lifecycle)
    {
        this.lifecycle = lifecycle;
    }

    /**
     * @return true if automatic multipart formdata processes is enabled
     */
    public boolean getMultipartProcessing()
    {
        return this.multipartProcessing;
    }

    /**
     * @param multipartProcessing the multipartProcessing to set
     */
    public void setMultipartProcessing(boolean multipartProcessing)
    {
        this.multipartProcessing = multipartProcessing;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.document.AbstractBaseDescription#parse(org.dom4j.Element)
     */
    @SuppressWarnings("unchecked")
    public void parse(Element elem) 
    {
        if (this.validateRootElement(elem, ROOT_ELEMENT_NAME)) 
        {
            super.parse(elem);

            // validate short name
            String shortName = this.getShortName();
            if (shortName == null || shortName.length() == 0) 
            {
                throw new WebScriptException("Expected <shortname> value");                
            }
            
            // retrieve kind
            String kind = null;
            String attrKindValue = elem.attributeValue("kind");
            if (attrKindValue != null)
            {
                kind = attrKindValue.trim();
            }

            // retrieve family[]
            Set<String> familys = new TreeSet<String>();
            List<Element> familyElements = elem.elements("family");
            if (familyElements == null || familyElements.size() > 0)
            {
                Iterator<Element> iterFamily = familyElements.iterator();
                while(iterFamily.hasNext())
                {
                    // retrieve family element
                    Element familyElement = (Element)iterFamily.next();
                    String family = familyElement.getTextTrim();
                    familys.add(family);
                }
            }

            // retrieve urls
            List urlElements = elem.elements("url");
            if (urlElements == null || urlElements.size() == 0)
            {
                throw new WebScriptException("Expected at least one <url> element");
            }
            List<String> uris = new ArrayList<String>(4);
            Iterator iterElements = urlElements.iterator();
            while(iterElements.hasNext())
            {
                // retrieve url element
                Element urlElement = (Element)iterElements.next();

                // retrieve url template
                String template = urlElement.getTextTrim();
                if (template == null || template.length() == 0)
                {
                    // NOTE: for backwards compatibility only
                    template = urlElement.attributeValue("template");
                    if (template == null || template.length() == 0)
                    {
                        throw new WebScriptException("Expected <url> element value");
                    }
                }
                uris.add(template);
            }

            // retrieve authentication
            RequiredAuthentication reqAuth = RequiredAuthentication.none;
            String runAs = null;
            Element authElement = elem.element("authentication");
            if (authElement != null)
            {
                String reqAuthStr = authElement.getTextTrim();
                if (reqAuthStr == null || reqAuthStr.length() == 0)
                {
                    throw new WebScriptException("Expected <authentication> value");
                }
                try
                {
                    reqAuth = RequiredAuthentication.valueOf(reqAuthStr);
                }
                catch (IllegalArgumentException e)
                {
                    throw new WebScriptException("Authentication '" + reqAuthStr + "' is not a valid value");
                }
                String runAsStr = authElement.attributeValue("runas");
                if (runAsStr != null)
                {
                    runAsStr = runAsStr.trim();
                    if (runAsStr.length() != 0)
                    {
                        if (!this.getStore().isSecure())
                        {
                            throw new WebScriptException("runas user declared for script in insecure store");
                        }
                        runAs = runAsStr;
                    }
                }
            }

            // retrieve transaction
            TransactionParameters trxParams = new TransactionParameters();
            RequiredTransaction reqTrx = (reqAuth == RequiredAuthentication.none) ? RequiredTransaction.none : RequiredTransaction.required;
            TransactionCapability trxCapability = TransactionCapability.readwrite;
            int bufferSize = 4096;
            Element trxElement = elem.element("transaction");
            if (trxElement != null)
            {
                // requires...
                String reqTrxStr = trxElement.getTextTrim();
                if (reqTrxStr != null && reqTrxStr.length() > 0)
                {
                    try
                    {
                        reqTrx = RequiredTransaction.valueOf(reqTrxStr);
                    }
                    catch (IllegalArgumentException e)
                    {
                        throw new WebScriptException("Transaction '" + reqTrxStr + "' is not a valid value");
                    }
                }

                // capability...
                String capabilityStr = trxElement.attributeValue("allow");
                if (capabilityStr != null)
                {
                    try
                    {
                        trxCapability = TransactionCapability.valueOf(capabilityStr);
                    }
                    catch (IllegalArgumentException e)
                    {
                        throw new WebScriptException("Transaction allow '" + capabilityStr + "' is not a valid value");
                    }
                }

                // buffer size
                String bufferSizeStr = trxElement.attributeValue("buffersize");
                if (bufferSizeStr != null)
                {
                    try
                    {
                        bufferSize = new Integer(bufferSizeStr);
                    }
                    catch(NumberFormatException e)
                    {
                        throw new WebScriptException("Buffer size '" + bufferSizeStr + "' is not a valid integer");
                    }
                }
            }
            trxParams.setRequired(reqTrx);
            trxParams.setCapability(trxCapability);
            trxParams.setBufferSize(bufferSize);

            // retrieve lifecycle
            Lifecycle lifecycle = Lifecycle.none;
            Element lifecycleElement = elem.element("lifecycle");
            if (lifecycleElement != null)
            {
                String reqLifeStr = lifecycleElement.getTextTrim();
                if (reqLifeStr == null || reqLifeStr.length() == 0)
                {
                    throw new WebScriptException("Expected <lifecycle> value");
                }
                try
                {
                    lifecycle = Lifecycle.valueOf(reqLifeStr);
                }
                catch (IllegalArgumentException e)
                {
                    throw new WebScriptException("Lifecycle '" + reqLifeStr + "' is not a valid value");
                }
            }

            // retrieve format
            String defaultFormat = "html";
            String defaultFormatMimetype = null; 
            FormatStyle formatStyle = FormatStyle.any;
            Element formatElement = elem.element("format");
            if (formatElement != null)
            {
                // establish if default is set explicitly
                String attrDefaultValue = formatElement.attributeValue("default");
                if (attrDefaultValue != null)
                {
                    defaultFormat = (attrDefaultValue.length() == 0) ? null : attrDefaultValue;
                }
                // establish format declaration style
                String formatStyleStr = formatElement.getTextTrim();
                if (formatStyleStr != null && formatStyleStr.length() > 0)
                {
                    try
                    {
                        formatStyle = FormatStyle.valueOf(formatStyleStr);
                    }
                    catch (IllegalArgumentException e)
                    {
                        throw new WebScriptException("Format Style '" + formatStyle + "' is not a valid value");
                    }
                }
            }

            // retrieve negotiation
            NegotiatedFormat[] negotiatedFormats = null;
            List negotiateElements = elem.elements("negotiate");
            if (negotiateElements.size() > 0)
            {
                negotiatedFormats = new NegotiatedFormat[negotiateElements.size() + (defaultFormatMimetype == null ? 0 : 1)];
                int iNegotiate = 0;
                Iterator iterNegotiateElements = negotiateElements.iterator();
                while(iterNegotiateElements.hasNext())
                {
                    Element negotiateElement = (Element)iterNegotiateElements.next();
                    String accept = negotiateElement.attributeValue("accept");
                    if (accept == null || accept.length() == 0)
                    {
                        throw new WebScriptException("Expected 'accept' attribute on <negotiate> element");
                    }
                    String format = negotiateElement.getTextTrim();
                    if (format == null || format.length() == 0)
                    {
                        throw new WebScriptException("Expected <negotiate> value");
                    }
                    negotiatedFormats[iNegotiate++] = new NegotiatedFormat(new MediaType(accept), format);
                }
                if (defaultFormatMimetype != null)
                {
                    negotiatedFormats[iNegotiate++] = new NegotiatedFormat(new MediaType(defaultFormatMimetype), defaultFormat);
                }
            }

            // retrieve caching
            Cache cache = new Cache();
            Element cacheElement = elem.element("cache");
            if (cacheElement != null)
            {
                Element neverElement = cacheElement.element("never");
                if (neverElement != null)
                {
                    String neverStr = neverElement.getTextTrim();
                    boolean neverBool = (neverStr == null || neverStr.length() == 0) ? true : Boolean.valueOf(neverStr);
                    cache.setNeverCache(neverBool);
                }
                Element publicElement = cacheElement.element("public");
                if (publicElement != null)
                {
                    String publicStr = publicElement.getTextTrim();
                    boolean publicBool = (publicStr == null || publicStr.length() == 0) ? true : Boolean.valueOf(publicStr);
                    cache.setIsPublic(publicBool);
                }
                Element revalidateElement = cacheElement.element("mustrevalidate");
                if (revalidateElement != null)
                {
                    String revalidateStr = revalidateElement.getTextTrim();
                    boolean revalidateBool = (revalidateStr == null || revalidateStr.length() == 0) ? true : Boolean.valueOf(revalidateStr);
                    cache.setMustRevalidate(revalidateBool);
                }
            }

            // retrieve formdata multipart processing setting
            boolean multipartProcessing = true;
            Element formdataElement = elem.element("formdata");
            if (formdataElement != null)
            {
                String strProcessing = formdataElement.attributeValue("multipart-processing");
                if (strProcessing == null || strProcessing.length() == 0)
                {
                    throw new WebScriptException("Expected 'multipart-processing' attribute on <formdata> value");
                }
                multipartProcessing = Boolean.parseBoolean(strProcessing);
            }

            // retrieve arguments
            ArgumentTypeDescription[] arguments = null;            
            Element argsElement = elem.element("args");            
            if ( argsElement != null ) 
            {
                List<Element> argElements = argsElement.elements("arg");
                arguments = new ArgumentTypeDescription[argElements.size()];
                int iArg = 0;               
                Iterator<Element> iterArgElements = argElements.iterator();              
                while (iterArgElements.hasNext()) 
                {
                    Element argElement = iterArgElements.next();
                    ArgumentTypeDescription argument = new ArgumentTypeDescription();                   
                    argument.parse(argElement);
                    argument.setRequired(true);
                    Pattern p = Pattern.compile(argument.getShortName()+"=\\{[a-zA-Z0-9]*\\?\\}");
                    for (String uriStr:uris) 
                    {
                        Matcher m = p.matcher(uriStr);
                        if (m.find()) 
                        {
                            argument.setRequired(false);
                            continue;
                        }
                    }
                    arguments[iArg++] = argument;
                }               
            }

            // retrieve request formats
            TypeDescription[] requestTypes = null;
            Element requestsElement = elem.element("requests");
            if (requestsElement != null) 
            {
                List<Element> requestElements = requestsElement.elements("request");
                requestTypes = new TypeDescription[requestElements.size()];
                int iRequest = 0;
                Iterator<Element> iterRequestElements = requestElements.iterator();
                while (iterRequestElements.hasNext()) 
                {
                    Element requestElement = iterRequestElements.next();
                    // check if it uses type reference
                    if (requestElement.attribute(TypeDescription.ROOT_ELEMENT_NAME) != null) 
                    {
                        String refTypeId = requestElement.attribute(TypeDescription.ROOT_ELEMENT_NAME).getText();
                        TypeDescription requestType = new TypeDescription();
                        requestType.setId(refTypeId);
                        requestTypes[iRequest++] = requestType;
                    } 
                    else 
                    {
                        if (requestElement.element(TypeDescription.ROOT_ELEMENT_NAME) != null) 
                        {
                            TypeDescription requestType = new TypeDescription();
                            requestType.parse(requestElement.element(TypeDescription.ROOT_ELEMENT_NAME));
                            requestTypes[iRequest++] = requestType;
                        }
                    }
                }
            }

            // retrieve response formats
            TypeDescription[] responseTypes = null;
            Element responsesElement = elem.element("responses");
            if (responsesElement != null) 
            {
                List<Element> responseElements = responsesElement.elements("response");
                responseTypes = new TypeDescription[responseElements.size()];
                int iResponse = 0;
                Iterator<Element> iterresponseElements = responseElements.iterator();
                while (iterresponseElements.hasNext()) 
                {
                    Element responseElement = iterresponseElements.next();
                    // check if it uses type reference
                    if (responseElement.attribute(TypeDescription.ROOT_ELEMENT_NAME) != null) 
                    {
                        String refTypeId = responseElement.attribute(TypeDescription.ROOT_ELEMENT_NAME).getText();
                        TypeDescription responseType = new TypeDescription();
                        responseType.setId(refTypeId);
                        responseTypes[iResponse++] = responseType;
                    } 
                    else 
                    {
                        if (responseElement.element(TypeDescription.ROOT_ELEMENT_NAME) != null) 
                        {
                            TypeDescription responseType = new TypeDescription();
                            responseType.parse(responseElement.element(TypeDescription.ROOT_ELEMENT_NAME));
                            responseTypes[iResponse++] = responseType;
                        }
                    }
                }
            }
            this.setScriptPath(scriptPath);
            this.setKind(kind);
            this.setLifecycle(lifecycle);
            this.setShortName(shortName);
            this.setFamilys(familys);
            this.setRequiredAuthentication(reqAuth);
            this.setRunAs(runAs);
            this.setRequiredTransactionParameters(trxParams);
            this.setRequiredCache(cache);
            this.setUris(uris.toArray(new String[uris.size()]));
            this.setDefaultFormat(defaultFormat);
            this.setNegotiatedFormats(negotiatedFormats);
            this.setFormatStyle(formatStyle);
            this.setMultipartProcessing(multipartProcessing);

            this.setArguments(arguments);
            this.setRequestTypes(requestTypes);
            this.setResponseTypes(responseTypes);
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append(AbstractBaseDescriptionDocument.COMMON_XML_HEADER).append(AbstractBaseDescriptionDocument.NEW_LINE);
        sb.append("<").append(DescriptionImpl.ROOT_ELEMENT_NAME).append(" ").append(AbstractBaseDescriptionDocument.COMMON_XML_NS);
        if (this.getKind() != null && !this.getKind().equals(""))
        {
            sb.append(" ").append("kind=\"").append(this.getKind()).append("\"");
        }
        sb.append(">").append(AbstractBaseDescriptionDocument.NEW_LINE);
        sb.append(AbstractBaseDescriptionDocument.TAB).append("<shortname>").append(this.getShortName()).append("</shortname>").append(AbstractBaseDescriptionDocument.NEW_LINE);
        sb.append(AbstractBaseDescriptionDocument.TAB).append("<description>").append(this.getDescription()).append("</description>").append(AbstractBaseDescriptionDocument.NEW_LINE);
        for (String url: this.getURIs())
        {
            sb.append(AbstractBaseDescriptionDocument.TAB).append("<url>").append(url).append("</url>").append(AbstractBaseDescriptionDocument.NEW_LINE);            
        }
        if (this.getFormatStyle() != null)
        {
            sb.append(AbstractBaseDescriptionDocument.TAB).append("<format");
            if (this.getDefaultFormat() != null && !this.getDefaultFormat().equals(""))
            {
                sb.append(" ").append("default=\"").append(this.getDefaultFormat()).append("\"");
            }
            sb.append(">").append(this.getFormatStyle().toString()).append("</format>").append(AbstractBaseDescriptionDocument.NEW_LINE);                        
        }
        if (this.getLifecycle() != null)
        {
            sb.append(AbstractBaseDescriptionDocument.TAB).append("<lifecycle>").append(this.getLifecycle().toString()).append("</lifecycle>").append(AbstractBaseDescriptionDocument.NEW_LINE); 
        }
        if (this.getRequiredAuthentication() != null)
        {
            sb.append(AbstractBaseDescriptionDocument.TAB).append("<authentication");
            if (this.getRunAs() != null && !this.getRunAs().equals(""))
            {
                sb.append(" ").append("runas=\"").append(this.getRunAs()).append("\"");
            }
            sb.append(">").append(this.getRequiredAuthentication().toString()).append("</authentication>").append(AbstractBaseDescriptionDocument.NEW_LINE);  
        }
        if (this.getRequiredTransactionParameters() != null)
        {
            sb.append(AbstractBaseDescriptionDocument.TAB).append("<transaction");
            if (this.getRequiredTransactionParameters() != null && this.getRequiredTransactionParameters().getCapability() != null)
            {
                sb.append(" ").append("allow=\"").append(this.getRequiredTransactionParameters().getCapability().toString()).append("\"");
            }
            if (this.getRequiredTransactionParameters() != null && this.getRequiredTransactionParameters().getBufferSize() != 4096)
            {
                sb.append(" ").append("buffersize=\"").append(this.getRequiredTransactionParameters().getBufferSize()).append("\"");
            }
            sb.append(">").append(this.getRequiredTransaction().toString()).append("</transaction>").append(AbstractBaseDescriptionDocument.NEW_LINE); 
        }
        if (this.getFamilys() != null)
        {
            for (String family : this.getFamilys())
            {
                sb.append(AbstractBaseDescriptionDocument.TAB).append("<family>").append(family).append("</family>").append(AbstractBaseDescriptionDocument.NEW_LINE); 
            }
        }
        if (this.getRequiredCache() != null)
        {
            sb.append(AbstractBaseDescriptionDocument.TAB).append("<cache>").append(AbstractBaseDescriptionDocument.NEW_LINE);
            sb.append(AbstractBaseDescriptionDocument.TAB).append(AbstractBaseDescriptionDocument.TAB).append("<never>").append(this.getRequiredCache().getNeverCache()).append("</never>").append(AbstractBaseDescriptionDocument.NEW_LINE);
            sb.append(AbstractBaseDescriptionDocument.TAB).append(AbstractBaseDescriptionDocument.TAB).append("<public>").append(this.getRequiredCache().getIsPublic()).append("</public>").append(AbstractBaseDescriptionDocument.NEW_LINE);
            sb.append(AbstractBaseDescriptionDocument.TAB).append(AbstractBaseDescriptionDocument.TAB).append("<mustrevalidate>").append(this.getRequiredCache().getMustRevalidate()).append("</mustrevalidate>").append(AbstractBaseDescriptionDocument.NEW_LINE);
            sb.append(AbstractBaseDescriptionDocument.TAB).append("</cache>").append(AbstractBaseDescriptionDocument.NEW_LINE);
        }
        if (this.getNegotiatedFormats() != null)
        {
            for(NegotiatedFormat negotiatedFormat: this.getNegotiatedFormats())
            {
                sb.append(AbstractBaseDescriptionDocument.TAB).append("<negotiate");
                sb.append(" ").append("accept=\"").append(negotiatedFormat.getMediaType().getType()+"/"+negotiatedFormat.getMediaType().getSubtype()).append("\">");
                sb.append(negotiatedFormat.getFormat()).append("</negotiate>").append(AbstractBaseDescriptionDocument.NEW_LINE);
            }
        }
        sb.append(AbstractBaseDescriptionDocument.TAB).append("<formdata multipart-processing=\"").append(this.getMultipartProcessing());
        sb.append("\"/>").append(AbstractBaseDescriptionDocument.NEW_LINE);
        if (this.getArguments() != null && this.getArguments().length > 0)
        {
            sb.append(AbstractBaseDescriptionDocument.TAB).append("<args>").append(AbstractBaseDescriptionDocument.NEW_LINE);;
            for (ArgumentTypeDescription arg: this.getArguments())
            {
                sb.append(arg.toString()).append(AbstractBaseDescriptionDocument.NEW_LINE);
            }
            sb.append(AbstractBaseDescriptionDocument.TAB).append("</args>").append(AbstractBaseDescriptionDocument.NEW_LINE);            
        }
        if (this.getRequestTypes() != null && this.getRequestTypes().length > 0)
        {
            sb.append(AbstractBaseDescriptionDocument.TAB).append("<requests>").append(AbstractBaseDescriptionDocument.NEW_LINE);;
            for (TypeDescription arg: this.getRequestTypes())
            {
                if (arg.getId() != null && !arg.getId().equals(""))
                {
                    sb.append(AbstractBaseDescriptionDocument.TAB).append(AbstractBaseDescriptionDocument.TAB).append("<request type=\"");
                    sb.append(arg.getId().toString()).append("\"/>").append(AbstractBaseDescriptionDocument.NEW_LINE);
                }
                else
                {
                    sb.append(AbstractBaseDescriptionDocument.TAB).append(AbstractBaseDescriptionDocument.TAB).append("<request>").append(AbstractBaseDescriptionDocument.NEW_LINE);
                    sb.append(arg.toString()).append(AbstractBaseDescriptionDocument.NEW_LINE);
                    sb.append(AbstractBaseDescriptionDocument.TAB).append(AbstractBaseDescriptionDocument.TAB).append("</request>").append(AbstractBaseDescriptionDocument.NEW_LINE);
                }
            }
            sb.append(AbstractBaseDescriptionDocument.TAB).append("</requests>").append(AbstractBaseDescriptionDocument.NEW_LINE);            
        }
        if (this.getResponseTypes() != null && this.getResponseTypes().length > 0)
        {
            sb.append(AbstractBaseDescriptionDocument.TAB).append("<responses>").append(AbstractBaseDescriptionDocument.NEW_LINE);;
            for (TypeDescription arg: this.getResponseTypes())
            {
                if (arg.getId() != null && !arg.getId().equals(""))
                {
                    sb.append(AbstractBaseDescriptionDocument.TAB).append(AbstractBaseDescriptionDocument.TAB).append("<response type=\"");
                    sb.append(arg.getId().toString()).append("\"/>").append(AbstractBaseDescriptionDocument.NEW_LINE);
                }
                else
                {
                    sb.append(AbstractBaseDescriptionDocument.TAB).append(AbstractBaseDescriptionDocument.TAB).append("<response>").append(AbstractBaseDescriptionDocument.NEW_LINE);
                    sb.append(arg.toString()).append(AbstractBaseDescriptionDocument.NEW_LINE);
                    sb.append(AbstractBaseDescriptionDocument.TAB).append(AbstractBaseDescriptionDocument.TAB).append("</response>").append(AbstractBaseDescriptionDocument.NEW_LINE);
                }
            }
            sb.append(AbstractBaseDescriptionDocument.TAB).append("</responses>").append(AbstractBaseDescriptionDocument.NEW_LINE);            
        }
        sb.append("</").append(DescriptionImpl.ROOT_ELEMENT_NAME).append(">");
        return sb.toString();
    }
    
    /**
     * Return a new instance of webscript
     * 
     * @return a new instance of webscript
     */
    public static DescriptionImpl newInstance()
    {
        DescriptionImpl newDescriptionImpl = new DescriptionImpl ("","shortname","description","/url");
        return newDescriptionImpl;
    }
    
    /**
     * Constructor with id, shortName, description and url
     * 
     * @param id
     * @param shortName
     * @param description
     * @param url
     */
    public DescriptionImpl(String id, String shortName, String description,String url)
    {
        super(id,shortName,description);
        this.setUris(new String[]{url});
    }

    /**
     * Default constructor
     */
    public DescriptionImpl()
    {
        super();
    }
}
