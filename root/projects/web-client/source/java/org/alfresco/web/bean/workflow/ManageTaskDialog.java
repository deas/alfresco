/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.web.bean.workflow;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.transaction.UserTransaction;

import org.alfresco.model.ApplicationModel;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.cmr.workflow.WorkflowTaskDefinition;
import org.alfresco.service.cmr.workflow.WorkflowTaskState;
import org.alfresco.service.cmr.workflow.WorkflowTransition;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.app.AlfrescoNavigationHandler;
import org.alfresco.web.app.Application;
import org.alfresco.web.app.servlet.FacesHelper;
import org.alfresco.web.bean.dialog.BaseDialogBean;
import org.alfresco.web.bean.repository.MapNode;
import org.alfresco.web.bean.repository.Node;
import org.alfresco.web.bean.repository.NodePropertyResolver;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.bean.repository.TransientNode;
import org.alfresco.web.bean.repository.User;
import org.alfresco.web.config.DialogsConfigElement.DialogButtonConfig;
import org.alfresco.web.ui.common.Utils;
import org.alfresco.web.ui.common.component.UIActionLink;
import org.alfresco.web.ui.common.component.data.UIRichList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Bean implementation for the "Manage Task" dialog.
 * 
 * @author gavinc
 */
public class ManageTaskDialog extends BaseDialogBean
{
    private static final long serialVersionUID = -3209544870892993135L;

    transient private WorkflowService workflowService;
    protected Node taskNode;
    transient private WorkflowTask task;
    transient private WorkflowInstance workflowInstance;
    transient private WorkflowTransition[] transitions;
    protected NodeRef workflowPackage;
    protected List<Node> resources;
    protected TaskCompleteResolver completeResolver = new TaskCompleteResolver();
    protected UIRichList packageItemsRichList;
    protected List<String> packageItemsToAdd;
    protected List<String> packageItemsToRemove;
    protected String[] itemsToAdd;
    protected boolean isItemBeingAdded = false;

    private final static Log LOGGER = LogFactory.getLog(ManageTaskDialog.class);

    protected static final String ID_PREFIX = "transition_";
    protected static final String CLIENT_ID_PREFIX = AlfrescoNavigationHandler.DIALOG_PREFIX + ID_PREFIX;

    // ------------------------------------------------------------------------------
    // Dialog implementation

