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
package org.alfresco.solr;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.M2Namespace;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class SolrSchemaGenerationTest extends TestCase
{
    public static final String TEST_RESOURCE_MESSAGES = "alfresco/messages/dictionary-messages";

    // private static final String TEST_URL = "http://www.alfresco.org/test/dictionarydaotest/1.0";
    private static final String TEST_MODEL = "org/alfresco/repo/dictionary/dictionarydaotest_model.xml";

    private static final String TEST_BUNDLE = "org/alfresco/repo/dictionary/dictionarydaotest_model";

    private DictionaryService service;

    @Override
    public void setUp()
    {
        boolean inRepoContext = true;
        List<String> bootstrapModels = new ArrayList<String>();
        bootstrapModels.add("alfresco/model/dictionaryModel.xml");
        if (inRepoContext)
        {
            bootstrapModels.add("alfresco/model/applicationModel.xml");
            bootstrapModels.add("alfresco/model/blogIntegrationModel.xml");
            bootstrapModels.add("alfresco/model/calendarModel.xml");
            bootstrapModels.add("alfresco/model/contentModel.xml");
            bootstrapModels.add("alfresco/model/datalistModel.xml");
            bootstrapModels.add("alfresco/model/emailServerModel.xml");
            bootstrapModels.add("alfresco/model/forumModel.xml");
            bootstrapModels.add("alfresco/model/imapModel.xml");
            bootstrapModels.add("alfresco/model/linksModel.xml");
            bootstrapModels.add("alfresco/model/siteModel.xml");
            bootstrapModels.add("alfresco/model/systemModel.xml");
            bootstrapModels.add("alfresco/model/transferModel.xml");
            bootstrapModels.add("alfresco/model/wcmAppModel.xml");
            bootstrapModels.add("alfresco/model/wcmModel.xml");
            
            bootstrapModels.add("org/alfresco/repo/security/authentication/userModel.xml");
            bootstrapModels.add("org/alfresco/repo/action/actionModel.xml");
            bootstrapModels.add("org/alfresco/repo/rule/ruleModel.xml");
            bootstrapModels.add("org/alfresco/repo/version/version2_model.xml");  
            
            bootstrapModels.add("alfresco/model/bpmModel.xml");  
        }
        else
        {
            bootstrapModels.add(TEST_MODEL);
        }
        HashMap<String, M2Model> modelMap = new HashMap<String, M2Model>();
        for (String bootstrapModel : bootstrapModels)
        {
            System.out.println("Loading ..."+bootstrapModel);
            InputStream modelStream = getClass().getClassLoader().getResourceAsStream(bootstrapModel);
            M2Model model = M2Model.createModel(modelStream);
            for (M2Namespace namespace : model.getNamespaces())
            {
                modelMap.put(namespace.getUri(), model);
            }
        }

        // Load the models ensuring that they are loaded in the correct order
        HashSet<String> loadedModels = new HashSet<String>();
        for (M2Model model : modelMap.values())
        {
            loadModel(modelMap, loadedModels, model);
        }

    }

    private void loadModel(Map<String, M2Model> modelMap, HashSet<String> loadedModels, M2Model model)
    {
        String modelName = model.getName();
        if (loadedModels.contains(modelName) == false)
        {
            for (M2Namespace importNamespace : model.getImports())
            {
                M2Model importedModel = modelMap.get(importNamespace.getUri());
                if (importedModel != null)
                {

                    // Ensure that the imported model is loaded first
                    loadModel(modelMap, loadedModels, importedModel);
                }
            }

            AlfrescoSolrDataModel.getInstance("test").putModel(model);
            loadedModels.add(modelName);
        }
    }

    public void testWrite() throws Exception
    {
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setNewLineAfterDeclaration(false);
        format.setIndentSize(3);
        format.setEncoding("UTF-8");

        File temp = File.createTempFile("SolrSchemaGenerationTest-", null, null);
        XMLWriter xmlWriter = new XMLWriter(new BufferedWriter(new FileWriter(temp)), format);
        AlfrescoSolrDataModel.getInstance("test").generateSchema(xmlWriter);
        System.out.println("Schema written to: " + temp.getCanonicalPath());
    }

}
