/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
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
package org.alfresco.wcm.client.directive;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.Section;
import org.alfresco.wcm.client.util.UrlUtils;

import freemarker.core.Environment;
import freemarker.ext.beans.StringModel;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * Freemarker directive to output the url of an asset or section.
 * Usage: 
 * <@makeurl section=xxx/> or 
 * <@makeurl asset=xxx force=fff/> where xxx is a variable which references an asset object.
 * The force parameter is optional. If set to 'short' it forces the code to use the short 
 * "asset/<id>/name.ext" style of URL. If set to 'long' a full, friendly URL is generated. 
 * If the force option is omitted then it leaves it to the logic in the UrlUtils class to
 * decide when a short or long URL is appropriate.
 * Alternatively:
 * <@makeurl asset=xxx rendition=rrr/> allows the URL of a rendition of the asset to be 
 * generated.    
 * @author Chris Lack
 */
public class UrlDirective implements TemplateDirectiveModel
{

    private UrlUtils urlUtils;

	@SuppressWarnings("unchecked")
    @Override
    public void execute(Environment env, 
    		            Map params, 
    		            TemplateModel[] loopVars,
            			TemplateDirectiveBody body) throws TemplateException, IOException
    {
		if (params.size() < 1 && params.size() > 2) throw new TemplateModelException("url directive expects one or two parameters");
					
		StringModel assetParam = (StringModel)params.get("asset");
		StringModel sectionParam = (StringModel)params.get("section");
		
		// Optional parameter for asset to get a rendition of it
		SimpleScalar renditionParam = (SimpleScalar)params.get("rendition");

		if ((assetParam == null || ! (assetParam.getWrappedObject() instanceof Asset))
				&& (sectionParam == null || ! (sectionParam.getWrappedObject() instanceof Section))) 
		{
			throw new TemplateModelException("url directive expects asset or section parameter");
		}
		
		SimpleScalar forceParam = (SimpleScalar)params.get("force");
		String force = null;
		if (forceParam != null)
		{
			force = forceParam.getAsString();
		}
				
		// Get the request url
		String requestUrl = ((HttpRequestHashModel)env.getDataModel().get("Request")).getRequest().getContextPath();

		// Build the url for the asset/section
		String url;		
		if (assetParam != null) 
		{
			if (renditionParam != null)
			{
				force = "short";
			}

			Asset asset = (Asset)assetParam.getWrappedObject();
			if ("short".equals(force))
			{
				url = requestUrl+urlUtils.getShortUrl(asset);
			}
			else if ("long".equals(force))
			{
				url = requestUrl+urlUtils.getLongUrl(asset);
			}
			else 
			{
				url = requestUrl+urlUtils.getUrl(asset);
			}
			
			if (renditionParam != null)
			{
				String rendition = renditionParam.getAsString();
				url += "?rendition="+URLEncoder.encode(rendition, "UTF-8");			
			}			
		}
		else 
		{
			Section section = (Section)sectionParam.getWrappedObject();
			url = requestUrl+urlUtils.getUrl(section);			
		}
		
		env.getOut().write(url);
    }
	
	public void setUrlUtils(UrlUtils urlUtils) {
		this.urlUtils = urlUtils;
	}
	
}
