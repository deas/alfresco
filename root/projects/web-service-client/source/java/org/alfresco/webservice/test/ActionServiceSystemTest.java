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
package org.alfresco.webservice.test;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.webservice.action.Action;
import org.alfresco.webservice.action.ActionExecutionResult;
import org.alfresco.webservice.action.ActionItemDefinition;
import org.alfresco.webservice.action.ActionItemDefinitionType;
import org.alfresco.webservice.action.ActionServiceSoapBindingStub;
import org.alfresco.webservice.action.Condition;
import org.alfresco.webservice.action.ParameterDefinition;
import org.alfresco.webservice.action.Rule;
import org.alfresco.webservice.action.RuleType;
import org.alfresco.webservice.repository.UpdateResult;
import org.alfresco.webservice.types.CML;
import org.alfresco.webservice.types.CMLCreate;
import org.alfresco.webservice.types.ContentFormat;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.Node;
import org.alfresco.webservice.types.ParentReference;
import org.alfresco.webservice.types.Predicate;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.types.Store;
import org.alfresco.webservice.util.ActionUtils;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.Utils;
import org.alfresco.webservice.util.WebServiceFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Roy Wetherall
 */
public class ActionServiceSystemTest extends BaseWebServiceSystemTest
{
    private static Log logger = LogFactory.getLog(ActionServiceSystemTest.class);
    
    private ActionServiceSoapBindingStub actionService = null;
    
    public ActionServiceSystemTest()
    {        
        this.actionService = WebServiceFactory.getActionService();
    }
    
    public void testGetActionDefinitions() throws Exception
    {
        ActionItemDefinition[] definitions = this.actionService.getActionDefinitions();
        assertNotNull(definitions);
        assertTrue(definitions.length > 0);
        
        if (logger.isDebugEnabled() == true)
        {
            // Output the names and titles of the action definitions
            System.out.println("Action definitions:");
            for (ActionItemDefinition definition : definitions)
            {
                System.out.println(definition.getName() + " - " + definition.getTitle());
            }
            System.out.println("");
        }
    }  
    
    public void testGetConditionDefinitions() throws Exception
    {
        ActionItemDefinition[] definitions = this.actionService.getConditionDefinitions();
        assertNotNull(definitions);
        assertTrue(definitions.length > 0);
        
        if (logger.isDebugEnabled() == true)
        {
            // Output the names and titles of the action definitions
            System.out.println("Condition definitions:");
            for (ActionItemDefinition definition : definitions)
            {
                System.out.println(definition.getName() + " - " + definition.getTitle());
            }
            System.out.println("");
        }
    }
    
    public void testGetActionItemDefinition() throws Exception
    {
        // Get an action
        ActionItemDefinition actionDefinition = this.actionService.getActionItemDefinition("add-features", ActionItemDefinitionType.action);
        assertNotNull(actionDefinition);
        assertEquals("add-features", actionDefinition.getName());
        assertNotNull(actionDefinition.getTitle());
        assertNotNull(actionDefinition.getDescription());
        assertEquals(ActionItemDefinitionType.action, actionDefinition.getType());        
        assertTrue(actionDefinition.isAdHocPropertiesAllowed());
        assertNotNull(actionDefinition.getParameterDefinition());
        
        // Check that the parameters are correct
        assertTrue(actionDefinition.getParameterDefinition().length == 1);
        ParameterDefinition param = actionDefinition.getParameterDefinition()[0];
        assertEquals("aspect-name", param.getName());
        assertTrue(param.isIsMandatory());
        assertEquals("{http://www.alfresco.org/model/dictionary/1.0}qname", param.getType());
        
        // Get a condition
        ActionItemDefinition conditionDefintion = this.actionService.getActionItemDefinition("no-condition", ActionItemDefinitionType.condition);
        assertNotNull(conditionDefintion);
        assertEquals("no-condition", conditionDefintion.getName());
        assertNotNull(conditionDefintion.getTitle());
        assertNotNull(conditionDefintion.getDescription());
        assertEquals(ActionItemDefinitionType.condition, conditionDefintion.getType());        
        assertFalse(conditionDefintion.isAdHocPropertiesAllowed());
        assertNull(conditionDefintion.getParameterDefinition());    
        
        // Check what happens when bad name provided
        ActionItemDefinition bad = this.actionService.getActionItemDefinition("badName", ActionItemDefinitionType.condition);
        assertNull(bad);
    }
    
