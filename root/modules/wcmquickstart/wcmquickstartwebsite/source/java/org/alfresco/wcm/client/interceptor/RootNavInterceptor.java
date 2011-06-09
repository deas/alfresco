package org.alfresco.wcm.client.interceptor;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.wcm.client.Section;
import org.alfresco.wcm.client.WebSite;
import org.alfresco.wcm.client.WebSiteService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * This attempts to pick the language based on the section name given.
 */
public class RootNavInterceptor extends HandlerInterceptorAdapter
{
    private static final Log log = LogFactory.getLog(RootNavInterceptor.class);
    private WebSiteService webSiteService;
    private Set<String> countryCodes = new TreeSet<String>();
    private Set<String> languageCodes = new TreeSet<String>();

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        // Grab the request details
        RequestContext requestContext = ThreadLocalRequestContext.getRequestContext();

        WebSite webSite = (WebSite) requestContext.getValue("webSite");
        if (webSite == null)
        {
            String serverName = request.getServerName();
            int serverPort = request.getServerPort();
            String contextPath = request.getContextPath();
            webSite = webSiteService.getWebSite(serverName, serverPort, contextPath);
        }

        // Break it up into paths
        String path = request.getPathInfo();
        int pathlength = path.length();

        // Do we have a directory?
        if (pathlength > 1)
        {
            // Split
            String[] pathElements = path.split("/");
            if (log.isDebugEnabled())
            {
                log.debug("RootNavInterceptor: " + pathElements.length + " : " + path);
            }

            // What's the top level section?
            String topLevelPath = pathElements[1];

            if ((topLevelPath.length() == 2) && languageCodes.contains(topLevelPath))
            {
                // Looks like a locale based path, treat as such
                Section section = webSite.getSectionByPath("/" + topLevelPath + "/");
                String language = section.getName();
                Locale locale = null;

                // set locale onto Alfresco thread local
                locale = I18NUtil.parseLocale(language);
                I18NUtil.setLocale(locale);

                requestContext.setValue("rootnav", section);
                requestContext.setValue("locale", locale);
            }
            if (log.isDebugEnabled())
            {
                log.debug("Picked " + I18NUtil.getLocale() + " from " + topLevelPath);
            }
        }

        return super.preHandle(request, response, handler);
    }

    public void setWebSiteService(WebSiteService webSiteService)
    {
        this.webSiteService = webSiteService;
    }

    public void init()
    {
        countryCodes.addAll(Arrays.asList(Locale.getISOCountries()));
        languageCodes.addAll(Arrays.asList(Locale.getISOLanguages()));
    }
}