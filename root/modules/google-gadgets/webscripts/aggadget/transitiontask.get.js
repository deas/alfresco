var id = args.id;

// ensure mandatory attributes have been located
if (id == null)
{
   status.code = 400;
   status.message = "Missing Task Id in request arguments.";
   status.redirect = true;
}
else
{
   // transition the task
   var task = workflow.getTaskById(id);
   if (task == null)
   {
      status.code = 400;
      status.message = "Unable to find task for task Id: " + id;
      status.redirect = true;
   }
   else
   {
      task.endTask(args.t);   // transition ID can be null
   }
}