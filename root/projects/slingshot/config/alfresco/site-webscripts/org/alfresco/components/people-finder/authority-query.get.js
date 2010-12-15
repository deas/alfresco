var MAPPING_TYPE =
{
   API: 0,
   STATIC: 1
};

var mapUser = function(data)
{
   return (
   {
      authorityType: "USER",
      shortName: data.userName,
      fullName: data.userName,
      displayName: (data.firstName ? data.firstName + " " : "") + (data.lastName ? data.lastName : ""),
      description: data.jobtitle ? data.jobtitle : "",
      metadata:
      {
         avatar: data.avatar || null,
         jobTitle: data.jobtitle || "",
         organization: data.organization || ""
      }
   });
};

var mapGroup = function(data)
{
   return (
   {
      authorityType: "GROUP",
      shortName: data.shortName,
      fullName: data.fullName,
      displayName: data.displayName,
      description: data.fullName,
      metadata:
      {
      }
   });
};

var getMappings = function()
{
   var mappings = [],
      authorityType = args.authorityType === null ? "all" : String(args.authorityType).toLowerCase();
   
   if (authorityType === "all" || authorityType == "user")
   {
      mappings.push(
      {
         type: MAPPING_TYPE.API,
         url: "/api/people?filter=" + encodeURIComponent(args.filter),
         rootObject: "people",
         fn: mapUser
      });
   }

   if (authorityType === "all" || authorityType === "group")
   {
      var url = "/api/groups?shortNameFilter=" + encodeURIComponent(args.filter);
      if (args.zone !== "all")
      {
         url += "&zone=" + encodeURIComponent(args.zone === null ? "APP.DEFAULT" : args.zone);
      }
      
      mappings.push(
      {
         type: MAPPING_TYPE.API,
         url: url,
         rootObject: "data",
         fn: mapGroup
      });

      mappings.push(
      {
         type: MAPPING_TYPE.STATIC,
         data: [
            {
               shortName: "EVERYONE",
               fullName: "GROUP_EVERYONE",
               displayName: msg.get("group.everyone"),
               description: "GROUP_EVERYONE"
            }
         ],
         fn: mapGroup
      });
   }
   return mappings;
};

function main()
{
   var mappings = getMappings(),
      connector = remote.connect("alfresco"),
      authorities = [],
      mapping, result, data, i, ii, j, jj;
   
   for (i = 0, ii = mappings.length; i < ii; i++)
   {
      mapping = mappings[i];
      if (mapping.type == MAPPING_TYPE.API)
      {
         result = connector.get(mapping.url);
         if (result.status == 200)
         {
            data = eval('(' + result + ')');
            for (j = 0, jj = data[mapping.rootObject].length; j < jj; j++)
            {
               authorities.push(mapping.fn.call(this, data[mapping.rootObject][j]));
            }
         }
      }
      else if (mapping.type == MAPPING_TYPE.STATIC)
      {
         for (j = 0, jj = mapping.data.length; j < jj; j++)
         {
            authorities.push(mapping.fn.call(this, mapping.data[j]));
         }
      }
   }
   
   return authorities;
}

model.authorities = main();