    public void testGetRuleTypes() throws Exception
    {
        RuleType[] ruleTypes = this.actionService.getRuleTypes();
        assertNotNull(ruleTypes);
        assertTrue(ruleTypes.length > 0);
        
        if (logger.isDebugEnabled() == true)
        {
            System.out.println("Rule types:");
            for (RuleType type : ruleTypes)
            {
                System.out.println(type.getName() + " - " + type.getDisplayLabel());
            }
        }
        
        RuleType ruleType = this.actionService.getRuleType("inbound");
        assertNotNull(ruleType);
        assertEquals("inbound", ruleType.getName());
        assertNotNull(ruleType.getDisplayLabel());
        
        // Check what happens when a bad name is provided
        RuleType badRuleType = this.actionService.getRuleType("basRuleName");
        assertNull(badRuleType);        
    }
    
    public void testActionPeristance() throws Exception
    {
        // Check there are no actions
        Action[] actions1 = this.actionService.getActions(BaseWebServiceSystemTest.contentReference, null);
        assertNull(actions1);
        
        // Create a new action
        NamedValue[] parameters = new NamedValue[]{new NamedValue("aspect-name", false, Constants.ASPECT_VERSIONABLE, null)};        
        Action newAction1 = new Action();
        newAction1.setActionName("add-features");
        newAction1.setTitle("Add the versionable aspect to the node.");
        newAction1.setDescription("This will add the verisonable aspect to the node and thus create a version history.");
        newAction1.setParameters(parameters);
        
        // Save the action
        Action[] saveResults1 = this.actionService.saveActions(BaseWebServiceSystemTest.contentReference, new Action[]{newAction1});
        assertNotNull(saveResults1);
        assertEquals(1, saveResults1.length);
        Action savedAction1 = saveResults1[0];
        assertNotNull(savedAction1);
        assertNotNull(savedAction1.getId());
        assertEquals("add-features", savedAction1.getActionName());
        assertEquals("Add the versionable aspect to the node.", savedAction1.getTitle());
        assertEquals("This will add the verisonable aspect to the node and thus create a version history.", savedAction1.getDescription());
        
        // Check the parameters of the saved action
        // TODO
        
        // Update the action
        savedAction1.setTitle("The title has been updated");

        
        // Save the action
        Action[] saveResults2 = this.actionService.saveActions(BaseWebServiceSystemTest.contentReference, new Action[]{savedAction1});
        assertNotNull(saveResults2);
        assertEquals(1, saveResults2.length);
        Action savedAction2 = saveResults2[0];
        assertNotNull(savedAction2);
        assertEquals(savedAction2.getId(), savedAction2.getId());
        assertEquals("add-features", savedAction2.getActionName());
        assertEquals("The title has been updated", savedAction2.getTitle());
        assertEquals("This will add the verisonable aspect to the node and thus create a version history.", savedAction2.getDescription());
        
        // TODO test action filters
        
        // Remove the all the actions
        this.actionService.removeActions(BaseWebServiceSystemTest.contentReference, null);
        
        // Check all actions have gone
        Action[] actions3 = this.actionService.getActions(BaseWebServiceSystemTest.contentReference, null);
        assertNull(actions3);
    }
    
    public void testActionExecution() throws Exception
    {
        Predicate predicate = new Predicate(new Reference[]{BaseWebServiceSystemTest.contentReference}, BaseWebServiceSystemTest.store, null);
        
        // First confirm that the content reference does not have the versionable aspect applied
        Node node1 = this.repositoryService.get(predicate)[0];
        for(String aspect : node1.getAspects())
        {
            if (Constants.ASPECT_VERSIONABLE.equals(aspect) == true)
            {
                fail("The content node already has the versionable aspect applied");
            }
        }
        
        // Create the action to add the versionable aspect
        NamedValue[] parameters = new NamedValue[]{new NamedValue("aspect-name", false, Constants.ASPECT_VERSIONABLE, null)};        
        Action newAction1 = new Action();
        newAction1.setActionName("add-features");
        newAction1.setTitle("Add the versionable aspect to the node.");
        newAction1.setDescription("This will add the verisonable aspect to the node and thus create a version history.");
        newAction1.setParameters(parameters);        
        
        // Execute the action
        ActionExecutionResult[] results = this.actionService.executeActions(predicate, new Action[]{newAction1});
        
        // Check the execution results
        assertNotNull(results);
        assertEquals(1, results.length);
        ActionExecutionResult result = results[0];
        assertEquals(BaseWebServiceSystemTest.contentReference.getUuid(), result.getReference().getUuid());
        assertNotNull(result.getActions());
        assertEquals(1, result.getActions().length);
        Action resultAction = result.getActions()[0];
        assertEquals("add-features", resultAction.getActionName());
        
        // Check that the versionable aspect has now been applied to that reference
        Node node2 = this.repositoryService.get(predicate)[0];
        boolean bFail = true;
        for(String aspect : node2.getAspects())
        {
            if (Constants.ASPECT_VERSIONABLE.equals(aspect) == true)
            {
                bFail = false;
            }
        }
        if (bFail == true)
        {
            fail("The action was executed but the versionable aspect has not been applied to the content reference");
        }
    }
    
