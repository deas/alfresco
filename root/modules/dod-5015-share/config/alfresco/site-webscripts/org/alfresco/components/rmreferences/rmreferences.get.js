function getDocName(nodeRef)
{
   var result = remote.call("/api/metadata?shortQNames=true&nodeRef=" + nodeRef);
   if (result.status == 200)
   {
      var data = eval('(' + result + ')');
      return data.properties["cm:name"];
   }
   return nodeRef;
}

/*
 * Note, "From" is customreferences from this node and *not* from other documents to this node.
 */ 
function getDocReferences()
{
   var nodeRef = page.url.args.nodeRef.replace(":/", "");
   var result = remote.call("/api/node/" + nodeRef + "/customreferences");
   var processDocRefs = function(docrefs, useTargetRef)
   {
      for (var i = 0, len = docrefs.length; i < len; i++)
      {
         var ref = docrefs[i];
         ref.refDocName = (ref.referenceType == 'parentchild') ? getDocName(ref.childRef) : getDocName(useTargetRef ? ref.targetRef : ref.sourceRef);
         if (ref.referenceType == 'parentchild')
         {
            ref.label = ref.target;
            ref.targetRef = ref.childRef;
            ref.sourceRef = ref.parentRef;
         }
         docrefs[i] = ref;
      }      
      return docrefs;
   };
   
   if (result.status == 200)
   {
      var data = eval('(' + result + ')');
      var docrefs =
      {
         from: processDocRefs(data.data.customReferencesFrom, true),
         to: processDocRefs(data.data.customReferencesTo, false)
      };
      
      return docrefs;
   }
   return [];
}

model.references = getDocReferences();