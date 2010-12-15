var record = behaviour.args[0];
if (record.hasAspect("rma:record") == true)
{ 
    var filePlan = rm.getFilePlan(record);
    if (filePlan != null)
    {
        logger.log("Cutoff on obsolete == " + filePlan.properties[rm.PROP_CUTOFF_ON_OBSOLETE]);
        if (filePlan.properties[rm.PROP_CUTOFF_ON_OBSOLETE] == true)        
        {
            // Add the cutoff aspect tot he record
            logger.log("Adding the cuttoff aspect to (" + record.id + ")");
            record.addAspect(rm.ASPECT_CUTOFF);
        }
    }
}