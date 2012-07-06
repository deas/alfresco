<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/workflow/workflow.lib.js">
model.workflowDefinitions = getWorkflowDefinitions();

// Widget instantiation metadata...
model.webScriptWidgets = [];
var startWorkflow = {};
startWorkflow.name = "Alfresco.component.StartWorkflow";
startWorkflow.provideMessages = true;
startWorkflow.provideOptions = true;
startWorkflow.options = {};
startWorkflow.options.failureMessage = "message.failure";
startWorkflow.options.submitButtonMessageKey = "button.startWorkflow";
startWorkflow.options.defaultUrl = getSiteUrl("my-tasks");
startWorkflow.options.selectedItems = (page.url.args.selectedItems != null) ? page.url.args.selectedItems: "";
startWorkflow.options.destination = (page.url.args.destination != null) ? page.url.args.destination : "";
startWorkflow.options.workflowDefinitions = model.workflowDefinitions;
model.webScriptWidgets.push(startWorkflow);