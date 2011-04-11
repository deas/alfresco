function main()
{
   var ruleNodeRef = page.url.args.nodeRef,
      ruleId = page.url.args.ruleId,
      connector = remote.connect("alfresco"),
      rule = null,
      result,
      data;

   // Load rule to edit of given in url
   if (ruleNodeRef && ruleId)
   {
      result = connector.get("/api/node/" + ruleNodeRef.replace("://", "/") + "/ruleset/rules/" + ruleId);
      if (result.status == 200)
      {
         data = eval('(' + result + ')');
         rule = jsonUtils.toJSONString(data);
         model.ruleTitle = data.title;
      }
   }
   model.rule = rule;

   // Load constraints
   result = connector.get("/api/actionConstraints");

   if (result.status == 200)
   {
      var constraintsArr = eval('(' + result + ')').data;
      var constraintsObj = {};
      for (var i = 0, il = constraintsArr.length, constraint; i < il; i++)
      {
         constraint = constraintsArr[i];
         constraintsObj[constraint.name] = constraint.values;
         if (constraint.name == "ac-scripts")
         {
            model.scripts = constraint.values;
         }
      }
      model.constraints = jsonUtils.toJSONString(constraintsObj);
   }

   // Repository Library root node
   var rootNode = "alfresco://company/home",
      repoConfig = config.scoped["RepositoryLibrary"]["root-node"];
   if (repoConfig !== null)
   {
      rootNode = repoConfig.value;
   }

   model.rootNode = rootNode;
}



main();