    @Override
    public void init(Map<String, String> parameters)
    {
        super.init(parameters);

        // reset variables
        this.task = null;
        this.taskNode = null;
        this.workflowInstance = null;
        this.transitions = null;
        this.workflowPackage = null;
        this.resources = null;
        this.itemsToAdd = null;
        this.packageItemsToAdd = null;
        this.packageItemsToRemove = null;
        this.isItemBeingAdded = false;
        if (this.packageItemsRichList != null)
        {
            this.packageItemsRichList.setValue(null);
            this.packageItemsRichList = null;
        }

        // get the task details
        String taskId = this.parameters.get("id");
        this.task = this.getWorkflowService().getTaskById(taskId);

        if (this.task != null)
        {
            // setup a transient node to represent the task we're managing
            WorkflowTaskDefinition taskDef = this.task.definition;
            this.taskNode = new TransientNode(taskDef.metadata.getName(), "task_" + System.currentTimeMillis(),
                        this.task.properties);

            // get access to the workflow instance for the task
            this.workflowInstance = this.task.path.instance;

            // setup the workflow package for the task
            this.workflowPackage = (NodeRef) this.task.properties.get(WorkflowModel.ASSOC_PACKAGE);

            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("Task: " + this.task);
                LOGGER.debug("Trasient node: " + this.taskNode);
                Boolean isSystemPackage = (Boolean) this.getNodeService().getProperty(this.workflowPackage,
                            WorkflowModel.PROP_IS_SYSTEM_PACKAGE);
                LOGGER.debug("Workflow package: " + this.workflowPackage + " system package: " + isSystemPackage);
            }
        }
    }

    @Override
    public void restored()
    {
        // reset the workflow package rich list so everything gets re-evaluated
        if (this.packageItemsRichList != null)
        {
            this.packageItemsRichList.setValue(null);
            this.packageItemsRichList = null;
        }
    }

    @Override
    protected String finishImpl(FacesContext context, String outcome) throws Exception
    {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Saving task: " + this.getWorkflowTask().id);

        // before updating the task still exists and is not completed
        WorkflowTask checkTask = this.getWorkflowService().getTaskById(this.getWorkflowTask().id);
        if (checkTask == null || checkTask.state == WorkflowTaskState.COMPLETED)
        {
            Utils.addErrorMessage(Application.getMessage(context, "invalid_task"));
            return outcome;
        }

        // prepare the edited parameters for saving
        Map<QName, Serializable> params = WorkflowUtil.prepareTaskParams(this.taskNode);

        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Saving task with parameters: " + params);

        // update the task with the updated parameters and resources
        updateResources();
        this.getWorkflowService().updateTask(this.getWorkflowTask().id, params, null, null);

        return outcome;
    }

    @Override
    public List<DialogButtonConfig> getAdditionalButtons()
    {
        List<DialogButtonConfig> buttons = null;

        if (this.getWorkflowTask() != null)
        {
            // get the transitions available from this task and
            // show them in the dialog as additional buttons
            this.transitions = this.getWorkflowTask().path.node.transitions;
            boolean isPooledTask = isPooledTask();

            if (isPooledTask || this.transitions != null)
            {
                buttons = new ArrayList<DialogButtonConfig>(this.transitions.length + 1);

                if (isPooledTask)
                {
                    if (this.taskNode.getProperties().get(ContentModel.PROP_OWNER) == null)
                    {
                        buttons.add(new DialogButtonConfig("button_take_ownership", null, "take_ownership",
                                    "#{DialogManager.bean.takeOwnership}", "false", null));
                    }
                    else
                    {
                        buttons.add(new DialogButtonConfig("button_return_to_pool", null, "return_ownership",
                                    "#{DialogManager.bean.returnOwnership}", "false", null));
                    }
                }

                if (this.transitions != null)
                {
                    Object hiddenTransitions = this.taskNode.getProperties().get(WorkflowModel.PROP_HIDDEN_TRANSITIONS);
                    for (WorkflowTransition trans : this.transitions)
                    {
                        if (hiddenTransitions == null
                                    || (hiddenTransitions instanceof String && ((String) hiddenTransitions).equals(""))
                                    || (hiddenTransitions instanceof String && !((String) hiddenTransitions)
                                                .equals(trans.id)) || (hiddenTransitions instanceof List<?>)
                                    && !((List<?>) hiddenTransitions).contains(trans.id))
                        {
                            if (this.taskNode.getProperties().get(ContentModel.PROP_OWNER) != null)
                            {
                                buttons.add(new DialogButtonConfig(ID_PREFIX + trans.title, trans.title, null,
                                        "#{DialogManager.bean.transition}", "false", null));
                            }
                        }
                    }
                }
            }
        }

        return buttons;
    }

    @Override
    public String getFinishButtonLabel()
    {
        return Application.getMessage(FacesContext.getCurrentInstance(), "save_changes");
    }

    @Override
    public boolean getFinishButtonDisabled()
    {
        return false;
    }

    @Override
    public String getContainerTitle()
    {
        String titleStart = Application.getMessage(FacesContext.getCurrentInstance(), "manage_task_title");

        return titleStart + ": " + this.getWorkflowTask().title;
    }

    @Override
    public String getContainerDescription()
    {
        return this.getWorkflowTask().description;
    }

    // ------------------------------------------------------------------------------
    // Event handlers

    @SuppressWarnings("deprecation")
    public String takeOwnership()
    {
        String outcome = getDefaultFinishOutcome();

        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Taking ownership of task: " + this.getWorkflowTask().id);

        FacesContext context = FacesContext.getCurrentInstance();

        // before taking ownership check the task still exists and is not
        // completed
        WorkflowTask checkTask = this.getWorkflowService().getTaskById(this.getWorkflowTask().id);
        if (checkTask == null || checkTask.state == WorkflowTaskState.COMPLETED)
        {
            Utils.addErrorMessage(Application.getMessage(context, "invalid_task"));
            return outcome;
        }

        UserTransaction tx = null;

        try
        {
            tx = Repository.getUserTransaction(context);
            tx.begin();

            // prepare the edited parameters for saving
            User user = Application.getCurrentUser(context);
            String userName = user.getUserName();
            Map<QName, Serializable> params = new HashMap<QName, Serializable>();
            params.put(ContentModel.PROP_OWNER, userName);

            // update the task with the updated parameters and resources
            updateResources();
            this.getWorkflowService().updateTask(this.getWorkflowTask().id, params, null, null);

            // commit the changes
            tx.commit();
        }
        catch (Throwable e)
        {
            // rollback the transaction
            try
            {
                if (tx != null)
                {
                    tx.rollback();
                }
            }
            catch (Exception ex)
            {
            }
            Utils.addErrorMessage(formatErrorMessage(e), e);
            outcome = this.getErrorOutcome(e);
        }

        return outcome;
    }

    @SuppressWarnings("deprecation")
    public String returnOwnership()
    {
        String outcome = getDefaultFinishOutcome();

        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Returning ownership of task to pool: " + this.getWorkflowTask().id);

        FacesContext context = FacesContext.getCurrentInstance();
        // before returning ownership check the task still exists and is not
        // completed
        WorkflowTask checkTask = this.getWorkflowService().getTaskById(this.getWorkflowTask().id);
        if (checkTask == null || checkTask.state == WorkflowTaskState.COMPLETED)
        {
            Utils.addErrorMessage(Application.getMessage(context, "invalid_task"));
            return outcome;
        }

        UserTransaction tx = null;

        try
        {
            tx = Repository.getUserTransaction(context);
            tx.begin();

            // prepare the edited parameters for saving
            Map<QName, Serializable> params = new HashMap<QName, Serializable>();
            params.put(ContentModel.PROP_OWNER, null);

            // update the task with the updated parameters and resources
            updateResources();
            this.getWorkflowService().updateTask(this.getWorkflowTask().id, params, null, null);

            // commit the changes
            tx.commit();
        }
        catch (Throwable e)
        {
            // rollback the transaction
            try
            {
                if (tx != null)
                {
                    tx.rollback();
                }
            }
            catch (Exception ex)
            {
            }
            Utils.addErrorMessage(formatErrorMessage(e), e);
            outcome = this.getErrorOutcome(e);
        }

        return outcome;
    }

    @SuppressWarnings("deprecation")
    public String transition()
    {
        String outcome = getDefaultFinishOutcome();

        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Transitioning task: " + this.getWorkflowTask().id);

        // before transitioning check the task still exists and is not completed
        FacesContext context = FacesContext.getCurrentInstance();
        WorkflowTask checkTask = this.getWorkflowService().getTaskById(this.getWorkflowTask().id);
        if (checkTask == null || checkTask.state == WorkflowTaskState.COMPLETED)
        {
            Utils.addErrorMessage(Application.getMessage(context, "invalid_task"));
            return outcome;
        }

        // to find out which transition button was pressed we need
        // to look for the button's id in the request parameters,
        // the first non-null result is the button that was pressed.
        Map<?, ?> reqParams = context.getExternalContext().getRequestParameterMap();

        String selectedTransition = null;
        for (WorkflowTransition trans : this.getWorkflowTransitions())
        {
            Object result = reqParams.get(CLIENT_ID_PREFIX + FacesHelper.makeLegalId(trans.title));
            if (result != null)
            {
                // this was the button that was pressed
                selectedTransition = trans.id;
                break;
            }
        }

        UserTransaction tx = null;

        try
        {
            tx = Repository.getUserTransaction(context);
            tx.begin();

            // prepare the edited parameters for saving
            Map<QName, Serializable> params = WorkflowUtil.prepareTaskParams(this.taskNode);

            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Transitioning task with parameters: " + params);

            // update the task with the updated parameters and resources
            updateResources();
            this.getWorkflowService().updateTask(this.getWorkflowTask().id, params, null, null);

            // signal the selected transition to the workflow task
            this.getWorkflowService().endTask(this.getWorkflowTask().id, selectedTransition);

            // commit the changes
            tx.commit();

            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Ended task with transition: " + selectedTransition);
        }
        catch (Throwable e)
        {
            // rollback the transaction
            try
            {
                if (tx != null)
                {
                    tx.rollback();
                }
            }
            catch (Exception ex)
            {
            }
            Utils.addErrorMessage(formatErrorMessage(e), e);
            outcome = this.getErrorOutcome(e);
        }

        return outcome;
    }

    /**
     * Prepares the dialog to allow the user to add an item to the workflow
     * package
     * 
     * @param event The event
     */
    public void prepareForAdd(ActionEvent event)
    {
        this.isItemBeingAdded = true;
    }

    /**
     * Cancels the adding of an item to the workflow package
     * 
     * @param event The event
     */
    public void cancelAddPackageItems(ActionEvent event)
    {
        this.isItemBeingAdded = false;
    }

    /**
     * Adds an item to the workflow package
     * 
     * @param event The event
     */
    public void addPackageItems(ActionEvent event)
    {
        if (this.itemsToAdd != null)
        {
            if (this.packageItemsToAdd == null)
            {
                // create the list of items to add if necessary
                this.packageItemsToAdd = new ArrayList<String>(this.itemsToAdd.length);
            }

            for (String item : this.itemsToAdd)
            {
                // if this item is in the remove list it means it was there
                // originally
                // and has now been re-added, as a result we don't need to do
                // anything
                // to the original workflow package, therefore remove from the
                // remove list
                if (this.packageItemsToRemove != null && this.packageItemsToRemove.contains(item))
                {
                    this.packageItemsToRemove.remove(item);

                    if (LOGGER.isDebugEnabled())
                        LOGGER.debug("Removed item from the removed list: " + item);
                }
                else
                {
                    this.packageItemsToAdd.add(item);

                    if (LOGGER.isDebugEnabled())
                        LOGGER.debug("Added item to the added list: " + item);
                }
            }

            // reset the rich list so it re-renders
            this.packageItemsRichList.setValue(null);
        }

        this.isItemBeingAdded = false;
        this.itemsToAdd = null;
    }

    /**
     * Removes an item from the workflow package
     * 
     * @param event The event containing a reference to the item to remove
     */
    public void removePackageItem(ActionEvent event)
    {
        UIActionLink link = (UIActionLink) event.getComponent();
        Map<String, String> params = link.getParameterMap();
        String nodeRef = new NodeRef(Repository.getStoreRef(), params.get("id")).toString();

        if (this.packageItemsToAdd != null && this.packageItemsToAdd.contains(nodeRef))
        {
            // remove the item from the added list if it was added in this
            // dialog session
            this.packageItemsToAdd.remove(nodeRef);

            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Removed item from the added list: " + nodeRef);
        }
        else
        {
            // add the node to the list of items to remove
            if (this.packageItemsToRemove == null)
            {
                this.packageItemsToRemove = new ArrayList<String>(1);
            }

            this.packageItemsToRemove.add(nodeRef);

            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Added item to the removed list: " + nodeRef);
        }

        // reset the rich list so it re-renders
        this.packageItemsRichList.setValue(null);
    }

    /**
     * Toggles the complete flag for a workflow package item
     * 
     * @param event The event containing a reference to the item to toggle the
     *            status for
     */
    public void togglePackageItemComplete(ActionEvent event)
    {
        // TODO: not supported yet
    }

    // ------------------------------------------------------------------------------
    // Bean Getters and Setters

    /**
     * Returns a String array of NodeRef's that are being added to the workflow
     * package
     * 
     * @return String array of NodeRef's
     */
    public String[] getItemsToAdd()
    {
        return this.itemsToAdd;
    }

    /**
     * Sets the NodeRef's to add as items to the workflow package
     * 
     * @param itemsToAdd NodeRef's to add to the workflow package
     */
    public void setItemsToAdd(String[] itemsToAdd)
    {
        this.itemsToAdd = itemsToAdd;
    }

    /**
     * Determines whether an item is currently being added to the workflow
     * package
     * 
     * @return true if an item is being added
     */
    public boolean isItemBeingAdded()
    {
        return this.isItemBeingAdded;
    }

    /**
     * Sets the rich list being used for the workflow package items
     * 
     * @param richList The rich list instance
     */
    public void setPackageItemsRichList(UIRichList richList)
    {
        this.packageItemsRichList = richList;
    }

    /**
     * Returns the rich list being used for the workflow package items
     * 
     * @return The rich list instance
     */
    public UIRichList getPackageItemsRichList()
    {
        return this.packageItemsRichList;
    }

    /**
     * Returns the Node representing the task
     * 
     * @return The node
     */
    public Node getTaskNode()
    {
        return this.taskNode;
    }

    /**
     * Returns whether this is a pooled task
     * 
     * @return true => pooled
     */
    public boolean isPooledTask()
    {
        List<?> pooledActors = (List<?>) taskNode.getAssociations().get(WorkflowModel.ASSOC_POOLED_ACTORS);
        return (pooledActors != null && pooledActors.size() > 0);
    }

    /**
     * Returns the WorkflowInstance that the current task belongs to
     * 
     * @return The workflow instance
     */
    public WorkflowInstance getWorkflowInstance()
    {
        if (workflowInstance == null)
        {
            workflowInstance = getWorkflowTask().path.instance;
        }
        return this.workflowInstance;
    }

    /**
     * Returns the URL to the Workflow Definition Image of the current task
     * 
     * @return the url
     */
    public String getWorkflowDefinitionImageUrl()
    {
        return "/workflowdefinitionimage/" + this.getWorkflowInstance().definition.id;
    }

    /**
     * Returns the action group the current task uses for the workflow package
     * 
     * @return action group id
     */
    public String getPackageActionGroup()
    {
        return (String) this.getWorkflowTask().properties.get(WorkflowModel.PROP_PACKAGE_ACTION_GROUP);
    }

    /**
     * Returns the action group the current task uses for each workflow package
     * item
     * 
     * @return action group id
     */
    public String getPackageItemActionGroup()
    {
        return (String) this.getWorkflowTask().properties.get(WorkflowModel.PROP_PACKAGE_ITEM_ACTION_GROUP);
    }

    /**
     * Returns a list of resources associated with this task i.e. the children
     * of the workflow package
     * 
     * @return The list of nodes
     */
    public List<Node> getResources()
    {
        this.resources = new ArrayList<Node>(4);

        if (this.workflowPackage != null)
        {
            UserTransaction tx = null;
            try
            {
                FacesContext context = FacesContext.getCurrentInstance();
                tx = Repository.getUserTransaction(context, true);
                tx.begin();

                List<NodeRef> contents = this.workflowService.getPackageContents(getWorkflowTask().id);

                for (NodeRef nodeRef : contents)
                {
                    if (this.getNodeService().exists(nodeRef))
                    {
                        // find it's type so we can see if it's a node we
                        // are interested in
                        QName type = this.getNodeService().getType(nodeRef);

                        // make sure the type is defined in the data
                        // dictionary
                        if (this.getDictionaryService().getType(type) != null)
                        {
                            // look for content nodes or links to content
                            // NOTE: folders within workflow packages are
                            // ignored for now
                            if (this.getDictionaryService().isSubClass(type, ContentModel.TYPE_CONTENT)
                                        || ApplicationModel.TYPE_FILELINK.equals(type))
                            {
                                // if the node is not in the removed list
                                // then add create the
                                // client side representation and add to the
                                // list
                                if (this.packageItemsToRemove == null
                                            || this.packageItemsToRemove.contains(nodeRef.toString()) == false)
                                {
                                    createAndAddNode(nodeRef);
                                }
                            }
                        }
                    }
                }

                // now iterate through the items to add list and add them to the
                // list of resources
                if (this.packageItemsToAdd != null)
                {
                    for (String newItem : this.packageItemsToAdd)
                    {
                        NodeRef nodeRef = new NodeRef(newItem);
                        if (this.getNodeService().exists(nodeRef))
                        {
                            // we know the type is correct as this was added as
                            // a result of a query
                            // for all content items so just add the item to the
                            // resources list
                            createAndAddNode(nodeRef);
                        }
                        else
                        {
                            if (LOGGER.isDebugEnabled())
                                LOGGER.debug("Ignoring " + nodeRef + " as it has been removed from the repository");
                        }
                    }
                }

                // commit the transaction
                tx.commit();
            }
            catch (Throwable err)
            {
                Utils.addErrorMessage(MessageFormat.format(Application.getMessage(FacesContext.getCurrentInstance(),
                            Repository.ERROR_GENERIC), err.getMessage()), err);
                this.resources = Collections.<Node> emptyList();
                try
                {
                    if (tx != null)
                    {
                        tx.rollback();
                    }
                }
                catch (Exception tex)
                {
                }
            }
        }
        else if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Failed to find workflow package for task: " + this.getWorkflowTask().id);
        }

        return this.resources;
    }

    /**
     * Sets the workflow service to use
     * 
     * @param workflowService WorkflowService instance
     */
    public void setWorkflowService(WorkflowService workflowService)
    {
        this.workflowService = workflowService;
    }

    protected WorkflowService getWorkflowService()
    {
        if (workflowService == null)
        {
            workflowService = Repository.getServiceRegistry(FacesContext.getCurrentInstance()).getWorkflowService();
        }
        return workflowService;
    }

    protected WorkflowTask getWorkflowTask()
    {
        if (task == null)
        {
            String taskId = this.parameters.get("id");
            task = this.getWorkflowService().getTaskById(taskId);
        }
        return task;
    }

    protected WorkflowTransition[] getWorkflowTransitions()
    {
        if (transitions == null)
        {
            transitions = getWorkflowTask().path.node.transitions;
        }
        return transitions;
    }

    // ------------------------------------------------------------------------------
    // Helper methods

    protected void createAndAddNode(NodeRef nodeRef)
    {
        // create our Node representation
        MapNode node = new MapNode(nodeRef, this.getNodeService(), true);
        this.browseBean.setupCommonBindingProperties(node);

        // add property resolvers to show path information
        node.addPropertyResolver("path", this.browseBean.resolverPath);
        node.addPropertyResolver("displayPath", this.browseBean.resolverDisplayPath);

        // add a property resolver to indicate whether the item has been
        // completed or not
        // node.addPropertyResolver("completed", this.completeResolver);

        // add the id of the task being managed
        node.getProperties().put("taskId", this.getWorkflowTask().id);

        this.resources.add(node);
    }

    protected void updateResources()
    {
        // remove any items the user selected to remove
        if (this.workflowPackage != null && this.packageItemsToRemove != null && this.packageItemsToRemove.size() > 0)
        {
            for (String removedItem : this.packageItemsToRemove)
            {
                this.getNodeService().removeChild(this.workflowPackage, new NodeRef(removedItem));
            }
        }

        // add any items the user selected to add
        if (this.workflowPackage != null && this.packageItemsToAdd != null && this.packageItemsToAdd.size() > 0)
        {
            for (String addedItem : this.packageItemsToAdd)
            {
                NodeRef addedNodeRef = new NodeRef(addedItem);
                this.getNodeService().addChild(
                            this.workflowPackage,
                            addedNodeRef,
                            WorkflowModel.ASSOC_PACKAGE_CONTAINS,
                            QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName
                                        .createValidLocalName((String) this.getNodeService().getProperty(addedNodeRef,
                                                    ContentModel.PROP_NAME))));
            }
        }
    }

    // ------------------------------------------------------------------------------
    // Inner classes

    /**
     * Property resolver to determine if the given node has been flagged as
     * complete
     */
    protected class TaskCompleteResolver implements NodePropertyResolver
    {
        private static final long serialVersionUID = 5862037943275638314L;

        public Object get(Node node)
        {
            String result = Application.getMessage(FacesContext.getCurrentInstance(), "no");
            List<?> completedItems = (List<?>) getWorkflowTask().properties.get(WorkflowModel.PROP_COMPLETED_ITEMS);
            if (completedItems != null && completedItems.size() > 0 && completedItems.contains(node.getNodeRef()))
            {
                result = Application.getMessage(FacesContext.getCurrentInstance(), "yes");
            }
            return result;
        }
    }
}
