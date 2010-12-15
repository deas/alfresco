// get user prefs from url args
model.filter = 0;
if (args["f"] != null)
{
   model.filter = Number(args["f"]);
}

model.mode = 0;
if (args["m"] != null)
{
   model.mode = Number(args["m"]);
}