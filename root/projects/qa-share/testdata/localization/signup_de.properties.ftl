<#assign label_signup_already_reg="Danke für Ihre Anmeldung bei Alfresco. Sie haben bereits ein mit dieser E-Mail-Adresse verbundenes Alfresco-Konto.">
<#assign label_log_in="Anmelden">
<#assign label_forgot="Passwort vergessen?">
<#assign label_capabilities="Auf Alfresco können Sie Dateien hochladen, verwalten und freigeben und mit Ihren Kollegen zusammenarbeiten.">
<#assign label_signed="Das Alfresco-Team">
<#assign label_legal="Sie erhalten diese Nachricht, weil Sie sich auf <a href='http://www.alfresco.com'>http://www.alfresco.com</a> für ein Alfresco-Konto registriert haben.">
<#assign label_activate_cta="Konto aktivieren">
<#if initiator_first_name?? && initiator_last_name??>
<#assign label_signup_invited="${initiator_first_name?html} ${initiator_last_name?html} hat Sie eingeladen, Alfresco beizutreten, um Dateien hochzuladen, zu verwalten und freizugeben und mit Ihren Kollegen über das Internet, mobile Geräte oder Tablets zusammenzuarbeiten.">
<#assign label_user_msg_msg="Nachricht von ${initiator_first_name?html} ${initiator_last_name?html}:">
</#if>
<#assign label_accept="Einladung annehmen">
<#assign label_learn_more="<a href='http://cloud.alfresco.com'>Sie möchten mehr über Alfresco Cloud erfahren?</a>">
<#if cancel_registration_url??>
<#assign label_ignore="Möchten Sie der Site nicht beitreten? Sie können die Einladung jeder jederzeit <a href='${cancel_registration_url}'>ablehnen</a>.">
</#if>
<#if activate_account_url??>
<#assign label_activate="Danke für Ihre Anmeldung bei Alfresco. Ihre Registrierung ist fast abgeschlossen. Bitte <a href='${activate_account_url}'>aktivieren Sie Ihr Konto</a>.">
<#assign label_activate_reminder="Danke für Ihre Anmeldung bei Alfresco. Bitte <a href='${activate_account_url}'>aktivieren Sie Ihr Konto</a>.">
<#assign label_saml_activate="Danke für Ihre Anmeldung bei Alfresco. Ihr Unternehmen hat bereits ein kontrolliertes Alfresco-Netzwerk eingerichtet. Bitte <a href='${activate_account_url}'>melden Sie sich an</a>, damit Sie mit Ihren Kollegen zusammenarbeiten können.">
<#assign label_saml_invited="Sie wurden zu Alfresco eingeladen. Bitte <a href='${activate_account_url}'>melden Sie sich an</a>, damit Sie mit Ihren Kollegen zusammenarbeiten können.">
</#if>
