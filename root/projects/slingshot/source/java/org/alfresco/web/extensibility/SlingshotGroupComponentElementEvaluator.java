package org.alfresco.web.extensibility;

import java.util.List;
import java.util.Map;

import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.extensibility.impl.DefaultSubComponentEvaluator;

public class SlingshotGroupComponentElementEvaluator extends DefaultSubComponentEvaluator
{
    protected SlingshotEvaluatorUtil util = null;

    public void setSlingshotEvaluatorUtil(SlingshotEvaluatorUtil slingshotExtensibilityUtil)
    {
        this.util = slingshotExtensibilityUtil;
    }

    public static final String GROUPS = "groups";
    public static final String RELATION = "relation";
    public static final String AND = "AND";

    /**
     * Checks to see whether or not the current user satisfies the group membership requirements
     * specified.
     */
    @Override
    public boolean evaluate(RequestContext context, Map<String, String> params)
    {
        boolean memberOfAllGroups = getRelationship(context, params);
        List<String> groups = util.getGroups(params.get(GROUPS));
        boolean apply = util.isMemberOfGroups(context, groups, memberOfAllGroups);
        return apply;
    }

    /**
     * Gets the logical relationship between all the groups to test for membership of. By default
     * this boils down to a straight choice between "AND" (must be a member of ALL groups) and "OR"
     * (only needs to be a member of one group)
     *
     * @param context
     * @param evaluationProperties
     * @return
     */
    protected boolean getRelationship(RequestContext context, Map<String, String> evaluationProperties)
    {
        String relationParam = evaluationProperties.get(RELATION);
        return (relationParam != null && relationParam.trim().equalsIgnoreCase(AND));
    }
}
