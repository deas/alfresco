// ensure mandatory attributes have been located
if (args.id == null)
{
   status.code = 400;
   status.message = "Missing Task ID in request arguments.";
   status.redirect = true;
}
else
{
   var task = workflow.getTaskById(args.id);
   if (task == null) 
   {
      status.code = 400;
      status.message = "Error: Workflow Task for details page does not exist";
      status.redirect = true;
   }
}