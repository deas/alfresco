<#assign label_signup_already_reg="Thank you for signing up to Alfresco. You already have an Alfresco account associated with this e-mail address.">
<#assign label_log_in="Login">
<#assign label_forgot="Forgot Password?">
<#assign label_capabilities="Upload, manage and share files, collaborate with your colleagues, and much more on Alfresco.">
<#assign label_signed="Alfresco Team">
<#assign label_legal="You're receiving this because you registered for an Alfresco account from <a href='http://www.alfresco.com'>http://www.alfresco.com</a>.">
<#assign label_activate_cta="Activate Account">
<#if initiator_first_name?? && initiator_last_name??>
<#assign label_signup_invited="${initiator_first_name?html} ${initiator_last_name?html} has invited you to join Alfresco - upload, manage and share files, collaborate with your colleagues, across web, mobile and tablet.">
<#assign label_user_msg_msg="Message from ${initiator_first_name?html} ${initiator_last_name?html}:">
</#if>
<#assign label_accept="Accept Invitation">
<#assign label_learn_more="<a href='http://cloud.alfresco.com'>Want to know all about using Alfresco Cloud?</a>">
<#if cancel_registration_url??>
<#assign label_ignore="Not going to join the site? You can always <a href='${cancel_registration_url}'>reject the invitation</a>.">
</#if>
<#if activate_account_url??>
<#assign label_activate="Thank you for signing up to Alfresco. Your registration is almost complete. Please <a href='${activate_account_url}'>activate your account</a>.">
<#assign label_activate_reminder="Thank you for signing up to Alfresco. Please <a href='${activate_account_url}'>activate your account</a>.">
<#assign label_saml_activate="Thank you for signing up to Alfresco. Your company has already set up a controlled network on Alfresco. Please <a href='${activate_account_url}'>login</a> to start collaborating with your colleagues">
<#assign label_saml_invited="You have been invited to Alfresco. Please <a href='${activate_account_url}'>login</a> to start collaborating with your colleagues">
</#if>