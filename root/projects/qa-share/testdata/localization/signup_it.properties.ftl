<#assign label_signup_already_reg="Grazie per aver effettuato la registrazione ad Alfresco. Disponi già di un account Alfresco associato a questo indirizzo e-mail.">
<#assign label_log_in="Accedi">
<#assign label_forgot="Password dimenticata?">
<#assign label_capabilities="Su Alfresco è possibile caricare, gestire, condividere file, collaborare con i colleghi e molto altro ancora.">
<#assign label_signed="Alfresco Team">
<#assign label_legal="Hai ricevuto questa e-mail perché hai effettuato la registrazione per un account Alfresco al sito <a href='http://www.alfresco.com'>http://www.alfresco.com</a>.">
<#assign label_activate_cta="Attiva account">
<#if initiator_first_name?? && initiator_last_name??>
<#assign label_signup_invited="${initiator_first_name?html} ${initiator_last_name?html} ti ha invitato a unirti ad Alfresco: è possibile caricare, gestire, condividere file e collaborare con i colleghi, tramite Web, cellulare e tablet.">
<#assign label_user_msg_msg="Messaggio da ${initiator_first_name?html} ${initiator_last_name?html}:">
</#if>
<#assign label_accept="Accetta invito">
<#assign label_learn_more="<a href='http://cloud.alfresco.com'>Desideri avere informazioni sull'uso di Alfresco Cloud?</a>">
<#if cancel_registration_url??>
<#assign label_ignore="Non desideri unirti al sito? È sempre possibile <a href='${cancel_registration_url}'>respingere l'invito</a>.">
</#if>
<#if activate_account_url??>
<#assign label_activate="Grazie per aver effettuato la registrazione ad Alfresco. La registrazione è quasi completata. <a href='${activate_account_url}'>Attiva account</a>.">
<#assign label_activate_reminder="Grazie per aver effettuato la registrazione ad Alfresco. <a href='${activate_account_url}'>Attiva account</a>.">
<#assign label_saml_activate="Grazie per aver effettuato la registrazione ad Alfresco. L'azienda ha già configurato una rete controllata su Alfresco. Per iniziare a collaborare con i colleghi, <a href='${activate_account_url}'>login</a> effettuare l'accesso</a>.">
<#assign label_saml_invited="È stato ricevuto un invito ad Alfresco. Per iniziare a collaborare con i colleghi, <a href='${activate_account_url}'>effettuare l'accesso</a>.">
</#if>