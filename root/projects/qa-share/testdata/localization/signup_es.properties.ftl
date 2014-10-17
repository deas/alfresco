<#assign label_signup_already_reg="Gracias por registrarse en Alfresco. Ya tiene una cuenta de Alfresco asociada a esta dirección de email.">
<#assign label_log_in="Iniciar sesión">
<#assign label_forgot="¿Ha olvidado su contraseña?">
<#assign label_capabilities="Cargue, gestione y comparta ficheros, colabore con sus colegas y haga mucho más con Alfresco.">
<#assign label_signed="El equipo de Alfresco">
<#assign label_legal="Ha recibido este email porque tiene registrada una cuenta de Alfresco en <a href='http://www.alfresco.com'>http://www.alfresco.com</a>.">
<#assign label_activate_cta="Activar cuenta">
<#if initiator_first_name?? && initiator_last_name??>
<#assign label_signup_invited="${initiator_first_name?html} ${initiator_last_name?html} le ha invitado a unirse a Alfresco. Cargue, gestione y comparta ficheros, colabore con sus colegas, en la web, de forma móvil o con su tableta.">
<#assign label_user_msg_msg="Mensaje de ${initiator_first_name?html} ${initiator_last_name?html}:">
</#if>
<#assign label_accept="Aceptar invitación">
<#assign label_learn_more="<a href='http://cloud.alfresco.com'>¿Desea información sobre cómo utilizar la nube de Alfresco?</a>">
<#if cancel_registration_url??>
<#assign label_ignore="¿No desea unirse al sitio? Siempre podrá <a href='${cancel_registration_url}'>rechazar la invitación</a>.">
</#if>
<#if activate_account_url??>
<#assign label_activate="Gracias por registrarse en Alfresco. Su registro casi se ha completado. Por favor, <a href='${activate_account_url}'>active su cuenta</a>.">
<#assign label_activate_reminder="Gracias por registrarse en Alfresco. Por favor, <a href='${activate_account_url}'>active su cuenta</a>.">
<#assign label_saml_activate="Gracias por registrarse en Alfresco. Su compañía ya ha configurado una red controlada en Alfresco. Puede <a href='${activate_account_url}'>iniciar sesión</a> para comenzar a colaborar con sus compañeros">
<#assign label_saml_invited="Le han invitado a Alfresco. Puede <a href='${activate_account_url}'>iniciar sesión</a> para comenzar a colaborar con sus compañeros">
</#if>