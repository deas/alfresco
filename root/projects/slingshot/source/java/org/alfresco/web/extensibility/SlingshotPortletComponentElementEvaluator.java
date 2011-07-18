package org.alfresco.web.extensibility;

import java.util.Map;

import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.extensibility.impl.DefaultSubComponentEvaluator;

/**
 * <p>
 * Evaluator used to decide if a {@code<sub-component>} shall be bound in to a {@code<component>} and {@code<@region>}.
 * </p>
 *
 * <p>
 * Makes it possible to decide if we are viewed from a portal and (optional) which portal using a regexp
 * in the {@code<portletUrls>} parameter.
 * </p>
 *
 * <p>
 * Note! The regexp is expressed without using the surrounding // characters.
 * </p>
 *
 * <p>
 * Example:
 * </p>
 *
 * <pre>{@code<evaluator type="portlet.component.evaluator"/>}</pre>
 *
 * <p>
 * Will return true if we are viewed from inside a portlet.
 * </p>
 *
 * <p>
 * Example 2:
 * </p>
 *
 * <pre>{@code
 * <evaluator type="portlet.component.evaluator">
 *    <params>
 *       <portletUrls>regexp matching portlet urls</portletUrls>
 *    </params>
 * </evaluator>
 * }</pre>
 *
 * <p>
 * Will return true if we are ina portal with a url matching the defined regexp.
 * </p>
 *
 * @author ewinlof
 */
public class SlingshotPortletComponentElementEvaluator extends DefaultSubComponentEvaluator
{

    /* Evaluator parameters */
    public static final String PORTLET_URL_FILTER = "portletUrls";

    protected SlingshotEvaluatorUtil util = null;

    public void setSlingshotEvaluatorUtil(SlingshotEvaluatorUtil slingshotExtensibilityUtil)
    {
        this.util = slingshotExtensibilityUtil;
    }

    /**
     * Decides if we are inside a portal or not.
     *
     * @param context
     * @param params
     * @return true if we are in a portlet and its url matches the {@code<portletUrls>} param (defaults to ".*")
     */
    @Override
    public boolean evaluate(RequestContext context, Map<String, String> params)
    {
        // Find the portlet host
        Boolean portletHost = util.getPortletHost(context);
        String portletUrl = util.getPortletUrl(context);

        // Check if we are viewed from inside a portlet
        if (portletHost)
        {
            // Yes we are viewed from a portlet
            if (portletUrl == null)
            {
                // If no url was provided we set it to something that will match a non existing filter
                portletUrl = "";
            }

            // Match against the url filter
            return portletUrl.matches(util.getEvaluatorParam(params, PORTLET_URL_FILTER, ".*"));
        }

        // No we are not in a portlet
        return false;
    }
}
