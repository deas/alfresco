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

//Widget instantiation metadata...
model.widgets = [];
var ruleEdit = {};
ruleEdit.name = "Alfresco.RuleEdit";
ruleEdit.useMessages = true;
ruleEdit.useOptions = true;
ruleEdit.options = {};
ruleEdit.options.nodeRef = (page.url.args.nodeRef != null) ? page.url.args.nodeRef : "";
if (model.rule)
{
   ruleEdit.options.rule = model.rule;
}
if (model.constraints)
{
   ruleEdit.options.constraints = model.constraints;
}
ruleEdit.options.siteId = (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "";
model.widgets.push(ruleEdit);