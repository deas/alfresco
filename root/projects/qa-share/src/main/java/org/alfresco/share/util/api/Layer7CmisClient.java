package org.alfresco.share.util.api;

import org.alfresco.opencmis.CMISDispatcherRegistry;
import org.alfresco.rest.api.tests.client.*;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.webdrone.WebDrone;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dmitry.yukhnovets on 13.10.2014.
 */
public class Layer7CmisClient extends PublicApiClient {
    private PublicApiHttpClient client;
    private ThreadLocal<RequestContext> rc = new ThreadLocal<RequestContext>();
    private WebDrone drone;

    public Layer7CmisClient(WebDrone drone, PublicApiHttpClient client, UserDataService userDataService) {
        super(client, userDataService);
        this.client = client;
        this.drone = drone;
    }

    @Override
    public CmisSession createPublicApiCMISSession(CMISDispatcherRegistry.Binding binding, String version, String objectFactoryName)
    {
        CmisSession cmisSession = null;

        RequestContext context = rc.get();
        if(context == null)
        {
            throw new RuntimeException("Must set a request context");
        }


        String networkId = context.getNetworkId();
        String username = context.getRunAsUser();
        UserData userData = findUser(context.getRunAsUser());

        if(userData != null)
        {
            String password = userData.getPassword();
            String tokenKey = AbstractUtils.getTokenKey(drone, username, password);
            // default factory implementation
            SessionFactory factory = SessionFactoryImpl.newInstance();
            Map<String, String> parameters = new HashMap<String, String>();

            // connection settings
            parameters.put(SessionParameter.AUTH_HTTP_BASIC, "false");
            // user credentials
            parameters.put(SessionParameter.HEADER + ".0", "Authorization: Bearer " + tokenKey);

            if(binding == CMISDispatcherRegistry.Binding.atom)
            {

                parameters.put(SessionParameter.BINDING_TYPE, binding.getOpenCmisBinding().value());
                parameters.put(SessionParameter.ATOMPUB_URL, client.getPublicApiCmisUrl(networkId, binding, version, null));

            }
            else if(binding == CMISDispatcherRegistry.Binding.browser)
            {
                parameters.put(SessionParameter.BROWSER_URL, client.getPublicApiCmisUrl(networkId, binding, version, null));
                parameters.put(SessionParameter.BINDING_TYPE, binding.getOpenCmisBinding().value());
            }
            if(networkId != null)
            {
                parameters.put(SessionParameter.REPOSITORY_ID, networkId);
            }
            if(objectFactoryName != null)
            {
                parameters.put(SessionParameter.OBJECT_FACTORY_CLASS, objectFactoryName);
            }

            // create session
            Session session = factory.createSession(parameters);

            cmisSession = new CmisSession(session);
        }

        return cmisSession;
    }

    @Override
    public void setRequestContext(RequestContext rc)
    {
        this.rc.set(rc);
        super.setRequestContext(rc);
    }


}
