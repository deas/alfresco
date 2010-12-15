/**
 * Helper to determine if the current user has the given Capability in any assigned Role
 * 
 * @method hasCapability
 * @param conn Connector to use
 * @param cap Capability ID to test e.g. "AccessAudit"
 * @return true if the capability is present for this user, false otherwise
 */
function hasCapability(conn, cap)
{
   var hasCapability = false;
   var res = conn.get("/api/rma/admin/rmroles?user=" + encodeURIComponent(user.name));
   if (res.status == 200)
   {
      var roles = eval('(' + res + ')').data;
      for each (var role in roles)
      {
         for each (var c in role.capabilities)
         {
            if (c == cap)
            {
               hasCapability = true;
               break;
            }
         }
         if (hasCapability) break;
      }
   }
   return hasCapability;
}