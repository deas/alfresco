package org.alfresco.repo.lotus.ws.impl.helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.model.QuickrModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.workflow.WorkflowDefinition;
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.alfresco.service.cmr.workflow.WorkflowPath;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.cmr.workflow.WorkflowTaskQuery;
import org.alfresco.service.cmr.workflow.WorkflowTaskState;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

/**
 * Helper class that can start and end workflows using QucikR logic.
 */
public class AlfrescoQuickrWorkflowHelper
{
    private final static String WORKFLOW_STATUS_REVIEW = "review";
    private final static String WORKFLOW_SATUS_APPROVED = "approved";
    private final static String WORKFLOW_STATUS_REJECTED = "rejected";

    private final static String WORKFLOW_JBPM_PARALLEL = "jbpm$wf:parallelreview";
    private final static String WORKFLOW_JBPM_SERIAL = "jbpm$swf:serialreview";

    private final static String WORKFLOW_PARALLEL = "Parallel";
    private final static String WORKFLOW_SERIAL = "Serial";

    private AlfrescoQuickrDocumentHelper documentHelper;
    private WorkflowService workflowService;
    private PersonService personService;
    private DictionaryService dictionaryService;
    private NodeService nodeService;

    public void setDocumentHelper(AlfrescoQuickrDocumentHelper documentHelper)
    {
        this.documentHelper = documentHelper;
    }

    public void setWorkflowService(WorkflowService workflowService)
    {
        this.workflowService = workflowService;
    }

    public void setPersonService(PersonService personService)
    {
        this.personService = personService;
    }

    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    /**
     * Start workflow that correspond document type of provided node.
     * 
     * @param nodeRef target node reference.
     * @return
     */
    public boolean startWorkflowTask(NodeRef nodeRef)
    {
        if (workflowService.getWorkflowsForContent(nodeRef, true).size() > 0)
        {
            // It was already submitted
            throw new AlfrescoRuntimeException("A submitted draft exists.");
        }

        AspectDefinition docTypeAspect = documentHelper.getDocumentTypeAspect(nodeRef.getId(), null);
        String workflowName = "";
        for (AspectDefinition aspectDefinition : docTypeAspect.getDefaultAspects(false))
        {
            if (dictionaryService.isSubClass(aspectDefinition.getName(), QuickrModel.ASPECT_QUICKR_DRAFT_APPROVAL_TYPE))
            {
                String workflowType = aspectDefinition.getTitle();
                if (WORKFLOW_PARALLEL.equals(workflowType))
                {
                    workflowName = WORKFLOW_JBPM_PARALLEL;
                }
                else if (WORKFLOW_SERIAL.equals(workflowType))
                {
                    workflowName = WORKFLOW_JBPM_SERIAL;
                }

            }

        }

        if (workflowName.length() == 0)
        {
            return false;
        }

        WorkflowDefinition workflowDef = workflowService.getDefinitionByName(workflowName);
        TypeDefinition metadata = workflowDef.getStartTaskDefinition().metadata;
        Map<QName, Serializable> workflowParams = new HashMap<QName, Serializable>();
        workflowParams.putAll(metadata.getDefaultValues());

        QName assigneesQName = QName.createQName("{http://www.alfresco.org/model/bpm/1.0}assignees");

        Map<QName, PropertyDefinition> docTypeProps = docTypeAspect.getProperties();
        for (QName propKey : docTypeProps.keySet())
        {
            ArrayList<NodeRef> approversList = new ArrayList<NodeRef>();
            if (propKey.getNamespaceURI().equals(QuickrModel.DRAFT_APPROVE__MODEL_1_0_URI))
            {
                String approvers = docTypeProps.get(propKey).getDefaultValue();
                for (String approver : approvers.split(","))
                {
                    approversList.add(personService.getPerson(approver));
                }

                workflowParams.put(assigneesQName, approversList);
            }
        }

        NodeRef workflowPackage = workflowService.createPackage(null);
        workflowParams.put(WorkflowModel.ASSOC_PACKAGE, workflowPackage);
        // getUnprotectedNodeService
        nodeService.addChild(workflowPackage, nodeRef, WorkflowModel.ASSOC_PACKAGE_CONTAINS, QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName
                .createValidLocalName((String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME))));

        WorkflowPath path = workflowService.startWorkflow(workflowDef.getId(), workflowParams);
        if (path != null)
        {
            // extract the start task
            List<WorkflowTask> tasks = workflowService.getTasksForWorkflowPath(path.id);
            if (tasks.size() == 1)
            {
                WorkflowTask startTask = tasks.get(0);

                if (startTask.state == WorkflowTaskState.IN_PROGRESS)
                {
                    // end the start task to trigger the first 'proper'
                    // task in the workflow
                    workflowService.endTask(startTask.id, null);
                }
            }

        }

        return true;
    }

    /**
     * End the Task (i.e. complete the task)
     * 
     * @param documentRef the repository content item to get workflow for
     * @param transition the task transition to take on completion (or null, for the default transition)
     * @return true if workflow for provided node is completed.
     */
    public boolean endWorkflowTask(NodeRef documentRef, String transition)
    {
        WorkflowInstance activeWorkflow = workflowService.getWorkflowsForContent(documentRef, true).get(0);

        WorkflowTaskQuery filter = new WorkflowTaskQuery();
        filter.setProcessId(activeWorkflow.id);
        filter.setOrderBy(new WorkflowTaskQuery.OrderBy[] { WorkflowTaskQuery.OrderBy.TaskName_Asc, WorkflowTaskQuery.OrderBy.TaskState_Asc });
        filter.setTaskState(WorkflowTaskState.IN_PROGRESS);
        List<WorkflowTask> inProgressTasks = workflowService.queryTasks(filter);

        String currentUserName = AuthenticationUtil.getFullyAuthenticatedUser();
        for (WorkflowTask workflowTask : inProgressTasks)
        {
            if (workflowTask.title.equalsIgnoreCase(WORKFLOW_STATUS_REVIEW))
            {
                String userName = (String) workflowTask.properties.get(ContentModel.PROP_OWNER);
                if (userName.equalsIgnoreCase(currentUserName))
                {
                    workflowService.endTask(workflowTask.id, transition);
                    break;
                }
            }
        }

        // Now we should check if approve/reject step is passed
        boolean draftPublished = false;
        inProgressTasks = workflowService.queryTasks(filter);
        if (inProgressTasks.size() == 1)
        {
            String workflowTitle = inProgressTasks.get(0).title;
            if (workflowTitle.equalsIgnoreCase(WORKFLOW_SATUS_APPROVED))
            {
                draftPublished = true;
                workflowService.endTask(inProgressTasks.get(0).id, null);
            }
            else if (workflowTitle.equalsIgnoreCase(WORKFLOW_STATUS_REJECTED))
            {
                workflowService.endTask(inProgressTasks.get(0).id, null);
            }

        }
        else if (inProgressTasks.size() == 0)
        {
            draftPublished = true;
        }

        return draftPublished;
    }

    /**
     * Cancel all workflows for provided nodeRef
     * 
     * @param nodeRef nodeRef
     */
    public void cancelWorkflows(NodeRef nodeRef)
    {
        for (WorkflowInstance workflowInstance : workflowService.getWorkflowsForContent(nodeRef, true))
        {
            workflowService.cancelWorkflow(workflowInstance.id);
        }
    }
}