    public void testRulePersistance() throws Exception
    {
        // Check there are no rules
        Rule[] rules1 = this.actionService.getRules(BaseWebServiceSystemTest.contentReference, null);
        assertNull(rules1);
        
        // Create the action
        NamedValue[] parameters = new NamedValue[]{new NamedValue("aspect-name", false, Constants.ASPECT_CLASSIFIABLE, null)};        
        Action newAction = new Action();
        newAction.setActionName("add-features");
        newAction.setParameters(parameters);
        
        // Create the rule
        Rule newRule = new Rule();
        newRule.setRuleTypes(new String[]{"incomming"});
        newRule.setTitle("This rule adds the classificable aspect");
        newRule.setAction(newAction);
        
        // Save the rule
        Rule[] saveResults1 = this.actionService.saveRules(BaseWebServiceSystemTest.contentReference, new Rule[]{newRule});
        assertNotNull(saveResults1);
        assertEquals(1, saveResults1.length);
        Rule savedRule1 = saveResults1[0];
        assertNotNull(savedRule1);
        assertNotNull(savedRule1.getRuleReference());
        assertNotNull(savedRule1.getOwningReference());
        assertEquals(BaseWebServiceSystemTest.contentReference.getUuid(), savedRule1.getOwningReference().getUuid());
        assertEquals("incomming", savedRule1.getRuleTypes()[0]);
        assertEquals("This rule adds the classificable aspect", savedRule1.getTitle());
        assertFalse(savedRule1.isExecuteAsynchronously());
        
        // Check the actions of the saved rule
        // TODO
        
        // Update the rule
        savedRule1.setTitle("The title has been updated");
        savedRule1.setExecuteAsynchronously(true);
        
        // Save the action
        Rule[] saveResults2 = this.actionService.saveRules(BaseWebServiceSystemTest.contentReference, new Rule[]{savedRule1});
        assertNotNull(saveResults2);
        assertEquals(1, saveResults2.length);
        Rule savedRule2 = saveResults2[0];
        assertNotNull(savedRule2);
        assertEquals(savedRule2.getRuleReference().getUuid(), savedRule2.getRuleReference().getUuid());
        assertEquals(BaseWebServiceSystemTest.contentReference.getUuid(), savedRule2.getOwningReference().getUuid());
        assertEquals("incomming", savedRule2.getRuleTypes()[0]);
        assertEquals("The title has been updated", savedRule2.getTitle());
        assertTrue(savedRule2.isExecuteAsynchronously());
        
        // TODO test rule filters
        
        // Remove the all the rules
        this.actionService.removeRules(BaseWebServiceSystemTest.contentReference, null);
        
        // Check all rules have gone
        Rule[] rules3 = this.actionService.getRules(BaseWebServiceSystemTest.contentReference, null);
        assertNull(rules3);
    }
    
