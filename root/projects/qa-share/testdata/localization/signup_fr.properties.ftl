<#assign label_signup_already_reg="Merci d'avoir créé un compte Alfresco. Vous possédez déjà un compte Alfresco associé à cette adresse e-mail.">
<#assign label_log_in="Connexion">
<#assign label_forgot="Mot de passe oublié ?">
<#assign label_capabilities="Téléchargez, gérez et partagez des fichiers, collaborez avec vos collègues et bien plus sur Alfresco.">
<#assign label_signed="L'équipe Alfresco">
<#assign label_legal="Vous recevez ce message car vous avez créé un compte Alfresco sur le site <a href='http://www.alfresco.com'>http://www.alfresco.com</a>.">
<#assign label_activate_cta="Activer mon compte">
<#if initiator_first_name?? && initiator_last_name??>
<#assign label_signup_invited="${initiator_first_name?html} ${initiator_last_name?html} vous invite à rejoindre Alfresco - téléchargez, gérez et partagez des fichiers, collaborez avec vos collègues, sur le web, sur votre mobile ou sur votre tablette.">
<#assign label_user_msg_msg="Message de ${initiator_first_name?html} ${initiator_last_name?html} :">
</#if>
<#assign label_accept="Accepter l'invitation">
<#assign label_learn_more="<a href='http://cloud.alfresco.com'>Vous souhaitez en savoir plus sur l'utilisation d'Alfresco Cloud ?</a>">
<#if cancel_registration_url??>
<#assign label_ignore="Vous ne souhaitez pas rejoindre ce site ? Vous pouvez toujours <a href='${cancel_registration_url}'>rejeter l'invitation</a>.">
</#if>
<#if activate_account_url??>
<#assign label_activate="Merci d'avoir créé un compte Alfresco. Votre inscription est presque terminée. Veuillez <a href='${activate_account_url}'>activer votre compte</a>.">
<#assign label_activate_reminder="Merci d'avoir créé un compte Alfresco. Veuillez <a href='${activate_account_url}'>activer votre compte</a>.">
<#assign label_saml_activate="Merci d'avoir créé un compte Alfresco. Votre entreprise a déjà configuré un réseau contrôlé dans Alfresco. Veuillez <a href='${activate_account_url}'>vous connecter</a> pour collaborer avec vos collègues">
<#assign label_saml_invited="Vous avez été invité à rejoindre Alfresco. Veuillez <a href='${activate_account_url}'>vous connecter</a> pour collaborer avec vos collègues">
</#if>