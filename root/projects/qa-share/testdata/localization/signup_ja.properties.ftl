<#assign label_signup_already_reg="Alfrescoにご登録いただきありがとうございます。お客様はすでにこのEメールアドレスでAlfrescoアカウントを取得されています。">
<#assign label_log_in="ログイン">
<#assign label_forgot="パスワードを忘れた場合">
<#assign label_capabilities="Alfrescoでは、ファイルのアップロード、管理、共有や、同僚との共同作業などを行うことができます。">
<#assign label_signed="Alfrescoチーム一同">
<#assign label_legal="このメールは、お客様が<a href='http://www.alfresco.com'>http://www.alfresco.com</a>からAlfrescoアカウントを登録した時点で送信される自動配信メールです。">
<#assign label_activate_cta="アカウントを有効にする">
<#if initiator_first_name?? && initiator_last_name??>
<#assign label_signup_invited="${initiator_first_name?html} ${initiator_last_name?html} 様から、 Alfrescoに参加するよう招待されました。Alfrescoでは、ファイルのアップロード、管理、共有を行ったり、Webや携帯電話、タブレットを使って同僚と共同作業を行うことができます。">
<#assign label_user_msg_msg="${initiator_first_name?html} ${initiator_last_name?html} 様からのメッセージ:">
</#if>
<#assign label_accept="招待を承諾する">
<#assign label_learn_more="<a href='http://cloud.alfresco.com'>Alfresco Cloudの使い方</a>">
<#if cancel_registration_url??>
<#assign label_ignore="サイトに参加しない場合は、いつでも<a href='${cancel_registration_url}'>招待を拒否</a>できます。">
</#if>
<#if activate_account_url??>
<#assign label_activate="Alfrescoにご登録いただきありがとうございます。登録手続きはあと少しで完了します。<a href='${activate_account_url}'>お客様のアカウントを有効に</a>してください。">
<#assign label_activate_reminder="Alfrescoにご登録いただきありがとうございます。<a href='${activate_account_url}'>お客様のアカウントを有効に</a>してください。">
<#assign label_saml_activate="Alfrescoに登録いただき、ありがとうございました。 お客様の会社はすでにAlfresco内に専用のネットワークをお持ちです。 <a href=''>ログイン</a>して、同僚の方と共同作業を始めてください。">
<#assign label_saml_invited="Alfrescoに招待されました。 <a href='${activate_account_url}'>ログイン</a>して、同僚の方と共同作業を始めてください。">
</#if>