    public void testActionUtilMethods()
    	throws Exception
    {
    	Predicate predicate = new Predicate(new Reference[]{BaseWebServiceSystemTest.contentReference}, BaseWebServiceSystemTest.store, null);
    	
    	Map<String, String> parameters = new HashMap<String, String>(1);
    	parameters.put("aspect-name", "{http://www.alfresco.org/model/content/1.0}summarizable");
    	String result = ActionUtils.executeAction(BaseWebServiceSystemTest.contentReference, "add-features", parameters);
    	
    	assertNull(result);
    	
    	// Need to check that the aspect has been applied to the node
    	Node node2 = this.repositoryService.get(predicate)[0];
        boolean bFail = true;
        for(String aspect : node2.getAspects())
        {
            if ("{http://www.alfresco.org/model/content/1.0}summarizable".equals(aspect) == true)
            {
                bFail = false;
            }
        }
        if (bFail == true)
        {
            fail("The action was executed but the summarizable aspect has not been applied to the content reference");
        }
        
        ParentReference parentReference = new ParentReference();
        parentReference.setAssociationType(Constants.ASSOC_CHILDREN);
        parentReference.setChildName("{" + Constants.NAMESPACE_CONTENT_MODEL + "}tempFolder");
        parentReference.setStore(BaseWebServiceSystemTest.store);
        parentReference.setUuid(BaseWebServiceSystemTest.rootReference.getUuid());
        
        CMLCreate createFolder = new CMLCreate();
        createFolder.setId("folder1");
        createFolder.setType(Constants.TYPE_FOLDER);
        createFolder.setParent(parentReference);
        createFolder.setProperty(new NamedValue[] {
                new NamedValue(
                        Constants.PROP_NAME,
                        false,
                        "tempFolder",
                        null)});
        
        // Create a content document beneth the folder
        NamedValue[] contentProperties2 = new NamedValue[]{new NamedValue(Constants.PROP_NAME, false, "test.js", null)};
        CMLCreate createContent2 = new CMLCreate("testContent2", null, "folder1", Constants.ASSOC_CONTAINS, "{" + Constants.NAMESPACE_CONTENT_MODEL + "}test.js", Constants.TYPE_CONTENT, contentProperties2);
        
        CML cml = new CML();
        cml.setCreate(new CMLCreate[]{createFolder, createContent2});
        
        UpdateResult[] updateResult = this.repositoryService.update(cml);
        Reference folder = updateResult[0].getDestination();
        Reference script = updateResult[1].getDestination();
        
        // Write the test content to the reference
        this.contentService.write(script, Constants.PROP_CONTENT, "\"VALUE\"".getBytes(), new ContentFormat(Constants.MIMETYPE_TEXT_PLAIN, "UTF-8"));
        
        // Execute 'count children' action
        String result2 = ActionUtils.executeAction(folder, "count-children", null);        
        assertNotNull(result2);
        assertEquals("1", result2);
        
        // Execute scipt
        String scriptResult = ActionUtils.executeScript(folder, script);
        assertNotNull(scriptResult);
        assertEquals("VALUE", scriptResult);
    }
    
    public void testCreateAndEditRule()
        throws Exception
    {
        Store spacesStore = new Store(Constants.WORKSPACE_STORE, "SpacesStore");
  
        String folderName = "rulesFolder" + System.currentTimeMillis();
        ParentReference parentReference = new ParentReference(
                spacesStore,
                null, 
                "/app:company_home",
                Constants.ASSOC_CONTAINS, 
                Constants.createQNameString(Constants.NAMESPACE_CONTENT_MODEL, folderName));
        NamedValue[] properties = new NamedValue[]{Utils.createNamedValue(Constants.PROP_NAME, folderName)};
        CMLCreate create = new CMLCreate("1", parentReference, null, null, null, Constants.TYPE_FOLDER, properties);
        CML cml = new CML();
        cml.setCreate(new CMLCreate[]{create});
        UpdateResult[] results = WebServiceFactory.getRepositoryService().update(cml);   
        Reference folder = results[0].getDestination();
        
        // Create the action
        NamedValue[] parameters = new NamedValue[]{new NamedValue("aspect-name", false, "{http://www.alfresco.org/model/content/1.0}generalclassifiable", null)};        
        Action newAction = new Action();
        newAction.setActionName("add-features");
        newAction.setParameters(parameters);
        
        // Create the action condition
        Condition condition = new Condition();
        condition.setConditionName("no-condition");    
        
        // Create the composite action
        Action action = new Action();
        action.setActionName("composite-action");
        action.setActions(new Action[]{newAction});
        action.setConditions(new Condition[]{condition});
        
        // Create the rule
        Rule newRule = new Rule();
        newRule.setRuleTypes(new String[]{"inbound"});
        newRule.setTitle("This rule adds the classificable aspect");
        newRule.setDescription("This is the description of the rule");
        newRule.setAction(action);
        
        WebServiceFactory.getActionService().saveRules(folder, new Rule[]{newRule});
    }
}
