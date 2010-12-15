<?php

// TODO need to set $wgAuth in order to use this auth plugin ...

// Don't let anonymous people do things
$wgGroupPermissions['*']['createaccount']   = false;
$wgGroupPermissions['*']['read']            = false;
$wgGroupPermissions['*']['edit']            = false;

require_once('AuthPlugin.php');

class AuthAlfresco extends AuthPlugin
{
	var $log;
	
	function AuthAlfresco() 
	{	  	
		$this->log = new Logger();
		$this->log->debug("AuthAlfresco - Calling constructor AuthAlfresco");
	}
	 
	/**
	 * Disallow password change.
	 *
	 * @return bool
	 */
	function allowPasswordChange() 
	{
		// TODO .. could we allow this?
		return true;
	}
 
  	/**
   	 * This should not be called because we do not allow password change.  Always
   	 * fail by returning false.
   	 *
   	 * @param $user User object.
   	 * @param $password String: password.
   	 * @return bool
     * @public
   	 */
	 function setPassword($user, $password) 
	 {
	    return true;
	 }
 
	  /**
	   * We don't support this but we have to return true for preferences to save.
	   *
	   * @param $user User object.
	   * @return bool
	   * @public
	   */
	  function updateExternalDB($user) 
	  {
	    return true;
	  }
	 
	  /**
	   * We can't create external accounts so return false.
	   *
	   * @return bool
	   * @public
	   */
	  function canCreateAccounts() 
	  {
	    return true;
	  }
 
	  /**
	   * We don't support adding users to whatever service provides REMOTE_USER, so
	   * fail by always returning false.
	   *
	   * @param User $user
	   * @param string $password
	   * @return bool
	   * @public
	   */
	  function addUser($user, $password) 
	  {
	    return true;
	  }
	 	 
	  function userExists($username) 
	  {
	  	$this->log->debug("AuthAlfresco - Calling function: userExits");
	  	
	  	//TODO ???
	    return true;
	  }

  	  function authenticate($username, $password) 
  	  {
  	  	global $alfTicket, $alfRepository, $alfWikiSpaceNodeRef, $alfLog;

  	  	$result = false;
  	  	$localTicket = MediaWikiSpace::validate($alfRepository, $alfWikiSpaceNodeRef, $username, $password);
  	  	if ($localTicket != null)
		{
			// Store the ticket for later use now we know it's good	
			$alfTicket = $localTicket;
			$_SESSION["alfTicket"] = $localTicket;
			$result = true;
		}
		
		return $result;
	  }
	  
	  /**
	   * Chceks to see whether the passed string is a ticket or not
	   * 
	   * @param   $ticket	the string to check
	   * Return   boolean	true if the string is a ticket, false otherwise
	   */
	  private function isTicket($ticket)
	  {
	  	$result = false;
	  	if (preg_match("/^TICKET_/", $ticket) > 0)
	  	{
	  		$result = true;
	  	}
	  	return $result;
	  }
 
	  /**
	   * Check to see if the specific domain is a valid domain.
	   *
	   * @param $domain String: authentication domain.
	   * @return bool
	   * @public
	   */
	  function validDomain($domain) 
	  {
	    return true;
	  }
 
	  /**
	   * When a user logs in, optionally fill in preferences and such.
	   * For instance, you might pull the email address or real name from the
	   * external user database.
	   *
	   * The User object is passed by reference so it can be modified; don't
	   * forget the & on your function declaration.
	   *
	   * @param User $user
	   * @public
	   */
	  function updateUser(&$user) 
	  {
	    // We only set this stuff when accounts are created.
	    return true;
	  }
 
	  function autoCreate() 
	  {
	  	// TODO .. presume we should be returning true here (for now?)
	    return true;
	  }
 
	  /**
	   * Return true to prevent logins that don't authenticate here from being
	   * checked against the local database's password fields.
	   *
	   * @return bool
	   * @public
	   */
	  function strict() 
	  {
	    return true;
	  }
 
	  /**
	   * When creating a user account, optionally fill in preferences and such.
	   * For instance, you might pull the email address or real name from the
	   * external user database.
	   *
	   * @param $user User object.
	   * @public
	   */
	  function initUser(&$user) 
	  {
	    //global $_SERVER;
	    //$username = $_SERVER['REMOTE_USER'];
	 
	    //// Using your own methods put the users real name here.
	    //$user->setRealName('');
	    //// Using your own methods put the users email here.
	    //$user->setEmail($username . '@example.com');
	 
	    //$user->mEmailAuthenticated = wfTimestampNow();
	    //$user->setToken();
	 
	    ////turn on e-mail notifications by default
	    //$user->setOption('enotifwatchlistpages', 1);
	    //$user->setOption('enotifusertalkpages', 1);
	    //$user->setOption('enotifminoredits', 1);
	    //$user->setOption('enotifrevealaddr', 1);
	 
	    //$user->saveSettings();
	  }
	 
	  /**
	   * Modify options in the login template.  This shouldn't be very important
	   * because no one should really be bothering with the login page.
	   *
	   * @param $template UserLoginTemplate object.
	   * @public
	   */
	  function modifyUITemplate(&$template) 
	  {
	  	// TODO ???
	  	
	    //disable the mail new password box
	    $template->set('useemail', false);
	    $template->set('create', false);
	    $template->set('domain', false);
	    $template->set('usedomain', false);
	  }
	 
	  /**
	   * Normalize user names to the mediawiki standard to prevent duplicate
	   * accounts.
	   *
	   * @param $username String: username.
	   * @return string
	   * @public
	   */
	  //function getCanonicalName($username) 
	  //{
	  	// TODO ???
	  	
	    // lowercase the username
	  //  $username = strtolower($username);
	    // uppercase first letter to make mediawiki happy
	  //  $username[0] = strtoupper($username[0]);
	  //  return $username;
	 // }
}
?>  